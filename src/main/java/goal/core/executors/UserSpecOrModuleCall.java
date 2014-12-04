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
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Var;
import goal.core.mentalstate.MentalState;
import goal.core.program.literals.MentalStateCond;
import goal.core.program.rules.Rule;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.tools.debugger.Debugger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Action used for parsing purposes only. Used because at the parsing time it
 * cannot yet be determined whether the parsed object should be resolved to
 * either a user-specified action or to a focus action (module invocation).<br>
 * This 'action' is replaced during validation of the agent program by a proper
 * action.<br>
 * A UserOrFocusAction cannot be executed.
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class UserSpecOrModuleCall extends Action {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 48843417364416454L;
	/**
	 * The parameters of this {@link UserSpecOrModuleCall}.
	 */
	private final List<Term> parameters;
	/**
	 * The parent {@link Rule} that this {@link UserSpecOrModuleCall} is a part
	 * of.
	 */
	private Rule parent;

	/**
	 * Creates an action that can either be a {@link ModuleCallAction} or a
	 * {@link UserSpecAction}.
	 * <p>
	 * Method calls will result in an {@link UnsupportedOperationException} with
	 * the following exceptions:
	 * <ul>
	 * <li>{@link #getName()}</li>
	 * <li>{@link #getParameters()}</li>
	 * <li>{@link #getFreeVar()}</li>
	 * <li>{@link #getRule()}</li>
	 * <li>{@link #setRule(Rule)}</li>
	 * <li>{@link #toString()}</li>
	 * </ul>
	 * </p>
	 *
	 * @param name
	 *            The name of the action.
	 * @param parameters
	 *            The parameters of the action.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public UserSpecOrModuleCall(String name, List<Term> parameters,
			InputStreamPosition source) {
		super(name, source);
		if (parameters == null) {
			// Parser may return null value
			this.parameters = new ArrayList<>(0);
		} else {
			this.parameters = parameters;
		}
	}

	/**
	 * Returns the parameters of this {@link UserSpecOrModuleCall}.
	 *
	 * @return The parameters of this action.
	 */
	public List<Term> getParameters() {
		return this.parameters;
	}

	/**
	 * Returns the {@link Rule} this action is a part of.
	 *
	 * @return The rule this action is a part of.
	 */
	public Rule getRule() {
		return parent;
	}

	/**
	 * Set the {@link Rule} this action is a part of.
	 *
	 * @param parent
	 *            The rule this action is a part of.
	 */
	public void setRule(Rule parent) {
		this.parent = parent;
	}

	/**
	 * Returns the free variables in the parameters of this
	 * {@link UserSpecOrModuleCall}.
	 *
	 * @return The free variables that occur in the parameters of this action.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = new LinkedHashSet<>();
		for (Term parameter : this.parameters) {
			freeVars.addAll(parameter.getFreeVar());
		}
		return freeVars;
	}

	@Override
	public MentalStateCond getPrecondition(KRlanguage language) {
		throw new UnsupportedOperationException(
				"The UserOrFocusAction cannot be executed and is not supported.");
	}

	@Override
	public Action applySubst(Substitution subst) {
		throw new UnsupportedOperationException(
				"The UserOrFocusAction cannot be executed and is not supported.");
	}

	@Override
	public UserSpecOrModuleCall evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		throw new UnsupportedOperationException(
				"The UserOrFocusAction cannot be executed and is not supported.");
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		throw new UnsupportedOperationException(
				"The UserOrFocusAction cannot be executed and is not supported.");
	}

	@Override
	public String toString() {
		String parameterList = "";

		if (!this.parameters.isEmpty()) {
			for (Term term : this.parameters) {
				if (parameterList.length() == 0) {
					parameterList += term.toString();
				} else {
					parameterList += "," + term.toString();
				}
			}
			parameterList = "(" + parameterList + ")";
		}
		return this.getName() + parameterList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
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
		UserSpecOrModuleCall other = (UserSpecOrModuleCall) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		return true;
	}

}
