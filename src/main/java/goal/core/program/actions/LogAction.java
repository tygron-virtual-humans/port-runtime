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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Exports the mental state to a file. The argument should be one of the
 * {@link LogOptions}s. Everything else is mapped to {@link LogOptions#TEXT}.
 *
 * TODO: - support multiple {@link LogOptions} at the same time. - support a
 * more liberal style of argument with variables? - support use of
 * {@link Selector} to be able to also log mental models of other agents.
 *
 * @author W.Pasman 6jun2011
 * @modified K.Hindriks
 */
public class LogAction extends Action {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -5949722832056366117L;
	/**
	 * The argument that determines what will be logged.
	 */
	private final String argument;

	/**
	 * Creates a {@link LogAction} that logs content to a file.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param argument
	 *            The argument that determines what needs to be logged.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public LogAction(Selector selector, String argument,
			InputStreamPosition source) {
		super("log", source);

		this.argument = argument;
	}

	/**
	 * Returns the (free) variables that occur in the argument of this
	 * {@link LogAction}.
	 *
	 * @return The empty set of variables.
	 */
	@Override
	public Set<Var> getFreeVar() {
		return new LinkedHashSet<>(0);
	}

	/**
	 * Applies the given {@link Substitution} to this {@link LogAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return Action "as is".
	 */
	@Override
	public LogAction applySubst(Substitution substitution) {
		return this;
	}

	/**
	 * The precondition of a {@link LogAction} is {@code true}, or, to be more
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
	 * Checks whether the precondition of this {@link LogAction} holds. See
	 * also: {@link LogAction#getPrecondition(KRlanguage)}).
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
	public LogAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
	}

	/**
	 * {@inheritDoc}<br>
	 * Executes the {@link LogAction} by exporting the requested database to a
	 * file. The parameters for the action are extracted from this action's
	 * argument. .
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		boolean self = false;
		boolean bb = false, gb = false, kb = false, mb = false, pb = false;
		switch (LogOptions.fromString(argument.toString())) {
		case BB:
			bb = true;
			break;
		case GB:
			gb = true;
			break;
		case KB:
			kb = true;
			break;
		case MB:
			mb = true;
			break;
		case PB:
			pb = true;
			break;
		default:
		case TEXT:
			runState.doLog(argument.toString());
			break;
		}
		if (kb || bb || pb || mb || gb) {
			String ms = mentalState.toString(kb, bb, pb, mb, gb, !self);
			runState.doLog(ms);
		}

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		return "log(" + argument + ")";
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
		LogAction other = (LogAction) obj;
		if (argument == null) {
			if (other.argument != null) {
				return false;
			}
		} else if (!argument.equals(other.argument)) {
			return false;
		}
		return true;
	}

	/**
	 * Available options for logging.
	 *
	 * @author W.Pasman
	 * @modified K.Hindriks
	 */
	private enum LogOptions {
		/** export belief base */
		BB,
		/** export goal base */
		GB,
		/** export percept base */
		PB,
		/** export mailbox */
		MB,
		/** export knowledge base */
		KB,
		/** export plain text */
		TEXT;

		/**
		 * Maps a string to an {@link LogOptions}.
		 *
		 * @param type
		 *            A string representing the type of logging to be done.
		 * @return The action type that corresponds with the parameter.
		 */
		public static LogOptions fromString(String type) {
			try {
				return valueOf(type.toUpperCase());
			} catch (Exception e) {
				return TEXT;
			}
		}
	}

}
