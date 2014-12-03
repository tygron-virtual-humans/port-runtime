package goal.tools.unittest.result;

import languageTools.program.agent.AgentId;
import goal.tools.debugger.DebuggerKilledException;
import goal.tools.unittest.AgentTest;
import goal.tools.unittest.UnitTest;
import goal.tools.unittest.UnitTestInterpreter;
import goal.tools.unittest.result.testsection.AssertTestResult;

/**
 * Result of running the {@link UnitTestInterpreter} for a single agent.
 * Includes the agent's test and any unexpected exceptions thrown during the
 * execution of the test. Can be considered passed if no exceptions were thrown
 * and the test result passed.
 *
 * Note that an agent may not have had a test.
 *
 * @author mpkorstanje
 */
public class UnitTestInterpreterResult {
	@Override
	public String toString() {
		return "Result of Unit Test Run [test=" + test + ", id=" + id
				+ ", testResult=" + testResult + ", uncaughtThrowable="
				+ uncaughtThrowable + ", passed=" + passed + "]";
	}

	private final AgentTest test;
	private final AgentId id;
	private final AgentTestResult testResult;
	private final Throwable uncaughtThrowable;
	private final boolean passed;

	/**
	 * @param test
	 * @param id
	 * @param testResult
	 * @param uncaughtThrowable
	 */
	public UnitTestInterpreterResult(AgentTest test, AgentId id,
			AgentTestResult testResult, Throwable uncaughtThrowable) {
		this.test = test;
		this.id = id;
		this.testResult = testResult;
		if (uncaughtThrowable instanceof DebuggerKilledException) {
			this.uncaughtThrowable = null;
		} else {
			this.uncaughtThrowable = uncaughtThrowable;
		}
		this.passed = checkTestPassed();
	}

	/**
	 * Because results and tests are immutable we only compute the result once.
	 *
	 * @return true if agent did not crash and its agent test passed.
	 */
	private boolean checkTestPassed() {
		// Agent did not crash
		if (uncaughtThrowable != null) {
			return false;
		}

		// If we had no test. Test passes by default.
		if (test == null) {
			return true;
		}

		// If a test was ran, it should produce a result and be successful
		return testResult != null && testResult.isPassed();
	}

	/**
	 * When an agent terminates in an abnormal manner any exceptions thrown are
	 * stored. These are collected as part of the test.
	 *
	 * @return the exception that caused the agent to stop or null of the agent
	 *         terminated as expected
	 */
	public Throwable getUncaughtThrowable() {
		return uncaughtThrowable;
	}

	/**
	 * Returns true if the test passed. The test is considered passed if all
	 * {@link AssertTestResult} are passed no exception was thrown.
	 *
	 * @return true if the test passed
	 */
	public boolean isPassed() {
		return passed;
	}

	/**
	 * Returns the test that produced this result. Can be <code>null</code> when
	 * ever if agent did not have a test associated with it in the
	 * {@link UnitTest}.
	 *
	 * @return the test that produced this result, can <code>null</code>,
	 */

	public AgentTest getTest() {
		return test;
	}

	/**
	 *
	 * @return the AgentId of the agent that produced this result
	 */
	public AgentId getId() {
		return id;
	}

	/**
	 * Returns the result of this tests Can be <code>null</code> if agent did
	 * not have a test associated with it in the {@link UnitTest} or terminated
	 * unexpectedly.
	 *
	 * @see UnitTestInterpreterResult#getUncaughtThrowable()
	 *
	 * @return the result produced by the agent test of this test, can
	 *         <code>null</code>,
	 */
	public AgentTestResult getResult() {
		return testResult;
	}

	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
