package goal.tools.unittest.testsection.executors;

import goal.core.agent.Agent;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.DebuggerKilledException;
import goal.tools.unittest.UnitTestInterpreter;
import goal.tools.unittest.result.AgentTestResult;
import goal.tools.unittest.result.TestResult;
import goal.tools.unittest.result.UnitTestResultFormatter;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.result.testsection.TestSectionResult;

import java.util.ArrayList;
import java.util.List;

import languageTools.program.test.AgentTest;
import languageTools.program.test.testsection.TestSection;

public class TestExecutor {
	private final AgentTest test;

	public TestExecutor(AgentTest test) {
		this.test = test;
	}

	/**
	 * Runs this test on the given run state.
	 *
	 * @param agent
	 *            the agent executing the test
	 * @return a list of {@link AssertTestResult}s containing the results of the
	 *         tests.
	 */
	public AgentTestResult run(Agent<UnitTestInterpreter> agent,
			Debugger debugger) {
		List<TestSection> testSections = this.test.getTests().getTestSections();
		List<TestSectionResult> results = new ArrayList<>(testSections.size());
		for (TestSection section : testSections) {
			TestSectionExecutor executor = TestSectionExecutor
					.getTestConditionExecutor(section);
			try {
				TestSectionResult result = executor.run(agent.getController()
						.getRunState());
				results.add(result);
			} catch (TestSectionFailed e) {
				UnitTestResultFormatter formatter = new UnitTestResultFormatter();
				debugger.breakpoint(Channel.TESTFAILURE, null, null,
						e.accept(formatter));
				return new AgentTestResult(this.test, new TestResult(
						this.test.getTests(), results, e));
			} catch (DebuggerKilledException e) {
				TestSectionFailed interupted = new TestSectionInterupted(
						executor, e);
				return new AgentTestResult(this.test, new TestResult(
						this.test.getTests(), results, interupted));
			}
		}
		return new AgentTestResult(this.test, new TestResult(
				this.test.getTests(), results));
	}
}
