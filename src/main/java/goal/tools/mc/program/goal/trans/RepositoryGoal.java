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

import goal.core.kr.language.Update;
import goal.tools.mc.core.lmhashset.LMHashSet;

/**
 * Represents a goal in a goal repository as implemented by subclasses of
 * {@link GoalRepository}. The convenience of this class lies in the
 * representation of goals: rather than as an update, this class represents as
 * sets of atoms.
 * 
 * @author sungshik
 *
 * @param <A>
 *            - The type of atom associated with the KRT that is used with the
 *            goals that instances of this class represent.
 * @param <U>
 *            - The type of update associated with the KRT that is used with the
 *            goals that instances of this class represent.
 */
public abstract class RepositoryGoal<A extends Atom, U extends Update> {

	//
	// Protected fields
	//

	/**
	 * The goal as represented by the interpreter.
	 */
	protected U goal;

	/**
	 * The goal as a set of atoms.
	 */
	protected LMHashSet<A> atoms;

	//
	// Constructor
	//

	/**
	 * Constructs a goal for use in a repository given the specified goal.
	 * 
	 * @param goal
	 *            - The goal.
	 */
	public RepositoryGoal(U goal) {
		this.goal = goal;
		initAtoms();
	}

	//
	// Abstract methods
	//

	/**
	 * Initializes the set of atoms, which is KRT specific, hence an abstract
	 * method.
	 */
	protected abstract void initAtoms();

	//
	// Public methods
	//

	/**
	 * Gets the atoms that occur in this goal.
	 * 
	 * @return The atoms.
	 */
	public LMHashSet<A> getAtoms() {
		return atoms;
	}

	/**
	 * Gets the goal in its original form, i.e. as an update.
	 * 
	 * @return The goal.
	 */
	public U getGoal() {
		return goal;
	}
}
