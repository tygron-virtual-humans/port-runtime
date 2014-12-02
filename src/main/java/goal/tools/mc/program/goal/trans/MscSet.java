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

package goal.tools.mc.program.goal.trans;

import goal.core.program.literals.MentalStateCond;
import goal.tools.mc.core.lmhashset.LMHashSet;

import java.util.Iterator;

/**
 * Represents a set of mental state conditions.
 * 
 * @author sungshik
 *
 */
public class MscSet implements Iterable<MentalStateCond> {

	//
	// Private fields
	//

	/**
	 * The set of mental state conditions.
	 */
	private final LMHashSet<MentalStateCond> set = new LMHashSet<MentalStateCond>();

	//
	// Constructors
	//

	/**
	 * Constructs an empty set of mental state conditions.
	 */
	public MscSet() {
	}

	/**
	 * Constructs a set of mental state conditions according to the specified
	 * set.
	 * 
	 * @param mscSet
	 *            - The set of MSCs that the {@link MscSet} to be constructed
	 *            should contain.
	 */
	public MscSet(LMHashSet<MentalStateCond> mscSet) {
		this.set.addAll(mscSet);
	}

	//
	// Public methods
	//

	/**
	 * Adds a mental state condition to this set.
	 * 
	 * @param msc
	 *            - The mental state condition to be added.
	 */
	public void add(MentalStateCond msc) {
		set.add(msc);
	}

	/**
	 * Adds a set of mental state conditions to this set.
	 * 
	 * @param mscSet
	 *            - The set of mental state conditions to be added.
	 */
	public void addAll(MscSet mscSet) {
		this.set.addAll(mscSet.set);
	}

	/**
	 * Checks if this set is empty.
	 * 
	 * @return <code>true</code> if this set is empty; <code>false</code>
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<MentalStateCond> iterator() {
		return set.iterator();
	}

	@Override
	public String toString() {
		return set.toString() + " (size: " + set.size() + ")";
	}
}
