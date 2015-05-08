package goal.core.agent;

public class NoLoggingCapabilities implements LoggingCapabilities {

	@Override
	public void log(String message) {
		// new Warning("Failure to log '" + message
		// + "' because this agent is not attached to a logger.");
	}

	@Override
	public void dispose() {
	}
}