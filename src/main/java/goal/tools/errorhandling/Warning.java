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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * <p>
 * Handles warning messages. Also counts the number of times a particular type
 * of message has been printed. For debugging purposes, this class also supports
 * printing stack dumps.
 * </p>
 * <p>
 * If requested, the stack trace is printed including all sub-causes. This is
 * essential to recovering what really caused the problem.
 * </p>
 * <p>
 * By default, a warning message is printed at most 5 times and the stack trace
 * is not printed. These defaults can be changed via the help > preferences >
 * warnings menu.
 * </p>
 *
 * @author W.Pasman
 * @version 2 4mar09: uses preferences for all settings.
 * @modified N.Kraayenbrink warnings can now be suppressed (temporarily).
 * @modified N.Kraayenbrink Moved stack-helper method to {@link StackHelper} and
 *           moved MyWarningException out to DummyException.
 */
public class Warning extends GOALLogRecord {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = -4305453410259189219L;
	/**
	 * Counts number of message repetitions. The hash table key represents the
	 * warning message, the corresponding integer value represents the
	 * #repetitions.
	 */
	private static Hashtable<String, Integer> messageLog;
	/**
	 * A flag indicating if warnings should be suppressed (temporarily).
	 * Intended use is not for the user, but for tidying up the output when
	 * parsing. Use {@link #suppress()} and {@link #release()} to toggle this
	 * value. TODO Can be opened up to the user once warnings are printed to a
	 * different tab, and then IOManager should not need to call
	 * {@link #suppress()} any more.
	 */
	private static boolean suppressWarnings;
	/**
	 * A list of warnings that have been suppressed. If {@link #release} is
	 * called, these warning messages are printed immediately.
	 */
	private static List<Warning> suppressedWarnings;
	/**
	 * A formatter for warning messages
	 */
	private static Formatter warningFormatter;
	/**
	 * A flag signaling to the formatter that this Warning is the last warning
	 * with the same message to be displayed.
	 */
	private boolean isLastDisplayed = false;

	/**
	 *
	 */
	static {
		messageLog = new Hashtable<>();
		suppressWarnings = false;
		suppressedWarnings = new LinkedList<>();
		warningFormatter = new Formatter();
	}

	/**
	 * Returns The {@link GOALLogger} that handles all warning messages.
	 *
	 * @return The logger.
	 */
	public static GOALLogger getLogger() {
		return Loggers.getWarningLogger();
	}

	/**
	 * @return A formatter for warnings.
	 */
	@Override
	public Formatter getFormatter() {
		return Warning.warningFormatter;
	}

	/**
	 * <p>
	 * Logs a warning message issued by the GOAL interpreter. A stack trace is
	 * generated using a 'fake' DummyException DummyException is removed from
	 * the stacktrace later, if the stacktrace is requested at all.
	 * </p>
	 * <p>
	 * Use the constructor Warning(warning, Throwable) if you are converting an
	 * exception into a warning. Otherwise a debugger cannot be provided with
	 * the proper information about what happened.
	 * </p>
	 *
	 * @param warning
	 *            The message to be printed.
	 */
	public Warning(String warning) {
		this(warning, null);
	}

	/**
	 * <p>
	 * Logs a warning message issued by the GOAL interpreter. A stack trace is
	 * generated using a 'fake' DummyException DummyException is removed from
	 * the stacktrace later, if the stacktrace is requested at all.
	 * </p>
	 * <p>
	 * Use the constructor Warning(warning, Throwable) if you are converting an
	 * exception into a warning. Otherwise a debugger cannot be provided with
	 * the proper information about what happened.
	 * </p>
	 * <p>
	 * By providing a {@link SteppingDebugger}, the issued warning message will
	 * contain a reference to that {@link SteppingDebugger}, by prefixing the
	 * message with [DEBUGGERNAME].
	 * </p>
	 *
	 * @param warning
	 *            The message to be printed. It will be prefixed with
	 *            [DEBUGGERNAME]
	 * @param debugger
	 *            The {@link SteppingDebugger} in control of the call.
	 */
	public Warning(Debugger debugger, String warning) {
		this(String.format("[%1$s] %2$s", debugger.getName(),
				warning == null ? "(no explanation available)" : warning));
	}

	/**
	 * Logs a warning message if the message has not already been printed more
	 * often than suppression threshold. Also supports printing stack trace
	 * information of the related cause (only if the user preferences indicate
	 * this information should be printed).
	 *
	 * @param warning
	 *            The message to be printed. If this is null, the message
	 *            "(no explanation available)" will be used. (Trac 1048)
	 * @param error
	 *            The exception that was the cause of this warning.
	 */
	public Warning(String warning, Throwable error) {
		super(Level.WARNING, warning, error);

		// handle null cases (trac #1048)
		if (warning == null) {
			warning = "(no explanation available)";
		}

		// log simple warning to avoid printing too many similar warnings that
		// only differ because of third party related specifics (e.g. KR
		// technology, JADE, Java)
		if (Warning.messageLog.containsKey(warning)) {
			// Update number of warning occurrences in the hash table.
			Warning.messageLog
			.put(warning, Warning.messageLog.get(warning) + 1);
		} else {
			Warning.messageLog.put(warning, 0);
		}

		// do not try to log a warning already shown too many times
		if (Warning.messageLog.get(warning) >= LoggingPreferences
				.getSuppressLevel()
				&& LoggingPreferences.getSuppressLevel() != -1) {
			return;
		}

		// do not try to log a warning when nothing is going to be displayed
		if (warning.length() == 0
				&& (error == null || !(LoggingPreferences.getShowJavaDetails() || LoggingPreferences
						.getShowStackdump()))) {
			return;
		}

		// append some extra text if this is the last warning with the same
		// message to be displayed
		if (Warning.messageLog.get(warning) == LoggingPreferences
				.getSuppressLevel() - 1) {
			this.isLastDisplayed = true;
		}

		this.tryLog();
	}

	/**
	 * Check if this Warning is the last warning with the same message to be
	 * displayed.
	 *
	 * @return true if last time to be displayed.
	 */
	public boolean isLastDisplayed() {
		return isLastDisplayed;
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
	public Warning(Debugger debugger, String warning, Throwable cause) {
		this(String.format("[%1$s] %2$s", debugger.getName(),
				warning == null ? "(no explanation available)" : warning),
				cause);
	}

	/**
	 * Tries to log this warning. If {@link #suppressWarnings} is true, the
	 * warning is not logged but saved. Call {@link #release} to print any
	 * suppressed warnings.
	 */
	private void tryLog() {
		if (!Warning.suppressWarnings) {
			Warning.getLogger().log(this);
		} else {
			Warning.suppressedWarnings.add(this);
		}
	}

	/**
	 * Temporarily suppress warnings. To enable printing warnings again, call
	 * {@link release}.
	 */
	public static void suppress() {
		Warning.suppressWarnings = true;
	}

	/**
	 * Stop suppressing warnings. Any suppressed warnings are printed
	 * immediately.
	 */
	public static void release() {
		Warning.suppressWarnings = false;
		for (Warning w : Warning.suppressedWarnings) {
			w.tryLog();
		}
		Warning.suppressedWarnings.clear();
	}

}