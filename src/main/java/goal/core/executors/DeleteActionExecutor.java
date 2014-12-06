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
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.rmi.activation.UnknownObjectException;

import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.DeleteAction;
import mentalState.BASETYPE;
import mentalstatefactory.MentalStateFactory;

public class DeleteActionExecutor extends ActionExecutor {

	private final DeleteAction action;

	public DeleteActionExecutor(DeleteAction act) {
		this.action = act;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		try {
			mentalState.MentalState state = MentalStateFactory
					.getInterface(this.action.getKRInterface().getClass());
			MentalState mentalState = runState.getMentalState();
			mentalState.delete(
					state.filterMailUpdates(this.action.getUpdate(), false),
					BASETYPE.BELIEFBASE, debugger);
			mentalState.delete(
					state.filterMailUpdates(this.action.getUpdate(), true),
					BASETYPE.MAILBOX, debugger);
			mentalState.updateGoalState(debugger);
		} catch (UnknownObjectException e) {
			throw new GOALRuntimeErrorException(
					"Separating beliefs from mails for deletion failed: "
							+ e.getMessage(), e);
		}

		// Report action was performed.
		report(debugger);

		return new Result(this.action);
	}

	@Override
	public DeleteActionExecutor applySubst(
			krTools.language.Substitution substitution) {
		return new DeleteActionExecutor(this.action.applySubst(substitution));
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}
