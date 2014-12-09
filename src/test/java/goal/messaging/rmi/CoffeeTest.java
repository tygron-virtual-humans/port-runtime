package goal.messaging.rmi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.tools.ResultInspector;
import goal.tools.SingleRun;
import goal.tools.debugger.Debugger;
import goal.tools.logging.Loggers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import krTools.errors.exceptions.ParserException;

import org.apache.commons.cli.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rmimessaging.RmiMessaging;

public class CoffeeTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Loggers.removeConsoleLogger();
	}

	@Test
	public void testCoffee() throws ParserException, FileNotFoundException,
	ParseException, Exception {
		SingleRun run = new SingleRun(new File(
				"GOALagents/CoffeeAgents/coffee.mas2g"));
		run.setResultInspector(new ResultInspector<GOALInterpreter<Debugger>>() {
			@Override
			public void handleResult(
					Collection<Agent<GOALInterpreter<Debugger>>> agents) {
				for (Agent<GOALInterpreter<Debugger>> agent : agents) {
					if (agent.getId().getName().equals("maker")) {
						checkMaker(agent);
					} else if (agent.getId().getName().equals("grinder")) {
						checkGrinder(agent);
					} else {
						fail("unexpected agent" + agent);
					}
				}
			}

			/**
			 * check that grinder is in good end state
			 *
			 * @param agent
			 */
			private void checkGrinder(Agent<GOALInterpreter<Debugger>> agent) {
				assertTrue(agent.getController().isRunning());
			}

			/**
			 * check that maker is in good end state
			 *
			 * @param agent
			 */
			private void checkMaker(Agent<GOALInterpreter<Debugger>> agent) {
				assertFalse(agent.getController().isRunning());
			}
		});
		run.setMessaging(new RmiMessaging());
		// run.run(); FIXME: keeps running?!
	}
}
