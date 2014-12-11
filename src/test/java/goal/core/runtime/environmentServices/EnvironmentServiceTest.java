package goal.core.runtime.environmentServices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.iilang.Parameter;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.environment.EnvironmentService;
import goal.core.runtime.service.environment.EnvironmentServiceObserver;
import goal.core.runtime.service.environment.LocalMessagingEnvironment;
import goal.core.runtime.service.environment.events.EnvironmentPortRemovedEvent;
import goal.core.runtime.service.environment.events.EnvironmentServiceEvent;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.tools.PlatformManager;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.Loggers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import languageTools.program.mas.MASProgram;
import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnvironmentServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	EnvironmentInterfaceStandard eis = new MockEnvironment();

	MessagingService messaging;
	LocalMessagingEnvironment environment;
	// we launch the env ourselves; the mas tries to connect with it.
	String environmentName = "dummyEnvironment";
	Map<String, Parameter> initialization = new HashMap<String, Parameter>();
	EnvironmentPort environmentPort;
	EnvironmentService environmentService;
	MASProgram program;

	@Before
	public void setUp() throws Exception {
		this.event = null;
		this.messaging = new MessagingService("localhost", new LocalMessaging());
		this.environment = new LocalMessagingEnvironment(this.eis,
				this.environmentName, this.initialization, this.messaging);

		File file = new File(
				"src/test/resources/goal/core/runtime/environmentServices/dummy.mas2g");
		this.program = PlatformManager.createNew().parseMASFile(file);

		this.environmentService = new EnvironmentService(this.program,
				this.messaging);

		this.environmentService.start();
	}

	@After
	public void tearDown() throws Exception {
		this.environmentService.shutDown();
		this.environment.shutDown();
		this.messaging.shutDown();

		this.event = null;
	}

	private Object event;

	private class TestObserver implements EnvironmentServiceObserver {
		@Override
		public void environmentServiceEventOccured(
				EnvironmentService environmentService,
				EnvironmentServiceEvent evt) {
			EnvironmentServiceTest.this.event = evt;
		}
	}

	@Test
	public void testAddEnvironmentPortMessageBox() throws MessagingException {
		// FIXME: This doesn't work. It is not possible to add environments
		// later on. Only way to discover new environment is to listen to
		// MessagagingEvent of environment MessageBoxes being created.
		// However at this point none is listening to them yet.

		// TestObserver observer = new TestObserver();
		// environmentService.addObserver(new TestObserver());
		//
		// MessageBoxId id = messaging.getNewUniqueID("secondDummyEnvironment",
		// Type.ENVIRONMENT);
		// MessageBox box = messaging.getNewMessageBox(id);
		//
		// assertTrue(observer.event instanceof EnvironmentPortAddedEvent);
		// assertNotNull(((EnvironmentPortAddedEvent)observer.event).getPort()
		// );
	}

	@Test
	public void testRemoveEnvironmentPortDirect()
			throws GOALLaunchFailureException, ManagementException,
			InterruptedException, MessagingException {
		TestObserver observer = new TestObserver();
		this.environmentService.addObserver(new TestObserver());

		this.environmentService.removeEnvironmentPort(this.environment
				.getMessageBoxId());

		assertTrue(this.event instanceof EnvironmentPortRemovedEvent);
		assertNotNull(((EnvironmentPortRemovedEvent) this.event).getPort());
	}

	@Test
	public void testRemoveEnvironmentPortMessageBox()
			throws ManagementException, InterruptedException,
			MessagingException, GOALLaunchFailureException {
		TestObserver observer = new TestObserver();
		this.environmentService.addObserver(observer);
		this.environment.shutDown();

		assertTrue(this.event instanceof EnvironmentPortRemovedEvent);
		assertNotNull(((EnvironmentPortRemovedEvent) this.event).getPort());
	}

	@Test
	public void testGetEnvironmentConnector() {
		// Null because we started a remote Environment.
		assertNull(this.environmentService.getLocalEnvironment());
	}

	@Test
	public void testGetEnvironmentPort() throws GOALLaunchFailureException,
	MessagingException, InterruptedException,
	EnvironmentInterfaceException {
		MessageBoxId id = this.environment.getMessageBoxId();
		assertNotNull(this.environmentService.getEnvironmentPort(id));
	}

	@Test
	public void testGetEnvironmentPorts() throws GOALLaunchFailureException,
	MessagingException, InterruptedException,
	EnvironmentInterfaceException {
		this.environmentService.start();

		assertFalse(this.environmentService.getEnvironmentPorts().isEmpty());
		assertEquals(1, this.environmentService.getEnvironmentPorts().size());
	}

}
