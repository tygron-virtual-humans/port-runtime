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

import krTools.language.Substitution;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.InsertAction;
import mentalState.BASETYPE;
import mentalstatefactory.MentalStateFactory;

public class InsertActionExecutor extends ActionExecutor {

	private final InsertAction action;

	public InsertActionExecutor(InsertAction act) {
		this.action = act;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		try {
			mentalState.MentalState state = MentalStateFactory
					.getInterface(this.action.getKRInterface().getClass());
			MentalState mentalState = runState.getMentalState();
			mentalState.insert(
					state.filterMailUpdates(this.action.getUpdate(), false),
					BASETYPE.BELIEFBASE, debugger);
			mentalState.insert(
					state.filterMailUpdates(this.action.getUpdate(), true),
					BASETYPE.MAILBOX, debugger);
			mentalState.updateGoalState(debugger);
		} catch (UnknownObjectException e) {
			throw new GOALRuntimeErrorException(
					"Separating beliefs from mails for insertion failed: "
							+ e.getMessage(), e);
		}

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
