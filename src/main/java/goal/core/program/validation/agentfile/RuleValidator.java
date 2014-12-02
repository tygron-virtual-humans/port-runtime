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

import goal.core.kr.KRFactory;
import goal.core.kr.language.Var;
import goal.core.program.NameSpace;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.ExitModuleAction;
import goal.core.program.literals.AGoalLiteral;
import goal.core.program.literals.GoalALiteral;
import goal.core.program.literals.Macro;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalLiteral;
import goal.core.program.rules.ListallDoRule;
import goal.core.program.rules.Rule;
import goal.core.program.validation.Validator;
import goal.core.program.validation.ValidatorError;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link Validator} for a {@link Rule}.<br>
 *
 * Checks for the following errors:
 * <ul>
 * <li>The occurrence of actions without specifications in the body of the rule.
 * </li>
 * <li>The occurrence of a nested action combo, null action, or focus action in
 * the body of the rule.</li>
 * <li>The occurrence of unbound variables in (postconditions of) actions in the
 * body of the rule. This includes anonymous variables.</li>
 * <li>The occurrence of anonymous variables in a {@link ListallDoRule}.</li>
 * <li>The use of undefined macros, or related isues.</li>
 * </ul>
 * Checks for the following warning:
 * <ul>
 * <li>The occurrence of more than one exit-module action in the body of the
 * rule.</li>
 * </ul>
 *
 * @author N.Kraayenbrink
 * @author K.Hindriks
 *
 */
public class RuleValidator extends Validator<Rule> {

	/**
	 * The set of global variables that are bound in the rule set that is
	 * validated. This set should be equal to the variables that occur in
	 * parameters of the (closest non-anonymous) parent module.
	 */
	private final Set<Var> boundVar;
	/**
	 * The name space of the parent of the {@link Rule} that is validated.
	 */
	private final NameSpace parent;

	/**
	 * Validates a rule.
	 *
	 * @param parent
	 *            The scope of this rule.
	 * @param boundVar
	 *            Variables that have been bound in the rule's scope (must be
	 *            module parameters).
	 */
	public RuleValidator(NameSpace parent, Set<Var> boundVar) {
		this.parent = parent;
		this.boundVar = new LinkedHashSet<>(boundVar);
	}

	/**
	 * Checks if a {@link Rule} contains only actions with variables that are
	 * bound in its condition. The error messages also contain the names of the
	 * free variables. Also resolves references to macro's in the rule's
	 * condition.
	 *
	 * @param subject
	 *            The rule to validate.
	 */
	@Override
	protected void doValidate(Rule subject) {
		// Validate the mental state condition of the rule.
		// Check whether macros used in the rule's condition are defined. If so,
		// mark macro as used.
		Map<String, List<Macro>> macros = this.parent.getMacros().getItems();
		for (MentalFormula formula : subject.getCondition().getSubFormulas()) {
			if (formula instanceof Macro && !this.mod2g) {
				Macro macro = (Macro) formula;
				if (macros.containsKey(macro.getSignature())) {
					if (macros.get(macro.getSignature()).size() == 1) {
						// Resolve reference and add macro definition.
						try {
							macro.setDefinition(macros
									.get(macro.getSignature()).get(0));
							macros.get(macro.getSignature()).get(0).markUsed();
						} catch (GOALBug ex) {
							new Warning(ex.getMessage(), ex);
						}
					} else {
						report(new ValidatorError(
								GOALError.MACRO_DUPLICATE_NAME, macro,
								macro.getSignature()));
					}
				} else {
					report(new ValidatorError(
							GOALError.MACRO_USED_NEVER_DEFINED, macro,
							macro.getSignature()));
				}
			}
		}
		// Check whether a-goal and goal-a literals do not use anonymous
		// variables. Prolog dependency.
		// See TRAC #174 for explanation. TODO: Magic name.
		for (MentalLiteral literal : subject.getCondition().getLiterals()) {
			try {
				if (literal.getFormula().getLanguage()
						.equals(KRFactory.get("swiprolog"))) {
					if (literal instanceof AGoalLiteral
							|| literal instanceof GoalALiteral) {
						for (Var var : literal.getFormula().getFreeVar()) {
							if (var.toString().startsWith("_")) {
								report(new ValidatorError(
										GOALError.MENTAL_LITERAL_ANONYMOUS_VARIABLE,
										literal, literal.toString(), literal
										.getLiteralTypeString()));
							}
						}
					}
				}
			} catch (KRInitFailedException e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_RULE_VALIDATE),
						this.toString()), e);
			}
		}

		// Validate the action combo of the rule.
		// Set variables that are bound if they occur in rule condition.
		Set<Var> boundByRule = subject.getBoundVar();
		boundVar.addAll(boundByRule);

		ActionComboValidator actionComboValidator = new ActionComboValidator(
				this.parent, boundVar);
		actionComboValidator.validate(subject.getAction(), this.mod2g);
		actionComboValidator.reportToSuperior(this);

		// Check that rule contains at most one exit-module action.
		if (RuleValidator.countOfExitModuleActions(subject) > 1) {
			report(new ValidatorError(GOALError.EXITMODULE_CANNOTREACH,
					subject, subject.toRuleString()));
		}

		// The variable to which results of listall-do rules are assigned cannot
		// be an anonymous variable.
		if (subject instanceof ListallDoRule) {
			Var var = ((ListallDoRule) subject).getVariable();
			if (var.isAnonymous()) {
				report(new ValidatorError(GOALError.LISTALL_ANONYMOUS_VARIABLE,
						var, var.toString()));
			}
		}
	}

	/**
	 * Helper method used by {@link RuleValidator} and {@link ModuleValidator}
	 * for checking that <code>exit-module</code> action is used at most once in
	 * a rule and never in a top-level module.
	 *
	 * @param rule
	 *            The rule to check.
	 * @return The number of occurrences of the <code>exit-module</code> action
	 *         in the {@link ActionCombo} of this {@link Rule}.
	 */
	protected static int countOfExitModuleActions(Rule rule) {
		int count = 0;
		for (Action action : rule.getAction()) {
			if (action instanceof ExitModuleAction) {
				count++;
			}
		}
		return count;
	}

}
