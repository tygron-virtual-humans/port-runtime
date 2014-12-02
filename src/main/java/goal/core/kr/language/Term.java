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
 * Interface to represent terms that may occur in queries, action parameters,
 * etc. in GOAL programs. Allows setup where GOAL interpreter abstracts from
 * particular KR language used to represent concrete terms.
 *
 *
 * @author K.Hindriks
 *
 */

public interface Term extends Expression {

	/**
	 * Instantiates (or renames) any free variables that are bound by
	 * substitution by applying substitution.
	 *
	 * @param pSubst
	 *            The substitution to apply.
	 * @return The new Term with the substitution applied.
	 */
	Term applySubst(Substitution pSubst);

	/**
	 * Converts the term to an EIS parameter.
	 *
	 * @return An EIS parameter.
	 */
	eis.iilang.Parameter convert();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

}
