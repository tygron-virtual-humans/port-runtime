package goal.parser.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import goal.parser.antlr.UnitTestLexer;
import goal.parser.antlr.UnitTestParser;
import goal.tools.UnitTestRun;
import goal.tools.UnitTestRunResultInspector;
import goal.tools.errorhandling.exceptions.GOALCommandCancelledException;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.errorhandling.exceptions.GOALParseException;
import goal.tools.logging.Loggers;
import goal.tools.unittest.UnitTest;
import goal.tools.unittest.result.UnitTestResult;
import goal.tools.unittest.result.UnitTestResultFormatter;

import java.io.File;
import java.io.IOException;

import nl.tudelft.goal.messaging.exceptions.MessagingException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

@SuppressWarnings("javadoc")
public class AbstractUnitTestTest {

	protected UnitTestWalker visitor;

	@BeforeClass
	public static void setupBeforeClass() {
		Loggers.addConsoleLogger();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	public AbstractUnitTestTest() {
		super();
	}

	protected static void assertPassedAndPrint(UnitTestResult results) {
		UnitTestResultFormatter formatter = new UnitTestResultFormatter();
		System.out.println(formatter.visit(results));
		assertTrue(results.isPassed());
	}

	protected static void assertFailedAndPrint(UnitTestResult results) {
		UnitTestResultFormatter formatter = new UnitTestResultFormatter();
		System.out.println(formatter.visit(results));
		assertFalse(results.isPassed());
	}

	public UnitTest setup(String path) throws IOException {

		File file = new File(path);
		ANTLRFileStream stream = new ANTLRFileStream(path);
		UnitTestLexer lexer = new UnitTestLexer(stream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		UnitTestParser parser = new UnitTestParser(tokens);
		visitor = new UnitTestWalker(file);
		parser.removeErrorListeners();
		parser.addErrorListener(visitor);

		UnitTest program = visitor.visitUnitTest(parser.unitTest());

		if (!visitor.getErrors().isEmpty()) {
			System.out.println(visitor.getErrors());
			System.out.println(visitor.getWarnings());
			return null;
		}

		return program;

	}

	protected UnitTestResult runTest(String testFileName) throws IOException,
	GOALCommandCancelledException, GOALParseException,
	GOALLaunchFailureException, MessagingException,
	InterruptedException, Exception {
		UnitTest unitTest = setup(testFileName);

		assertNotNull(unitTest);

		UnitTestRun testRun = new UnitTestRun(unitTest);

		UnitTestRunResultInspector inspector = new UnitTestRunResultInspector(
				unitTest);
		testRun.setResultInspector(inspector);
		testRun.run();

		return inspector.getResults();

	}

	@After
	public void tearDown() {
		visitor = null;
	}

}