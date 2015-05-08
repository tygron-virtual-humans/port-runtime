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

import goal.preferences.LoggingPreferences;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.logging.GOALLogRecord;
import goal.tools.logging.GOALLogger;
import goal.tools.logging.Loggers;

import java.util.Hashtable;
import java.util.logging.Formatter;
import java.util.logging.Level;

/**
 * <p>
 * Contains a log record for an exception. This log record will be passed to the
 * logger. The logger then will request the ExceptionFormatter to format the
 * exception for display.
 * 
 * <br>
 * 
 * This LogRecord can also be (ab)used to just show general warning messages. In
 * that case, pass stacktrace null.
 * 
 * @author W.Pasman 5feb15. This replaces the old structures with Warning,
 *         WarningFormatter, GOALBugReport etc.
 */
public class ExceptionLogRecord extends GOALLogRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7973818628779319163L;

	/**
	 * Counts number of message repetitions. The hash table key represents the
	 * warning message, the corresponding integer value represents the
	 * #repetitions.
	 */
	private static Hashtable<String, Integer> messageLog = new Hashtable<>();

	enum ShowMode {
		/* just show it */
		SHOW,
		/* last time we show it */
		LASTTIME,
		/* don't show it */
		SUPPRESS
	};

	private ShowMode showmode = ShowMode.SHOW;

	/**
	 * Returns The {@link GOALLogger} that handles all warning messages.
	 *
	 * @return The logger.
	 */
	public static GOALLogger getLogger() {
		return Loggers.getWarningLogger();
	}

	@Override
	public Formatter getFormatter() {
		return new ExceptionLogFormatter();
	}

	/**
	 * Creates a new log record for given exception. The log record is then
	 * pushed to the logging system for reporting.
	 *
	 * @param warning
	 *            The message to be printed. If this is null, the message
	 *            "(no explanation available)" will be used. (Trac 1048)
	 * @param error
	 *            The exception that was the cause of this warning.
	 */
	public ExceptionLogRecord(String warning, Throwable error) {
		super(error instanceof RuntimeException ? Level.SEVERE : Level.WARNING,
				warning, error);

		// handle null cases (trac #1048)
		if (warning == null) {
			warning = "(no explanation available)";
		}

		if (!(error instanceof RuntimeException)) {
			showmode = checkOccurences(warning);
		}

		if (showmode == ShowMode.SUPPRESS)
			return;

		getLogger().log(this);

	}

	/**
	 * Check if this warning should be shown. Notice that runtime exceptions
	 * should always be shown anyway and for those this function should not be
	 * called.
	 * 
	 * @param message
	 *            the general message to show
	 * @return ShowMode what to do with this warning message.
	 */
	private ShowMode checkOccurences(String message) {

		// do not try to log a warning when nothing is going to be displayed
		if (message.length() == 0
				&& (!(LoggingPreferences.getShowJavaDetails() || LoggingPreferences
						.getShowStackdump()))) {
			return ShowMode.SUPPRESS;
		}

		// log simple warning to avoid printing too many similar warnings that
		// only differ because of third party related specifics (e.g. KR
		// technology, JADE, Java)
		Integer count = 0;

		if (messageLog.containsKey(message)) {
			count = messageLog.get(message) + 1;
		}

		messageLog.put(message, count);

		// do not try to log a warning already shown too many times
		if (count >= LoggingPreferences.getSuppressLevel()
				&& LoggingPreferences.getSuppressLevel() != -1) {
			return ShowMode.SUPPRESS;
		}

		// append some extra text if this is the last warning with the same
		// message to be displayed
		if (count == LoggingPreferences.getSuppressLevel() - 1) {
			return ShowMode.LASTTIME;
		}

		return ShowMode.SHOW;
	}

	/**
	 * get the show mode for this log record.
	 *
	 * @return {@link ShowMode}.
	 */
	public ShowMode getShowMode() {
		return showmode;
	}

	/**
	 * Logs a warning message if the message has not already been printed more
	 * often than suppression threshold. Also supports printing stack trace
	 * information of the related cause (only if the user preferences indicate
	 * this information should be printed).
	 *
	 * @param debugger
	 *            The {@link SteppingDebugger} in control of the call.
	 * @param warning
	 *            The message to be printed. If this is null, the message
	 *            "(no explanation available)" will be used. (Trac 1048) The
	 *            message will be prefixed with [DEBUGGERNAME].
	 * @param cause
	 *            The original stack trace.
	 * @param error
	 *            The exception that was the cause of this warning.
	 */
	public ExceptionLogRecord(Debugger debugger, String warning, Throwable cause) {
		this(String.format("[%1$s] %2$s", debugger.getName(),
				warning == null ? "(no explanation available)" : warning),
				cause);
	}

}