package goal.parser.unittest.example.tictactoe;

import goal.parser.unittest.AbstractUnitTestTest;
import goal.tools.unittest.result.UnitTestResult;

/**
 * Test for the learning algorithm with tictactoe
 *
 * @author W.Pasman 13may2014
 */
@SuppressWarnings("javadoc")
public class ExampleTest extends AbstractUnitTestTest {

	// @Test
	public void testTTTLearning() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/tictactoe/ttt.test2g");
		assertPassedAndPrint(results);
	}

}
