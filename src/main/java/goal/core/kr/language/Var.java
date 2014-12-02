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
 * Interface to represent variables that may occur in queries, action
 * parameters, etc. in GOAL programs. Allows setup where GOAL interpreter
 * abstracts from particular kr language used to represent concrete variables.
 *
 * @author K.Hindriks
 */
public interface Var extends Term {

	/**
	 * <p>
	 * Renames this {@link Var} into another variable, represented as a
	 * {@link Term}. The original label remains, but something is prefixed
	 * and/or appended to the original label.
	 * </p>
	 * TODO: need this to STANDARDIZE APART variables in a context...
	 *
	 * <p>
	 * In Prolog there is the "anonymous variable" '_' that should not be
	 * renamed, because all renaming of this variable would change it into a
	 * non-anonymous variable. It is up to renameVar to handle such special
	 * cases. See TRAC #1151.
	 * </p>
	 *
	 * @param prefix
	 *            The string to prefix to the original label
	 * @param postfix
	 *            The string to append to the original label
	 * @return a variant that can be used to rename the variable (should be a
	 *         variable again, but represented as a term as this is needed in
	 *         substitution; see GOALProgram.java and Macro.java where this
	 *         method is called).
	 */
	Substitution renameVar(String prefix, String postfix);

	/**
	 * Returns a substitution that binds term to this {@link Var}.
	 *
	 * @param term
	 *            The term.
	 * @return The substitution with the term.
	 */
	Substitution assign(Term term);

	/**
	 * Instantiates (or renames) any free variables that are bound by
	 * substitution by applying substitution.
	 *
	 * @return general Expression,
	 */
	@Override
	Term applySubst(Substitution pSubst);

	/**
	 * Checks if this variable is an anonymous variable.
	 *
	 * @return <code>true</code> if this {@link Var} is anonymous.
	 */
	boolean isAnonymous();

}
