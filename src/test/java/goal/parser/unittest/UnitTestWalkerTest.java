package goal.parser.unittest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class UnitTestWalkerTest extends AbstractUnitTestTest {

	@Test
	public void testCorrectMinimal() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctMinimal.test2g");

		assertNoMessages();

	}

	@Test
	public void testCorrectExhaustive() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctExhaustive.test2g");

		System.out.println(this.visitor.getErrors());

		assertNoMessages();

	}

	@Test
	public void testCorrectExhaustiveModuleActions() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctExhaustiveModuleActions.test2g");

		System.out.println(this.visitor.getErrors());

		assertNoMessages();

	}

	@Test
	public void testCorrectMissingModuleUnderTest() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctMissingModuleUnderTest.test2g");

		assertNoMessages();

	}

	@Test
	public void testIncorrectMissingAgentUnderTest() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectMissingAgentUnderTest.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectMissingAgentUnderTestId() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectMissingAgentUnderTestId.test2g");

		// FIXME: Too many errors.
		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(9, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectMissingMasUnderTest() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectMissingMasUnderTest.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectMissingMasFile() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectMissingMasFile.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectInvalidMasFile() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectInvalidMasFile.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectDuplicateAgentNames() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectDuplicateAgentNames.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectNonExistingModule() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectNonExistingModule.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(3, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectWrongNumberOfParameters() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectWrongNumberOfParameters.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testIncorrectUnknownAgent() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectUnknownAgent.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());

	}

	@Test
	public void testCorrectMASInGOALAgentFolder() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctMASInGOALAgentFolder.test2g");

		assertNoMessages();
	}

	@Test
	public void testIncorrectKRLangIncorrect() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectKRLangIncorrect.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(9, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());
	}

	@Test
	public void testIncorrectKRLangIncorrectInMentalStateTest()
			throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectKRLangIncorrectInMentalStateTest.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(6, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());
	}

	@Test
	public void testIncorrectSameNameDifferentGOALFiles() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectSameNameDifferentGOALFiles.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());
	}

	@Test
	public void testIncorrectSameNameDifferentGOALFiles2() throws IOException {
		setup("src/test/resources/goal/parser/unittest/incorrectSameNameDifferentGOALFiles2.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());
	}

	@Test
	public void testCorrectMinimalLTL() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctMinimalLTL.test2g");

		assertNoMessages();
	}

	@Test
	public void testCorrectExhaustiveLTL() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctExhaustiveLTL.test2g");

		assertNoMessages();
	}

	@Test
	public void testCorrectModuleAction() throws IOException {
		setup("src/test/resources/goal/parser/unittest/correctModuleAction.test2g");

		assertNoMessages();
	}

	@Test
	public void testMain() throws IOException {
		setup("src/test/resources/goal/parser/unittest/main.test2g");

		assertNoMessages();
	}

	@Test
	public void testNoArgument() throws IOException {
		setup("src/test/resources/goal/parser/unittest/noargument.test2g");

		assertNoMessages();
	}

	@Test
	public void testModuleTwoArguments() throws IOException {
		setup("src/test/resources/goal/parser/unittest/moduleTwoArguments.test2g");

		assertFalse(this.visitor.getErrors().isEmpty());
		// assertEquals(1, visitor.getErrors().size());
		assertTrue(this.visitor.getWarnings().isEmpty());
	}

	private void assertNoMessages() {
		assertTrue(this.visitor.getErrors().isEmpty());
		assertTrue(this.visitor.getWarnings().isEmpty());
	}

}
