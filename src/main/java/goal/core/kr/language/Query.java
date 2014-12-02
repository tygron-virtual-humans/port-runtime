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

package goal.core.kr.language;

/**
 *
 * Represents an expression that can be queried on a database. The expression
 * should be a formula that may evaluate to true or false, possibly after
 * instantiating any free variables. The corresponding InferenceEngine should
 * support the evaluation of a QueryExpression on a database. Typically, as a
 * result of the evaluation, a substitution - binding free variables in the
 * QueryExpression with terms - may be returned by the InferenceEngine.
 *
 * @author K.Hindriks
 * @modified N.Kraayenbrink 31jan10; Removed toDatabaseFormula, added toUpdate
 *
 */

public interface Query extends Expression {

	/**
	 * Instantiates (or renames) any free variables that are bound by
	 * substitution by applying substitution.
	 *
	 * @param pSubst
	 *            The substitution to apply.
	 * @return The new Query with the substitution applied.
	 */
	Query applySubst(Substitution pSubst);

	/**
	 * Converts a Query to an {@link Update}.
	 *
	 * All Mental Literals contain Queries. However goals are represented by
	 * Updates. In order to convert a mental literal into a goal, as is
	 * necessary when instantiating a module, a way to convert a Query into an
	 * Update is needed.
	 *
	 * @return An Update with an empty delete list and an add list with the
	 *         content of this Query.
	 */
	Update toUpdate();

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();
}
