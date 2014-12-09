package goal.tools.unittest.result.testcondition;

import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

/**
 * Exception thrown when a boundary is reached. This is an unchecked exception
 * to allow evaluation to end immediately.
 *
 * @author V.Koeman
 */
public class TestBoundaryException extends GOALRuntimeErrorException {
	/**
	 * Date of last change
	 */
	private static final long serialVersionUID = 201410152058L;

	/**
	 * Creates a new failed test boundary exception.
	 *
	 * @param message
	 *            of the exception.
	 */
	public TestBoundaryException(String message) {
		super(message);
	}
}
