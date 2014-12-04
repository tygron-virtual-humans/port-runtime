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

import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import krTools.language.Update;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.DropAction;

/**
 * 
 * @author W.Pasman 4dec14
 */
public class DropActionExecutor extends ActionExecutor {

	private DropAction action;

	public DropActionExecutor(DropAction act) {
		action = act;
	}

	/**
	 * Executes the {@link DropActionExecutor} by dropping all goals that follow
	 * from the {@link Update} goal to be dropped from the {@link GoalBase}.
	 *
	 * TODO: only goals of agent itself can be removed but not those of other //
	 * agents? Selector is not used??
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		mentalState.drop(action.getUpdate(), debugger);

		// Report action was performed.
		report(debugger);

		return new Result(action);
	}

	@Override
	public String toString() {
		return "drop(" + action + ")";
	}

	@Override
	public Action getAction() {
		return action;
	}

}
