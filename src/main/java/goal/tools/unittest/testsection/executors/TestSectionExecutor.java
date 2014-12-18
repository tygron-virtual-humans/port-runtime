package goal.tools.unittest.testsection.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;
import languageTools.program.test.testsection.AssertTest;
import languageTools.program.test.testsection.DoActionSection;
import languageTools.program.test.testsection.EvaluateIn;
import languageTools.program.test.testsection.TestSection;

public abstract class TestSectionExecutor {
	/**
	 * Get the parsed {@link TestSection}.
	 *
	 * @return {@link TestSection}
	 */
	abstract public TestSection getSection();

	/**
	 * Runs the test section.
	 *
	 * @param runState
	 * @return the result of running the section.
	 * @throws TestSectionFailed
	 */
	abstract public TestSectionResult run(
			RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed;

	/**
	 * Results are formatted by using double dispatch. Implementing classes
	 * should call <code>ResultFormatter#visit(this)</code>
	 *
	 * @param formatter
	 *            used to format results
	 * @return result of formatting
	 */
	public abstract <T> T accept(ResultFormatter<T> formatter);

	public static TestSectionExecutor getTestConditionExecutor(
			TestSection section) {
		if (section instanceof AssertTest) {
			return new AssertExecutor((AssertTest) section);
		} else if (section instanceof DoActionSection) {
			return new DoActionExecutor((DoActionSection) section);
		} else if (section instanceof EvaluateIn) {
			return new EvaluateInExecutor((EvaluateIn) section);
		} else {
			return null;
		}
	}
}
