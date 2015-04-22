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
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import krTools.language.Substitution;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.DropAction;

public class DropActionExecutor extends ActionExecutor {
	private final DropAction action;

	public DropActionExecutor(DropAction act) {
		this.action = act;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
		MentalState mentalState = runState.getMentalState();

		try {
			mentalState.drop(this.action.getUpdate(), debugger);
		} catch (GOALDatabaseException e) {
			throw new GOALActionFailedException("Failed to execute action "+this.action,e);
		}

		report(debugger);

		return new Result(this.action);
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		return new DropActionExecutor(this.action.applySubst(subst));
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}
