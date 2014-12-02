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

package goal.core.program;

import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Update;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.validation.agentfile.ActionComboValidator;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;

/**
 * Container for an {@link ActionSpecification}. An action specification
 * includes:
 * <ul>
 * <li>The name and parameters of the action (a {@link UserSpecAction})</li>
 * <li>The precondition of the action that is specified (a {@link Query}), and</li>
 * <li>The postcondition of the action that is specified (a {@link Update}).</li>
 * </ul>
 * 
 * An action specification is obtained from the action specification section in
 * a module in a GOAL program. Specifications look like:<br>
 * <tt>
 * 		move(X, Y) {<br>
		&nbsp;   pre{ clear(X), clear(Y), on(X, Z), not(on(X, Y)) }<br>
		&nbsp;   post{ not(on(X, Z)), on(X, Y) }<br>
		}<br>
	</tt>
 * 
 * @author K.Hindriks
 * 
 */
public class ActionSpecification extends ParsedObject {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -6170734471473354759L;

	/**
	 * The action that is specified, including action parameters.
	 */
	private final UserSpecAction action;
	/**
	 * The precondition of the action.
	 */
	private final Query precondition;
	/**
	 * The postcondition of the action.
	 */
	private final Update postcondition;
	/**
	 * isUsed is used during validation to check whether specification is
	 * actually used.
	 */
	private boolean isUsed;

	/**
	 * Creates a new {@link ActionSpecification}.
	 *
	 * @param action
	 *            The {@link UserSpecAction} that is specified.
	 * @param precondition
	 *            The precondition of the action.
	 * @param postcondition
	 *            The postcondition of the acton.
	 * @param source
	 *            The input position in the source program text.
	 */
	public ActionSpecification(UserSpecAction action, Query precondition,
			Update postcondition, InputStreamPosition source) {
		super(source);

		this.action = action;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.isUsed = false;
	}

	/**
	 * Returns the {@link UserSpecAction} that is specified.
	 *
	 * @return The action that is specified.
	 */
	public UserSpecAction getAction() {
		return action;
	}

	/**
	 * Returns the precondition of the specification.
	 *
	 * @return The precondition of the specification.
	 */
	public Query getPreCondition() {
		return precondition;
	}

	/**
	 * Returns the postcondition of the specification.
	 *
	 * @return The postcondition of the specification.
	 */
	public Update getPostCondition() {
		return postcondition;
	}

	/**
	 * Returns false by default, but <code>true</code> once {@link #markUsed()}
	 * has been called at least once.
	 * 
	 * @return <code>true</code> if this action specification has been used
	 *         somewhere; <code>false</code> otherwise.
	 */
	public boolean isUsed() {
		return this.isUsed;
	}

	/**
	 * Marks this action specification as used. See also
	 * {@link ActionComboValidator#doValidate}.
	 */
	public void markUsed() {
		this.isUsed = true;
	}

	@Override
	public void setID(int id) {
		super.setID(id);
		// make sure to set the ID of the inner action as well
		this.action.getSource().setID(id);
	}

	/**
	 * Creates a new instance of this {@link ActionSpecification} by applying
	 * the parameter substitution to it.
	 * 
	 * @param subst
	 *            The {@link Substitution} that is applied to this
	 *            specification.
	 * @return An instantiation (or version with variables renamed) of this
	 *         specification obtained by applying the given substitution.
	 */
	public ActionSpecification applySubst(Substitution subst) {
		return new ActionSpecification(action.applySubst(subst),
				precondition.applySubst(subst),
				postcondition.applySubst(subst), this.getSource());
	}

	@Override
	public String toString() {
		return this.toString("");
	}

	/**
	 * Generalization of {@link ActionSpecification#toString()}, allowing the
	 * returned string to be indented at various levels.
	 * 
	 * @param linePrefix
	 *            What to prefix to every line, generally some tabs.
	 * @return A string-representation of this action specification.
	 */
	public String toString(String linePrefix) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(linePrefix + this.action.toString() + "{\n");

		sBuilder.append(linePrefix + "\tpre {" + this.precondition.toString()
				+ "}\n");
		sBuilder.append(linePrefix + "\tpost {" + this.postcondition.toString()
				+ "}\n");

		sBuilder.append(linePrefix + "}\n");

		return sBuilder.toString();
	}

	/**
	 * Returns a short version of this {@link ActionSpecification}'s string
	 * representation.
	 * 
	 * @return A short version of this specification's string representation.
	 *         Only contains the action name and parameter list.
	 */
	public String toShortString() {
		return this.action.toString();
	}

	/**
	 * Returns a string representing the signature of this
	 * {@link ActionSpecification}.
	 *
	 * @return A string of the format {action name}/{number of parameters}.
	 */
	public String getSignature() {
		return this
				.getAction()
				.getName()
				.concat("/")
				.concat(String.valueOf(this.getAction().getParameters().size()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result
				+ ((postcondition == null) ? 0 : postcondition.hashCode());
		result = prime * result
				+ ((precondition == null) ? 0 : precondition.hashCode());
		return result;
	}

	/**
	 * Checks whether tow action specifications are equal. The isUsed field is
	 * ignored.
	 */
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
		ActionSpecification other = (ActionSpecification) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (postcondition == null) {
			if (other.postcondition != null) {
				return false;
			}
		} else if (!postcondition.equals(other.postcondition)) {
			return false;
		}
		if (precondition == null) {
			if (other.precondition != null) {
				return false;
			}
		} else if (!precondition.equals(other.precondition)) {
			return false;
		}
		return true;
	}

}
