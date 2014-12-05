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
import krTools.language.Substitution;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.PrintAction;

public class PrintActionExecutor extends ActionExecutor {

	private PrintAction action;

	public PrintActionExecutor(PrintAction action) {
		this.action = action;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		String output = action.getParameters().toString();
		boolean beginQuote = output.startsWith("\"") || output.startsWith("'");
		boolean endQuote = output.endsWith("\"") || output.endsWith("'");
		System.out.println(output.substring(beginQuote ? 1 : 0,
				endQuote ? output.length() - 1 : output.length()));

		report(debugger);

		return new Result(action);
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		return new PrintActionExecutor(action.applySubst(subst));
	}
	
	@Override
	public Action<?> getAction() {
		return action;
	}
}
