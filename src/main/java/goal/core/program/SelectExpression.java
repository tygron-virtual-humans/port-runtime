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

import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Var;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SelectExpression represents a single agent or a variable agent or a dynamic
 * set of agents (i.e. ALL agents, SOMEOTHER agent). Run-time resolution of the
 * AgentExpression depends on its type, and in the case of SELF, ALL and SOME
 * also on the running agent itself.
 *
 * @author W.Pasman
 * @modified W.Pasman 12mar10 TRAC #999: selectExpression can now also point to
 *           module name. CHECK what has been changed? How does that work?
 * @modified K.Hindriks select expression cannot refer to module anymore.
 * @modified W.Pasman 20sep2012 made serializable #2246
 */
public class SelectExpression implements Serializable {

	/**
	 * Selector types represent different categories of selector expressions
	 * that can be used as prefixes of mental atoms and actions.
	 *
	 * @author K.Hindriks
	 */
	public enum SelectorType {
		SELF, ALL, ALLOTHER, SOME, SOMEOTHER, THIS, VARIABLE, CONSTANT;
	}

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 7865380710104734140L;
	/**
	 * The {@link Term} that represents either a {@link SelectorType#CONSTANT}
	 * or {@link SelectorType#VARIABLE}. For all other types term should be
	 * equal to {@code null}.
	 */
	private Term term = null;
	/**
	 * The {@link SelectorType} of this expression. Default type is
	 * {@link SelectorType#THIS}.
	 */
	private SelectorType type;

	/**
	 * Creates a {@link SelectExpression} for one of the reserved selector types
	 * {@link SelectorType#CONSTANT} and {@link SelectorType#VARIABLE}.
	 *
	 * @param type
	 *            A selector type {@link SelectorType#CONSTANT} or
	 *            {@link SelectorType#VARIABLE}.
	 * @param term
	 *            A {@link Term} representing either the constant or variable
	 *            selector.
	 */
	public SelectExpression(SelectorType type, Term term) {
		switch (type) {
		case CONSTANT:
		case VARIABLE:
			this.type = type;
			this.term = term;
			break;
		default:
			throw new UnsupportedOperationException(
					"A term "
							+ term
							+ " was provided but is redundant when "
							+ "creating a Selector other than type CONSTANT or VARIABLE.");
		}
	}

	/**
	 * Creates a {@link SelectExpression} for one of the reserved selector types
	 * other than {@link SelectorType#CONSTANT} and
	 * {@link SelectorType#VARIABLE}.
	 *
	 * @param type
	 *            A selector type other than {@link SelectorType#CONSTANT} and
	 *            {@link SelectorType#VARIABLE}.
	 * @throws UnsupportedOperationException
	 *             In case the value of type is a constant or variable type.
	 */
	public SelectExpression(SelectorType type) {
		switch (type) {
		case CONSTANT:
		case VARIABLE:
			throw new UnsupportedOperationException("Cannot create a Selector "
					+ "of type CONSTANT or VARIABLE without providing a term.");
		default:
			this.type = type;
		}
	}

	/**
	 * Applies the given {@link Substitution} to this {@link SelectExpression}
	 * in case the expression is of {@link SelectorType#VARIABLE}. In that case,
	 * the substitution is applied to the variable. And then it's tested if the
	 * new value is one of the special {@link SelectorType}s.
	 *
	 * If it's not a {@link SelectorType#VARIABLE}, the original expression is
	 * returned.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The instantiated expression where the substitution has been
	 *         applied to the variable in case the expression's type is
	 *         VARIABLE; otherwise the original expression is returned.
	 */
	public SelectExpression applySubst(Substitution substitution) {
		if (type == SelectorType.VARIABLE) {
			Term term = this.term.applySubst(substitution);
			if (term.isClosed()) {
				// #1308 the closed term may be 'all', 'allother' etc.
				// this determines the TYPE.
				SelectorType newtype;
				try {
					newtype = SelectorType.valueOf(term.toString()
							.toUpperCase());
					if (newtype == SelectorType.CONSTANT
							|| newtype == SelectorType.THIS
							|| newtype == SelectorType.VARIABLE) {
						throw new IllegalArgumentException(
								"variable containing destination for send contains illegal value "
										+ term);
					}
					return new SelectExpression(newtype);
				} catch (IllegalArgumentException e) {
					// if we get here, the selector is not a reserved keyword.
					// it would have been better if valueOf returned null in
					// this case...
				}
				return new SelectExpression(SelectorType.CONSTANT, term);
			} else {
				return new SelectExpression(SelectorType.VARIABLE, term);
			}
		} else {
			return this;
		}
	}

	/**
	 * Checks whether this {@link SelectExpression} is closed, i.e., does not
	 * contain any occurrences of (free) variables.
	 *
	 * @return {@code true} if the expression is of another type
	 */
	public boolean isClosed() {
		if (type == SelectorType.VARIABLE) {
			return term.isClosed();
		} else {
			return true;
		}
	}

	/**
	 * @return The type of this expression
	 */
	public SelectorType getType() {
		return type;
	}

	/**
	 * @return A textual representation of this expression
	 */
	public String getName() {
		String signature = term.getSignature();
		return signature.substring(0, signature.indexOf('/'));
	}

	/**
	 * Returns the free variables in the agent expression. This is only relevant
	 * for VARIABLE type agent expressions, so return an empty set if it is of
	 * an other type.
	 *
	 * @return The free variables
	 */
	public Set<Var> getFreeVar() {
		if (type == SelectorType.VARIABLE) {
			return term.getFreeVar();
		} else {
			return new HashSet<>(0);
		}
	}

	/**
	 * @author W.Pasman 3jun10 TRAC #1128, #1125
	 * @return The term associated with this expression
	 */
	public Term getTerm() {
		return term;
	}

	/**
	 * Returns a string representation of this AgentExpression.
	 *
	 * @return String a string representation of this AgentExpression.
	 */
	@Override
	public String toString() {
		switch (type) {
		case VARIABLE:
		case CONSTANT:
			return term.toString();
		case SELF:
		case ALL:
		case ALLOTHER:
		case SOME:
		case SOMEOTHER:
		case THIS:
		default:
			return type.toString().toLowerCase();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SelectExpression other = (SelectExpression) obj;
		if (term == null) {
			if (other.term != null) {
				return false;
			}
		} else if (!term.equals(other.term)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
