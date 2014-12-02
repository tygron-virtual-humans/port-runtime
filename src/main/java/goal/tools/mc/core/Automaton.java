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
 * Represents the structure of an automaton. Implementations may decide how to
 * store the automaton internally.
 * 
 * @author sungshik
 *
 */
public interface Automaton {

	/**
	 * Generates the entire state space of the automaton. Although
	 * implementations of this interface may decide how the automaton is
	 * represented and stored internally, it is assumed that some internal
	 * storage facility is present (otherwise, this method would not make much
	 * sense).
	 */
	void generate();

	/**
	 * Gets the initial state of the automaton.
	 * 
	 * @return The initial state of the automaton.
	 */
	State getInitial();

	/**
	 * Gets the number of states for which successors have been defined (this is
	 * not necessarily equal to the number of states that have already been
	 * identified).
	 * 
	 * @return The number of states for which successors have been defined.
	 */
	int getSize();

	/**
	 * Gets the successors of <code>q</code>.
	 * 
	 * @param q
	 *            - The state to get the successors of.
	 * @return The successors of <code>q</code>.
	 */
	State[] getSuccessors(State q);
}
