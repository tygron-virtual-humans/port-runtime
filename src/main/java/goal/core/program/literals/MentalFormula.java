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

import java.util.Set;

/**
 * Abstraction of a {@link MentalLiteral} and a {@link Macro}, to facilitate
 * storing {@link Macro}s in {@link MentalStateCond}s.
 *
 * @author N.Kraayenbrink
 *
 */
public interface MentalFormula {
	/**
	 * Transforms this MentalAtom.
	 *
	 * @param pSubst
	 *            The substitution to transform this atom with.
	 * @return The transformed version of this atom.
	 */
	MentalFormula applySubst(Substitution pSubst);

	/**
	 * @return All free variables in this atom. When this atom is used in a
	 *         query, these variables are bound.
	 */
	Set<Var> getFreeVar();

	@Override
	String toString();
}
