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

package goal.core.mentalstate;

import krTools.language.DatabaseFormula;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a set of formulas that have been inserted into a corresponding
 * database. A theory is associated with a corresponding database. A theory thus
 * is the equivalent at the GOAL level of a database created by some kr
 * technology. A theory is used to represent the belief base, percept base,
 * mailbox, and goal base (which consists of multiple databases each of which
 * have an associated theory).
 *
 * Formulas in a theory need to be {@link DatabaseFormula}, i.e. formulas that
 * can be inserted into a database created by the associated KR technology.
 *
 * Summarizing, the motivation for introducing the Theory class are that it
 * allows to: - represent the content of belief and goal bases, as well as
 * databases in general. - avoid the need for (complex) access methods to
 * databases maintained by the underlying knowledge technology such as Prolog. -
 * avoid duplication of clauses or facts in a database (a Theory itself is a
 * set).
 *
 * @author K.Hindriks
 */
public class Theory {

	/**
	 * The formulas in this {@link Theory}.
	 */
	private final Set<DatabaseFormula> content;

	/**
	 * Creates a theory and adds all given formulas to it.
	 *
	 * @param formulas
	 *            A collection of formulas to set the theory's initial content.
	 */
	protected Theory(Collection<DatabaseFormula> formulas) {
		this.content = new LinkedHashSet<>(formulas);
	}

	/**
	 * Returns copy of the set of all formulas that are part of this theory.
	 *
	 * @return a copy of the {@link DatabaseFormula}s in this theory.
	 */
	public synchronized Set<DatabaseFormula> getFormulas() {
		return new HashSet<>(this.content);
	}

	// *************** insertion methods *************/

	/**
	 * Adds a formula to this {@link Theory}, if it not already occurs in the
	 * theory.
	 *
	 * @param formula
	 *            The formula.
	 *
	 * @return <code>true</code> if the theory changed; <code>false</code>
	 *         otherwise.
	 */
	protected synchronized boolean add(DatabaseFormula formula) {
		return this.content.add(formula);
	}

	/**
	 * Adds all formulas in theory literally to this theory (but does not
	 * introduce duplicates).
	 *
	 * @param content
	 *            The content.
	 *
	 * @return {@code true} if theory changed; {@code false} otherwise.
	 */
	protected synchronized boolean add(Set<DatabaseFormula> content) {
		return this.content.addAll(content);
	}

	// *************** deletion methods *************/

	/**
	 * Removes a formula from the theory if it occurs literally as element in
	 * theory.
	 *
	 * @param formula
	 *            formula to be removed from theory.
	 * @return true if the theory contained formula as element and it has been
	 *         successfully removed.
	 */
	protected synchronized boolean remove(DatabaseFormula formula) {
		return this.content.remove(formula);
	}

	/**
	 * Erases all content in the theory.
	 */
	protected void eraseContent() {
		this.content.clear();
	}

	/**
	 * Generates string with all formulas in the theory on separate lines.
	 */
	@Override
	public synchronized String toString() {
		String text = "";
		for (DatabaseFormula formula : content) {
			text += formula.toString() + ".\n";
		}
		return text;
	}
}
