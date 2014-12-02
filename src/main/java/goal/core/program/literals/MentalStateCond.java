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

package goal.core.program.literals;

import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.mentalstate.MentalState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Mental state conditions occur as conditions in the rules of a GOAL program
 * but are also used for other purposes.
 * <p>
 * Mental state conditions are conjunctions of mental atoms and are represented
 * as a list of belief and goal literals. The order of literals is important for
 * evaluation. Because GOAL also allows the use of macros, a mental state
 * condition is represented as a list of {@link MentalFormula}s.
 * </p>
 *
 * @author K.Hindriks
 *
 */
public class MentalStateCond extends ParsedObject {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 2588480710614917621L;

	/**
	 * A {@link MentalStateCond} is defined in terms of a list of
	 * {@link MentalFormula}s. Mental formulas are used here to be able to
	 * define an MSC in terms of both {@link MentalLiteral}s as well as
	 * {@link Macro}s. So {@link #formulas} stores the <i>original</i>
	 * definition of this {@link MentalStateCond} but should only be used if
	 * this original definition is needed for presentation purposes. For al
	 * other purposes, use {@link #literals}.
	 */
	private final List<MentalFormula> formulas;
	/**
	 * A mental state condition effectively is a conjunction of
	 * {@link MentalLiteral}s. All mental literals are collected (e.g. from
	 * {@link Macro}'s that occur in the condition) and put in a list when
	 * {@link #getLiterals()} is called for the first time.
	 */
	private List<MentalLiteral> literals = null;
	private Set<Substitution> latest = new HashSet<>(0);

	/**
	 * Creates a {@link MentalStateCond} from a list of {@link MentalFormula}.
	 * Mental state conditions occur as conditions in the rules of a GOAL
	 * program but are also used for other purposes.
	 *
	 * @param formulas
	 *            The formulas from which the mental state condition is created.
	 * @param source
	 *            The location of definition of the mental state condition in
	 *            the source code. May be null if not created by a parser.
	 */
	public MentalStateCond(List<MentalFormula> formulas,
			InputStreamPosition source) {
		super(source);
		this.formulas = formulas;
	}

	/**
	 * @return The latest set of substitutions from an evaluate() call
	 */
	public Set<Substitution> getLatestSubstitutions() {
		return this.latest;
	}

	/**
	 * Extract all literals from macros, if needed, and insert them into
	 * {@link #literals}. This is done in
	 *
	 * @param formulas
	 *            The {@link MentalFormula}s that this {@link MentalStateCond}
	 *            consists of.
	 */
	private List<MentalLiteral> collectLiterals() {
		List<MentalLiteral> literals = new LinkedList<>();
		for (MentalFormula formula : this.formulas) {
			if (formula instanceof MentalLiteral) {
				literals.add((MentalLiteral) formula);
			} else if (formula instanceof Macro) {
				Macro macro = (Macro) formula;
				/*
				 * Macro definition can be null when validator fails. Validator
				 * will continue with validation and ask for literals.
				 */
				if (macro.getDefinition() != null) {
					literals.addAll(((macro).getDefinition().collectLiterals()));
				}
			}
			// ignore TrueLiterals :)
		}
		return literals;
	}

	/**
	 * Returns all {@link MentalLiteral}s that are part of this
	 * {@link MentalStateCond}.
	 *
	 * @return All mental literals that are part of this mental state condition.
	 */
	public List<MentalLiteral> getLiterals() {
		if (this.literals != null) {
			return this.literals;
		} else {
			literals = this.collectLiterals();
			return this.literals;
		}
	}

	/**
	 * Returns the sub-{@link MentalFormula}s of this {@link MentalStateCond},
	 * where a sub-formula may be a {@link Macro} or a {@link MentalLiteral}.
	 * Use {@link #getLiterals()} if macro's are not important (i.e. use
	 * {@link #getSubFormulas()} only for presentation purposes, when the
	 * <i>orginal</i> definition of this {@link MentalStateCond} is needed.
	 *
	 * @return A {@link List} of the sub-formulas of this mental state
	 *         condition.
	 */
	public List<MentalFormula> getSubFormulas() {
		return this.formulas;
	}

	/**
	 * @return The free variable(s).
	 */
	public Set<Var> getFreeVar() {
		Set<Var> freeVar = new LinkedHashSet<>();
		for (MentalFormula formula : this.formulas) {
			freeVar.addAll(formula.getFreeVar());
		}
		return freeVar;
	}

	/**
	 * Applies a given substitution to this {@link MentalStateCond}.
	 *
	 * @param subst
	 *            The substitution that is applied to the mental state
	 *            condition.
	 * @return The new {@link MentalStateCond}.
	 */
	public MentalStateCond applySubst(Substitution subst) {
		List<MentalFormula> instantiatedFormulas = new ArrayList<>(
				this.formulas.size());
		for (MentalFormula formula : this.formulas) {
			instantiatedFormulas.add(formula.applySubst(subst));
		}
		return new MentalStateCond(instantiatedFormulas, this.getSource());
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
		Set<Substitution> result = this.applySubst(substitution).evaluate(
				mentalState, debugger);
		Set<Substitution> combinedResult = new LinkedHashSet<>(result.size());
		for (Substitution resultSubst : result) {
			combinedResult.add(resultSubst.combine(substitution));
		}
		this.latest = combinedResult;
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

		if (this.getLiterals().isEmpty()) {
			// The mental state condition 'empty' represents 'true'.
			// Return an empty substitution, as no variables need to be bound.
			result = new LinkedHashSet<>(1);
			result.add(mentalState.getKRLanguage().getEmptySubstitution());
		} else {
			// There is at least one mental literal, so evaluate it.
			result = mentalState.query(this.literals.get(0), debugger);
			// evaluate the other formulas in order
			for (int i = 1; i < this.literals.size(); i++) {
				newResults = new LinkedHashSet<>();
				currentFormula = this.literals.get(i);

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

	/**
	 * Returns a string representation of this {@link MentalStateCond}.
	 *
	 * @return A string representation of this mental state condition.
	 */
	@Override
	public String toString() {
		List<MentalLiteral> literals = getLiterals();
		if (literals.isEmpty()) {
			return "true";
		}

		StringBuilder builder = new StringBuilder();
		builder.append(literals.get(0));
		for (int i = 1; i < literals.size(); i++) {
			builder.append(", ");
			builder.append(literals.get(i));
		}
		return builder.toString();
	}

}
