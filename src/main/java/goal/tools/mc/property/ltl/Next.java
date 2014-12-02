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
 * Represents an LTL next formula.
 * 
 * @author sungshik
 * 
 */
public final class Next extends Formula {

	//
	// Private fields
	//

	/**
	 * The argument of this next formula.
	 */
	private final Formula arg;

	//
	// Constructors
	//

	/**
	 * Constructs a next formula with the specified formula as argument.
	 * 
	 * @param arg
	 *            - The argument.
	 */
	public Next(Formula arg) {
		this.arg = arg;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Next && arg.equals(((Next) o).arg);
	}

	@Override
	public int hashCode() {
		return 6;
	}

	//
	// Public methods
	//

	/**
	 * Get the argument of this next formula.
	 * 
	 * @return The argument.
	 */
	public Formula getArg() {
		return arg;
	}

	@Override
	public LMHashSet<Formula> getArgs() {
		LMHashSet<Formula> arguments = new LMHashSet<Formula>();
		arguments.add(arg);
		return arguments;
	}

	@Override
	public String toString() {
		if (arg instanceof Proposition || arg instanceof Negation
				|| arg instanceof Next) {
			return "X " + arg.toString();
		}
		return "X [ " + arg.toString() + " ]";
	}
}
