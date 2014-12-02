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

package goal.parser;

/**
 * Interface for {@link ParsedObject}, such that object need to extend it.
 *
 * @author N.Kraayenbrink
 */
public interface IParsedObject {
	/**
	 * @return A pointer to where the definition of this object starts (in some
	 *         input stream). May be null, which indicates that this object was
	 *         not parsed from some string supplied by the user.
	 */
	InputStreamPosition getSource();

	/**
	 * Sets the location of this object's definition in the stream it was
	 * defined in. Intended use is only from within GOALParser.
	 *
	 * @param pos
	 *            The location.
	 */
	void setSource(InputStreamPosition pos);
}
