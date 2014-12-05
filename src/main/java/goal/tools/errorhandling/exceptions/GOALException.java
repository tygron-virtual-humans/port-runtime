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

import goal.tools.errorhandling.Warning;
import languageTools.parser.InputStreamPosition;

/**
 * <p>
 * Generic <i>checked</i> exception for issues while running GOAL.
 * <p>
 * Simple wrapper around {@link Exception}, so that generic Exceptions are no
 * longer necessary.
 * </p>
 * <p>
 * Do not add dots ('.') to the end of messages, as these will already be added
 * by {@link Warning} class
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman aug10
 *
 */
public abstract class GOALException extends Exception implements
		Comparable<GOALException> {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = 51927072972577978L;

	/**
	 * Around what position in the source something is located that made this
	 * exception occur.
	 */
	private InputStreamPosition source = null;

	public GOALException(String message) {
		super(message);
	}

	public GOALException(String message, InputStreamPosition source) {
		super(message);
		this.source = source;
	}

	public GOALException(String message, Throwable cause) {
		super(message, cause);
	}

	public GOALException(String message, Throwable cause,
			InputStreamPosition source) {
		super(message, cause);
		this.source = source;
	}

	/**
	 * @return The approximate location of the source of this exception.
	 */
	public InputStreamPosition getSource() {
		return this.source;
	}

	/**
	 * Generates a {@link Warning}.
	 */
	public void generateNotification(String message) {
		this.generateWarning(message);
	}

	/**
	 * Generates a {@link Warning} based on this exception. See trac #1190
	 */
	public void generateWarning(String message) {
		new Warning(message, this);
	}

	/**
	 * @param builder
	 */
	protected void appendSource(StringBuilder builder) {
		if (this.source != null) {
			builder.append(" around ");
			builder.append(this.source.toString());
		}
	}

	@Override
	public int compareTo(GOALException o) {
		if (this.source == null || o.source == null) {
			return 0;
		}
		return this.source.compareTo(o.source);
	}
}
