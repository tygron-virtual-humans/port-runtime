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

package goal.tools.errorhandling;

import goal.tools.logging.GOALLogRecord;
import goal.tools.logging.GOALLogger;
import goal.tools.logging.Loggers;
import goal.tools.logging.SingleLineFormatter;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>
 * A slight variation on the {@link Warning} class, {@link GOALBugReport}
 * handles the display of GOAL bugs that occurred while running a multi-agent
 * system. These are errors that seem to be a bug in the core, not a error by
 * the user.
 * </p>
 * <p>
 * Instead of printing the text 'Warning' this class prints the message 'GOAL
 * BUG'.
 * </p>
 *
 * @author K.Hindriks
 */
public class GOALBugReport extends GOALLogRecord {

	/** auto-generated serial version UID */
	private static final long serialVersionUID = 2962833590822427461L;
	/**
	 * A formatter for {@link GOALBugReport}s.
	 */
	private static Formatter bugFormatter;

	/**
	 *
	 */
	static {
		GOALBugReport.bugFormatter = new GOALBugReport.Formatter();
	}

	/**
	 * Returns the {@link GOALLogger} logging all {@link GOALBugReport}s.
	 *
	 * @return The logger.
	 */
	public static GOALLogger getLogger() {
		return Loggers.getRuntimeErrorLogger();
	}

	/**
	 * @return A Formatter for {@link GOALBugReport}s.
	 */
	@Override
	public Formatter getFormatter() {
		return GOALBugReport.bugFormatter;
	}

	/**
	 * Displays report of the error to the user.
	 * <p>
	 * If this is the first time in this session that the given message is
	 * shown, the stack trace (if available) will be printed. If the same
	 * message is displayed again, the stack trace is only printed if
	 * {@link #alwaysShowStackTrace} is set to <code>true</code>
	 *
	 * @param message
	 *            The error message to report to the user. Is prefixed with
	 *            'Runtime error: '.
	 */
	public GOALBugReport(String message) {
		this(message, null);
	}

	/**
	 * Displays a new bug report to the user. If this is the first time in this
	 * session that the given message is shown, the stack trace (if available)
	 * will be printed. If the same message is displayed again, the stack trace
	 * is only printed if {@link #alwaysShowStackTrace} is set to
	 * <code>true</code>
	 *
	 * @param message
	 *            The bug-message to report to the user. When printed, is
	 *            prefixed with 'BUG: '.
	 * @param cause
	 *            The reason the bug occurred, and/or an exception with more
	 *            information about the bug. SHOULD NEVER BE NULL.
	 */
	public GOALBugReport(String message, Throwable cause) {
		// FIXME can we test cause!=null here?
		super(Level.SEVERE, message, cause);

		this.tryLog();
	}

	/**
	 * Logs this {@link GOALBugReport} to the logger defined in
	 * {@link #theLogger}.
	 */
	private void tryLog() {
		GOALBugReport.getLogger().log(this);
	}

	/**
	 * DOC
	 */
	private static class Formatter extends SingleLineFormatter {
		@Override
		public String format(LogRecord record) {
			if (record instanceof GOALBugReport) {
				StringBuilder builder = new StringBuilder();
				builder.append("GOAL BUG: ");
				builder.append(super.format(record));

				GOALBugReport error = (GOALBugReport) record;
				builder.append("\nStack trace:\n");
				builder.append(StackHelper.getFullStackTraceInfo(error
						.getCause()));

				return builder.toString();
			} else {
				return super.format(record);
			}
		}
	}
}
