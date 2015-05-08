package goal.tools.unittest.result;

import goal.tools.unittest.result.testsection.AssertTestResult;
import languageTools.program.test.AgentTest;

/**
 * Stores the result of a {@link AgentTest}. A test produces a list of a
 * {@link AssertTestResult} and an exception if the agent under test ran into an
 * exception.
 *
 * The test is considered passed if all {@link AssertTestResult} are passed and
 * no exception was thrown.
 *
 * @author M.P. Korstanje
 */
public class AgentTestResult {
	private final boolean passed;
	private final AgentTest agentTest;
	private final TestResult test;

	/**
	 * @param agentTest
	 * @param testResult
	 */
	public AgentTestResult(AgentTest agentTest, TestResult testResult) {
		this.agentTest = agentTest;

		this.test = testResult;

		this.passed = checkTestPassed();
	}

	/**
	 * Because results and tests are immutable we only compute the result once.
	 *
	 * @return true if before, test and after passed.
	 */
	private boolean checkTestPassed() {
		return this.test.isPassed();
	}

	/**
	 * Returns the test associated with this results.
	 *
	 * @return the test associated with this results.
	 */
	public AgentTest getTest() {
		return this.agentTest;
	}

	/**
	 * Returns true if the test passed. The test is considered passed if all
	 * {@link AssertTestResult} are passed no exception was thrown.
	 *
	 * @return true if the test passed.
	 */
	public boolean isPassed() {
		return this.passed;
	}

	/**
	 * @return The test result
	 */
	public TestResult getTestResult() {
		return this.test;
	}

	/**
	 * @param formatter
	 * @return
	 */
	protected <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
