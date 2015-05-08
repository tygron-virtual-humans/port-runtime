package goal.core.agent;

import goal.tools.logging.GOALLogger;

/**
 * Provides the Agents {@link LoggingCapabilities} through an {@link GOALLogger}
 * .
 *
 */
public class DefaultLoggingCapabilities implements LoggingCapabilities {
	private final GOALLogger logActionLogger;

	/**
	 * @param logger
	 *            The GOALLogger to use
	 */
	public DefaultLoggingCapabilities(GOALLogger logger) {
		this.logActionLogger = logger;
	}

	@Override
	public void log(String message) {
		this.logActionLogger.logln(message);
	}

	@Override
	public void dispose() {
		// TODO (Wouter): Implement dispose in logger to close file.
	}
}
