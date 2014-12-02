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

package goal.tools.mc.property.ltl2aut;

import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.property.ltl.Conjunction;
import goal.tools.mc.property.ltl.Disjunction;
import goal.tools.mc.property.ltl.False;
import goal.tools.mc.property.ltl.Formula;
import goal.tools.mc.property.ltl.Negation;
import goal.tools.mc.property.ltl.Next;
import goal.tools.mc.property.ltl.Proposition;
import goal.tools.mc.property.ltl.Release;
import goal.tools.mc.property.ltl.True;
import goal.tools.mc.property.ltl.Until;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a single (public) methods for cover computation, as described in
 * Daniele et al. (1999) for LTL2AUT.
 * 
 * @author sungshik
 * 
 */
public class LTL2AUTCover {

	//
	// Public methods
	//

	/**
	 * Computes the cover of the specified set of formulas.
	 * 
	 * @param toCover
	 *            - Set of formulas for which the cover should be computed.
	 * @return The cover.
	 */
	public static LMHashSet<LMHashSet<Formula>> cover(LMHashSet<Formula> toCover) {

		return cover(toCover, new LMHashSet<Formula>(),
				new LMHashSet<LMHashSet<Formula>>());
	}

	//
	// Private methods
	//

	/**
	 * Determines whether the specified formula contradicts with formulas in the
	 * specified set, according to Daniele et al. (1999).
	 * 
	 * @param f
	 *            - The formula to check contradiction for.
	 * @param x
	 *            - The set with which the formula may contradict.
	 * @return <code>true</code> if there is a contradiction; <code>false</code>
	 *         otherwise.
	 */
	private static boolean contradiction(Formula f, LMHashSet<Formula> x) {
		return derivable(new Negation(f).nnf(), x);
	}

