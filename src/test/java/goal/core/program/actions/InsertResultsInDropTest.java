package goal.core.program.actions;

import goal.parser.unittest.AbstractUnitTestTest;
import goal.tools.unittest.result.UnitTestResult;

import org.junit.Test;

/**
 * Test to verify whether drop action works.
 *
 * @author wouter
 *
 */
public class InsertResultsInDropTest extends AbstractUnitTestTest {

	/**
	 * @throws Exception
	 */
	@Test
	public void insertResultsIndropTest() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/core/program/actions/insertresultsindroptest.test2g");
		assertPassedAndPrint(results);
	}

}
