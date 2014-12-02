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

package goal.tools.mc.program.goal;

import goal.core.kr.language.Update;

/**
 * Represents a goal in a GOAL conversion universe.
 * 
 * @author sungshik
 *
 */
public class GOALCE_GoalAtDepth implements GOALConversionElement {

	//
	// Public fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = 4485661910514917978L;

	/**
	 * The depth at which this goal occurs in the attention stack.
	 */
	public final int depth;

	/**
	 * The actual representation of the goal.
	 */
	public final Update goal;

	//
	// Constructors
	//

	/**
	 * Constructs a goal according to the actual goal and specified depth.
	 * 
	 * @param goal
	 *            - The actual goal.
	 * @param depth
	 *            - The depth at which the goal occurs in the attention stack.
	 */
	public GOALCE_GoalAtDepth(Update goal, int depth) {
		this.goal = goal;
		this.depth = depth;
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof GOALCE_GoalAtDepth) {
			GOALCE_GoalAtDepth other = (GOALCE_GoalAtDepth) o;
			return depth == other.depth && goal.equals(other.goal);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return goal.hashCode();
	}

	@Override
	public String toString() {
		return "goal@" + depth + ".[" + goal.toString() + "]";
	}
}