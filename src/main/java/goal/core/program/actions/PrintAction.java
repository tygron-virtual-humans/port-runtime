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
import goal.core.kr.language.Term;
import goal.core.kr.language.Var;
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
 * Prints its argument to Sytem.out.
 *
 * @author K.Hindriks
 */
public class PrintAction extends Action {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -5949722832056366117L;
	/**
	 * The argument that is printed.
	 */
	private final Term argument;

	/**
	 * Creates a {@link PrintAction}.
	 *
	 * @param argument
	 *            The argument that determines what needs to be printed.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public PrintAction(Term argument, InputStreamPosition source) {
		super("log", source);

		this.argument = argument;
	}

	/**
	 * Returns the (free) variables that occur in the argument of this
	 * {@link PrintAction}.
	 *
	 * @return The set of variables that occur in the action's argument.
	 */
	@Override
	public Set<Var> getFreeVar() {
		return this.argument.getFreeVar();
	}

	/**
	 * Applies the given {@link Substitution} to this {@link PrintAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return Action with its argument instantiated.
	 */
	@Override
	public PrintAction applySubst(Substitution substitution) {
		return new PrintAction(this.argument.applySubst(substitution),
				this.getSource());
	}

	/**
	 * The precondition of a {@link PrintAction} is {@code true}, or, to be more
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
		formulaList.add(new BelLiteral(true, query, new Selector(null),
				getSource()));
		return new MentalStateCond(formulaList, getSource());
	}

	/**
	 * Checks whether the precondition of this {@link PrintAction} holds. See
	 * also: {@link PrintAction#getPrecondition(KRlanguage)}).
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
	 * @return This action since the log action has an "empty" precondition.
	 */
	@Override
	public PrintAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
	}

	/**
	 * {@inheritDoc}<br>
	 * Executes the {@link PrintAction}.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {

		String output = this.argument.toString();

		boolean beginQuote = output.startsWith("\"") || output.startsWith("'");
		boolean endQuote = output.endsWith("\"") || output.endsWith("'");

		// Print string without initial or final quotes
		System.out.println(output.substring(beginQuote ? 1 : 0,
				endQuote ? output.length() - 1 : output.length()));

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		return "print(" + argument + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
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
		PrintAction other = (PrintAction) obj;
		if (argument == null) {
			if (other.argument != null) {
				return false;
			}
		} else if (!argument.equals(other.argument)) {
			return false;
		}
		return true;
	}

}