	/**
	 * Computes the cover of a set of formulas according to the algorithm for
	 * cover computation by Daniele et al. (1999).
	 * 
	 * @param toCover
	 *            - Formulas that still need be covered.
	 * @param current
	 *            - Formulas for which a cover is currently being computed.
	 * @param cover
	 *            - The cover computed so far.
	 * @return The cover.
	 */
	private static LMHashSet<LMHashSet<Formula>> cover(
			LMHashSet<Formula> toCover, LMHashSet<Formula> current,
			LMHashSet<LMHashSet<Formula>> cover) {

		try {

			/* If there is nothing left to cover, return */
			if (toCover.isEmpty()) {
				if (!current.contains(new False())) {
					cover.add(current);
				}
				return cover;
			}

			/* If there is a formula in toCover, compute its cover */
			else {

				Formula f = toCover.iterator().next();
				toCover.remove(f);
				LMHashSet<Formula> x = new LMHashSet<Formula>(toCover);
				x.addAll(current);
				x.add(new True());

				/*
				 * Return if the current formula is involved in a contradiction
				 * with formulas in x
				 */
				if (LTL2AUTCover.contradiction(f, x)) {
					return cover;
				} else {

					/*
					 * Omit the current formula if it is redundant with respect
					 * to elements in x
					 */
					if (LTL2AUTCover.redundant(f, x)) {
						return cover(toCover, current, cover);
					} else {

						/*
						 * If the current formula is elementary, add it to
						 * current, and resume computation
						 */
						if (LTL2AUTCover.elementary(f)) {
							if (!(f instanceof Proposition && f
									.equals(new True()))) {
								current.add(f);
							}
							return cover(toCover, current, cover);
						} else {

							/*
							 * Process the current formula if it is not already
							 * elementary, by decomposing it in elementary
							 * formulas.
							 */
							for (LMHashSet<Formula> elem : LTL2AUTCover
									.decompose(f)) {

								/* Copy toCover and current */
								LMHashSet<Formula> toCoverCopy = new LMHashSet<Formula>(
										toCover);
								LMHashSet<Formula> currentCopy = new LMHashSet<Formula>(
										current);

								/* Remove all current formulas from elem */
								elem.removeAll(current);

								/*
								 * Add the remaining formulas in elem to
								 * toCoverCopy
								 */
								toCoverCopy.addAll(elem);

								/* Recursively compute cover */
								cover = cover(toCoverCopy, currentCopy, cover);
							}
							return cover;
						}
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Decompose the specified formula into elementary formulas according to
	 * Daniele et al. (1999).
	 * 
	 * @param f
	 *            - The formula to decompose.
	 * @return A list of elementary formulas in which the specified formula is
	 *         decomposed.
	 */
	private static List<LMHashSet<Formula>> decompose(Formula f) {

		try {

			/* Return variable */
			ArrayList<LMHashSet<Formula>> decomposition = new ArrayList<LMHashSet<Formula>>();

			/*
			 * If f is a conjunction, there is a single set of elementary
			 * formulas that contains all conjuncts
			 */
			if (f instanceof Conjunction) {
				decomposition.add(((Conjunction) f).getArgs());
			}

			/*
			 * If f is a disjunction, every set of elementary formulas consists
			 * of a single disjunct
			 */
			if (f instanceof Disjunction) {
				for (Formula disjunct : ((Disjunction) f).getArgs()) {
					LMHashSet<Formula> elem = new LMHashSet<Formula>();
					elem.add(disjunct);
					decomposition.add(elem);
				}
			}

			/*
			 * If f is an until or release formula, there are always exactly two
			 * sets of elementary formulas; these are initialized first
			 */
			LMHashSet<Formula> elem1 = new LMHashSet<Formula>();
			LMHashSet<Formula> elem2 = new LMHashSet<Formula>();

			/*
			 * If f is an until formula, one set of elementary formulas consists
			 * of the formula's right argument, while the other set of
			 * elementary formulas consists of the formula's left argument and
			 * the formula itself as argument of a next formula
			 */
			if (f instanceof Until) {
				Until fUntil = (Until) f;
				elem1.add(fUntil.getRightArg());
				elem2.add(fUntil.getLeftArg());
				elem2.add(new Next(fUntil));
				decomposition.add(elem1);
				decomposition.add(elem2);
			}

			/*
			 * If f is a release formula, one set of elementary formulas
			 * consists of the formula's two arguments, while the other set of
			 * elementary formulas consists of the formula's right argument and
			 * the formula itself as argument of a next formula
			 */
			if (f instanceof Release) {
				Release fRelease = (Release) f;
				elem1.add(fRelease.getLeftArg());
				elem1.add(fRelease.getRightArg());
				elem2.add(fRelease.getRightArg());
				elem2.add(new Next(fRelease));
				decomposition.add(elem1);
				decomposition.add(elem2);
			}

			/* Return */
			return decomposition;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Determines whether the specified formula is derivable from the specified
	 * set of formulas by syntactic analysis, according to Daniele et al.
	 * (1999).
	 * 
	 * @param f
	 *            - The formula to check whether it is derivable for.
	 * @param x
	 *            - The set of formulas that may or may not derive the specified
	 *            formula.
	 * @return - <code>true</code> if the specified formula is derivable;
	 *         <code>false</code> otherwise.
	 */
	private static boolean derivable(Formula f, LMHashSet<Formula> x) {

		try {

			/* If f is elementary, check if it is contained in x */
			if (elementary(f)) {
				return x.contains(f);
			}

			/*
			 * If f is not elementary, check if its decomposition is (partly)
			 * derivable
			 */
			else {
				for (LMHashSet<Formula> elem : decompose(f)) {
					boolean derivable = true;

					/*
					 * If all formulas in a set of elementary formulas in the
					 * decomposition of f are derivable, then f is derivable;
					 * otherwise not
					 */
					for (Formula e : elem) {
						if (!derivable(e, x)) {
							derivable = false;
							break;
						}
					}
					if (derivable) {
						return true;
					}
				}

				/* If this point is ever reached, f is underivable */
				return false;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Determines whether the specified formula is redundant due to formulas in
	 * the specified set, according to Daniele et al. (1999).
	 * 
	 * @param f
	 *            - The formula to check redundancy for.
	 * @param x
	 *            - The set in which the formula may be redundant.
	 * @return <code>true</code> if the formula is redundant; <code>false</code>
	 *         otherwise.
	 */
	private static boolean redundant(Formula f, LMHashSet<Formula> x) {
		boolean redundant = derivable(f, x);
		if (redundant && f instanceof Until) {
			redundant = derivable(((Until) f).getRightArg(), x);
		}
		return redundant;
	}

	/**
	 * Determines whether the specified formula is an elementary formula,
	 * according to Daniele et al. (1999).
	 * 
	 * @param f
	 *            - The formula to check whether it is elementary for.
	 * @return <code>true</code> if the formula is elementary;
	 *         <code>false</code> otherwise.
	 */
	private static boolean elementary(Formula f) {
		return !(f instanceof Conjunction || f instanceof Disjunction
				|| f instanceof Until || f instanceof Release);
	}
}
