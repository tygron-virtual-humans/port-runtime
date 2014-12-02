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

package goal.tools.mc.property.ltl.goal;

import goal.core.program.literals.MentalStateCond;
import goal.tools.mc.property.ltl.Proposition;

/**
 * Represents a mental state condition that serves as an LTL proposition.
 *
 * @author sungshik
 *
 */
public class MentalStateCondition extends Proposition {

	//
	// Class variables
	//

	/**
	 * The actual mental state condition.
	 */
	private final MentalStateCond msc;

	//
	// Constructors
	//

	/**
	 * Constructs a mental state condition proposition corresponding to the
	 * specified actual MSC.
	 *
	 * @param msc
	 *            - The mental state condition to which the object to be created
	 *            should correspond.
	 */
	public MentalStateCondition(MentalStateCond msc) throws Exception {
		super();
		if (msc == null) {
			throw new Exception("msc is empty");
		}
		this.msc = msc;
		this.name = msc.toString();
	}

	//
	// Public methods
	//

	/**
	 * Gets the actual mental state condition.
	 *
	 * @return The actual MSC.
	 */
	public MentalStateCond getCondition() {
		return msc;
	}
}
