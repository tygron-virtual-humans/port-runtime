package goal.core.runtime.runtimeServices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.AgentFactory;
import goal.core.agent.GOALInterpreter;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.agent.AgentServiceEventObserver;
import goal.core.runtime.service.agent.events.AgentServiceEvent;
import goal.tools.AbstractRun;
import goal.tools.PlatformManager;
import goal.tools.adapt.Learner;
import goal.tools.debugger.NOPDebugger;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.Loggers;

import java.io.File;

import languageTools.program.mas.MASProgram;
import localmessaging.LocalMessaging;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AgentServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	AgentService<NOPDebugger, GOALInterpreter<NOPDebugger>> runtimeService;
	MessagingService messaging;

	@Before
	public void setUp() throws Exception {
		final PlatformManager platform = PlatformManager.createNew();
		final MASProgram program = platform
				.parseMASFile(new File(
						"src/test/resources/goal/core/runtime/runtimeServices/fibonaci.mas2g"));
		this.messaging = new MessagingService("localhost", new LocalMessaging());
		AgentFactory<NOPDebugger, GOALInterpreter<NOPDebugger>> factory = new AbstractAgentFactory<NOPDebugger, GOALInterpreter<NOPDebugger>>(
				this.messaging) {
			@Override
			protected NOPDebugger provideDebugger() {
				return new NOPDebugger(this.agentId);
			}

			@Override
			protected GOALInterpreter<NOPDebugger> provideController(
					NOPDebugger debugger, Learner learner) {
				return new GOALInterpreter<NOPDebugger>(this.program, debugger,
						learner);
			}
		};

		this.runtimeService = new AgentService<NOPDebugger, GOALInterpreter<NOPDebugger>>(
				program, platform.getParsedAgentPrograms(), factory);
	}

	@After
	public void tearDown() throws Exception {
		this.runtimeService
				.awaitTermination(AbstractRun.TIMEOUT_FIRST_AGENT_SECONDS);
		this.runtimeService.dispose();
		this.messaging.shutDown();
	}

	@Test
	public void testStart() throws GOALLaunchFailureException,
			InterruptedException {
		this.runtimeService.start();
		this.runtimeService.awaitTermination(0); // TODO: timeout?!
	}

	int agentsStarted = 0;

	@Test
	public void testStartStop() throws GOALLaunchFailureException,
			InterruptedException {
		this.runtimeService.addObserver(new AgentServiceEventObserver() {
			@Override
			public void agentServiceEvent(AgentService<?, ?> rs,
					AgentServiceEvent evt) {
				AgentServiceTest.this.agentsStarted++;
			}
		});

		this.runtimeService.start();
		this.runtimeService.shutDown();
		this.runtimeService.awaitTermination(1);

		assertEquals(4, this.agentsStarted);
		assertEquals(4, this.runtimeService.getAgents().size());
		assertTrue(this.runtimeService.getAliveAgents().isEmpty());
	}
}
