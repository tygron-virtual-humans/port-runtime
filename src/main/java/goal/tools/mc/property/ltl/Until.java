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

package goal.tools.mc.property.ltl;

import goal.tools.mc.core.lmhashset.LMHashSet;

/**
 * Represents an LTL until formula.
 * 
 * @author sungshik
 * 
 */
public final class Until extends Formula {

	//
	// Private fields
	//

	/**
	 * Represent the left and right arguments of this formula.
	 */
	private final Formula leftArg, rightArg;

	//
	// Constructors
	//

	/**
	 * Constructs an until formula according to the specified left and right
	 * arguments.
	 * 
	 * @param leftArg
	 *            - The left argument.
	 * @param rightArg
	 *            - The right argument.
	 */
	public Until(Formula leftArg, Formula rightArg) {
		this.leftArg = leftArg;
		this.rightArg = rightArg;
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		return o instanceof Until && leftArg.equals(((Until) o).leftArg)
				&& rightArg.equals(((Until) o).rightArg);
	}

	@Override
	public int hashCode() {
		return 7;
	}

	@Override
	public LMHashSet<Formula> getArgs() {
		LMHashSet<Formula> arguments = new LMHashSet<Formula>();
		arguments.add(leftArg);
		arguments.add(rightArg);
		return arguments;
	}

	/**
	 * Get the left argument of this formula.
	 * 
	 * @return The left argument.
	 */
	public Formula getLeftArg() {
		return leftArg;
	}

	/**
	 * Get the right argument of this formula.
	 * 
	 * @return The right argument.
	 */
	public Formula getRightArg() {
		return rightArg;
	}

	@Override
	public String toString() {
		String string = "";
		if (leftArg instanceof Conjunction || leftArg instanceof Disjunction
				|| leftArg instanceof Until || leftArg instanceof Release) {
			string += "[ " + leftArg.toString() + " ]";
		} else {
			string += leftArg.toString();
		}
		string += " U ";
		if (rightArg instanceof Conjunction || rightArg instanceof Disjunction
				|| rightArg instanceof Until || rightArg instanceof Release) {
			string += "[ " + rightArg.toString() + " ]";
		} else {
			string += rightArg.toString();
		}
		return string;
	}
}
