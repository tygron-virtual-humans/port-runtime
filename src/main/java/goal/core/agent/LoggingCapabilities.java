package goal.core.agent;

import languageTools.program.agent.actions.LogAction;

/**
 * Provides an abstract representation of the logging capabilities of the agent.
 * Mainly meant for the {@link LogAction}.
 *
 * Implementing classes can provide this functionality as they see fit.
 *
 */
public interface LoggingCapabilities {
	/**
	 * Log to a file
	 *
	 * @param message
	 */
	void log(String message);

	/**
	 * Dispose the logger (clean-up resources)
	 */
	void dispose();
}
