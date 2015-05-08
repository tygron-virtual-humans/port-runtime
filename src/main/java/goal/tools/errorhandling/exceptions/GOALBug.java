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

/**
 * Represents errors that indicate that there is a bug in the GOAL code
 * somewhere.<br>
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman 27jun2011 bugs should be runtime exception, see #1807
 *
 */
public class GOALBug extends RuntimeException {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = 2090964219474322362L;

	/**
	 * Creates a new {@link GOALBug}.
	 *
	 * @param message
	 *            A description of what went wrong.
	 */
	public GOALBug(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link GOALBug} caused by some inner exception. The message
	 * will be '[BUG]: &lt;CauseClassName>'.
	 *
	 * @param cause
	 *            The cause of the bug.
	 */
	public GOALBug(Throwable cause) {
		super("[BUG]: " + cause.getClass().getCanonicalName(), cause);
	}

	/**
	 * Creates a new {@link GOALBug} with some cause.
	 *
	 * @param message
	 *            A description of what went wrong.
	 * @param cause
	 *            The cause of the bug.
	 */
	public GOALBug(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String toString() {
		return this.getMessage();
	}
}
