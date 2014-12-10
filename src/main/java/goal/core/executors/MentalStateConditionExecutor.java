package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.msc.MentalFormula;
import languageTools.program.agent.msc.MentalLiteral;
import languageTools.program.agent.msc.MentalStateCondition;

public class MentalStateConditionExecutor {
	private final MentalStateCondition condition;

	public MentalStateConditionExecutor(MentalStateCondition condition) {
		this.condition = condition;
	}

	/**
	 * Evaluates this {@link MentalStateCond}. First applies a given
	 * {@link Substitution} to the condition and then evaluates the condition on
	 * the given {@link MentalState}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the mental state condition.
	 * @param mentalState
	 *            The mental state on which the condition is evaluated.
	 * @param debugger
	 *            The {@link SteppingDebugger} to report on.
	 * @return The set of substitutions for which this mental state condition
	 *         holds in the given mental state. If empty the condition does not
	 *         hold; if non-empty, each of the resulting substitutions includes
	 *         the original substitution provided as a parameter. For example,
	 *         if the given substitution is {X/1} and the condition is
	 *         bel(on(X,Y)), then any of the resulting substitutions will be of
	 *         the form {X/1, Y/..}.
	 */
	public Set<Substitution> evaluate(Substitution substitution,
			MentalState mentalState, Debugger debugger) {
		Set<Substitution> result = new MentalStateConditionExecutor(
				this.condition.applySubst(substitution)).evaluate(mentalState,
						debugger);
		Set<Substitution> combinedResult = new LinkedHashSet<>(result.size());
		for (Substitution resultSubst : result) {
			combinedResult.add(resultSubst.combine(substitution));
		}
		return combinedResult;
	}

	/**
	 * Evaluates this {@link MentalStateCond} on the given {@link MentalState}.
	 *
	 * @param mentalState
	 *            The mental state on which the condition is evaluated.
	 * @param debugger
	 *            The {@link Debugger} to report on.
	 * @return The set of substitutions for which this mental state condition
	 *         holds in the given mental state. If non-empty the condition
	 *         holds, otherwise it does not hold.
	 */
	public Set<Substitution> evaluate(MentalState mentalState, Debugger debugger) {
		Set<Substitution> result, newResults, subResults;
		MentalLiteral currentFormula;
		List<MentalLiteral> formulas = new LinkedList<>();
		for (final MentalFormula formula : this.condition.getSubFormulas()) {
			if (formula instanceof MentalLiteral) {
				formulas.add((MentalLiteral) formula);
			}
		}
		if (formulas.isEmpty()) {
			// The mental state condition 'empty' represents 'true'.
			// Return an empty substitution, as no variables need to be bound.
			result = new LinkedHashSet<>(1);
			result.add(mentalState.getOwner().getKRInterface()
					.getSubstitution(null));
		} else {
			// There is at least one mental literal, so evaluate it.
			result = mentalState.query(formulas.get(0), debugger);
			// evaluate the other formulas in order
			for (int i = 1; i < formulas.size(); i++) {
				newResults = new LinkedHashSet<>();
				currentFormula = formulas.get(i);

				// for each partial result that we already have, evaluate
				// the new mental formula
				for (Substitution oldSubst : result) {
					subResults = mentalState.query(
							currentFormula.applySubst(oldSubst), debugger);
					// copy the results of the evaluation of the new formula.
					// make sure to combine the results with the current
					// partial result, since we're not using a hierarchy.
					for (Substitution subResult : subResults) {
						newResults.add(oldSubst.combine(subResult));
					}
				}
				// Update the result set.
				result = newResults;
			}
		}
		return result;
	}
}
