package goal.core.performance;

import goal.tools.Run;
import goal.tools.logging.Loggers;

import java.io.FileNotFoundException;

import krTools.errors.exceptions.ParserException;

import org.apache.commons.cli.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Performance test.
 *
 * Token ring consists of
 *
 *
 * @author mpkorstanje
 *
 */
public class Performance {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Loggers.removeConsoleLogger();
	}

	// @Test
	public void testTokenRingLocal() throws ParserException,
	FileNotFoundException, ParseException, Exception {
		Run.run("src/test/resources/goal/core/performance/tokenring/token1.mas2g");
	}

	// @Test
	public void testTokenRingRmi() throws ParserException,
	FileNotFoundException, ParseException, Exception {
		Run.run("src/test/resources/goal/core/performance/tokenring/token1.mas2g",
				"--messagingtype", "rmi");
	}

	// @Test
	public void testChameneos() throws ParserException, FileNotFoundException,
			ParseException, Exception {
		Run.run("src/test/resources/goal/core/performance/chameneos/chameneos.mas2g");
	}

	// @Test
	public void testChameneosOnRMI() throws ParserException,
	FileNotFoundException, ParseException, Exception {
		Run.run("src/test/resources/goal/core/performance/chameneos/chameneos.mas2g",
				"--messagingtype", "rmi");
	}
}
