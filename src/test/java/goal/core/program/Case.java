package goal.core.program;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class Case extends SimpleProgramTest {

	@Test
	public void QuotedAtoms() throws Exception {
		assertSame(RunResult.OK,
				runAgent("src/test/resources/goal/core/program/testcase.goal"));
	}

}