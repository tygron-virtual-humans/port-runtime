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

package goal.tools.errorhandling.exceptions;

import goal.parser.InputStreamPosition;

/**
 * Represents a throwable error that serves as a notification that an attempt to
 * parse a string/file failed.
 *
 * @author W.Pasman
 *
 */
public class GOALParseException extends GOALException {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = 492982845607805845L;

	public GOALParseException(String message) {
		super(message);
	}

	public GOALParseException(String message, InputStreamPosition source) {
		super(message, source);
	}

	public GOALParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public GOALParseException(String message, Throwable cause,
			InputStreamPosition source) {
		super(message, cause, source);
	}

	@Override
	public String toString() {
		return "Parse failed: " + this.getMessage();
	}
}
