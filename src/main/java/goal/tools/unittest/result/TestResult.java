package goal.tools.unittest.result;

import goal.tools.unittest.Test;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.result.testsection.TestSectionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the results of evaluating a test. A test is considered passed if none
 * of its test sections failed or were interrupted.
 *
 * @author mpkorstanje
 */
public class TestResult {
	@Override
	public String toString() {
		return "TestResult [Test= " + this.test + ", results=" + this.results
				+ ", ruleFailed=" + this.testSectionFailed + ", passed="
				+ this.passed + "]";
	}

	public static final TestResult EMPTY = new TestResult(Test.EMPTY);

	private final Test test;
	private final List<TestSectionResult> results;
	private final TestSectionFailed testSectionFailed;
	private final boolean passed;

	/**
	 * @param test
	 * @param results
	 * @param testSectionFailed
	 */
	public TestResult(Test test, List<TestSectionResult> results,
			TestSectionFailed testSectionFailed) {
		this.test = test;
		this.results = results;
		this.testSectionFailed = testSectionFailed;
		this.passed = checkPassed();
	}

	private boolean checkPassed() {
		return this.testSectionFailed == null;
	}

	/**
	 * @return
	 */
	public boolean isInterupted() {
		return this.testSectionFailed instanceof TestSectionInterupted;
	}

	/**
	 * @param program
	 * @param results
	 */
	public TestResult(Test program, List<TestSectionResult> results) {
		this(program, results, null);
	}

	/**
	 * @param program
	 */
	public TestResult(Test program) {
		this(program, new ArrayList<TestSectionResult>(0));
	}

	/**
	 * @return @see {@link Test#getTestID()}.
	 */
	protected String getTestProgramID() {
		return this.test.getTestID();
	}

	/**
	 * @return
	 */
	public boolean isPassed() {
		return this.passed;
	}

	/**
	 * @return
	 */
	public TestSectionFailed getRuleFailed() {
		return this.testSectionFailed;
	}

	/**
	 * @return
	 */
	public List<TestSectionResult> getRuleResults() {
		return this.results;
	}

	/**
	 * @param formatter
	 * @return
	 */
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
