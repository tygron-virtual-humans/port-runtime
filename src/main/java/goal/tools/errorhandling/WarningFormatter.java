package goal.tools.errorhandling;

import goal.preferences.LoggingPreferences;
import goal.tools.logging.SingleLineFormatter;

import java.util.List;
import java.util.logging.LogRecord;

/**
 * Formatter for a {@link Warning}. Extracted from the old printMessage method
 * in {@link Warning}.
 *
 * @author N.Kraayenbrink
 *
 */
public class WarningFormatter extends SingleLineFormatter {

	@Override
	public String format(LogRecord record) {
		if (record instanceof Warning) {
			// Get content of warning.
			Warning w = (Warning) record;
			String warning = super.format(record);
			Throwable error = w.getCause();

			String cause = StackHelper.getAllCauses(error);
			if (!cause.isEmpty()) {
				warning += Resources.get(WarningStrings.BECAUSE)
						+ cause.toLowerCase() + "."; //$NON-NLS-1$
			}

			StringBuilder message = new StringBuilder();
			message.append(Resources.get(WarningStrings.WARNING));
			message.append(warning);

			if (error != null) {
				// see if we need to print the java details
				if (LoggingPreferences.getShowJavaDetails()) {
					message.append(Resources.get(WarningStrings.JAVA_DETAILS));
					message.append(error);
				}

				// show the stack dump if so desired
				if (LoggingPreferences.getShowStackdump()) {
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
			if (w.isLastDisplayed()) {
				message.append(Resources
						.get(WarningStrings.SUPPRESS_FURTHER_WARNINGS));
			}

			return message.toString();
		} else {
			return super.format(record);
		}
	}
}