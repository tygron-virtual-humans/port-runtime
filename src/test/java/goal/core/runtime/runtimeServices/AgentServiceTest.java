package goal.core.runtime.runtimeServices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.AgentFactory;
import goal.core.agent.GOALInterpreter;
import goal.core.mas.MASProgram;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.agent.AgentServiceEventObserver;
import goal.core.runtime.service.agent.events.AgentServiceEvent;
import goal.tools.PlatformManager;
import goal.tools.adapt.Learner;
import goal.tools.debugger.NOPDebugger;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.Loggers;

import java.io.File;

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
		MASProgram program = PlatformManager
				.createNew()
				.parseMASFile(
						new File(
								"src/test/resources/goal/core/runtime/runtimeServices/fibonaci.mas2g"));
		messaging = new MessagingService("localhost", new LocalMessaging());
		AgentFactory<NOPDebugger, GOALInterpreter<NOPDebugger>> factory = new AbstractAgentFactory<NOPDebugger, GOALInterpreter<NOPDebugger>>(
				messaging) {

			@Override
			protected NOPDebugger provideDebugger() {
				return new NOPDebugger(agentId);
			}

			@Override
			protected GOALInterpreter<NOPDebugger> provideController(
					NOPDebugger debugger, Learner learner) {
				return new GOALInterpreter<NOPDebugger>(program, debugger,
						learner);
			}
		};

		runtimeService = new AgentService<NOPDebugger, GOALInterpreter<NOPDebugger>>(
				program, factory);
	}

	@After
	public void tearDown() throws Exception {
		runtimeService.awaitTermination();
		runtimeService.dispose();
		messaging.shutDown();
	}

	@Test(timeout = 15000)
	public void testStart() throws GOALLaunchFailureException,
	InterruptedException {
		runtimeService.start();
		runtimeService.awaitTermination();
	}

	int agentsStarted = 0;

	@Test
	public void testStartStop() throws GOALLaunchFailureException,
	InterruptedException {
		runtimeService.addObserver(new AgentServiceEventObserver() {
			@Override
			public void agentServiceEvent(AgentService rs, AgentServiceEvent evt) {
				agentsStarted++;
			}
		});

		runtimeService.start();

		runtimeService.shutDown();
		runtimeService.awaitTermination();

		assertEquals(4, agentsStarted);
		assertEquals(4, runtimeService.getAgents().size());
		assertTrue(runtimeService.getAliveAgents().isEmpty());
	}
}
