package goal.tools.unittest.result.testsection;

/**
 * Exception that should be thrown when a test section fails.
 *
 * @author mpkorstanje
 */
public abstract class TestSectionFailed extends Exception implements
TestSectionResult {
	/** Date of last modification */
	private static final long serialVersionUID = 201401252120L;

	/**
	 * Creates a failed test section exception/result.
	 *
	 * @param t
	 *            throwable that caused the test section to fail.
	 */
	public TestSectionFailed(Throwable t) {
		super(t);
	}
}
