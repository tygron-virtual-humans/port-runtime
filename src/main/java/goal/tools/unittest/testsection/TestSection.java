package goal.tools.unittest.testsection;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.Test;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;

/**
 * Test sections consist of do statements for performing actions and modules, of
 * assert statements, and of test conditions.
 *
 * Upon successful evaluation a {@link TestSectionResult} should be provided. If
 * evaluation fails, a {@link TestSectionFailed} may be thrown.
 *
 * @see Test
 *
 * @author mpkorstanje
 */
public interface TestSection {

	/**
	 * Runs the test section.
	 *
	 * @param runState
	 * @return the result of running the section.
	 * @throws TestSectionFailed
	 */
	TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed;

	/**
	 * Results are formatted by using double dispatch. Implementing classes
	 * should call <code>ResultFormatter#visit(this)</code>
	 *
	 * @param formatter
	 *            used to format results
	 * @return result of formatting
	 */
	<T> T accept(ResultFormatter<T> formatter);
}