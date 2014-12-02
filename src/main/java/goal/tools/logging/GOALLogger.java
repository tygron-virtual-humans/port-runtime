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

import goal.preferences.LoggingPreferences;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The GOAL logging tool.
 * <p>
 * The standard {@link java.util.logging.Logger} requires quite a long chain of
 * components requiring piping of the messages through various layers before the
 * user can reading it. This is nice as long as all works but fatal if a part of
 * the chain hangs, or if these layers are not in place to start with (when
 * running a stand-alone GOAL without IDE). See also trac 1163 and 1191.
 * </p>
 * <p>
 * Therefore we use the private field {@link #showLogsInIDE} to turn this all
 * off at right at this point. When that property is true, all logging info is
 * piped through a set of {@link java.util.logging.Logger}s. If that property is
 * false, the log info is printed directly to {@link System.out}.
 * </p>
 * <p>
 * We can't call the constructor of {@link java.util.logging.Logger} therefore
 * we need to wrap the logger as a field of our logger class, deviating from the
 * Java version in this respect.
 * </p>
 * <p>
 * Logging to file is also handled here. The default {@link FileHandler}
 * settings are used. Files are written to the user's home directory. If you
 * turn off the logging-rerouting, log to file is also disabled.
 * </p>
 * <p>
 * Tech note: we don't really care about having unique GOALLoggers for a given
 * name, as all is in the end routed through the unique logging.Logger anyway,
 * and if the output is printed directly instead it also does not matter to have
 * a unique logger.
 * </p>
 *
 * @author W.Pasman aug2010
 * @modified K.Hindriks
 */
public class GOALLogger {
	/**
	 * The logger used by this {@link GOALLogger}.
	 */
	private final Logger logger;
	/**
	 * null unless we log to file
	 */
	protected FileHandler fileHandler = null;
	/**
	 * Logs messages to the console.
	 */
	private final Handler consoleHandler;

	/**
	 * Creates a logger.
	 *
	 * @param name
	 *            The name of the logger. Should be indicative of its main
	 *            function.
	 * @param eligibleForLogToFile
	 *            TODO: make this fully dependent on user's choices. must be set
	 *            to {@code true} if this logger can write the logs also to
	 *            file. Whether logs are written to file also depends on the
	 *            user's preferences.
	 */
	public GOALLogger(String name, boolean eligibleForLogToFile) {
		logger = Logger.getLogger(name);
		// do not use any parent handlers, they print too much.
		logger.setUseParentHandlers(false);
		if (eligibleForLogToFile && LoggingPreferences.getLogToFile()) {
			addLogToFileHandler();
		}

		consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new SingleLineFormatter());
		consoleHandler.setLevel(Level.ALL);
	}

	/**
	 * Returns the name of this {@link GOALLogger}.
	 *
	 * @return The name of the logger.
	 */
	public String getName() {
		return logger.getName();
	}

	/**
	 * Logs a {@link GOALLogrecord}.
	 *
	 * @param record
	 *            The log record to be logged.
	 */
	public void log(GOALLogRecord record) {
		if (record.getMessage() != null && !record.getMessage().isEmpty()) {
			logger.log(record);
		}
	}

	/**
	 * Logs a plain text message. Uses {@link Logger.warning(String)}
	 *
	 * @param message
	 *            The string to be logged.
	 */
	private void log(String message) {
		if (message != null && !message.isEmpty()) {
			logger.warning(message);
		}
	}

	/**
	 * Logs a plain text message, appended with the newline character. Uses
	 * {@link #log(String)}.
	 *
	 * @param message
	 *            The message to be logged.
	 */
	public void logln(String message) {
		if (message != null && !message.isEmpty()) {
			log(message + "\n"); //$NON-NLS-1$
		}
	}

	/**
	 * Add a log Handler to receive logging messages. see
	 * {@link Logger#addHandler(Handler)}
	 *
	 * @param handler
	 *            a logging Handler
	 */
	public void addHandler(Handler handler) {
		logger.addHandler(handler);
	}

	/**
	 * Adds a handler to this {@link GOALLogger} for writing to a log file.
	 */
	protected void addLogToFileHandler() {
		try {
			String fname = LoggingPreferences.getLogDirectory();
			fname += "/" + logger.getName() //$NON-NLS-1$
					+ (LoggingPreferences.getOverwriteFile() ? "" : "_%u") //$NON-NLS-1$ //$NON-NLS-2$
					+ ".log"; //$NON-NLS-1$
			fileHandler = new FileHandler(fname);
			addHandler(fileHandler);
		} catch (SecurityException e) {
			new Warning(String.format(
					Resources.get(WarningStrings.FAILED_LOG_TO_FILE),
					logger.getName()), e);
		} catch (IOException e) {
			new Warning(String.format(
					Resources.get(WarningStrings.FAILED_LOG_TO_FILE),
					logger.getName()), e);
		}
	}

	/**
	 * Removes a handler from the list of handlers that receive log messages
	 * from this logger.
	 *
	 * @param handler
	 *            The logging handler to remove.
	 */
	public void removeHandler(Handler handler) {
		logger.removeHandler(handler);
	}

	/**
	 * Do not log to file any more (if we did)
	 */
	public void removeLogToFileHandler() {
		if (fileHandler != null) {
			logger.removeHandler(fileHandler);
			fileHandler.close();
			fileHandler = null;
		}
	}

	/**
	 * Adds a handler to the logger that will print all messages to the console.
	 */
	public void addConsoleLogger() {
		logger.addHandler(consoleHandler);
	}

	/**
	 * Stops messages being printed to the console.
	 */
	public void removeConsoleLogger() {
		logger.removeHandler(consoleHandler);
	}

}