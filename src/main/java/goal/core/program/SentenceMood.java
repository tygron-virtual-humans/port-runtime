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

package goal.core.program;

/**
 * The content (sentence) of a message that an agent may send can have one of
 * three different moods:
 * <ul>
 * <li>{@link #INDICATIVE}</li>
 * <li>{@link #IMPERATIVE}</li>
 * <li>{@link #INTERROGATIVE}</li>
 * </ul>
 *
 * @author W. de Vries
 *
 */
public enum SentenceMood {
	/**
	 * Indicative mood is used for exchanging information.
	 */
	INDICATIVE,
	/**
	 * Interrogative mood is used for asking questions.
	 */
	INTERROGATIVE,
	/**
	 * Imperative mood is used for requests and commands.
	 */
	IMPERATIVE;

	@Override
	public String toString() {
		switch (this) {
		case INDICATIVE:
			return ":";
		case INTERROGATIVE:
			return "?";
		case IMPERATIVE:
			return "!";
		default:
			return "";
		}
	}

}
