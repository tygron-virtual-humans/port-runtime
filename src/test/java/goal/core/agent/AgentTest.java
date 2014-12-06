package goal.core.agent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import goal.tools.PlatformManager;
import goal.tools.adapt.FileLearner;
import goal.tools.adapt.Learner;
import goal.tools.debugger.NOPDebugger;
import goal.tools.logging.Loggers;

import java.io.File;

import krTools.errors.exceptions.KRInitFailedException;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		this.language = SWIPrologLanguage.getInstance();
		File file = new File("src/test/resources/goal/core/agent/fibonaci.goal");
		AgentProgram program = PlatformManager.createNew().parseGOALFile(file,
				this.language);
		MessagingCapabilities messagingCapabilities = new NoMessagingCapabilities();
		EnvironmentCapabilities environmentCapabilities = new NoEnvironmentCapabilities();
		LoggingCapabilities loggingCapabilities = new NoLoggingCapabilities();

		NOPDebugger debugger = new NOPDebugger(id);
		Learner learner = new FileLearner(id.getName(), program);
		this.controller = new GOALInterpreter<NOPDebugger>(program, debugger,
				learner);
		this.agent = new Agent<GOALInterpreter<NOPDebugger>>(id,
				environmentCapabilities, messagingCapabilities,
				loggingCapabilities, this.controller);
	}

	@After
	public void tearDown() throws Exception {
		this.language.reset();
	}

	@Test
	public void testStart() throws InterruptedException {
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
		this.controller.run();
		assertTrue(this.controller.isRunning());
		assertFalse(this.controller.isTerminated());
		this.controller.awaitTermination();
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
	}

	@Test
	public void testStartStop() throws InterruptedException {
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
		this.controller.run();
		assertTrue(this.controller.isRunning());
		assertFalse(this.controller.isTerminated());
		this.controller.terminate();
		this.controller.awaitTermination();
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
	}

	@Test
	public void testIsRunningAfterStop() {
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
		this.controller.run();
		assertTrue(this.controller.isRunning());
		assertFalse(this.controller.isTerminated());
		this.controller.terminate();
		assertFalse(this.controller.isRunning());
		assertFalse(this.controller.isTerminated());
	}

	@Test
	public void testReset() throws InterruptedException, KRInitFailedException {
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
		this.controller.run();
		assertTrue(this.controller.isRunning());
		assertFalse(this.controller.isTerminated());
		this.controller.reset();
		assertTrue(this.controller.isRunning());
		assertFalse(this.controller.isTerminated());
		this.controller.terminate();
		this.controller.awaitTermination();
		assertFalse(this.controller.isRunning());
		assertTrue(this.controller.isTerminated());
	}

}
