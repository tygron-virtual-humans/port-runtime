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
import goal.core.program.Module;
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
 * Forces an exit from the current (non-anonymous) {@link Module}.
 * <p>
 * Modules create an (implicit) stack of contexts <module1, module2, module3,
 * ..., moduleN> when they are called, where 'moduleN' is the last module that
 * has been entered. Executing the exit-module action means that all anonymous
 * modules higher on the stack than the first non-anonymous module are exited as
 * well as the first non-anonymous module. Execution then continues in the
 * module one level lower in the stack.
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class ExitModuleAction extends Action {

	/**
	 *
	 *
	 */
	public enum ExitTarget {
		/** go to top level module */
		ALL,
		/** defocus until we defocus a real module, skipping implicit modules */
		MODULE,
		/**
		 * exit the current implicit module. Do nothing if not inside implicit
		 * module
		 */
		IMPLICIT_MODULE,
		/**
		 * exit just once to any higher module, don't care about implicit or
		 * explicit
		 */
		ONE
	}

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 5982692313804382202L;

	/**
	 * Creates an {@link ExitModuleAction} that forces an exit from the current
	 * (non-anonymous) module, i.e., the highest non-anonymous modules on the
	 * (implicit) module stack; all higher anonymous modules are also exited.
	 *
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public ExitModuleAction(InputStreamPosition source) {
		super("exit-module", source);
	}

	/**
	 * Returns the (free) variables of this {@link ExitModuleAction}.
	 *
	 * @return The empty set of variables.
	 */
	@Override
	public Set<Var> getFreeVar() {
		return new LinkedHashSet<>(0);
	}

	/**
	 * Applies the given {@link Substitution} to this {@link ExitModuleAction}.
	 * As the action has no free variables there is nothing to do.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The exit module action "as is".
	 */
	@Override
	public Action applySubst(Substitution substitution) {
		return this;
	}

	/**
	 * The precondition of an {@link ExitModuleAction} is {@code true}, or, to
	 * be more precise "bel(true)"; this means it can always be performed if the
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
		formulaList.add(new BelLiteral(true, query, new Selector(null),
				getSource()));
		return new MentalStateCond(formulaList, getSource());
	}

	/**
	 * Checks whether the precondition of this {@link ExitModuleAction} holds.
	 * See also: {@link ExitModuleAction#getPrecondition(KRlanguage)}).
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
	 * @return {@code true} since the exit module action has an "empty"
	 *         precondition.
	 */
	@Override
	public ExitModuleAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		return result;
	}

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
		return true;
	}

}
