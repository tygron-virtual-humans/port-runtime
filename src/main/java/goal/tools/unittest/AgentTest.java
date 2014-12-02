package goal.tools.unittest;

import goal.core.agent.Agent;
import goal.core.program.Module;
import goal.tools.UnitTestRun;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.AgentTestResult;
import goal.tools.unittest.result.TestResult;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.testsection.AssertTest;

/**
 * A test program for a single agent. A test consists of three phases, a setup,
 * a test and clean up phase. In the setup phase a GOAL{@link Module} can be
 * executed that sets the bot in the proper configuration. During the test phase
 * the module under test is executed after which a test of {@link AssertTest}s
 * is ran. Finally the clean up is done by running the after module.
 *
 * A test can be ran by starting an agent that uses the
 * {@link UnitTestInterpreter} controller. Or using {@link UnitTestRun}.
 *
 * @see AgentTestResult
 * @author M.P. Korstanje
 */
public class AgentTest {
	/**
	 * Base name of the agent under test.
	 */
	private final String agentName;
	/**
	 * Module under test.
	 */
	private final Test test;

	/**
	 * Constructs an a test for the agent with <code>agentName</code> as its
	 * base name. The program is the test that will be will be executed.
	 *
	 * @param agentName
	 *            base name of the agent
	 * @param program
	 *            to run
	 */
	public AgentTest(String agentName, Test program) {
		this.agentName = agentName;
		this.test = program;
	}

	/**
	 * Base name of the agent under test.
	 *
	 * @return base name of the agent under test
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * Runs this test on the given run state.
	 *
	 * @param agent
	 *            the agent executing the test
	 * @return a list of {@link AssertTestResult}s containing the results of the
	 *         tests.
	 */
	public AgentTestResult run(
			Agent<UnitTestInterpreter<ObservableDebugger>> agent) {
		// Run module under test
		TestResult result = test.run(agent.getController().getRunState());
		return new AgentTestResult(this, result);
	}

	@Override
	public String toString() {
		return "AgentTest [agentName=" + agentName + ", test=" + test + "]";
	}
}
