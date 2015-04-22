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

import goal.core.mentalstate.GoalBase;

/**
 * Represents a focus in a GOAL conversion universe.
 *
 * @author sungshik
 *
 */
public class GOALCE_FocusAtDepth implements GOALConversionElement {

	/**
	 *
	 */
	private static final long serialVersionUID = -4703940893364729485L;

	/**
	 * The depth at which this focus occurs in the attention stack.
	 */
	public final int depth;

	/**
	 * The actual representation of the focus (i.e. simply its name).
	 */
	public final GoalBase focus;

	//
	// Constructors
	//

	/**
	 * Constructs a focus according to the actual focus and specified depth.
	 *
	 * @param focus
	 *            - The actual focus.
	 * @param depth
	 *            - The depth at which the focus occurs in the attention stack.
	 */
	public GOALCE_FocusAtDepth(GoalBase focus, int depth) {
		this.focus = focus;
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
		if (o instanceof GOALCE_FocusAtDepth) {
			GOALCE_FocusAtDepth other = (GOALCE_FocusAtDepth) o;
			if (focus == null || other.focus == null) {
				return focus == null && other.focus == null;
			}
			// FIXME: checking equality of focus names is not enough.
			// they may be different instances of same module.
			return depth == other.depth
					&& focus.getName().equals(other.focus.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (focus != null ? focus.getName().hashCode() : 0);
	}

	@Override
	public String toString() {
		return "focus@" + depth + "." + focus;
	}
}