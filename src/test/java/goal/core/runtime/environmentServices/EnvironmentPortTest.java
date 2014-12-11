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
		this.eis = new MockEnvironment();
		this.messaging = new MessagingService("localhost", new LocalMessaging());
		this.environment = new LocalMessagingEnvironment(this.eis,
				this.environmentName, this.initialization, this.messaging);
		this.environment.initialize();
		this.environmentPort = new EnvironmentPort(
				this.environment.getMessageBoxId(), this.messaging);
		this.environmentPort.startPort();

	}

	/**
	 * wait (max 2 seconds) for env to reach some state.
	 *
	 * @param state
	 */
	private void waitForEnvState(EnvironmentState state) {
		int tries = 0;
		while (this.eis.getState() != state && tries++ < 20) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		assert (this.eis.getState() == state);
	}

	@After
	public void tearDown() throws Exception {
		this.environment.shutDown();
		this.environmentPort.shutDown();
		this.messaging.shutDown();
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
		this.environmentPort.pause();
		waitForEnvState(EnvironmentState.PAUSED);
		this.environmentPort.start();
		waitForEnvState(EnvironmentState.RUNNING);
	}

	@Test
	public void testPause() throws MessagingException, InterruptedException,
			EnvironmentInterfaceException {
		assertNotEquals(EnvironmentState.PAUSED, this.eis.getState());
		this.environmentPort.pause();
		waitForEnvState(EnvironmentState.PAUSED);
	}

	@Test
	public void testKill() throws MessagingException, InterruptedException,
			EnvironmentInterfaceException {
		assertNotEquals(EnvironmentState.KILLED, this.eis.getState());
		this.environmentPort.kill();
		waitForEnvState(EnvironmentState.KILLED);
	}

	@Test
	public void testReset() throws MessagingException, InterruptedException,
			EnvironmentInterfaceException {
		this.environmentPort.kill();
		assertNotEquals(EnvironmentState.RUNNING, this.eis.getState());
		this.environmentPort.reset();
		waitForEnvState(EnvironmentState.RUNNING);
	}

	@Test(expected = PerceiveException.class)
	public void testGetPerceptsNonExistingAgent() throws MessagingException,
			EnvironmentInterfaceException {
		this.environmentPort.getPercepts("nonExistingAgent");
	}

	@Test
	public void testGetPercepts() throws MessagingException,
			EnvironmentInterfaceException {
		this.environmentPort.registerAgent("existingAgent");
		this.environmentPort.associateEntity("existingAgent", "existingEntity");
		assertFalse(this.environmentPort.getPercepts("existingAgent").isEmpty());
	}

	@Test
	public void testGetReward() throws MessagingException,
			EnvironmentInterfaceException {
		assertNotNull(this.environmentPort.getReward("nonExistingAgent"));
	}

	@Test
	public void testRegisterAgent() throws MessagingException,
			EnvironmentInterfaceException {
		this.environmentPort.registerAgent("existingAgent");
		assertTrue(this.eis.getAgents().contains("existingAgent"));
	}

	@Test
	public void testAssociateEntity() throws MessagingException,
			EnvironmentInterfaceException {
		assertFalse(this.eis.getFreeEntities().isEmpty());
		this.environmentPort.registerAgent("existingAgent");
		this.environmentPort.associateEntity("existingAgent", "existingEntity");
		assertTrue(this.eis.getFreeEntities().isEmpty());
	}

	@Test
	public void testPerformAction() throws MessagingException,
			EnvironmentInterfaceException {
		this.environmentPort.registerAgent("existingAgent");
		this.environmentPort.associateEntity("existingAgent", "existingEntity");
		assertNotNull(this.environmentPort.performAction("existingAgent",
				new Action("act")));
	}

}
