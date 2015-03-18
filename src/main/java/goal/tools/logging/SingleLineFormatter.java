package goal.tools.logging;

import goal.preferences.LoggingPreferences;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/***
 * @author vincent
 * @modified wouter check preferences before printing stacktrace.
 */
public class SingleLineFormatter extends Formatter {
	private static DateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	@Override
	public String format(LogRecord record) {
		final StringBuilder sb = new StringBuilder();

		sb.append(formatHeader(record)).append("\n");

		if (record.getThrown() != null && LoggingPreferences.getShowStackdump()) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (RuntimeException reportErr) { 
				System.out.println("failed to format log record: "+reportErr.getMessage());
			}
		}

		return sb.toString();
	}

	/**
	 * Format just the heaader, don't append the stackdump. Useful for classes
	 * extending this as the default format() may attach stacktraces too
	 * eagerly.
	 * 
	 * @param record
	 * @return
	 */
	public String formatHeader(LogRecord record) {
		final StringBuilder sb = new StringBuilder();

		if (LoggingPreferences.getShowTime()) {
			sb.append(format.format(new Date(record.getMillis()))).append(" ");
		}
		if (record.getLevel().equals(Level.WARNING)
				|| record.getLevel().equals(Level.SEVERE)) {
			sb.append(record.getLevel().getLocalizedName()).append(": ");
		}
		sb.append(formatMessage(record));

		return sb.toString();
	}
}