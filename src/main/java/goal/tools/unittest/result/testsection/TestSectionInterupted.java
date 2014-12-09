package goal.tools.unittest.result.testsection;

import goal.tools.debugger.DebuggerKilledException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.TestResult;
import goal.tools.unittest.testsection.executors.TestSectionExecutor;

/**
 * Exception used in a {@link TestResult} to show that while running a test
 * section the debugger was killed.
 *
 * @author mpkorstanje
 */
public class TestSectionInterupted extends TestSectionFailed {
	/** Date of last modification */
	private static final long serialVersionUID = 201401252118L;

	private final TestSectionExecutor testSection;

	@Override
	public String toString() {
		return "Test Section Interupted [Test Section= " + this.testSection
				+ "]";
	}

	/**
	 * Creates an interrupted test section.
	 *
	 * @param testSection
	 *            that was interrupted.
	 * @param exception
	 *            that killed the debugger.
	 */
	public TestSectionInterupted(TestSectionExecutor testSection,
			DebuggerKilledException exception) {
		super(exception);
		this.testSection = testSection;
	}

	/**
	 * @return the test section that was running when the debugger was killed.
	 */
	public TestSectionExecutor getTestSection() {
		return this.testSection;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
