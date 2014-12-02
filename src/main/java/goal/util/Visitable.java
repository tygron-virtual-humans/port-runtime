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

package goal.util;

/**
 * Interface for the object structure part. Iteration is the responsibility of
 * the elements in the object structure.
 */
public interface Visitable {
	/**
	 * Every element that is to be visited has to implement this method. The
	 * body of the accept must contain a call to the provided vistor object with
	 * the callee as first argument. Travelsal of the object structure is the
	 * responsibility of the object structure. Thus, an element has to call the
	 * 'visit' method for all its members that are relevant.
	 */
	void accept(Visitor visitor);
}