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

import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.BeliefBase;
import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import krTools.language.Update;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.DeleteAction;

/**
 * Deletes an {@link Update} from the {@link BeliefBase} base and/or mail box.
 * As these are two different databases that the agent maintains, two updates
 * are associated with the delete action. One update for the agent's belief base
 * and one for the agent's mail box.
 * <p>
 * If the action is closed, the delete action can be performed, but only the
 * update to the belief base is required to be closed. The update to the agent's
 * mail box may contain variables in order to be able to remove interrogatives
 * (questions) that have been sent or received from the mail box again.
 * </p>
 * <p>
 * Percepts of the form {@code percept(...)} cannot be removed from the percept
 * base by a delete action. Percepts are automatically removed from the agent's
 * percept base every start of a reasoning cycle.
 * </p>
 *
 * @author K.Hindriks
 */
public class DeleteActionExecutor extends ActionExecutor {

	private DeleteAction action;

	public DeleteActionExecutor(DeleteAction act) {
		action = act;
	}

	@Override
	public DeleteActionExecutor applySubst(
			krTools.language.Substitution substitution) {
		return new DeleteActionExecutor(action.applySubst(substitution));
	}

	/**
	 * {@inheritDoc} <br>
	 * Executes the {@link DeleteAction} by applying the {@link Update}s to the
	 * {@link BeliefBase} and/or mail box. Also may update the {@link GoalBase}
	 * if a goal holds after the update; those goals are removed.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		mentalState.delete(action.getUpdate().beliefUpdate,
				BASETYPE.BELIEFBASE, debugger);
		mentalState.delete(action.getUpdate().mailboxUpdate, BASETYPE.MAILBOX,
				debugger);

		// Check if goals have been achieved and, if so, update goal base.
		mentalState.updateGoalState(debugger);

		// Report action was performed.
		report(debugger);

		return new Result(action);
	}

	@Override
	public Action getAction() {
		return action;
	}

}
