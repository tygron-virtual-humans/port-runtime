package goal.core.program.actions;

import goal.parser.unittest.AbstractUnitTestTest;
import goal.tools.unittest.result.UnitTestResult;

import org.junit.Test;

/**
 * @author K.Hindriks
 *
 */
public class DropTest extends AbstractUnitTestTest {

	@Test
	public void dropTest() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/core/program/actions/droptest.test2g");
		assertPassedAndPrint(results);
	}
}
