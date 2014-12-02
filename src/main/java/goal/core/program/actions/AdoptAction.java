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
import goal.core.program.SelectExpression.SelectorType;
import goal.core.program.Selector;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.GoalLiteral;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.KRQueryFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Adopts a goal by adding it to the {@link GoalBase}.
 * <p>
 * The preconditions of the adopt action are:
 * <ul>
 * <li>the agent does not yet believe that the goal to be adopted is the case;</li>
 * <li>there is no other goal G' that subsumes the goal G to be adopted. That
 * is, G does not follow from G' (in combination with the agent's knowledge
 * base).</li>
 * </ul>
 * </p>
 *
 * @author K.Hindriks
 */
public class AdoptAction extends MentalAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -3888375609898364927L;
	/**
	 * The goal to be adopted.
	 */
	private final Update goal;

	/**
	 * Creates an {@link AdoptAction} that inserts a new goal into the
	 * {@link GoalBase} of an agent.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param goal
	 *            The goal, i.e. {@link Update}, to be adopted.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public AdoptAction(Selector selector, Update goal,
			InputStreamPosition source) {
		super("adopt", selector, source);

		this.goal = goal;
	}

	/**
	 * Returns the goal, represented by an {@link Update}, that is to be
	 * adopted.
	 *
	 * @return The goal to be adopted.
	 */
	public Update getGoal() {
		return this.goal;
	}

	/**
	 * Returns the (free) variables that occur in the selector and the goal,
	 * i.e., {@link Update}, to be adopted by this {@link AdoptAction}.
	 *
	 * @return The (free) variables that occur in the selector of this action
	 *         and the goal to be adopted.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = this.getSelector().getFreeVar();
		freeVars.addAll(this.goal.getFreeVar());
		return freeVars;
	}

	/**
	 * Applies the given {@link Substitution} to this {@link AdoptAction} by
	 * applying the substitution to the goal to be adopted and to the
	 * {@link Selector} of this {@link AdoptAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The instantiated adopt action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	@Override
	public AdoptAction applySubst(Substitution substitution) {
		AdoptAction adopt = new AdoptAction(this.getSelector().applySubst(
				substitution), this.goal.applySubst(substitution),
				this.getSource());
		return adopt;
	}

	/**
	 * Returns the precondition of this {@link AdoptAction}. The adopt action
	 * can be performed if the goal to be adopted is not believed to be the case
	 * and the goal does not already follow from one of the goals in the goal
	 * base.
	 *
	 * @param language
	 *            The KR language used for representing the precondition.
	 * @return A {@link MentalStateCond} that represents the action's
	 *         precondition.
	 */
	@Override
	public MentalStateCond getPrecondition(KRlanguage language) {
		// Construct the mental state condition that represents the
		// precondition.
		Query query = this.goal.toQuery();
		List<MentalFormula> formulalist = new ArrayList<>(2);
		// Construct the belief part of the query: NOT(BEL(query)).
		formulalist.add(new BelLiteral(false, query, this.getSelector(),
				getSource()));
		// Construct the goal part of the query: NOT(GOAL(query)).
		formulalist.add(new GoalLiteral(false, query, this.getSelector(),
				getSource()));
		// Combine both parts.
		return new MentalStateCond(formulalist, getSource());
	}

	/**
	 * Checks whether the precondition of this {@link AdoptOneAction} holds. See
	 * also: {@link AdoptOneAction#getPrecondition(KRlanguage)}).
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the precondition is
	 *            evaluated.
	 * @param debugger
	 *            The current debugger
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail..
	 * @return {@link AdoptAction} if the precondition holds; {@code null}
	 *         otherwise.
	 */
	@Override
	public AdoptAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		KRlanguage language = mentalState.getKRLanguage();
		Selector selector = this.getSelector();

		try {
			if (!getPrecondition(language).evaluate(mentalState, debugger)
					.isEmpty()) {
				// precondition holds for at least one instance.
				if (selector.isType(SelectorType.SELF)
						|| selector.isType(SelectorType.THIS)) {
					debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION,
							this, "Precondition of action %s holds.", this);
					return this;
				} else {
					throw new UnsupportedOperationException(
							"Only 'SELF' and 'THIS' are allowed right now.");
				}
			}
		} catch (KRQueryFailedException e) {
			throw new KRQueryFailedException("precondition check of " + this
					+ " failed", e);
		}

		debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION, this,
				"Precondition of action %s does not hold.", this);

		return null;
	}

	/**
	 * {@inheritDoc} Executes the {@link AdoptAction} by inserting the goal,
	 * i.e., {@link Update}, to be adopted into the {@link GoalBase}.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		// TODO: handle selector.
		// Set<String> agentNames = this.getSelector().resolve(mentalState);

		boolean topLevel = this.getSelector().isType(SelectorType.SELF);
		mentalState.adopt(this.getGoal(), !topLevel, debugger,
				mentalState.getAgentId());

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		return this.getSelector() + "adopt(" + this.goal.toString() + ")";
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
		AdoptAction other = (AdoptAction) obj;
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
