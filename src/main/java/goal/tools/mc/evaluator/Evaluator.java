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

package goal.tools.mc.evaluator;

import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.property.ltl.Formula;

/**
 * Represents the evaluator component of a model checker.
 * 
 * @author sungshik
 *
 */
public interface Evaluator {

	/**
	 * Determines whether the specified state entails the specified formula.
	 * 
	 * @param q
	 *            - The state to evaluate the formula in.
	 * @param formula
	 *            - The formula to check entailment for.
	 * @return <code>true</code> if <code>q</code> entails <code>formula</code>;
	 *         <code>false</code> otherwise.
	 */
	boolean entails(State q, Formula formula);

	/**
	 * Determines whether all formulas in the specified set are entailed by the
	 * specified state.
	 * 
	 * @param q
	 *            - The state to evaluate the formulas in.
	 * @param formulas
	 *            - The formulas to check entailment for.
	 * @return <code>true</code> if <code>q</code> entails all formulas in the
	 *         set; <code>false</code> otherwise.
	 */
	boolean entailsAll(State q, LMHashSet<Formula> formulas);

	/**
	 * Gets the number of distinct states that have been evaluated during the
	 * model checking run up to the point of invocation.
	 * 
	 * @return The number of distinct states.
	 */
	int getDistinctEvaluated();
}
