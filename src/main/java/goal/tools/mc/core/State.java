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
 * Represents a state of an automaton as represented by {@link Automaton}.
 * Implementations of this interface must define three "Jenkins" methods such
 * that a {@link State} state can efficiently be hashed with
 * {@link Jenkins#apply}.
 * 
 * @author sungshik
 *
 */
public interface State {

	/**
	 * Gets the seed for the second hash code as computed by
	 * {@link Jenkins#apply}.
	 * 
	 * @return The seed.
	 */
	int jenkinsB();

	/**
	 * Gets the seed for the first hash code as computed by
	 * {@link Jenkins#apply}.
	 * 
	 * @return The seed.
	 */
	int jenkinsC();

	/**
	 * Gets the key of the object for which the hash code need be computed by
	 * {@link Jenkins#apply} as an array of bytes.
	 * 
	 * @return The key.
	 */
	byte[] jenkinsKey();

	/**
	 * Returns the string representation of this state.
	 * 
	 * @param offset
	 *            - The margin, i.e. number of white spaces, that the string
	 *            must contain.
	 * @return The string.
	 */
	String toString(int offset);
}
