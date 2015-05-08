package goal.parser.unittest;

import goal.tools.unittest.result.UnitTestResult;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class UnitTestTest extends AbstractUnitTestTest {

	@Test
	public void testCorrectMinimal() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/correctMinimal.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testCorrectExhaustive() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/correctExhaustive.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testCorrectExhaustiveLTL() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/correctExhaustiveLTL.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testCountsTo100() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/CountsTo100.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testCorrectFailingLTL() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/correctFailingLTL.test2g");

		assertFailedAndPrint(results);
	}

	@Test
	public void testCorrectMinimalLTL() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/correctMinimalLTL.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testNewCorrectBoundary() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/newCorrectBoundary.test2g");

		assertPassedAndPrint(results);
	}

	// FIXME: This test references mas in local folder. @Test
	public void testCorrectMASInGOALAgentFolder() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/correctMASInGOALAgentFolder.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testExampleImprovedBlocksWorld() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/blocksworld/improved/blocksworld.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testScope() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/scope.test2g");

		assertPassedAndPrint(results);
	}

	@Test
	public void testModuleArgs() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/moduleTwoArguments.test2g");

		assertPassedAndPrint(results);
	}

    @Test
    public void testCalculateFive() throws Exception {
        UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/calculateFive.test2g");

        assertPassedAndPrint(results);
    }
}
