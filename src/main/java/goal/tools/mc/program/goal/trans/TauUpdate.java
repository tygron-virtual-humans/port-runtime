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

package goal.tools.mc.program.goal.trans;

import goal.tools.mc.core.lmhashset.LMHashSet;

/**
 * Represents updates that are brought about by execution of a step, transition,
 * or transition class.
 *
 * @author sungshik
 *
 * @param <A>
 *            - The type of atom associated with the KRT that the agent in which
 *            the updates are brought about uses.
 */
public abstract class TauUpdate<A> {

	//
	// Enums
	//

	/**
	 * Represents the type of update that are brought about: ADD represents
	 * addition, DEL represents deletion.
	 */
	public enum Op {
		ADD, DEL
	}

	//
	// Private fields
	//

	/**
	 * The goals that are deleted.
	 */
	private final LMHashSet<LMHashSet<A>> gammaMin = new LMHashSet<LMHashSet<A>>();

	/**
	 * The goals that are added.
	 */
	private final LMHashSet<LMHashSet<A>> gammaPlus = new LMHashSet<LMHashSet<A>>();

	/**
	 * The beliefs that are deleted.
	 */
	private final LMHashSet<A> sigmaMin = new LMHashSet<A>();

	/**
	 * The beliefs that are added.
	 */
	private final LMHashSet<A> sigmaPlus = new LMHashSet<A>();

	//
	// Abstract methods
	//

	/**
	 * Determines whether these updates are not in conflict with the specified
	 * updates. A conflict arises if the same belief is both added and removed,
	 * or if the same goal is both added and removed.
	 */
	public abstract boolean areConflicting(TauUpdate<A> updateTuple);

	//
	// Public methods
	//

	/**
	 * Adds a goal to the set of added or deleted goals depending on the
	 * specified operation.
	 *
	 * @param op
	 *            - The operation that is performed on the specified goal.
	 * @param goal
	 *            - The goal that is updated.
	 */
	public void addGoal(Op op, LMHashSet<A> goal) {

		try {
			switch (op) {
			case ADD:
				gammaPlus.add(goal);
				break;
			case DEL:
				gammaMin.add(goal);
				break;
			default:
				throw new Exception();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a belief to the set of added or deleted beliefs depending on the
	 * specified operation.
	 *
	 * @param op
	 *            - The operation that is performed on the specified belief.
	 * @param belief
	 *            - The belief that is updated.
	 */
	public void addBelief(Op op, A belief) {

		try {
			switch (op) {
			case ADD:
				sigmaPlus.add(belief);
				break;
			case DEL:
				sigmaMin.add(belief);
				break;
			default:
				throw new Exception();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the deleted goals.
	 *
	 * @return The deleted goals.
	 */
	public LMHashSet<LMHashSet<A>> getGammaMin() {
		return gammaMin;
	}

	/**
	 * Gets the added goals.
	 *
	 * @return The added goals.
	 */
	public LMHashSet<LMHashSet<A>> getGammaPlus() {
		return gammaPlus;
	}

	/**
	 * Gets the deleted beliefs.
	 *
	 * @return The deleted beliefs.
	 */
	public LMHashSet<A> getSigmaMin() {
		return sigmaMin;
	}

	/**
	 * Gets the added beliefs.
	 *
	 * @return The added beliefs.
	 */
	public LMHashSet<A> getSigmaPlus() {
		return sigmaPlus;
	}

	@Override
	public String toString() {
		String string = "";
		string += "Sigma^+ = " + sigmaPlus.toString() + " , ";
		string += "Sigma^- = " + sigmaMin.toString() + " , ";
		string += "Gamma^+ = " + gammaPlus.toString() + " , ";
		string += "Gamma^- = " + gammaMin.toString();
		return string;
	}
}