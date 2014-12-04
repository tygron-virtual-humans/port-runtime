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

package goal.core.program.actions;

import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.BeliefBase;
import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import krTools.language.Update;
import languageTools.program.agent.actions.InsertAction;

/**
 * Inserts an {@link Update} into the agent's {@link BeliefBase} and/or mail
 * box. As these are two different databases that the agent maintains, two
 * updates are associated with the insert action. One update for the agent's
 * belief base and one for the agent's mail box.
 * <p>
 * If the action is closed, the insert action can be performed.
 * </p>
 * <p>
 * Percepts of the form {@code percept(...)} cannot be inserted into the percept
 * base by an insert action. Percepts are automatically retrieved from the
 * environment and inserted into the agent's percept base every start of a
 * reasoning cycle.
 * </p>
 *
 * @author K.Hindriks
 * @author W.Pasman
 */
public class InsertActionExecutor extends ActionExecutor {

	private InsertAction action;

	public InsertActionExecutor(InsertAction act) {
		action = act;
	}

	/**
	 * {@inheritDoc}<br>
	 * Executes the {@link InsertAction} by applying the {@link Update}s to the
	 * {@link BeliefBase} and/or mail box. Also may update the {@link GoalBase}
	 * if a goal holds after the update; those goals are removed.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		mentalState.insert(this.beliefUpdate, BASETYPE.BELIEFBASE, debugger);
		mentalState.insert(this.mailboxUpdate, BASETYPE.MAILBOX, debugger);

		// Check if goals have been achieved and, if so, update goal base.
		mentalState.updateGoalState(debugger);

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("insert(");
		if (this.beliefUpdate.isEmpty() && this.mailboxUpdate.isEmpty()) {
			builder.append("true");
		} else if (this.beliefUpdate.isEmpty()) {
			builder.append(this.mailboxUpdate.toString());
		} else if (this.mailboxUpdate.isEmpty()) {
			builder.append(this.beliefUpdate.toString());
		} else {
			builder.append(this.beliefUpdate.toString());
			builder.append(",");
			builder.append(this.mailboxUpdate.toString());
		}
		builder.append(")");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((beliefUpdate == null) ? 0 : beliefUpdate.hashCode());
		result = prime * result
				+ ((mailboxUpdate == null) ? 0 : mailboxUpdate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InsertAction other = (InsertAction) obj;
		if (beliefUpdate == null) {
			if (other.beliefUpdate != null) {
				return false;
			}
		} else if (!beliefUpdate.equals(other.beliefUpdate)) {
			return false;
		}
		if (mailboxUpdate == null) {
			if (other.mailboxUpdate != null) {
				return false;
			}
		} else if (!mailboxUpdate.equals(other.mailboxUpdate)) {
			return false;
		}
		return true;
	}

}
