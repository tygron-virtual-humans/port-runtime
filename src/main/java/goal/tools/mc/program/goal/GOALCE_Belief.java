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

package goal.tools.mc.program.goal;

import goal.core.kr.language.DatabaseFormula;

/**
 * Represents a belief in a GOAL conversion universe.
 *
 * @author sungshik
 *
 */
public class GOALCE_Belief implements GOALConversionElement {

	/**
	 *
	 */
	private static final long serialVersionUID = 3086888026489408114L;

	/**
	 * The actual representation of the belief.
	 */
	public final DatabaseFormula belief;

	//
	// Constructors
	//

	/**
	 * Constructs a belief according to the actual belief.
	 *
	 * @param belief
	 *            - The actual belief.
	 */
	public GOALCE_Belief(DatabaseFormula belief) {
		this.belief = belief;
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof GOALCE_Belief) {
			GOALCE_Belief other = (GOALCE_Belief) o;
			return belief.equals(other.belief);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return belief.hashCode();
	}

	@Override
	public String toString() {
		return "bel." + belief.toString();
	}
}
