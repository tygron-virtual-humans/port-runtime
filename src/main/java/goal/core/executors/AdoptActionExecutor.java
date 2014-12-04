package goal.core.executors;

import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;

import java.util.ArrayList;
import java.util.List;

import krTools.errors.exceptions.KRQueryFailedException;
import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.actions.AdoptAction;
import languageTools.program.agent.actions.AdoptOneAction;
import languageTools.program.agent.msc.BelLiteral;
import languageTools.program.agent.msc.GoalLiteral;
import languageTools.program.agent.msc.MentalFormula;
import languageTools.program.agent.selector.Selector;
import languageTools.program.agent.selector.Selector.SelectorType;

/**
 * 
 * @author W.Pasman
 *
 */
public class AdoptActionExecutor extends ActionExecutor {

	AdoptAction action;

	public AdoptActionExecutor(AdoptAction act) {
		super(act);
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
	public MentalState getPrecondition(KRlanguage language) {
		// Construct the mental state condition that represents the
		// precondition.
		Query query = this.goal.toQuery();
		List<MentalFormula> formulalist = new ArrayList<>(2);
		// Construct the belief part of the query: NOT(BEL(query)).
		formulalist.add(new BelLiteral(false, query, action.getSelector(),
				getSource()));
		// Construct the goal part of the query: NOT(GOAL(query)).
		formulalist.add(new GoalLiteral(false, query, action.getSelector(),
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
		Selector selector = action.getSelector();

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
}