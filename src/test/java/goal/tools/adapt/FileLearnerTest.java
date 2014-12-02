package goal.tools.adapt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import eis.iilang.Percept;
import goal.core.agent.Agent;
import goal.core.agent.AgentId;
import goal.core.agent.EnvironmentCapabilities;
import goal.core.agent.GOALInterpreter;
import goal.core.agent.LoggingCapabilities;
import goal.core.agent.MessagingCapabilities;
import goal.core.agent.NoLoggingCapabilities;
import goal.core.agent.NoMessagingCapabilities;
import goal.core.kr.KRlanguage;
import goal.core.program.GOALProgram;
import goal.core.program.actions.UserSpecAction;
import goal.tools.PlatformManager;
import goal.tools.debugger.NOPDebugger;
import goal.tools.logging.Loggers;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import swiprolog3.engines.SWIPrologLanguage;

public class FileLearnerTest {

	private class DummyEnvironment implements EnvironmentCapabilities {
		@Override
		public Double getReward() {
			return 1.0;
		}

		@Override
		public void performAction(UserSpecAction action) {
			System.out.println(action);
		}

		@Override
		public Set<Percept> getPercepts() {
			return new HashSet<Percept>();
		}

		@Override
		public void dispose() {
		}
	}

	@BeforeClass
	public static void setupBeforeClass() {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	Agent<GOALInterpreter<NOPDebugger>> agent;
	GOALInterpreter<NOPDebugger> controller;
	KRlanguage language;

	@Before
	public void setUp() throws Exception {
		AgentId id = new AgentId("TestAgent");
		language = SWIPrologLanguage.getInstance();
		File file = new File("src/test/resources/goal/tools/adapt/adapt.goal");
		GOALProgram program = PlatformManager.createNew().parseGOALFile(file,
				language);
		MessagingCapabilities messagingCapabilities = new NoMessagingCapabilities();
		EnvironmentCapabilities environmentCapabilities = new DummyEnvironment();
		LoggingCapabilities loggingCapabilities = new NoLoggingCapabilities();

		NOPDebugger debugger = new NOPDebugger(id);
		Learner learner = new FileLearner(id.getName(), program);
		controller = new GOALInterpreter<NOPDebugger>(program, debugger,
				learner);
		agent = new Agent<GOALInterpreter<NOPDebugger>>(id,
				environmentCapabilities, messagingCapabilities,
				loggingCapabilities, controller);
	}

	@Test
	public void testStart() throws InterruptedException {
		controller.run();
		assertTrue(controller.isRunning());
		controller.awaitTermination();
		assertFalse(controller.isRunning());
	}

	@After
	public void tearDown() throws Exception {
		language.reset();
	}

}
