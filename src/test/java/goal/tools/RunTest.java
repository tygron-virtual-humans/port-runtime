package goal.tools;

import java.io.FileNotFoundException;

import krTools.errors.exceptions.ParserException;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class RunTest {
	@Test
	public void testSinglefile() throws ParserException, ParseException,
	Exception {
		Run.run("src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test
	public void testMultipleFiles() throws ParserException, ParseException,
	Exception {
		Run.run("src/test/resources/goal/tools/adoptfreevar.mas2g",
				"src/test/resources/goal/tools/testselect.mas2g");
	}

	// @Test
	// FIXME: Too slow.
	public void testFileInDir() throws ParserException, ParseException,
	Exception {
		Run.run("src/test/resources/goal/tools");
	}

	@Test
	public void testVerboseReverse() throws ParserException, ParseException,
	Exception {
		Run.run("src/test/resources/goal/tools/testselect.mas2g", "--verbose");
	}

	@Test
	public void testVerbose() throws ParserException, ParseException, Exception {
		Run.run("--verbose", "src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test
	public void testVerboseArgumentsNoSeperator() throws ParserException,
	ParseException, Exception {
		Run.run("--verbose", "-piew",
				"src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test
	public void testVerboseNoArguments() throws ParserException,
	ParseException, Exception {
		Run.run("--verbose", "src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test(expected = FileNotFoundException.class)
	public void testVerboseArguments() throws ParserException, ParseException,
			FileNotFoundException, Exception {
		Run.run("--verbose", "iwep",
				"src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test(expected = FileNotFoundException.class)
	public void testVerboseArgumentsException() throws ParserException,
	ParseException, FileNotFoundException, Exception {
		Run.run("--verbose=all",
				"src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test
	public void testRepeats() throws ParserException, ParseException, Exception {
		Run.run("--repeats", "5",
				"src/test/resources/goal/tools/testselect.mas2g");
	}

	@Test
	public void testUnitTest() throws ParserException, ParseException,
	Exception {
		Run.run("src/test/resources/goal/parser/unittest/correctMinimal.test2g",
				"src/test/resources/goal/parser/unittest/correctExhaustive.test2g");
	}

	@Test
	public void testRMI() throws ParserException, ParseException, Exception {
		Run.run("--rmi", "localhost",
				"src/test/resources/goal/parser/unittest/correctMinimal.test2g");
	}
}
