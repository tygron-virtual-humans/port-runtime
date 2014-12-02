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
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.goal.trans.MscSet;
import goal.tools.mc.property.ltl.Proposition;

/**
 * Represents a GOAL vocabulary, i.e. a set of mental state conditions occurring
 * in an LTL formula.
 * 
 * @author sungshik
 *
 */
public class GOALVocabulary {

	//
	// Private fields
	//

	/**
	 * The mental state conditions in this vocabulary.
	 */
	private final LMHashSet<MentalStateCond> mscs = new LMHashSet<MentalStateCond>();

	/**
	 * The set of mental state conditions in this vocabulary.
	 */
	private MscSet mscSet;

	//
	// Constructors
	//

	/**
	 * Creates a vocabulary according to the specified set of propositions.
	 * 
	 * @param propositions
	 *            - The propositions that constitute the vocabulary to be
	 *            constructed
	 */
	public GOALVocabulary(LMHashSet<Proposition> propositions) {

		try {
			for (Proposition p : propositions) {
				if (p instanceof MentalStateCondition) {
					mscs.add(((MentalStateCondition) p).getCondition());
				}
			}
			this.mscSet = new MscSet(mscs);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// Public methods
	//

	/**
	 * Returns this vocabulary as a set of mental state conditions.
	 * 
	 * @return The vocabulary as a set of MSCs.
	 */
	public MscSet toMscSet() {
		return mscSet;
	}

	@Override
	public String toString() {
		return mscSet.toString();
	}
}
