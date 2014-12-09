package goal.tools.unittest.result;

import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.result.testsection.TestSectionResult;

import java.util.ArrayList;
import java.util.List;

import languageTools.program.test.TestCollection;

/**
 * Stores the results of evaluating a test. A test is considered passed if none
 * of its test sections failed or were interrupted.
 *
 * @author mpkorstanje
 */
public class TestResult {
	@Override
	public String toString() {
		return "TestResult [Tests= " + this.tests + ", results=" + this.results
				+ ", ruleFailed=" + this.testSectionFailed + ", passed="
				+ this.passed + "]";
	}

	public static final TestResult EMPTY = new TestResult(TestCollection.EMPTY);

	private final TestCollection tests;
	private final List<TestSectionResult> results;
	private final TestSectionFailed testSectionFailed;
	private final boolean passed;

	/**
	 * @param tests
	 * @param results
	 * @param testSectionFailed
	 */
	public TestResult(TestCollection tests, List<TestSectionResult> results,
			TestSectionFailed testSectionFailed) {
		this.tests = tests;
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
	 * @param tests
	 * @param results
	 */
	public TestResult(TestCollection tests, List<TestSectionResult> results) {
		this(tests, results, null);
	}

	/**
	 * @param tests
	 */
	public TestResult(TestCollection tests) {
		this(tests, new ArrayList<TestSectionResult>(0));
	}

	/**
	 * @return @see {@link TestCollection#getTestID()}.
	 */
	protected String getTestProgramID() {
		return this.tests.getTestID();
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
