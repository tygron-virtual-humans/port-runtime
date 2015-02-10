package goal.tools.unittest.result.testcondition;

import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;
import goal.tools.unittest.testcondition.executors.TestConditionEvaluator;
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;

/**
 * Exception thrown when the {@link TestConditionExecutor} fails. This is an
 * unchecked exception to allow evaluation to end immediately.
 *
 * @author mpkorstanje
 */
public class TestConditionFailedException extends GOALRuntimeErrorException {
	/**
	 * Date of last change
	 */
	private static final long serialVersionUID = 201312122233L;
	private final TestConditionEvaluator evaluator;

	/**
	 * Creates a new failed test condition exception.
	 *
	 * @param message
	 *            of the exception.
	 * @param evaluator
	 *            that failed.
	 */
	public TestConditionFailedException(String message,
			TestConditionEvaluator evaluator) {
		super(message);
		this.evaluator = evaluator;
	}

	/**
	 * @return the evaluator that failed to evaluate.
	 */
	public TestConditionEvaluator getEvaluator() {
		return this.evaluator;
	}
}
