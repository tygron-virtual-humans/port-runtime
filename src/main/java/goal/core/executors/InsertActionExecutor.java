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
import languageTools.program.agent.actions.InsertAction;
import mentalState.BASETYPE;

public class InsertActionExecutor extends ActionExecutor {
	private final InsertAction action;

	public InsertActionExecutor(InsertAction act) {
		this.action = act;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
		mentalState.MentalState state = runState.getMentalState().getState();
		MentalState mentalState = runState.getMentalState();
		try {
			mentalState.insert(
					state.filterMailUpdates(this.action.getUpdate(), false),
					BASETYPE.BELIEFBASE, debugger);
			mentalState.insert(
					state.filterMailUpdates(this.action.getUpdate(), true),
					BASETYPE.MAILBOX, debugger);
		} catch (GOALDatabaseException e) {
			throw new GOALActionFailedException("Failed to execute action "+this.action,e);
		}
		mentalState.updateGoalState(debugger);

		report(debugger);

		return new Result(this.action);
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		return new InsertActionExecutor(this.action.applySubst(subst));
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}
