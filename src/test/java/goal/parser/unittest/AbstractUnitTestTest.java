package goal.parser.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import goal.tools.UnitTestRun;
import goal.tools.UnitTestRunResultInspector;
import goal.tools.errorhandling.exceptions.GOALRunFailedException;
import goal.tools.logging.Loggers;
import goal.tools.unittest.result.UnitTestResult;
import goal.tools.unittest.result.UnitTestResultFormatter;

import java.io.IOException;
import java.util.Set;

import languageTools.analyzer.test.TestValidator;
import languageTools.errors.Message;
import languageTools.program.test.UnitTest;

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
			Set<Message> errors = this.visitor.getErrors();
			errors.addAll(this.visitor.getSyntaxErrors());
			System.out.println(errors);
			return null;
		}
	}

	protected UnitTestResult runTest(String testFileName)
			throws GOALRunFailedException {
		UnitTest unitTest;
		try {
			unitTest = setup(testFileName);
		} catch (IOException e) {
			throw new GOALRunFailedException("error while reading test file "
					+ testFileName, e);
		}

		assertNotNull(unitTest);

		UnitTestRun testRun = new UnitTestRun(unitTest);
		testRun.setDebuggerOutput(true);
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