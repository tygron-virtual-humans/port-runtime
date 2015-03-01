package goal.parser.unittest.example.blocksworld;

import goal.parser.unittest.AbstractUnitTestTest;
import goal.tools.unittest.result.UnitTestResult;

import org.junit.Test;

/**
 * Runs the examples referenced in by
 * https://ii.tudelft.nl/trac/goal/wiki/Projects/Testing.
 *
 * When fixing these tests please update the documentation.
 *
 * @author mpkorstanje
 *
 */
@SuppressWarnings("javadoc")
public class ExampleTest extends AbstractUnitTestTest {

	@Test
	public void testSmallBlocksWorld() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/blocksworld/small/blocksworld.test2g");
		assertPassedAndPrint(results);
	}

	@Test
	public void testSimpleBlocksWorld() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/blocksworld/simple/blocksworld.test2g");
		assertPassedAndPrint(results);
	}

	@Test
	public void testNewOperators() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/blocksworld/simple/newOperators.test2g");
		assertPassedAndPrint(results);
	}

	@Test
	public void testImprovedBlocksWorld() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/blocksworld/improved/blocksworld.test2g");
		assertPassedAndPrint(results);
	}

	@Test
	public void testPingPong() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/pingpong/pingpong.test2g");
		assertPassedAndPrint(results);
	}

	@Test
	public void testAfter() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/parser/unittest/example/after/pingpong.test2g");
		assertFailedAndPrint(results);
	}
}
