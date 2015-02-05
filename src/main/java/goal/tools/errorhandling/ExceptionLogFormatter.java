package goal.tools.errorhandling;

import goal.preferences.LoggingPreferences;
import goal.tools.errorhandling.ExceptionLogRecord.ShowMode;
import goal.tools.logging.SingleLineFormatter;

import java.util.List;
import java.util.logging.LogRecord;

/**
 * Formatter for a {@link ExceptionLogRecord}. Replaces old GOALBugReport and
 * WarningFormatter.
 *
 * @author W.Pasman 5feb15
 *
 */
public class ExceptionLogFormatter extends SingleLineFormatter {

	@Override
	public String format(LogRecord record) {
		if (!(record instanceof ExceptionLogRecord)) {
			throw new IllegalArgumentException(
					"ExceptionFormatter can only format  ExceptionLogRecord but received "
							+ record.getClass().getCanonicalName());
		}

		// Get content of warning.
		ExceptionLogRecord elRecord = (ExceptionLogRecord) record;
		Throwable error = elRecord.getCause();
		StringBuilder message = new StringBuilder();
		message.append(formatHeader(record));

		String cause = StackHelper.getAllCauses(error);
		if (!cause.isEmpty()) {
			message.append(Resources.get(WarningStrings.BECAUSE)
					+ cause.toLowerCase()); //$NON-NLS-1$
		}
		message.append(".\n");

		if (error != null) {
			// see if we need to print the java details
			if (LoggingPreferences.getShowJavaDetails()) {
				message.append(Resources.get(WarningStrings.JAVA_DETAILS));
				message.append(error);
			}

			// show the stack dump if so desired or if runtime exception
			if (LoggingPreferences.getShowStackdump()
					|| error instanceof RuntimeException) {
				message.append(Resources.get(WarningStrings.STACKDUMP));
				message.append(StackHelper.getFullStackTraceInfo(error));
				// show more java details afterwards, if so desired
			} else if (LoggingPreferences.getShowJavaDetails()) {
				List<StackTraceElement> stackelts = StackHelper
						.getTopStack(error);
				if (!(stackelts.isEmpty())) {
					message.append(Resources.get(WarningStrings.AT));
					message.append(stackelts.get(0));
				} else {
					message.append(Resources
							.get(WarningStrings.EMPTY_STACK_POINT));
				}
			}
		}
		if (elRecord.getShowMode() == ShowMode.LASTTIME) {
			message.append(Resources
					.get(WarningStrings.SUPPRESS_FURTHER_WARNINGS));
		}

		return message.toString();
	}
}