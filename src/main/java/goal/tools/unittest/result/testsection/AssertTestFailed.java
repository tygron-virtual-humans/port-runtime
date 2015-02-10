package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.executors.AssertExecutor;

public class AssertTestFailed extends TestSectionFailed {
	/**
	 *
	 */
	private static final long serialVersionUID = 8967793218598029712L;

	private final AssertExecutor test;

	public AssertExecutor getTest() {
		return this.test;
	}

	public AssertTestFailed(AssertExecutor test) {
		this.test = test;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
