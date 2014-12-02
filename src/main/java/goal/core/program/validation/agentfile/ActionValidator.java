/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.core.program.validation.agentfile;

import goal.core.kr.language.Query;
import goal.core.kr.language.Var;
import goal.core.program.Module;
import goal.core.program.NameSpace;
import goal.core.program.SelectExpression;
import goal.core.program.SentenceMood;
import goal.core.program.actions.Action;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.actions.SendAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.validation.Validator;
import goal.core.program.validation.ValidatorError;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link Validator} for an {@link Action}.<br>
 *
 * {@link ActionValidator} checks for the following errors:
 * <ul>
 * <li>In the action's parameters variables occur that are not bound. Note that
 * anonymous variables are also counted as unbound (but also that anonymous
 * variables are allowed inside the message content of an interrogative send
 * action). Variables of an action are bound by its precondition, by variables
 * that occur in the condition of the rule in which the action is used, and by
 * variables that occur in parameters of the action's parent module. The latter
 * two sets of variables must be set using {@link #setBoundVar(Set)}.</li>
 * <li>In the action's postcondition variables occur that are not bound. This
 * case is similar to the previous one</li>
 * <li>A {@link ModuleCallAction} that attempts to focus on one of the built-in
 * {@link Module}s (the main program, and the <code>init</code>,
 * <code>main</code> and <code>event</code> modules), just in case the parser
 * somehow recognizes it as a focus action.</li>
 * </ul>
 * It also checks for the following warning:
 * <ul>
 * <li>The use of selectors for 'mental actions' (adopt, drop, insert, delete)
 * other than {@link SelectExpression.SELF} or {@link SelectExpression.THIS}.</li>
 * </ul>
 *
 * @author N.Kraayenbrink
 * @author W.Pasman
 * @author K.Hindriks
 */
public class ActionValidator extends Validator<Action> {

	/**
	 * The variables that are bound by the condition of the rule in which the
	 * action is used, (one of) its parent module, and the action's
	 * precondition.
	 */
	private final Set<Var> boundVar;
	/**
	 * The {@link Module}s available in the scope of the action that is
	 * validated.
	 */
	private final NameSpace parent;

	/**
	 * Validates actions by verifying that all variables that occur in action
	 * parameters are bound and resolves names in case of user specified actions
	 * and module calls.
	 *
	 * @param parent
	 *            The scope of the action. Used to resolve names.
	 * @param boundVar
	 *            The variables that have been already bound within the scope of
	 *            the action.
	 */
	public ActionValidator(NameSpace parent, Set<Var> boundVar) {
		this.parent = parent;
		this.boundVar = new LinkedHashSet<>(boundVar);
	}

	/**
	 * Checks for the following issues:
	 * <ul>
	 * <li>Whether all variables of the {@link Action} are bound by the
	 * condition of the associated rule, its parent module, or by the action's
	 * precondition.</li>
	 * </ul>
	 */
	@Override
	protected void doValidate(Action subject) {
		// Check whether variables in action parameters are bound.
		// Add variables that are bound by all preconditions to the set of
		// variables that are bound. Take into account that a user-specified
		// action may have multiple preconditions.
		Set<Var> boundByPrecondition = new HashSet<>();
		if (subject instanceof UserSpecAction) {
			UserSpecAction userspec = (UserSpecAction) subject;
			List<Query> preconditions = userspec.getPreconditions();
			boundByPrecondition = preconditions.get(0).getFreeVar();
			for (Query preCondition : preconditions) {
				boundByPrecondition.retainAll(preCondition.getFreeVar());
			}
			boundVar.addAll(boundByPrecondition);

			// Check consistency of action specifications.
			// TODO: check that all action specs set same EXTERNAL flag, i.e.,
			// flag to determine whether to sent action to environment or not.
			// See GOAL.g: @int or @env can be used.
		}

		// Validate anonymous modules 'in place'.
		// Bound variables need to be passed on to anonymous modules.
		if (subject instanceof ModuleCallAction) {
			ModuleCallAction focus = (ModuleCallAction) subject;
			if (focus.getTarget().isAnonymous()) {
				// Validate anonymous modules here.
				ModuleValidator moduleValidator = new ModuleValidator(
						this.parent, this.boundVar);
				moduleValidator.validate(focus.getTarget(), this.mod2g);
				moduleValidator.reportToSuperior(this);
			}
		}

		Set<Var> freeVar = subject.getFreeVar();
		if (freeVar.isEmpty()) {
			return;
		}

		// split the free variables in the action in named and anonymous
		// variables.
		Set<Var> anonymousVars = new LinkedHashSet<>();
		Set<Var> namedVars = new LinkedHashSet<>();
		for (Var v : freeVar) {
			if (v.isAnonymous()) {
				anonymousVars.add(v);
			} else {
				namedVars.add(v);
			}
		}

		// unbound named variables are not allowed
		if (!boundVar.containsAll(namedVars)) {
			report(new ValidatorError(GOALError.ACTION_UNBOUND_VARIABLE,
					subject, subject.getName(), namedVars.toString()));
		}

		// Check that the action parameters do not have occurrences of anonymous
		// variables, with the EXCEPTION of interrogative send actions.
		boolean warned = false;
		if (subject instanceof SendAction) {
			SendAction act = (SendAction) subject;
			if (act.getMessage().getMood() == SentenceMood.INTERROGATIVE) {
				// only disallow anonymous variables in the selector.
				anonymousVars.retainAll(act.getSelector().getFreeVar());
				if (!anonymousVars.isEmpty()) {
					report(new ValidatorError(
							GOALError.SEND_ANONYMOUS_VARIABLE, subject));
					warned = true;
				}
			}
		}
		if (!warned && !anonymousVars.isEmpty()) {
			report(new ValidatorError(GOALError.ACTION_ANONYMOUS_VARIABLE,
					subject, subject.getName()));
		}
	}
}
