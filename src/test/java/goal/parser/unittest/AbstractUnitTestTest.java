package goal.parser.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import goal.tools.UnitTestRun;
import goal.tools.UnitTestRunResultInspector;
import goal.tools.errorhandling.exceptions.GOALCommandCancelledException;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.Loggers;
import goal.tools.unittest.result.UnitTestResult;
import goal.tools.unittest.result.UnitTestResultFormatter;

import java.io.IOException;

import krTools.errors.exceptions.ParserException;
import languageTools.analyzer.test.TestValidator;
import languageTools.program.test.UnitTest;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

@SuppressWarnings("javadoc")
public class AbstractUnitTestTest {
	protected TestValidator visitor;

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
		this.visitor = new TestValidator(path);
		this.visitor.validate();
		UnitTest program = this.visitor.getProgram();
		if (program != null && program.isValid()) {
			return program;
		} else {
			System.out.println(this.visitor.getSyntaxErrors());
			System.out.println(this.visitor.getErrors());
			System.out.println(this.visitor.getWarnings());
			return null;
		}
	}

	protected UnitTestResult runTest(String testFileName) throws IOException,
			GOALCommandCancelledException, ParserException,
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
		this.visitor = null;
	}
}