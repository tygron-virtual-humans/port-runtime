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

import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ExitModuleAction;

/**
 * Forces an exit from the current (non-anonymous) {@link Module}.
 * <p>
 * Modules create an (implicit) stack of contexts <module1, module2, module3,
 * ..., moduleN> when they are called, where 'moduleN' is the last module that
 * has been entered. Executing the exit-module action means that all anonymous
 * modules higher on the stack than the first non-anonymous module are exited as
 * well as the first non-anonymous module. Execution then continues in the
 * module one level lower in the stack.
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class ExitModuleActionExecutor extends ActionExecutor {

	private ExitModuleAction action;

	public ExitModuleActionExecutor(ExitModuleAction act) {
		action = act;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		// Report action was performed.
		report(debugger);

		return new Result(action);
	}

	@Override
	public Action getAction() {
		return action;
	}

}
