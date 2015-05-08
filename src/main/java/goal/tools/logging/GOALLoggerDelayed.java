package goal.tools.logging;

import goal.preferences.LoggingPreferences;

/**
 * A {@link GOALLogger} that waits with creating a log file till the first log
 * is actually made.
 *
 * @author W.Pasman 11dec2013
 */
public class GOALLoggerDelayed extends GOALLogger {
	private final boolean eligibleForLogToFile;

	public GOALLoggerDelayed(String name, boolean eligibleForLogToFile) {
		super(name, false);
		this.eligibleForLogToFile = eligibleForLogToFile;
	}

	private void checkAttachLogFile() {
		if (this.eligibleForLogToFile && this.fileHandler == null
				&& LoggingPreferences.getLogToFile()) {
			addLogToFileHandler();
		}
	}

	/**
	 * Logs a {@link GOALLogrecord}.
	 *
	 * @param record
	 *            The log record to be logged.
	 */
	@Override
	public void log(GOALLogRecord record) {
		checkAttachLogFile();
		super.log(record);
	}

	/**
	 * Logs a plain text message, appended with the newline character.
	 *
	 * @param message
	 *            The message to be logged.
	 */
	@Override
	public void logln(String message) {
		checkAttachLogFile();
		super.logln(message);
	}
}
