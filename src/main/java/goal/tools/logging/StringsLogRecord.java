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

package goal.tools.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class representing a {@link LogRecord} with a parameterized message. Does not
 * build the message until it is published and formatted by the
 * {@link StringsLogRecord.WarningFormatter}.
 *
 * @author N.Kraayenbrink
 */
public class StringsLogRecord extends GOALLogRecord {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -4608288283917448265L;

	/**
	 * Creates a new LogRecord with a parameterized log message
	 *
	 * @param level
	 *            The level of the new log record.
	 * @param message
	 *            The message to log. May include format parts (eg:
	 *            "%1$s : $2$d").
	 * @param params
	 *            The parameters of the message to log.
	 */
	public StringsLogRecord(Level level, String message, Object... params) {
		super(level, message, null);
		setParameters(params);
	}

	@Override
	public Formatter getFormatter() {
		return new SingleLineFormatter();
	}
}
