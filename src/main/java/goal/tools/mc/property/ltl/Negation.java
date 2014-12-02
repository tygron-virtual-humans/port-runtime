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
 * Represents an LTL negation.
 * 
 * @author sungshik
 * 
 */
public final class Negation extends Formula {

	//
	// Private fields
	//

	/**
	 * The argument of this negation, i.e. the negated formula.
	 */
	private final Formula arg;

	//
	// Constructors
	//

	/**
	 * Constructs a negation of the specified formula.
	 * 
	 * @param arg
	 *            - The formula to be negated.
	 */
	public Negation(Formula arg) {
		this.arg = arg;
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		return o instanceof Negation && arg.equals(((Negation) o).arg);
	}

	@Override
	public int hashCode() {
		return 3;
	}

	/**
	 * Get the argument of this negation.
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
			return " ~ " + arg.toString();
		}
		return "! { " + arg.toString() + " }";
	}
}
