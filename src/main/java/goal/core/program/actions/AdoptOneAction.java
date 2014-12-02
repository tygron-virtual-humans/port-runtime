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

/**
 * Adopts a goal by adding it into the {@link GoalBase}.
 * <p>
 * The preconditions of the adoptone action are:
 * <ul>
 * <li>the agent does not already have a 'similar' goal of the same form;</li>
 * <li>does not yet believe the goal to be adopted is the case;</li>
 * <li>there is no other goal G' that subsumes the goal G to be adopted. That
 * is, G does not follow from G' (in combination with the agent's knowledge
 * base).</li>
 * </ul>
 * </p>
 * <p>
 * TODO: currently not yet supported because code that checks whether a
 * 'similar' goal already exists in the goal base needs still to be created.
 * Grammar GOAL.g also does not yet support adoptone action.
 * </p>
 *
 * @author K.Hindriks
 */
public class AdoptOneAction extends AdoptAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 7044848551378157018L;
	// TODO: create code to compute template?
	private Query template;

	/**
	 * Creates an {@link AdoptOneAction} action from an {@link Update} formula
	 * (the goal to be adopted) and a {@link Query} template that is assumed to
	 * subsume the goal to be adopted.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param goal
	 *            The goal, i.e. {@link Update}, to be adopted.
	 * @param template
	 *            TODO: template that may not occur in goal base if AdoptOne
	 *            action is to be executed.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public AdoptOneAction(Selector selector, Update goal, Query template,
			InputStreamPosition source) {
		super(selector, goal, source);
		// TODO: Name should be changed to "adoptone".

		throw new UnsupportedOperationException(
				"The adopt one action is not yet supported.");
		// this.template = template;
	}

	/**
	 * Applies the given {@link Substitution} to this {@link AdoptOneAction} by
	 * applying the substitution to the goal to be adopted and to the
	 * {@link Selector} of this {@link AdoptAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The instantiated adoptone action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	@Override
	public AdoptOneAction applySubst(Substitution substitution) {
		return new AdoptOneAction(super.getSelector().applySubst(substitution),
				super.getGoal().applySubst(substitution), this.template,
				super.getSource());
	}

	/**
	 * Returns the precondition of this {@link AdoptOneAction}. The adoptone
	 * action can be performed if the goal to be adopted is not believed to be
	 * the case and the goal does not already follow from one of the goals in
	 * the goal base. TODO: Also, it should be checked whether a 'similar' goal
	 * does not yet exist.
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
		Query query = super.getGoal().toQuery();
		List<MentalFormula> formulalist = new ArrayList<>(2);
		// Construct the belief part of the query: NOT(BEL(query)).
		formulalist.add(new BelLiteral(false, query, this.getSelector(),
				getSource()));
		// Construct the goal part of the query: NOT(GOAL(query)).
		formulalist.add(new GoalLiteral(false, query, this.getSelector(),
				getSource()));
		// Combine both parts.
		// TODO: need to construct check that a 'similar' goal is absent.
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
	 *            the action will fail.
	 * @return {@code true} if the precondition holds; {@code false} otherwise.
	 */
	@Override
	public AdoptOneAction evaluatePrecondition(MentalState mentalState,
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
							this, "Action %s can be performed.", this);
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
		return null;
	}

	/**
	 * {@inheritDoc}<br>
	 *
	 * Executes the {@link AdoptOneAction} by inserting the goal, i.e.,
	 * {@link Update}, to be adopted into the {@link GoalBase}.
	 *
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		// FIXME: does not handle selector, etc.
		mentalState.adopt(this.getGoal(), true, debugger);

		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		return this.getName() + "(" + super.getGoal().toString() + ")";
	}

	/**
	 * TODO: CHECK we ignore the template field.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getGoal() == null) ? 0 : getGoal().hashCode());
		return result;
	}

	/**
	 * TODO: CHECK we ignore the template field.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AdoptOneAction other = (AdoptOneAction) obj;
		if (getGoal() == null) {
			if (other.getGoal() != null) {
				return false;
			}
		} else if (!getGoal().equals(other.getGoal())) {
			return false;
		}
		return true;
	}

}
