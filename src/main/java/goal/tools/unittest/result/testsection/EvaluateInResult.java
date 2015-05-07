package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Stores the result of the {@link TestConditionExecutor}s evaluated while
 * executing an action.
 *
 * The test is considered passed if all evaluators passed.
 *
 * @author M.P. Korstanje
 */
public class EvaluateInResult implements TestSectionResult {
	private final EvaluateIn evaluateIn;
	private final TestConditionExecutor[] evaluators;
	private final boolean passed;

	/**
	 * @param evaluateIn
	 * @param evaluators
	 */
	public EvaluateInResult(EvaluateIn evaluateIn,
			TestConditionExecutor[] evaluators) {
		this.evaluateIn = evaluateIn;
		this.evaluators = evaluators;
		this.passed = checkPassed();
	}

	private boolean checkPassed() {
		for (TestConditionExecutor evaluator : this.evaluators) {
			if (!evaluator.isPassed()) {
				return false;
			}
		}
		return true;
	}

	public EvaluateIn getEvaluateIn() {
		return this.evaluateIn;
	}

	public TestConditionExecutor[] getEvaluators() {
		return this.evaluators;
	}

	public boolean isPassed() {
		return this.passed;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
