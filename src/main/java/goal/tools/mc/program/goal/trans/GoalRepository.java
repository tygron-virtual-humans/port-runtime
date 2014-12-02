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

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a repository of goals.
 *
 * @author sungshik
 *
 * @param <A>
 *            The type of atom that constitute goals occurring in this
 *            repository.
 * @param <U>
 *            The type of update that the goals occurring in this repository are
 *            represented by in the core interpreter.
 */
public abstract class GoalRepository<A extends Atom, U extends Update> {

	//
	// Protected fields
	//

	/**
	 * The repository of goals.
	 */
	protected List<RepositoryGoal<A, U>> goals = new ArrayList<RepositoryGoal<A, U>>();

	//
	// Abstract methods
	//

	/**
	 * Creates a goal that fits in this repository.
	 *
	 * @param u
	 *            - The update representing the goal in the interpreter.
	 * @param knowledge
	 *            - The knowledge base associated with this goal.
	 */
	public abstract RepositoryGoal<A, U> createRepositoryGoal(U u,
			Collection<DatabaseFormula> knowledge);

	//
	// Public methods
	//

	/**
	 * Adds a goal to this repository.
	 *
	 * @param goal
	 *            - The goal to be added.
	 */
	public void add(RepositoryGoal<A, U> goal) {
		goals.add(goal);
	}

	/**
	 * Gets all goals in this repository.
	 *
	 * @return All goals.
	 */
	public List<RepositoryGoal<A, U>> getGoals() {
		return goals;
	}
}
