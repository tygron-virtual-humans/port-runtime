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

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.mentalstate.BASETYPE;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.mc.core.lmhashset.LMHashSet;

import java.util.LinkedHashSet;

import jpl.Term;
import swiprolog3.engines.SWIPrologDatabase;
import swiprolog3.engines.SWIPrologLanguage;
import swiprolog3.language.JPLUtils;
import swiprolog3.language.PrologQuery;
import swiprolog3.language.PrologSubstitution;
import swiprolog3.language.PrologTerm;
import swiprolog3.language.VariableTerm;

/**
 * Provides a unification method for unifying two Prolog terms. One can choose
 * between two different unification algorithms: one is the algorithm of SWI
 * Prolog and works by dispatching the query directly to SWI; the other is a
 * basic unification algorithm without optimizations that is implemented
 * entirely in Java. The latter appears much faster and is hence the default.
 *
 * @author sungshik
 *
 */
public class PrologUnify {

	//
	// Public fields
	//

	/**
	 * Flag indicating whether unification queries must be dispatched to the SWI
	 * Prolog engine, or handled by the own Java implementation of unification.
	 */
	public static boolean jUnify = true;

	//
	// Private fields
	//

	/**
	 * Empty debugger for passing with functions (without being actually used).
	 */
	private static SteppingDebugger debugger = new SteppingDebugger("unify", null);

	/**
	 * Empty database to be passed with the unification query.
	 */
	private static SWIPrologDatabase database;

	/**
	 * Initializes {@link #theory}, {@link #database}, and {@link #engine}.
	 * These could not be initialized upon declaration, because their
	 * constructors may throw exceptions.
	 */
	static {
		try {
			database = (SWIPrologDatabase) SWIPrologLanguage.getInstance()
					.makeDatabase(BASETYPE.BELIEFBASE,
							new LinkedHashSet<DatabaseFormula>(), "mc");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// Public methods
	//

	/**
	 * Unifies the two specified terms, if possible.
	 *
	 * @param aTerm
	 *            - The left-hand side of the unification.
	 * @param anotherTerm
	 *            - The right-hand side of the unification.
	 * @return A unifier if the terms can be unified (may be empty);
	 *         <code>null</code> otherwise.
	 */
	public static Substitution unify(jpl.Term aTerm, jpl.Term anotherTerm) {

		try {

			// long time = System.nanoTime();
			// System.out.print(jUnify(aTerm, anotherTerm, new Substitution()) +
			// "  ");
			// System.out.print( (System.nanoTime() - time) + " v.s. ");
			// PrologTerm qTerm = new FuncTerm("=", aTerm, anotherTerm);
			// Iterable<Substitution> thetas = query(qTerm);
			// System.out.println( (System.nanoTime() - time) + "  " +
			// (thetas.iterator().hasNext()
			// ? thetas.iterator().next() : null) + "  " + qTerm);
			// return thetas.iterator().hasNext()
			// ? thetas.iterator().next() : null;

			/* Switch between java unification and prolog unification */
			if (jUnify) {
				return jUnify(aTerm, anotherTerm, new PrologSubstitution());
			} else {

				/* Build and perform query */
				PrologTerm qTerm = new PrologTerm(JPLUtils.createCompound("=",
						aTerm, anotherTerm), null);
				Iterable<Substitution> thetas = query(qTerm);

				/* Return substitution, or null otherwise */
				return thetas.iterator().hasNext() ? thetas.iterator().next()
						: null;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//
	// Private methods
	//

	/**
	 * Determines whether the specified term contains anonymous variables (or
	 * maybe is one itself).
	 *
	 * @param term
	 *            - The term to check anonymous variables for.
	 * @return <code>true</code> if the specified term does not contain
	 *         anonymous variables; <code>false</code> otherwise.
	 */
	private static boolean containsAnonymousVar(PrologTerm term) {

		try {
			if (term instanceof VariableTerm) {
				return term.toString().startsWith("_");
			}
			if (term.getTerm().isAtom()) {
				return false;
			}
			if (term.getTerm().isCompound()) {
				for (Var var : term.getFreeVar()) {
					if (var.isAnonymous()) {
						return true;
					}
				}

				return false;
			}
			throw new Exception("Unknown term " + term);
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Straightforward implementation of the unification algorithm as given in
	 * Russell and Norvig (2003). Please see Fig. 9.1. in Sect. 9.2. for
	 * details.
	 */
	private static Substitution jUnify(jpl.Term x, jpl.Term y,
			Substitution theta) {

		try {
			if (theta == null) {
				return null;
			} else if (x.equals(y)) {
				return theta;
			} else if (x.isVariable()) {
				return jUnifyVar((jpl.Variable) x, y, theta);
			} else if (y.isVariable()) {
				return jUnifyVar((jpl.Variable) y, x, theta);
			} else if (x.isCompound() && y.isCompound()) {
				if (JPLUtils.getSignature(x).equals(JPLUtils.getSignature(y))) {
					Term[] argsX = x.args();
					Term[] argsY = y.args();
					if (argsX.length != argsY.length) {
						return null;
					}
					for (int i = 0; i < argsX.length; i++) {
						theta = (Substitution) JPLUtils.mgu(argsX[i], argsY[i]);
						if (theta == null) {
							return null;
						}
					}
					return theta;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Straightforward implementation of the unification algorithm as given in
	 * Russell and Norvig (2003). Please see Fig. 9.1. in Sect. 9.2. for
	 * details.
	 */
	private static Substitution jUnifyVar(jpl.Variable var, jpl.Term x,
			Substitution theta) {

		try {
			jpl.Term val = (jpl.Term) theta.get((Var) var);
			if (val != null) {
				return jUnify(val, x, theta);
			}
			if (x.isVariable()) {
				val = (jpl.Term) theta.get((Var) x);
				if (val != null) {
					return jUnify(var, val, theta);
				}
			}
			if (JPLUtils.getFreeVar(x).contains(var)) {
				return null;
			}
			theta.addBinding((Var) var, new PrologTerm(x, null));
			return theta;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Dispatches the specified term to the SWI Prolog engine as a query.
	 *
	 * @param term
	 *            - The query to be dispatched.
	 * @return A set of substitutions that make the term derivable, which is
	 *         empty if the term is underivable.
	 */
	private static LMHashSet<Substitution> query(PrologTerm term) {

		try {
			PrologQuery query = new PrologQuery(term.getTerm(), null);
			LMHashSet<Substitution> thetas = new LMHashSet<Substitution>();

			/*
			 * Fire query and filter anonymous variables from the returned
			 * substitutions
			 */
			for (Substitution theta : database.query(query)) {
				Substitution filtered = new PrologSubstitution();
				for (Var var : theta.getVariables()) {
					if (!containsAnonymousVar((PrologTerm) theta.get(var))) {
						filtered.addBinding(var, theta.get(var));
					}
				}

				/* Add the filtered substitution */
				thetas.add(filtered);
			}

			/* Return */
			return thetas;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
