package goal.tools.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * This class is a stop gap measure to deal with the curious use
 * {@link Formatter} by {@link GOALLogRecord}s. LoggingRecords allow a message
 * and parameters to be passed on to the logging system. The {@link Formatter}s
 * then format these by pushing the parameters into the message, adding a time
 * stamp and other info. GOALLogrecords however provide their own formatter
 * which create the message.
 *
 * @author mpkorstanje
 */
public class GOALLogRecordFormatter extends SimpleFormatter {

	/**
	 * Formats a log record. If the record is an instance of
	 * {@link GOALLogRecord} the formatter provided by the record will be used
	 * to create the message. Otherwise SimpleFormatter is used.
	 *
	 */
	@Override
	public synchronized String formatMessage(LogRecord record) {
		if (record instanceof GOALLogRecord) {
			return ((GOALLogRecord) record).getFormatter().format(record);
		}
		return super.formatMessage(record);
	}
}
