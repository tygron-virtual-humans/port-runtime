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

import java.io.Serializable;

/**
 *
 * Represents an expression that may be inserted into a Database object.
 *
 * It is assumed that a DatabaseFormula does not consist of parts that
 * themselves are DatabaseFormulas again. That is, a DatabaseFormula must be an
 * expression that itself may be inserted into a database but does not have
 * constituent parts that may be inserted into a database.
 *
 * Examples of database formulas differ from one kr language to another. In
 * Prolog, for example, clauses of the form p :- q and p where p and q are
 * literals may be part of a Prolog database. A negative literal not(p),
 * however, cannot be part of a Prolog database. Also note that a Prolog
 * conjunction p, q is not considered a database formula, as both p and q
 * separately can be inserted into a database.
 *
 * @author K.Hindriks
 * @author W.Pasman DatabaseFormula made serializable #2246
 */

public interface DatabaseFormula extends Expression, Serializable {

	/**
	 * Instantiates (or renames) any free variables that are bound by
	 * substitution by applying substitution.
	 *
	 * @param pSubst
	 *            The substitution to apply.
	 * @return The new Query with the substitution applied.
	 */
	DatabaseFormula applySubst(Substitution pSubst);

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();
}
