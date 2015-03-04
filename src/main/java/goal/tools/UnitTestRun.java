package goal.tools;

import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.Agent;
import goal.core.agent.AgentFactory;
import goal.core.runtime.MessagingService;
import goal.core.runtime.RuntimeManager;
import goal.tools.adapt.Learner;
import goal.tools.debugger.LoggingObserver;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.UnitTestInterpreter;
import languageTools.program.test.AgentTest;
import languageTools.program.test.UnitTest;

/**
 * Runs a {@link UnitTest} program. During the test agent are created with a
 * {@link UnitTestInterpreter}. This interpreter will execute a
 * {@link AgentTest} provided by the {@link UnitTest}. The results can be
 * collected by using the {@link UnitTestRunResultInspector}.
 *
 * @author M.P. Korstanje
 */
public class UnitTestRun extends AbstractRun<IDEDebugger, UnitTestInterpreter> {
	/**
	 * Creates the agents used when running the test program. The agents are
	 * created with a {@link UnitTestInterpreter} controller. The agents base
	 * name is used to look up the {@link AgentTest} for the agent. If no test
	 * could be found, the agent will only check for runtime errors.
	 *
	 * @author M.P. Korstanje
	 */
	private class TestRunAgentFactory extends
	AbstractAgentFactory<IDEDebugger, UnitTestInterpreter> {
		public TestRunAgentFactory(MessagingService messaging) {
			super(messaging);
		}

		@Override
		protected IDEDebugger provideDebugger() {
			IDEDebugger observableDebugger = new IDEDebugger(this.agentId,
					this.program, this.environment);
			observableDebugger.setKeepRunning(true);
			if (UnitTestRun.this.debuggerOutput) {
				new LoggingObserver(observableDebugger);
			}
			return observableDebugger;
		}

		@Override
		protected UnitTestInterpreter provideController(IDEDebugger debugger,
				Learner learner) {
			AgentTest test = UnitTestRun.this.unitTest
					.getTest(this.agentBaseName);
			return new UnitTestInterpreter(this.program, test, debugger,
					learner);
		}
	}

	/**
	 * The test program.
	 */
	private final UnitTest unitTest;

	public UnitTestRun(UnitTest program) {
		super(program.getMasProgram(), program.getAgents(), program
				.getTimeout());
		this.unitTest = program;
	}

	/**
	 * Awaits termination using busy waiting. Checks if agents are running, if
	 * any dead agents failed their test and if timeout has passed.
	 *
	 * Note: Uses busy waiting, to do this with notifications is rather complex.
	 */
	@Override
	protected void awaitTermination(
			RuntimeManager<? extends IDEDebugger, ? extends UnitTestInterpreter> runtimeManager)
					throws InterruptedException {
		// Wait while agents are running
		while (runtimeManager.hasAliveLocalAgents()) {
			// Check if any dead agents failed their test.
			if (checkDeadAgentWithFailedTest(runtimeManager)) {
				return;
			}
			// Check if at least one of alive agents still is running a test
			if (!checkAtleastOneAgentWithTest(runtimeManager)) {
				return;
			}
			// Wait otherwise...
			Thread.sleep(100);
		}
	}

	private static boolean checkDeadAgentWithFailedTest(
			RuntimeManager<? extends ObservableDebugger, ? extends UnitTestInterpreter> runtimeManager) {
		for (Agent<? extends UnitTestInterpreter> a : runtimeManager
				.getDeadAgents()) {
			if (!a.getController().getTestResults().isPassed()) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkAtleastOneAgentWithTest(
			RuntimeManager<? extends ObservableDebugger, ? extends UnitTestInterpreter> runtimeManager) {
		for (Agent<? extends UnitTestInterpreter> a : runtimeManager
				.getAliveAgents()) {
			if (a.getController().getTest() != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected AgentFactory<IDEDebugger, UnitTestInterpreter> buildAgentFactory(
			MessagingService messaging) {
		return new TestRunAgentFactory(messaging);
	}
}
