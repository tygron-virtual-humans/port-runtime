package goal.tools.unittest;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebuggerKilledException;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.TestResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.result.testsection.TestSectionResult;
import goal.tools.unittest.testsection.TestSection;

import java.util.ArrayList;
import java.util.List;

/**
 * A test program. A test program consists of a list of {@link TestSection}s.
 * These rules are interpreted in sequential order. The rules are fast failing,
 * meaning that the program will stop at the first rule that fails.
 *
 * The results are provided in a {@link TestResult}.
 *
 * @author mpkorstanje
 */
public class Test {
	/**
	 * The name of the test.
	 */
	private final String id;
	/**
	 * A test without rules.
	 */
	public final static Test EMPTY = new Test("empty");

	private final List<TestSection> testSections;

	@Override
	public String toString() {
		return "Test " + this.id + " [Test Sections =" + this.testSections
				+ "]";
	}

	/**
	 * Creates a test.
	 *
	 * @param id
	 *            Name of the test.
	 * @param testSections
	 *            Parts of the test.
	 */
	public Test(String id, List<TestSection> testSections) {
		this.testSections = testSections;
		this.id = id;
	}

	/**
	 * Creates a test without any rules.
	 *
	 * @param id
	 *            Name of the test.
	 */
	public Test(String id) {
		this(id, new ArrayList<TestSection>(0));
	}

	/**
	 * @return The name of this test section.
	 */
	public String getTestID() {
		return this.id;
	}

	/**
	 * Executes the test, using the agent's run state, and returns the result of
	 * the test.
	 *
	 * @param runState
	 *            of the agent.
	 * @return result of executing the test.
	 */
	public TestResult run(RunState<? extends ObservableDebugger> runState) {
		List<TestSectionResult> results = new ArrayList<>(
				this.testSections.size());
		for (TestSection section : this.testSections) {
			try {
				TestSectionResult result = section.run(runState);
				results.add(result);
			} catch (TestSectionFailed e) {
				return new TestResult(this, results, e);
			} catch (DebuggerKilledException e) {
				TestSectionFailed interupted = new TestSectionInterupted(
						section, e);
				return new TestResult(this, results, interupted);
			}
		}
		return new TestResult(this, results);
	}

	/**
	 * @return {@code true} if the program is an actual test.
	 */
	public boolean isTest() {
		return !isBefore() && !isAfter();
	}

	/**
	 *
	 * @return true if the program is a before section
	 */
	public boolean isBefore() {
		return this.id.equalsIgnoreCase("before");
	}

	/**
	 *
	 * @return true if the program is an after section
	 */
	public boolean isAfter() {
		return this.id.equalsIgnoreCase("after");
	}
}
