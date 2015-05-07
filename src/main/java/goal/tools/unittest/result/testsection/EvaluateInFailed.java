package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

/**
 * @author M.P.Korstanje
 */
public class EvaluateInFailed extends TestSectionFailed {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 2119184965021739086L;
	private final EvaluateInExecutor evaluateIn;

	public EvaluateInExecutor getEvaluateIn() {
		return this.evaluateIn;
	}

	public TestConditionExecutor[] getEvaluators() {
		return this.evaluators;
	}

	public TestConditionExecutor getFirstFailureCause() {
		return this.exception != null ? this.exception.getEvaluator() : null;
	}

	private final TestConditionExecutor[] evaluators;
	private final TestConditionFailedException exception;

	/**
	 * @param evaluateIn
	 * @param evaluators
	 */
	public EvaluateInFailed(EvaluateInExecutor evaluateIn,
			TestConditionExecutor[] evaluators) {
		this(evaluateIn, evaluators, null);
	}

	/**
	 * @param evaluateIn
	 * @param evaluators
	 * @param exception
	 */
	public EvaluateInFailed(EvaluateInExecutor evaluateIn,
			TestConditionExecutor[] evaluators,
			TestConditionFailedException exception) {
		super(exception);
		this.evaluateIn = evaluateIn;
		this.evaluators = evaluators;
		this.exception = exception;

	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
