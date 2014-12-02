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
 * Represents a throwable error that only serves as a warning.<br>
 * If this is thrown by a {@link Validator}, the object it is validating is not
 * invalidated by this exception, although something else might still invalidate
 * the object.
 *
 * Can this be clarified? Normally you would just print a warning, and THEN
 * CONTINUE as the problem was RECOVERED. throwing a warning seems to be a
 * Contradictio in terminis
 *
 * @author N.Kraayenbrink
 *
 */
public class GOALWarning extends GOALException {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = 6095731560272230042L;

	public GOALWarning(String message) {
		super(message);
	}

	public GOALWarning(String message, InputStreamPosition source) {
		super(message, source);
	}

	public GOALWarning(String message, Throwable cause) {
		super(message, cause);
	}

	public GOALWarning(String message, Throwable cause,
			InputStreamPosition source) {
		super(message, cause, source);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Warning");
		super.appendSource(builder);
		builder.append(": ");
		builder.append(this.getMessage());
		return builder.toString();
	}
}
