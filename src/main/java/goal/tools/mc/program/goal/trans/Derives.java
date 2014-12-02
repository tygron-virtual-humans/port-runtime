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

/**
 * Provides two methods for establishing whether a mental state condition can
 * become (un)derivable if certain updates are brought about to the mental
 * state. It is important to note that the two methods are not its others dual.
 *
 * @author sungshik
 *
 * @param <A>
 *            - The type of atom that are used during derivation.
 */
public interface Derives<Atom> {

	/**
	 * Determines whether the specified mental state condition can become
	 * underivable from the mental state if the specified updates are brought
	 * about. Implementations of this method probably over-approximate in the
	 * sense that if the specified MSC can become underivable, then this method
	 * returns <code>true</code>. The converse is, however, not true: if this
	 * method returns <code>true</code>, then it is not necessarily the case
	 * that the specified MSC can really become underivable.
	 *
	 * @param msc
	 *            - The mental state condition to check whether it can become
	 *            underivable for.
	 * @param update
	 *            - The updates that are brought about to the mental state.
	 * @return <code>true</code> if the MSC can become underivable in an over-
	 *         approximative fashion; </code>false</code> otherwise.
	 */
	boolean mscderneg(MentalStateCond msc, TauUpdate<Atom> update);

	/**
	 * Determines whether the specified mental state condition can become
	 * derivable from the mental state if the specified updates are brought
	 * about. Implementations of this method probably over-approximate in the
	 * sense that if the specified MSC can become derivable, then this method
	 * returns <code>true</code>. The converse is, however, not true: if this
	 * method returns <code>true</code>, then it is not necessarily the case
	 * that the specified MSC can really become derivable.
	 *
	 * @param msc
	 *            - The mental state condition to check whether it can become
	 *            derivable for.
	 * @param update
	 *            - The updates that are brought about to the mental state.
	 * @return <code>true</code> if the MSC can become derivable in an over-
	 *         approximative fashion; </code>false</code> otherwise.
	 */
	boolean mscderpos(MentalStateCond msc, TauUpdate<Atom> update);
}