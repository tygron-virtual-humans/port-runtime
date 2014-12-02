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

import goal.core.kr.language.Term;
import goal.core.kr.language.Var;
import goal.core.program.ActionSpecification;
import goal.core.program.validation.Validator;
import goal.core.program.validation.ValidatorError;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link Validator} for {@link ActionSpecification}s.<br>
 *
 * Checks for the following error:
 * <ul>
 * <li>postcondition contains variables that are bound neither by the
 * precondition nor by the action's parameters.</li>
 * </ul>
 *
 * author K.Hindriks
 *
 */
public class ActionSpecificationValidator extends
		Validator<ActionSpecification> {

	/**
	 * The list of variables that are bound in the module enclosing the spec.
	 */
	private final Set<Var> boundVars;

	/**
	 * constructor
	 *
	 * @param boundVars
	 *            the list of variables that are bound in the module enclosing
	 *            the spec.
	 */
	public ActionSpecificationValidator(Set<Var> boundVars) {
		this.boundVars = boundVars;
	}

	/**
	 * Validates an action specification.
	 *
	 * @param subject
	 *            The {@link ActionSpecification} to be validated.
	 */
	@Override
	protected void doValidate(ActionSpecification subject) {
		// Check whether all variables in postcondition are bound.
		// Free variables in the action's precondition or action parameters bind
		// variables in postcondition.
		Set<Var> boundVar = subject.getPreCondition().getFreeVar();
		boundVar.addAll(boundVars);
		// At the same time check whether all action parameters in the
		// specification are variables that are all different from one another.
		// See TRAC #1528.
		// TODO: remove this constraint?
		int counter = 0;
		for (Term parameter : subject.getAction().getParameters()) {
			boundVar.addAll(parameter.getFreeVar());
			// Check whether parameter is variable.
			if (!parameter.isVar()) {
				report(new ValidatorError(GOALError.ACTION_INVALID_PARAMETER,
						subject, subject.getSignature(), parameter.toString()));
			} else
			// Check whether parameter is unique (variable) by checking that
			// it did not occur already earlier on in parameter list.
			if (subject.getAction().getParameters().subList(0, counter)
					.contains(parameter)) {
				report(new ValidatorError(GOALError.ACTION_DUPLICATE_PARAMETER,
						subject, subject.getSignature(), parameter.toString()));
			}
			// Increase count of number of parameters.
			counter++;
		}

		// TODO: remove next statement, duplicates work??
		boundVar.addAll(subject.getAction().getFreeVar());
		// note, boundVar now can contain also anonymous vars. We have to remove
		// them because these never "bind". #2491
		for (Var v : new HashSet<>(boundVar)) {
			if (v.isAnonymous()) {
				boundVar.remove(v);
			}
		}

		Set<Var> postConditionVars = subject.getPostCondition().getFreeVar();
		if (!boundVar.containsAll(postConditionVars)) {
			// Remove all bound variables.
			postConditionVars.removeAll(boundVar);
			report(new ValidatorError(GOALError.POSTCONDITION_UNBOUND_VARIABLE,
					subject, subject.getSignature(),
					postConditionVars.toString()));
		}
	}
}
