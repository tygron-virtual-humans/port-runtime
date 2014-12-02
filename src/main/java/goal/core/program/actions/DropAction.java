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
 * Drops all goals entailed by the goal to be dropped from the {@link GoalBase}.
 * <p>
 * If the action is closed, the drop action can be performed.
 * </p>
 *
 * @author K.Hindriks
 */
public class DropAction extends MentalAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 4982749501892230629L;
	/**
	 * The goal to be dropped.
	 */
	private final Update goal;

	/**
	 * Creates a {@link DropAction} that drops all goals from the
	 * {@link GoalBase} that follow from the goal to be dropped.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param goal
	 *            The goal, i.e., {@link Update}, to be dropped.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public DropAction(Selector selector, Update goal, InputStreamPosition source) {
		super("drop", selector, source);
		this.goal = goal;
	}

	/**
	 * Returns the goal, represented by an {@link Update}, that is to be
	 * dropped.
	 *
	 * @return The goal to be dropped.
	 */
	public Update getGoal() {
		return this.goal;
	}

	/**
	 * Returns the (free) variables that occur in the selector and the goal,
	 * i.e., {@link Update}, to be dropped by this {@link DropAction}.
	 *
	 * @return The (free) variables that occur in the selector of this action
	 *         and the goal to be dropped.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = this.getSelector().getFreeVar();
		freeVars.addAll(this.goal.getFreeVar());
		return freeVars;
	}

	/**
	 * Applies the given {@link Substitution} to this {@link DropAction} by
	 * applying the substitution to the goal to be dropped and to the
	 * {@link Selector} of this {@link DropAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The instantiated drop action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	@Override
	public DropAction applySubst(Substitution substitution) {
		return new DropAction(this.getSelector().applySubst(substitution),
				this.goal.applySubst(substitution), this.getSource());
	}

	/**
	 * The precondition of an {@link DropAction} is {@code true}, or, to be more
	 * precise "bel(true)"; this means it can always be performed if the action
	 * is closed.
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
	 * Checks whether the precondition of {@link DropAction} holds. See also:
	 * {@link DropAction#getPrecondition(KRlanguage)}.
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the precondition is
	 *            evaluated.
	 * @param debugger
	 *            The current debugger
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return {@code true} since insert has "empty" precondition.
	 */
	@Override
	public DropAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
	}

	/**
	 * Executes the {@link DropAction} by dropping all goals that follow from
	 * the {@link Update} goal to be dropped from the {@link GoalBase}.
	 *
	 * TODO: only goals of agent itself can be removed but not those of other //
	 * agents? Selector is not used??
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		mentalState.drop(this.getGoal(), debugger);

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		return "drop(" + this.goal.toString() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((goal == null) ? 0 : goal.hashCode());
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
		DropAction other = (DropAction) obj;
		if (goal == null) {
			if (other.goal != null) {
				return false;
			}
		} else if (!goal.equals(other.goal)) {
			return false;
		}
		return true;
	}

}
