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
import goal.tools.mc.core.lmhashset.LMHashSet;

import java.util.ArrayList;
import java.util.List;

import swiprolog3.language.JPLUtils;
import swiprolog3.language.PrologSubstitution;

/**
 * Provides methods for determining whether two atoms are equal in a possibly
 * over-approximative fashion. Three types of equality are distinguished:
 * <ul>
 * <li>Functor equality: two atoms are functor equal if their functor is equal.
 * <li>Signature equality: two atoms are signature equal if their signature is
 * equal.
 * <li>Theta equality: two atoms are theta equal if they can be unified.
 * </ul>
 * Note that if two atoms are theta equal, they are signature equal, and that if
 * two atoms are signature equal, they are functor equal.
 *
 * @author sungshik
 *
 */
public class PrologEquals {

	//
	// Enums
	//

	/**
	 * Defines the three types of equality.
	 */
	private static enum PrologStructType {
		FUNCTOR, SIGNATURE, THETA;
	}

	//
	// Private fields
	//

	/**
	 * The type of equality to be used.
	 */
	private static PrologStructType type = PrologStructType.THETA;

	//
	// Public methods
	//

	/**
	 * Determines whether <code>atomX</code> and <code>atomY</code> are equal
	 * under the type of equality as specified by {@link #type}. If so, this
	 * method returns the substitution that unifies <code>atomX</code> and
	 * <code>atomY</code>, where the empty substitution may indicate either that
	 * the two atoms are ground and equal, or that the equality specified by
	 * {@link type} does not require unification of the atoms to be deemed equal
	 * (hence it over-approximates the equality relation). If the atoms are not
	 * deemed equal, this method returns <code>null</code>.
	 *
	 * @param atomX
	 *            - The left-hand side (though the method is commutative).
	 * @param atomY
	 *            - The right-hand side (though the method is commutative).
	 * @return <code>null</code> if the atoms are not deemed equal; a
	 *         {@link Substitution} otherwise.
	 */
	public static Substitution areEqual(jpl.Atom atomX, jpl.Atom atomY) {

		try {
			switch (type) {
			case FUNCTOR:
				return areFunctorEqual(atomX, atomY);
			case SIGNATURE:
				return areSignatureEqual(atomX, atomY);
			case THETA:
				return areThetaEqual(atomX, atomY);
			default:
				return null;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Determines whether <code>setX</code> contains an element that is equal,
	 * according to {@link #areEqual}, to an element in <code>setY</code>. If no
	 * common element is found, this method returns an empty list. Otherwise,
	 * the list contains a {@link Substitution} for each two atoms in <code>setX
	 * </code> and <code>setY</code> that are deemed equal.
	 *
	 * @param setX
	 *            - The left-hand side (though the method is commutative).
	 * @param setY
	 *            - The right-hand side (though the method is commutative).
	 * @return An empty {@link ArrayList} if <code>setX</code> and <code>setY
	 * </code> do not have a common element, and an {@link ArrayList} containing
	 *         at least one {@link Substitution} otherwise.
	 */
	public static List<Substitution> areDisjoint(LMHashSet<jpl.Atom> setX,
			LMHashSet<jpl.Atom> setY) {

		try {
			ArrayList<Substitution> thetas = new ArrayList<Substitution>();
			Substitution theta;
			for (jpl.Atom atomX : setX) {
				for (jpl.Atom atomY : setY) {
					if ((theta = areEqual(atomX, atomY)) != null) {
						thetas.add(theta);
					}
				}
			}
			return thetas;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Set the required type of equality to functor equality.
	 */
	public static void useFunctorEquality() {
		type = PrologStructType.FUNCTOR;
	}

	/**
	 * Set the required type of equality to signature equality.
	 */
	public static void useSignatureEquality() {
		type = PrologStructType.SIGNATURE;
	}

	/**
	 * Set the required type of equality to theta equality.
	 */
	public static void useThetaEquality() {
		type = PrologStructType.THETA;
	}

	//
	// Private methods
	//

	/**
	 * Determines whether two atoms are functor equal.
	 *
	 * @return An empty substitution if the atoms are functor equal;
	 *         <code>null</code> otherwise.
	 */
	private static Substitution areFunctorEqual(jpl.Atom atomX, jpl.Atom atomY) {
		return JPLUtils.getSignature(atomX)
				.equals(JPLUtils.getSignature(atomY)) ? new PrologSubstitution()
				: null;
	}

	/**
	 * Determines whether two atoms are signature equal.
	 *
	 * @return An empty substitution if the atoms are signature equal;
	 *         <code>null</code> otherwise.
	 */
	private static Substitution areSignatureEqual(jpl.Atom atomX, jpl.Atom atomY) {
		return JPLUtils.getSignature(atomX)
				.equals(JPLUtils.getSignature(atomY)) ? new PrologSubstitution()
				: null;
	}

	/**
	 * Determines whether two atoms are theta equal.
	 *
	 * @return A substitution that unifies the atoms if they are theta equal;
	 *         <code>null</code> otherwise.
	 */
	private static Substitution areThetaEqual(jpl.Atom atomX, jpl.Atom atomY) {
		return PrologAtom.unify(atomX, atomY);
	}
}
