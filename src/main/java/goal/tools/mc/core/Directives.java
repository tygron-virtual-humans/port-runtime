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

package goal.tools.mc.core;

/**
 * Represents a set of directives that can be passed to the model checker, e.g.
 * to signal that slicing should be turned on.
 * 
 * @author sungshik
 *
 */
public class Directives {

	//
	// Public fields
	//

	/**
	 * Indicates whether the garbage collector should be called each time before
	 * successors are generated during exploration (typical use is for
	 * benchmarking; otherwise it just slows down verification for no reason).
	 * Default is <code>false</code>.
	 */
	public static boolean EXPLICIT_GC = false;

	/**
	 * Indicates whether partial order reduction should be applied. Works only
	 * for stuttering invariant properties, flat agents, and a restricted set of
	 * Prolog built-ins. Default is <code>false</code>.
	 */
	public static boolean POR = false;

	/**
	 * Indicates whether a search tree should be printed during exploration.
	 * Default is <code>false</code>.
	 */
	public static boolean PRINT_TREE = false;

	/**
	 * Indicates whether the property automaton should be constructed on-the-fly
	 * or before exploration commences. Default is <code>true</code>.
	 */
	public static boolean PROP_ON_THE_FLY = true;

	/**
	 * Indicates whether the program automaton should be constructed on-the-fly
	 * or before exploration commences. Default is <code>true</code>.
	 */
	public static boolean PROG_ON_THE_FLY = true;

	/**
	 * Indicates whether slicing should be applied. Works only for safety
	 * properties, flat agents, and a restricted set of Prolog built-ins.
	 * Default is <code>false</code>.
	 */
	public static boolean SLICING = false;

	//
	// Public methods
	//

	/**
	 * Sets a directive to true or false.
	 * 
	 * @param directive
	 *            - The directive to set.
	 * @param value
	 *            - The value to assign.
	 */
	public static void set(String directive, boolean value) {

		try {
			if (directive.equals("EXPLICIT_GC")) {
				EXPLICIT_GC = value;
				return;
			}
			if (directive.equals("POR")) {
				POR = value;
				return;
			}
			if (directive.equals("PRINT_TREE")) {
				PRINT_TREE = value;
				return;
			}
			if (directive.equals("PROP_ON_THE_FLY")) {
				PROP_ON_THE_FLY = value;
				return;
			}
			if (directive.equals("PROG_ON_THE_FLY")) {
				PROG_ON_THE_FLY = value;
				return;
			}
			if (directive.equals("SLICING")) {
				SLICING = value;
				return;
			}

			throw new Exception("Unknown directive " + directive);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the value of directive.
	 * 
	 * @param directive
	 *            - The directive whose value is requested.
	 * @return The value of <code>directive</code>
	 */
	public static boolean get(String directive) {

		try {
			if (directive.equals("EXPLICIT_GC")) {
				return EXPLICIT_GC;
			}
			if (directive.equals("POR")) {
				return POR;
			}
			if (directive.equals("PRINT_TREE")) {
				return PRINT_TREE;
			}
			if (directive.equals("PROP_ON_THE_FLY")) {
				return PROP_ON_THE_FLY;
			}
			if (directive.equals("PROG_ON_THE_FLY")) {
				return PROG_ON_THE_FLY;
			}
			if (directive.equals("SLICING")) {
				return SLICING;
			}
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
