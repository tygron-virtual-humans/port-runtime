package goal.core.runtime.environmentServices;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eis.EIDefaultImpl;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.environment.LocalMessagingEnvironment;
import goal.tools.logging.Loggers;

public class LocalMessagingEnvironmentTest {

	EnvironmentInterfaceStandard eis = new EIDefaultImpl() {

		/**
		 *
		 */
		private static final long serialVersionUID = 8942811457865949545L;

		@Override
		public String requiredVersion() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Percept performEntityAction(String arg0, Action arg1)
				throws ActException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected boolean isSupportedByType(Action arg0, String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected boolean isSupportedByEnvironment(Action arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected boolean isSupportedByEntity(Action arg0, String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected LinkedList<Percept> getAllPerceptsFromEntity(String arg0)
				throws PerceiveException, NoEnvironmentException {
			// TODO Auto-generated method stub
			return null;
		}
	};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	MessagingService messaging;
	LocalMessagingEnvironment environment;
	String environmentName = "dummyEnvironment";
	Map<String, Parameter> initialization = new HashMap<String, Parameter>();

	@Before
	public void setUp() throws Exception {
		messaging = new MessagingService("localhost", new LocalMessaging());
		environment = new LocalMessagingEnvironment(eis, environmentName,
				initialization, messaging);
	}

	@After
	public void tearDown() throws Exception {
		messaging.shutDown();
	}

	@Test(timeout = 5000)
	public void testShutDown() throws ManagementException,
	InterruptedException, MessagingException {
		environment.shutDown();
	}

	@Test(timeout = 5000)
	public void testDelayedShutDown() throws ManagementException,
	InterruptedException, MessagingException {

		TimeUnit.SECONDS.sleep(1);
		environment.shutDown();
	}

	@Test
	public void testGetMessageBoxId() {
		assertNotNull(environment.getMessageBoxId());
	}

}
