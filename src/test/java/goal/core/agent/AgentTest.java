package goal.core.agent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import krTools.KRlanguage;
import languageTools.program.agent.AgentProgram;
import goal.tools.PlatformManager;
import goal.tools.adapt.FileLearner;
import goal.tools.adapt.Learner;
import goal.tools.debugger.NOPDebugger;
import krTools.errors.exceptions.KRInitFailedException;
import goal.tools.logging.Loggers;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import swiprolog3.engines.SWIPrologLanguage;

public class AgentTest {

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
		File file = new File("src/test/resources/goal/core/agent/fibonaci.goal");
		AgentProgram program = PlatformManager.createNew().parseGOALFile(file,
				language);
		MessagingCapabilities messagingCapabilities = new NoMessagingCapabilities();
		EnvironmentCapabilities environmentCapabilities = new NoEnvironmentCapabilities();
		LoggingCapabilities loggingCapabilities = new NoLoggingCapabilities();

		NOPDebugger debugger = new NOPDebugger(id);
		Learner learner = new FileLearner(id.getName(), program);
		controller = new GOALInterpreter<NOPDebugger>(program, debugger,
				learner);
		agent = new Agent<GOALInterpreter<NOPDebugger>>(id,
				environmentCapabilities, messagingCapabilities,
				loggingCapabilities, controller);
	}

	@After
	public void tearDown() throws Exception {
		language.reset();
	}

	@Test
	public void testStart() throws InterruptedException {
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
		controller.run();
		assertTrue(controller.isRunning());
		assertFalse(controller.isTerminated());
		controller.awaitTermination();
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
	}

	@Test
	public void testStartStop() throws InterruptedException {
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
		controller.run();
		assertTrue(controller.isRunning());
		assertFalse(controller.isTerminated());
		controller.terminate();
		controller.awaitTermination();
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
	}

	@Test
	public void testIsRunningAfterStop() {
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
		controller.run();
		assertTrue(controller.isRunning());
		assertFalse(controller.isTerminated());
		controller.terminate();
		assertFalse(controller.isRunning());
		assertFalse(controller.isTerminated());
	}

	@Test
	public void testReset() throws InterruptedException, KRInitFailedException {
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
		controller.run();
		assertTrue(controller.isRunning());
		assertFalse(controller.isTerminated());
		controller.reset();
		assertTrue(controller.isRunning());
		assertFalse(controller.isTerminated());
		controller.terminate();
		controller.awaitTermination();
		assertFalse(controller.isRunning());
		assertTrue(controller.isTerminated());
	}

}
