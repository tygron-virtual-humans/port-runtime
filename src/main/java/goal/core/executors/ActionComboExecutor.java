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

package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ActionCombo;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.actions.UserSpecAction;

public class ActionComboExecutor {

	private ActionCombo actions;

	public ActionComboExecutor(ActionCombo act) {
		this.actions = act;
	}

	/**
	 * Searches linearly through the precondition(s) of the first action in this
	 * action combo until it finds one that holds. ALL instances of the combo
	 * for which that precondition holds are returned.
	 * <p>
	 * Note that preconditions of second, third, etc. actions in the combo are
	 * NOT evaluated. An action combo can be performed if (one of) the
	 * precondition(s) of the first action holds; if preconditions of other
	 * actions in the combo fail when they are evaluated, execution of the acion
	 * combo is simply terminated.
	 * </p>
	 *
	 * @param mentalState
	 *            The run state in which the precondition(s) is (are) evaluated.
	 * @param debugger
	 *            DOC
	 * @return A list of all available options for this action, i.e., instances
	 *         for which a precondition (the first found) of the first action
	 *         holds.
	 */
	public List<ActionCombo> getOptions(MentalState mentalState,
			Debugger debugger) {
		List<ActionCombo> options = new LinkedList<>();
		Set<Substitution> solutions;

		// Get the first action from the list of actions of this combo.
		Action<?> firstaction = this.actions.getActions().get(0);

		// TWO CASES: Distinguish user-specified actions from all other actions.
		if (firstaction instanceof UserSpecAction) {
			// USER-SPECIFIED ACTION MAY HAVE MULTIPLE ACTION SPECS
			// (PRECONDITIONS).
			UserSpecActionExecutor userspec = new UserSpecActionExecutor((UserSpecAction) firstaction);

			// Find the first action specification whose precondition holds.
			solutions = userspec.getOptions(mentalState, debugger);

			// If solutions were found, return list of instantiated action
			// combos where
			// all action specifications other than the one found have been
			// removed.
			if (!solutions.isEmpty()) {
				// Create new action which only has the action specification
				// found
				// by calling #getOptions(runState).
				UserSpecAction singleActionSpec = userspec.getSelectedActionSpec();
				// Create action combo using new user specified action.
				ActionCombo option = new ActionCombo();
				option.addAction(singleActionSpec);
				// Add other actions that follow first action.
				for (int i = 1; i < this.actions.size(); i++) {
					option.addAction(this.actions.getActions().get(i));
				}
				// Create instantiations and add to options.
				for (Substitution substitution : solutions) {
					// Check if first action is closed.
					if (singleActionSpec.applySubst(substitution).isClosed()) {
						options.add(option.applySubst(substitution));
					} else {
						throw new GOALActionFailedException(
								"Attempt to execute "
										+ option.applySubst(substitution)
										+ " with free variables.");
					}
				}
			}
		} else {
			// ALL ACTIONS OTHER THAN USER-SPECIFIED ACTIONS.
			// These actions have a single precondition, and, if the
			// precondition
			// holds and the action is closed, the action is an option (and so
			// is
			// the action combo).

			// Evaluate the precondition of first action in combo.
			if (ActionExecutor.getActionExecutor(firstaction).
					evaluatePrecondition(mentalState, debugger, false) != null) {
				// If action is not closed throw exception.
				if (firstaction.isClosed()) {
					// Action is an option, add the combo as option.
					options.add(actions);
				} else {
					throw new GOALActionFailedException("Attempt to execute "
							+ actions + " with free variables.");
				}
			}
		}

		return options;
	}

	/**
	 * Performs the {@link ActionCombo}.
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 * @param substitution
	 *            The substitution provided by the action's context.
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return The {@link Result} of this action.
	 */
	public Result run(RunState<?> runState, Substitution substitution,
			boolean last) {
		Result comboResult = new Result();

		for (Action<?> action : actions) {
			// FIXME is this ok if action is a ModuleCallAction??
			Result result = ActionExecutor.getActionExecutor(action)
					.run(runState, substitution,runState.getDebugger(), last);
			comboResult.merge(result);
			// If module needs to be terminated, i.e., {@link ExitModuleAction}
			// has been performed, then exit execution of combo action.
			// Also, stop executing combo if last action failed, but not if
			// action was {@link FocusAction}, i.e., entering of a module.
			if ((!comboResult.justPerformedAction() && !(action instanceof ModuleCallAction))
					|| comboResult.isModuleTerminated()) {
				break;
			}
		}

		runState.getDebugger().breakpoint(Channel.ACTIONCOMBO_FINISHED, this,
				"Performed %s.", comboResult.getActions());

		return comboResult;
	}
}
