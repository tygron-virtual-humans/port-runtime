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

import goal.core.kr.language.Var;
import goal.core.program.ActionSpecification;
import goal.core.program.Module;
import goal.core.program.NameSpace;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.actions.UserSpecOrModuleCall;
import goal.core.program.validation.Validator;
import goal.core.program.validation.ValidatorError;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link Validator} for {@link ActionCombo}s.<br>
 *
 * Main function is to resolve any {@link UserSpecOrModuleCall}s. Validates
 * action combo by validating individual actions.
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class ActionComboValidator extends Validator<ActionCombo> {
	/**
	 * The set of global variables that are bound in the action combo that is
	 * validated. This set should be equal to the variables that occur in
	 * parameters of a (closest non-anonymous) parent module combined with the
	 * variables in the condition of the action combo's rule.
	 */
	private final Set<Var> boundVar;
	/**
	 * The name space of the parent of the {@link ActionCombo} that is
	 * validated.
	 */
	private final NameSpace parent;

	/**
	 * Validator for action combo's.
	 *
	 * @param parent
	 *            The namespace this action combo is in
	 * @param boundVar
	 *            Variables that have been bound in the action combo's scope.
	 */
	public ActionComboValidator(NameSpace parent, Set<Var> boundVar) {
		this.parent = parent;
		this.boundVar = new LinkedHashSet<>(boundVar);
	}

	/**
	 * Validates an action combo by validating each individual action and
	 * resolving any {@link UserSpecOrModuleCall}s.
	 *
	 * @param subject
	 *            The {@link ActionCombo} to be validated.
	 */
	@Override
	protected void doValidate(ActionCombo subject) {
		Map<String, List<ActionSpecification>> actionSpecs = parent
				.getActionSpecifications().getItems();
		Map<String, List<Module>> modules = parent.getModules().getItems();

		// Resolve reference to either user-specified or focus action, if
		// needed.
		for (int i = 0; i < subject.getActions().size(); i++) {
			Action action = subject.getAction(i);
			// Action is a user specified action or a module call.
			if (action instanceof UserSpecOrModuleCall) {
				if (this.mod2g) {
					continue;
				}

				UserSpecOrModuleCall dummy = (UserSpecOrModuleCall) action;

				// Get action signature.
				String actionSignature = action.getName().concat("/")
						.concat(String.valueOf(dummy.getParameters().size()));
				// Check whether key matches with action specification XOR
				// module, and, if so, mark item as used.
				// Check whether key matches with action specification but not
				// with module.
				if (actionSpecs.containsKey(actionSignature)
						&& !modules.containsKey(actionSignature)) {
					UserSpecAction userSpecAction = new UserSpecAction(
							dummy.getName(), dummy.getParameters(), true,
							dummy.getSource());
					// Boolean 'specified' keeps track of whether action
					// actually matches with an action specification.
					// Although key matches (and name and number of parameters
					// therefore match with at least one specification) it
					// may be the case that the action parameters of the
					// specification introduce additional constraints and actual
					// and specified parameters cannot be unified.
					// TODO: given current grammar constraints (see GOAL.g) all
					// available action specifications should match... See TRAC
					// #1528.
					boolean specified = false;
					for (ActionSpecification actionSpec : actionSpecs
							.get(actionSignature)) {
						boolean used = false;
						try {
							used = userSpecAction.addSpecification(actionSpec);
						} catch (KRInitFailedException e) {
							new Warning(
									Resources
											.get(WarningStrings.FAILED_VALIDATE_USERSPEC),
									e);
						}
						specified = specified || used;
						// Mark action specification as used.
						if (used) {
							actionSpec.markUsed();
						}
					}
					if (specified) {
						action = userSpecAction;
					} else {
						report(new ValidatorError(
								GOALError.ACTION_USED_NEVER_DEFINED, action,
								action.getName()));
					}
					// Check whether key matches with module but not action
					// specification.
				} else if (!actionSpecs.containsKey(actionSignature)
						&& modules.containsKey(actionSignature)) {
					// Check whether key matches with exactly one module.
					if (modules.get(actionSignature).size() == 1) {
						// Check that module can be called (and, e.g., is not
						// equal to one of the special built-in modules).
						// create a focus action
						action = new ModuleCallAction(modules.get(
								actionSignature).get(0), dummy.getRule(),
								dummy.getParameters(), dummy.getSource());
						// Mark module as used.
						modules.get(actionSignature).get(0).markUsed();
					} // else module reference cannot be resolved but this error
						// has already been reported by {@link
						// NameSpaceValidator}.
						// Key does not match with either action specification
						// or
						// module.
				} else if (!actionSpecs.containsKey(actionSignature)
						&& !modules.containsKey(actionSignature)) {
					report(new ValidatorError(
							GOALError.ACTION_USED_NEVER_DEFINED, action,
							actionSignature));
					// Key matches with an action specification and a module;
					// reference cannot be resolved.
				} else {
					report(new ValidatorError(GOALError.ACTION_NAME_CLASH,
							action, actionSignature));
					// Use trick by marking action specifications and modules as
					// used to suppress 'never used' warnings.
					for (ActionSpecification actionSpec : actionSpecs
							.get(actionSignature)) {
						actionSpec.markUsed();
					}
					for (Module module : modules.get(actionSignature)) {
						module.markUsed();
					}
				}
				/**
				 * Replace {@link UserOrFocusAction} in {@link ActionCombo}.
				 * Either action variable has been updated to a
				 * {@link UserSpecAction} or a {@link FocusAction}, or errors
				 * occurred and it is still a {@link UserOrFocusAction}.
				 */
				subject.setAction(i, action);
			}
			// Validate action.
			// Check whether UserOrFocusActions have been resolved; no use in
			// validating if a UserOrFocusAction could not be resolved.
			// Note: no else-clause because action could have been changed.
			if (!(action instanceof UserSpecOrModuleCall)) {
				ActionValidator actionValidator = new ActionValidator(
						this.parent, boundVar);
				actionValidator.validate(action, this.mod2g);
				actionValidator.reportToSuperior(this);
			}
		}
	}
}
