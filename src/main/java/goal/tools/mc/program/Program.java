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

package goal.tools.mc.program;

import goal.tools.mc.core.Automaton;
import goal.tools.mc.core.State;

/**
 * Represents the program component of a model checker, which basically is a
 * representation of an automaton (which is why this interface extends
 * {@link Automaton}).
 * 
 * @author sungshik
 *
 */
public interface Program extends Automaton {

	/**
	 * Gets the action (as a string of text) that need be performed to get from
	 * the specified state to its successor.
	 * 
	 * @param state
	 *            - The state in which the action to be determined need be
	 *            performed.
	 * @param successorState
	 *            - The state that results from performing the action to be
	 *            determined.
	 * @return A string representation of the action if the successor can be
	 *         reached; <tt>null</tt> otherwise.
	 */
	String getPerformedAction(State state, State successorState);

	/**
	 * Slices the program. Assumes that all necessary information to compute the
	 * slice is already present (for instance as class fields), hence no
	 * parameters are passed.
	 */
	void slice();

	/**
	 * Gets the current memory consumption of computation of the program that is
	 * not Java-related, i.e. does not involve the Java heap. For instance, if
	 * Prolog is in any way during successor generation, this method should
	 * return Prolog's memory demands.
	 * 
	 * @return The memory consumption.
	 */
	long getMemoryConsumption();
}
