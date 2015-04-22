package goal.parser.unittest.example.bw4t;

import goal.parser.unittest.AbstractUnitTestTest;
import goal.tools.unittest.result.UnitTestResult;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Do some testing of BW4T. To run this test, you must have the server running
 * before starting the test. #2955 the idea of this test is that we test the
 * testframework itself.
 *
 * @author W.Pasman 29jan2014
 */
@SuppressWarnings("javadoc")
public class ExampleTest extends AbstractUnitTestTest {

	@Ignore("We should make sure that the server is running.")
	@Test
	public void testBW4TExplorer() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/bw4t/bw4texplore.test2g");
		assertPassedAndPrint(results);
	}

	@Ignore("We should make sure that the server is running.")
	@Test
	public void testBW4TFinder() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/bw4t/bw4tfind.test2g");
		assertPassedAndPrint(results);
	}
}
