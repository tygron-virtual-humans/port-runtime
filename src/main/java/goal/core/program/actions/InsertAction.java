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
 */
public class InsertAction extends MentalAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -4935525441608695461L;
	/**
	 * The {@link Update} that is to be inserted into the {@link BeliefBase}.
	 */
	private final Update beliefUpdate;
	/**
	 * The {@link Update} to be inserted into the mail box.
	 */
	private final Update mailboxUpdate;

	/**
	 * Creates an {@link InsertAction} that inserts an {@link Update} into the
	 * {@link BeliefBase} and/or mail box of an agent.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param beliefUpdate
	 *            The {@link Update} to be inserted into to the
	 *            {@link BeliefBase}.
	 * @param mailboxUpdate
	 *            The {@link Update} to be removed from the mail box.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public InsertAction(Selector selector, Update beliefUpdate,
			Update mailboxUpdate, InputStreamPosition source) {
		super("insert", selector, source);

		this.beliefUpdate = beliefUpdate;
		this.mailboxUpdate = mailboxUpdate;
	}

	/**
	 * Returns the {@link Update} that is to be inserted by this
	 * {@link InsertAction} into the {@link BeliefBase}.
	 *
	 * @return The update that is to be inserted into the belief base.
	 */
	public Update getBelief() {
		return this.beliefUpdate;
	}

	/**
	 * Returns the (free) variables that occur in the {@link Update} to be
	 * inserted into the {@link BeliefBase} by this {@link InsertAction} as well
	 * as the variables that occur in the update that is to be performed on the
	 * mail box.
	 *
	 * @return The (free) variables that occur in the updates to the belief base
	 *         and mail box.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = this.getSelector().getFreeVar();
		freeVars.addAll(this.beliefUpdate.getFreeVar());
		freeVars.addAll(this.mailboxUpdate.getFreeVar());
		return freeVars;
	}

	/**
	 * Applies the given {@link Substitution} to the {@link Update}s associated
	 * with this {@link InsertAction} and to the {@link Selector} of this
	 * {@link InsertAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return A new instantiated insert action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	@Override
	public InsertAction applySubst(Substitution substitution) {
		return new InsertAction(this.getSelector().applySubst(substitution),
				this.beliefUpdate.applySubst(substitution),
				this.mailboxUpdate.applySubst(substitution), this.getSource());
	}

	/**
	 * The precondition of an {@link InsertAction} is {@code true}, or, to be
	 * more precise "bel(true)"; this means it can always be performed if the
	 * action is closed.
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
	 * Checks whether the precondition of {@link InsertAction} holds. See also:
	 * {@link InsertAction#getPrecondition(KRlanguage)}.
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the precondition is
	 *            evaluated.
	 *
	 * @return This action since insert has "empty" precondition.
	 */
	@Override
	public InsertAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
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
