package goal.core.runtime.environmentServices;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.environment.LocalMessagingEnvironment;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.tools.logging.Loggers;

import java.util.HashMap;
import java.util.Map;

import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnvironmentPortTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	EnvironmentInterfaceStandard eis;

	MessagingService messaging;
	LocalMessagingEnvironment environment;
	String environmentName = "dummyEnvironment";
	Map<String, Parameter> initialization = new HashMap<String, Parameter>();
	EnvironmentPort environmentPort;

	@Before
	public void setUp() throws Exception {
		eis = new MockEnvironment();
		messaging = new MessagingService("localhost", new LocalMessaging());
		environment = new LocalMessagingEnvironment(eis, environmentName,
				initialization, messaging);
		environment.initialize();
		environmentPort = new EnvironmentPort(environment.getMessageBoxId(),
				messaging);
		environmentPort.startPort();

	}

	/**
	 * wait (max 2 seconds) for env to reach some state.
	 *
	 * @param state
	 */
	private void waitForEnvState(EnvironmentState state) {
		int tries = 0;
		while (eis.getState() != state && tries++ < 20) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		assert (eis.getState() == state);
	}

	@After
	public void tearDown() throws Exception {
		environment.shutDown();
		environmentPort.shutDown();
		messaging.shutDown();
	}

	@Test
	public void testStartPortShutDown() throws MessagingException,
	InterruptedException {
		// FIXME: Executing this will hang tear down. Message boxes that have
		// been deleted can still send messages but not recieve them.
		// environmentPort.startPort();
		// TimeUnit.SECONDS.sleep(5);
		// environmentPort.shutDown();
	}

	@Test
	public void testStart() throws MessagingException, InterruptedException,
	EnvironmentInterfaceException {
		environmentPort.pause();
		waitForEnvState(EnvironmentState.PAUSED);
		environmentPort.start();
		waitForEnvState(EnvironmentState.RUNNING);
	}

	@Test
	public void testPause() throws MessagingException, InterruptedException,
	EnvironmentInterfaceException {
		assertNotEquals(EnvironmentState.PAUSED, eis.getState());
		environmentPort.pause();
		waitForEnvState(EnvironmentState.PAUSED);
	}

	@Test
	public void testKill() throws MessagingException, InterruptedException,
	EnvironmentInterfaceException {
		assertNotEquals(EnvironmentState.KILLED, eis.getState());
		environmentPort.kill();
		waitForEnvState(EnvironmentState.KILLED);
	}

	@Test
	public void testReset() throws MessagingException, InterruptedException,
	EnvironmentInterfaceException {
		environmentPort.kill();
		assertNotEquals(EnvironmentState.RUNNING, eis.getState());
		environmentPort.reset();
		waitForEnvState(EnvironmentState.RUNNING);
	}

	@Test(expected = PerceiveException.class)
	public void testGetPerceptsNonExistingAgent() throws MessagingException,
	EnvironmentInterfaceException {
		environmentPort.getPercepts("NonExistingAgent");
	}

	@Test
	public void testGetPercepts() throws MessagingException,
	EnvironmentInterfaceException {
		environmentPort.registerAgent("ExistingAgent");
		environmentPort.associateEntity("ExistingAgent", "ExistingEntity");
		assertFalse(environmentPort.getPercepts("ExistingAgent").isEmpty());
	}

	@Test
	public void testGetReward() throws MessagingException,
	EnvironmentInterfaceException {
		assertNotNull(environmentPort.getReward("NonExistingAgent"));
	}

	@Test
	public void testRegisterAgent() throws MessagingException,
	EnvironmentInterfaceException {
		environmentPort.registerAgent("ExistingAgent");
		assertTrue(eis.getAgents().contains("ExistingAgent"));
	}

	@Test
	public void testAssociateEntity() throws MessagingException,
	EnvironmentInterfaceException {
		assertFalse(eis.getFreeEntities().isEmpty());
		environmentPort.registerAgent("ExistingAgent");
		environmentPort.associateEntity("ExistingAgent", "ExistingEntity");
		assertTrue(eis.getFreeEntities().isEmpty());
	}

	@Test
	public void testPerformAction() throws MessagingException,
	EnvironmentInterfaceException {
		environmentPort.registerAgent("ExistingAgent");
		environmentPort.associateEntity("ExistingAgent", "ExistingEntity");
		assertNotNull(environmentPort.performAction("ExistingAgent",
				new Action("Act")));
	}

}
