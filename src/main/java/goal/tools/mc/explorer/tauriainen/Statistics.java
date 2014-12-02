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

package goal.tools.mc.explorer.tauriainen;

/**
 * Maintains the statistics of an exploration run by a {@link Tauriainen}
 * object.
 * 
 * @author sungshik
 *
 */
public class Statistics {

	//
	// Fields
	//

	/**
	 * The external memory requirements. For instance, if Prolog is used by the
	 * interpreter that generates the program automaton, then it is only fair to
	 * also take Prolog's memory consumption into consideration when reporting
	 * on the model checker's resource uses.
	 */
	long exMemory = 0;

	/**
	 * The internal memory requirements. This basically comes down to the memory
	 * requirements of Java.
	 */
	long inMemory = 0;

	/**
	 * The starting time of the exploration run.
	 */
	long start = System.currentTimeMillis();

	//
	// Methods
	//

	/**
	 * Updates the internal memory requirements as maintained by
	 * {@link #inMemory}.
	 */
	void updateInMemory() {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long consumption = total - free;
		inMemory = consumption > inMemory ? consumption : inMemory;
	}
}