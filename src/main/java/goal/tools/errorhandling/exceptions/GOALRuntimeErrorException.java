package goal.tools.errorhandling.exceptions;

public class GOALRuntimeErrorException extends RuntimeException {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 2864122461637581518L;

	/**
	 * Creates a {@link GOALRuntimeErrorException}. Should be used only for
	 * reporting errors that occur while running a GOAL agent that are caused by
	 * issues in the agent program that is run.
	 *
	 * @param string
	 *            The error message.
	 * @param e
	 *            The exception ...
	 */
	public GOALRuntimeErrorException(String string, Exception exception) {
		super(string, exception);
	}

	public GOALRuntimeErrorException(String string, Throwable cause) {
		super(string, cause);
	}

	public GOALRuntimeErrorException(Exception exception) {
		super(exception);
	}

	public GOALRuntimeErrorException(String string) {
		super(string);
	}

}
