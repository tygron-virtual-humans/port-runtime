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
package goal.core.runtime.service.agent;

import goal.core.program.Module;
import goal.core.program.actions.Action;
import goal.core.program.actions.ExitModuleAction;
import goal.core.program.actions.LogAction;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.rules.Rule;
import goal.core.program.rules.RuleSet;

import java.util.LinkedList;
import java.util.List;

/**
 * Container for the results obtained by executing actions that is passed on to
 * {@link Module}s, {@link RuleSet}s, and {@link Rule}s.
 *
 * See, e.g.: {@link Module#run(goal.core.agent.Agent)} and
 * {@link Rule#run(goal.core.agent.AgentInt)}.
 *
 * @author W.Pasman #2540
 * @modified K.Hindriks
 */
public class Result {

	/**
	 * Flag that keeps track of whether last call to {@link #Result(Action)}
	 * reported that action was performed, i.e., Action was not {@code null}.
	 */
	private boolean justPerformedAction = false;
	/**
	 * Indicates whether an exit module action has been performed.
	 */
	private boolean moduleIsTerminated = false;
	/**
	 * The list of actions that have been performed.
	 */
	private final List<Action> executedActions = new LinkedList<>();

	/**
	 * Creates default result.
	 */
	public Result() {
	}

	/**
	 * Add action to the result so far. An action counts as a 'real' action and
	 * is added to the list of executed actions if it is different from a
	 * FocusAction, ExitModule, and LogAction. The first two only control the
	 * program flow and the last one only is used for logging information to
	 * file.
	 *
	 * @param action
	 *            The result of executing an action that is to be added to this
	 *            result, or, {@code null} if no action was performed.
	 */
	public Result(Action action) {
		justPerformedAction = (action != null);
		if (justPerformedAction) {
			if (!(action instanceof ExitModuleAction)
					&& !(action instanceof ModuleCallAction)
					&& !(action instanceof LogAction)) {
				this.executedActions.add(action);
			}
			if (action instanceof ExitModuleAction) {
				this.moduleIsTerminated = true;
			}
		}
	}

	/**
	 * Returns the list of actions that were executed.
	 *
	 * @return List of actions that were executed.
	 */
	public List<Action> getActions() {
		return executedActions;
	}

	/**
	 * Reports whether actions other than {@link ExitModuleAction},
	 * {@link ModuleCallAction}, and {@link LogAction} have been performed. The
	 * latter are filtered out when adding actions to the list of exeucted
	 * actions in {@link #add(Action)}.
	 *
	 * @return {@code true} if a 'real' action has been performed; {@code false}
	 *         otherwise.
	 */
	public boolean hasPerformedAction() {
		return !this.executedActions.isEmpty();
	}

	/**
	 * Checks if any action has just been performed, i.e., last call to
	 * {@link #add(Action)} reported that action was performed and Action was
	 * not {@code null}.
	 * <p>
	 * Note that {@link ExitModuleAction}, {@link ModuleCallAction}, and
	 * {@link LogAction} ARE counted here as actions as well contrary to method
	 * {@link #hasPerformedAction()}.
	 * </p>
	 *
	 * @return {@code true} if action has been performed; {@code false}
	 *         otherwise.
	 */
	public boolean justPerformedAction() {
		return this.justPerformedAction;
	}

	/**
	 * Set flag whether the current module should be terminated.
	 *
	 * @param terminated
	 *            The value for the module terminated flag.
	 */
	public void setModuleTerminated(boolean terminated) {
		moduleIsTerminated = terminated;
	}

	/**
	 * check if module was terminated
	 *
	 * @return true if module was terminated.
	 */
	public boolean isModuleTerminated() {
		return moduleIsTerminated;
	}

	/**
	 * Check if module is finished: Some action was performed or module has been
	 * terminated by {@link ExitModuleAction}.
	 *
	 * @return True if the module is finished
	 */
	public boolean isFinished() {
		return !this.executedActions.isEmpty() || isModuleTerminated();
	}

	/**
	 * Merges a new result with this {@link Result}.
	 *
	 * @param result
	 *            The result to be merged with this one.
	 */
	public void merge(Result result) {
		// Combine the lists of actions.
		this.executedActions.addAll(result.getActions());
		// Assume that result to be merged contains latest information.
		this.justPerformedAction = result.justPerformedAction();
		this.moduleIsTerminated |= result.isModuleTerminated();
	}

}
