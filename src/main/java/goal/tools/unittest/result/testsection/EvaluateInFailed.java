package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.EvaluateIn;
import goal.tools.unittest.testsection.testconditions.TestConditionEvaluator;
import goal.tools.unittest.testsection.testconditions.TestConditionFailedException;

import java.util.List;

/**
 * @author M.P.Korstanje
 */
public class EvaluateInFailed extends TestSectionFailed {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 2119184965021739086L;
	private final EvaluateIn evaluateIn;

	@Override
	public String toString() {
		return "EvaluateInFailed [evaluateIn=" + this.evaluateIn
				+ ", evaluators=" + this.evaluators + ", exception="
				+ this.exception + "]";
	}

	public EvaluateIn getEvaluateIn() {
		return this.evaluateIn;
	}

	public List<TestConditionEvaluator> getEvaluators() {
		return this.evaluators;
	}

	public TestConditionEvaluator getFirstFailureCause() {
		return this.exception != null ? this.exception.getEvaluator() : null;
	}

	private final List<TestConditionEvaluator> evaluators;
	private final TestConditionFailedException exception;

	/**
	 * @param evaluateIn
	 * @param evaluators
	 */
	public EvaluateInFailed(EvaluateIn evaluateIn,
			List<TestConditionEvaluator> evaluators) {
		this(evaluateIn, evaluators, null);
	}

	/**
	 * @param evaluateIn
	 * @param evaluators
	 * @param exception
	 */
	public EvaluateInFailed(EvaluateIn evaluateIn,
			List<TestConditionEvaluator> evaluators,
			TestConditionFailedException exception) {
		this.evaluateIn = evaluateIn;
		this.evaluators = evaluators;
		this.exception = exception;

	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
