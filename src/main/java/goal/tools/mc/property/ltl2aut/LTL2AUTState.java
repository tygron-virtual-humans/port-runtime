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
import goal.tools.mc.property.PropState;
import goal.tools.mc.property.ltl.Formula;

/**
 * Represents a state of the property automaton represented by an
 * {@link LTL2AUTProp} object.
 * 
 * @author sungshik
 *
 */
public class LTL2AUTState implements PropState {

	//
	// Private fields
	//

	/**
	 * The set of formulas occurring in this state.
	 */
	private final LMHashSet<Formula> formulas;

	/**
	 * Value for computation of Jenkins' hash. Stored as a class field, because
	 * it is used often, and re-computation is expensive.
	 */
	private int jenkinsB = 0;

	/**
	 * The set of literals occurring in this state. Stored as a class field,
	 * because they are requested often, and re-computation is expensive.
	 */
	private final LMHashSet<Formula> literals;

	//
	// Constructors
	//

	/**
	 * Constructs a new state for the specified set of formulas.
	 */
	public LTL2AUTState(LMHashSet<Formula> formulas) {
		this.formulas = formulas;
		this.literals = Formula.literals(this.formulas);
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		if (o instanceof LTL2AUTState) {
			LTL2AUTState state = (LTL2AUTState) o;
			if (formulas.size() == state.formulas.size()) {
				return formulas.containsAll(state.formulas);
			}
		}
		return false;
	}

	@Override
	public LMHashSet<Formula> getFormulas() {
		return formulas;
	}

	@Override
	public LMHashSet<Formula> getLiterals() {
		return literals;
	}

	@Override
	public int hashCode() {
		return 41;
	}

	@Override
	public int jenkinsB() {

		try {
			if (jenkinsB == 0) {

				/* Add all hash codes of the formulas occurring in this state */
				for (Formula f : formulas) {
					jenkinsB += f.hashCode();
				}
			}
			return jenkinsB;
		}

		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int jenkinsC() {
		return formulas.size();
	}

	@Override
	public byte[] jenkinsKey() {
		return null;
	}

	@Override
	public String toString(int offset) {
		return formulas.toString();
	}

	/**
	 * Converts the specified set of sets of formulas to a set of states.
	 * 
	 * @param sets
	 *            - The sets to be converted.
	 * @return - A set of states corresponding to the specified sets.
	 */
	public static LTL2AUTState[] toStates(LMHashSet<LMHashSet<Formula>> sets) {

		try {
			LTL2AUTState[] states = new LTL2AUTState[sets.size()];
			int i = 0;
			for (LMHashSet<Formula> set : sets) {
				states[i] = (new LTL2AUTState(set));
				i++;
			}
			return states;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return formulas.toString();
	}
}