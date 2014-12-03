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

import languageTools.parser.InputStreamPosition;

/**
 * An exception that serves as a <b>notification</b> of the fact that we could
 * not handle something that the user did.
 * <p>
 * This exception should <b>never be thrown</b> at runtime. The exception should
 * only be used within {@link languageTools.program.agent} for parser and validation
 * related issues and within GUI/User interaction components.
 * </p>
 * <p>
 * This is <i>not</i> a warning that can simply always be ignored; by throwing
 * this exception it is made clear that something went seriously wrong.
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks Changed doc.
 */
public class GOALUserError extends GOALException {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = -7825010212940544992L;

	public GOALUserError(String message) {
		super(message);
	}

	public GOALUserError(String message, InputStreamPosition source) {
		super(message, source);
	}

	public GOALUserError(String message, Throwable cause) {
		super(message, cause);
	}

	public GOALUserError(String message, Throwable cause,
			InputStreamPosition source) {
		super(message, cause, source);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Error");
		super.appendSource(builder);
		builder.append(": ");
		builder.append(this.getMessage());
		return builder.toString();
	}
}
