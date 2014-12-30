package goal.tools.unittest;

import goal.parser.unittest.AbstractUnitTestTest;
import goal.tools.unittest.result.UnitTestResult;

import org.junit.Test;

/**
 * @author K.Hindriks
 *
 */
public class PostConditionTest extends AbstractUnitTestTest {

	/**
	 * Test to check whether unit test evaluators immediately pick up post
	 * condition changes (and not only by atend operator). Program performs two
	 * rules (linearall) where first rule performs user-defined action to insert
	 * fact {@code test1} and remove {@code test2} by means of postcondition and
	 * second rule performs delete action to remove fact {@code test1} and
	 * insert action to add {@code test2} again.
	 *
	 * @throws Exception
	 *             TODO: {@link AbstractUnitTestTest#runTest} throws lots of
	 *             unexpected exceptions...
	 */
	@Test
	public void test() throws Exception {
		UnitTestResult results = runTest("src/test/resources/goal/tools/unittest/postConditionTest.test2g");
		assertPassedAndPrint(results);
	}

}
