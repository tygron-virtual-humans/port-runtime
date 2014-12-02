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

package goal.tools.mc.explorer;

import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;

/**
 * Represents the explorer component of a model checker. Actual exploration may
 * be anything: nested depth-first search, strongly connected components,
 * symbolic, SAT, et cetera.
 * 
 * @author sungshik
 *
 */
public interface Explorer {

	/**
	 * Start exploration.
	 */
	void start();

	/**
	 * Gets the current search path of the first search in an NDFS algorithm.
	 * 
	 * @return The current search path as an unordered set.
	 */
	LMHashSet<State> getFirstSearchPath();
}
