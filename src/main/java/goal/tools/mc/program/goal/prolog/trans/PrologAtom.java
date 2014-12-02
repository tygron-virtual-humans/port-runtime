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

package goal.tools.mc.program.goal.prolog.trans;

import goal.core.kr.language.Substitution;
import goal.tools.mc.program.goal.trans.Atom;
import swiprolog3.language.JPLUtils;
import swiprolog3.language.PrologTerm;
import swiprolog3.parser.PrologOperators;

/**
 * Represents an atom when the Prolog KRT is used.
 *
 * @author sungshik.
 *
 */
public class PrologAtom implements Atom {

	//
	// Private fields
	//

	/**
	 * The atom.
	 */
	private jpl.Term atom;

	//
	// Constructors
	//

	/**
	 * Private constructor to enforce use of {@link #factory}.
	 */
	private PrologAtom() {
	}

	//
	// Public methods
	//

	/**
	 * Affirms <code>term</code>, i.e. strips the not operator of a Prolog term.
	 *
	 * @param term
	 *            The {@link PrologTerm} to be affirmed.
	 * @return The affirmed {@link PrologTerm}.
	 */
	public static jpl.Term affirm(jpl.Term term) {

		try {
			if (JPLUtils.getSignature(term).equals("not/1")) {
				return (term.args())[0];
			} else if (term.isAtom()) {
				return term;
			} else {
				throw new Exception();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PrologAtom) {
			return atom.equals(((PrologAtom) o).atom);
		}
		return false;
	}

	/**
	 * Constructs a {@link PrologAtom} object iff <code>term</code> is a Prolog
	 * atom.
	 *
	 * @param term
	 *            The {@link PrologTerm} that is (or is not) an atom.
	 * @return The constructed {@link PrologAtom} object.
	 */
	public static PrologAtom factory(jpl.Term term) {

		try {
			assert term.isAtom();
			PrologAtom atom = new PrologAtom();
			atom.atom = term;
			return atom;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the functor of this atom.
	 *
	 * @return The functor.
	 */
	public String getFunctor() {
		String signature = JPLUtils.getSignature(atom);
		return signature.substring(0, signature.indexOf('/'));
	}

	/**
	 * Gets the signature of this atom.
	 *
	 * @return The signature.
	 */
	public String getSignature() {
		return JPLUtils.getSignature(atom);
	}

	/**
	 * Gets the term that constitutes this atom.
	 *
	 * @return The term.
	 */
	public jpl.Term getTerm() {
		return atom;
	}

	@Override
	public int hashCode() {
		return atom.hashCode();
	}

	/**
	 * Determines whether <code>term</code> is a positive atom. For this to be
	 * the case, two conditions must hold: (i) <code>term</code> is not a
	 * variable term, and (ii) <code>term</code> is not a built-in Prolog
	 * predicate.
	 *
	 * @param term
	 *            The term to test.
	 * @return <code>true</code> if the two conditions are satisfied;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isAtom(jpl.Term term) {
		return (!term.isVariable() && !PrologOperators.prologBuiltin(JPLUtils
				.getSignature(term)));

	}

	@Override
	public String toString() {
		return atom.toString();
	}

	/**
	 * Unifies <code>atomX</code> and <code>atomY</code>. Returns the unifier if
	 * unification is successful, and <code>null</code> otherwise. This method
	 * is commutative.
	 *
	 * @param atomX
	 *            The left-hand side.
	 * @param atomY
	 *            The right-hand side.
	 * @return A unifier that unifies <code>atomX</code> and <code>atomY</code>
	 *         if one exists, and <code>null</code> otherwise.
	 */
	public static Substitution unify(jpl.Atom atomX, jpl.Atom atomY) {
		return PrologUnify.unify(atomX, atomY);
	}
}
