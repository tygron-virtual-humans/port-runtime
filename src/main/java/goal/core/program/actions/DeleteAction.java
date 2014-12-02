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

import goal.core.kr.KRlanguage;
import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Update;
import goal.core.kr.language.Var;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.BeliefBase;
import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.program.Selector;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class DeleteAction extends MentalAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -7398548693373521578L;
	/**
	 * The {@link Update} to be deleted from the {@link BeliefBase}.
	 */
	private final Update beliefUpdate;
	/**
	 * The {@link Update} to be deleted from the mail box.
	 */
	private final Update mailboxUpdate;

	/**
	 * Creates a {@link DeleteAction} that removes a belief from the
	 * {@link BeliefBase} of an agent; also may remove content from the mail
	 * box.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param beliefUpdate
	 *            The {@link Update} to be removed from to the
	 *            {@link BeliefBase}.
	 * @param mailboxUpdate
	 *            The {@link Update} to be removed from the mail box.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public DeleteAction(Selector selector, Update beliefUpdate,
			Update mailboxUpdate, InputStreamPosition source) {
		super("delete", selector, source);
		this.beliefUpdate = beliefUpdate;
		this.mailboxUpdate = mailboxUpdate;
	}

	/**
	 * Returns the {@link Update} that is to be deleted by this
	 * {@link DeleteAction} from the {@link BeliefBase}.
	 *
	 * @return The update that is to be removed from the belief base.
	 */
	public Update getBelief() {
		return this.beliefUpdate;
	}

	/**
	 * Returns the (free) variables that occur in the selector of this action
	 * and the {@link Update} that is to be removed from the {@link BeliefBase}
	 * by this {@link DeleteAction} as well as the variables that occur in the
	 * update that is to be performed on the mail box.
	 *
	 * @return The (free) variables that occur in the selector of this action
	 *         and the updates to the belief base and mail box.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = this.getSelector().getFreeVar();
		freeVars.addAll(this.beliefUpdate.getFreeVar());
		freeVars.addAll(this.mailboxUpdate.getFreeVar());
		return freeVars;
	}

	/**
	 * Checks whether the {@link Update} to the {@link BeliefBase} and the
	 * {@link Selector} is closed, i.e., both do not have any occurrences of
	 * (free) variables.
	 * <p>
	 * Note that we consider the {@link DeleteAction} to be closed even though
	 * the update to the mail box may contain free variables! We need to allow
	 * for free variables in the mail box update in order to be able to remove
	 * interrogatives, i.e., questions, from the mailbox. As a consequence, the
	 * {@link #isClosed()} method cannot simply be defined in terms of the
	 * {@link #getFreeVar()} method.
	 * </p>
	 *
	 * @return {@code true} if the belief update and selector are closed;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean isClosed() {
		return this.beliefUpdate.isClosed() && this.getSelector().isClosed();
	}

	/**
	 * Applies the given {@link Substitution} to the {@link Update}s associated
	 * with this {@link InsertAction} and to the {@link Selector} of this
	 * {@link DeleteAction}.
	 *
	 * @return A new instantiated delete action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	@Override
	public DeleteAction applySubst(Substitution pSubst) {
		return new DeleteAction(this.getSelector().applySubst(pSubst),
				this.beliefUpdate.applySubst(pSubst),
				this.mailboxUpdate.applySubst(pSubst), this.getSource());
	}

	/**
	 * The precondition of a {@link DeleteAction} is {@code true}, or, to be
	 * more precise "bel(true)"; this means it can always be performed if the
	 * {@link Update} to the {@link BeliefBase} is closed.
	 *
	 * @param language
	 *            The KR language used for representing the precondition.
	 * @return The {@link MentalStateCond} "bel(true)".
	 * @throws GOALParseException
	 *             In case the KR parser had a problem parsing "true".
	 */
	@Override
	public MentalStateCond getPrecondition(KRlanguage language)
			throws GOALParseException {
		Query query;
		query = language.parseUpdate("true").toQuery();
		List<MentalFormula> formulaList = new ArrayList<>(1);
		formulaList.add(new BelLiteral(true, query, this.getSelector(),
				getSource()));
		return new MentalStateCond(formulaList, getSource());
	}

	/**
	 * Checks whether the precondition of {@link DeleteAction} holds. See also:
	 * {@link DeleteAction#getPrecondition(KRlanguage)}.
	 *
	 * @return {@code true} since delete has "empty" precondition.
	 */
	@Override
	public DeleteAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
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

		mentalState.delete(this.beliefUpdate, BASETYPE.BELIEFBASE, debugger);
		mentalState.delete(this.mailboxUpdate, BASETYPE.MAILBOX, debugger);

		// Check if goals have been achieved and, if so, update goal base.
		mentalState.updateGoalState(debugger);

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	/**
	 * Returns a {@link String} representation of this {@link DeleteAction}.
	 *
	 * @return A string representation of this action.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getSelector());
		builder.append("delete(");
		if (!(this.beliefUpdate.isEmpty() && this.mailboxUpdate.isEmpty())) {
			// print nothing when both updates are empty
			if (this.beliefUpdate.isEmpty()) {
				builder.append(this.mailboxUpdate.toString());
			} else if (this.mailboxUpdate.isEmpty()) {
				builder.append(this.beliefUpdate.toString());
			} else {
				builder.append(this.beliefUpdate.toString());
				builder.append(",");
				builder.append(this.mailboxUpdate.toString());
			}
		}
		builder.append(")");
		return builder.toString();
	}

	/**
	 * Returns a hash code for this {@link DeleteAction}.
	 *
	 * @return A hash code for the delete action.
	 */
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

	/**
	 * Checks whether this {@link DeleteAction} is equal to another.
	 *
	 * @return {@code true} if the action is equal to the other; {@code false}
	 *         otherwise.
	 */
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
		DeleteAction other = (DeleteAction) obj;
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
