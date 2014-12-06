package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.EvaluateIn;
import goal.tools.unittest.testsection.testconditions.TestCondition;
import goal.tools.unittest.testsection.testconditions.TestConditionEvaluator;

import java.util.List;

/**
 * Stores the result of the {@link TestCondition}s evaluated while executing an
 * action.
 *
 * The test is considered passed if all evaluators passed.
 *
 * @author M.P. Korstanje
 */
public class EvaluateInResult implements TestSectionResult {

	@Override
	public String toString() {
		return "EvaluateInResult [evaluateIn=" + this.evaluateIn
				+ ", evaluators=" + this.evaluators + ", passed=" + this.passed
				+ "]";
	}

	private final EvaluateIn evaluateIn;
	private final List<TestConditionEvaluator> evaluators;
	private final boolean passed;

	/**
	 * @param evaluateIn
	 * @param evaluators
	 */
	public EvaluateInResult(EvaluateIn evaluateIn,
			List<TestConditionEvaluator> evaluators) {
		this.evaluateIn = evaluateIn;
		this.evaluators = evaluators;

		this.passed = checkPassed();
	}

	private boolean checkPassed() {
		for (TestConditionEvaluator evaluator : this.evaluators) {
			if (!evaluator.isPassed()) {
				return false;
			}
		}
		return true;
	}

	public EvaluateIn getEvaluateIn() {
		return this.evaluateIn;
	}

	public List<TestConditionEvaluator> getEvaluators() {
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
