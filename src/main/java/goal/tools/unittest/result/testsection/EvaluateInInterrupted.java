package goal.tools.unittest.result.testsection;

import goal.tools.debugger.DebuggerKilledException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testcondition.executors.TestConditionEvaluator;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.List;

/**
 * @author M.P.Korstanje
 */
public class EvaluateInInterrupted extends TestSectionInterupted {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 2119184965021739086L;
	private final List<TestConditionEvaluator> evaluators;

	@Override
	public String toString() {
		return "EvaluateInInterrupted [evaluateIn=" + super.getTestSection()
				+ ", evaluators=" + this.evaluators + ", exception="
				+ super.getCause() + "]";
	}

	/**
	 * @param evaluateIn
	 * @param evaluators
	 * @param exception
	 */
	public EvaluateInInterrupted(EvaluateInExecutor evaluateIn,
			List<TestConditionEvaluator> evaluators,
			DebuggerKilledException exception) {
		super(evaluateIn, exception);
		this.evaluators = evaluators;
	}

	/**
	 * @return the evaluators of the interrupted test section.
	 */
	public List<TestConditionEvaluator> getEvaluators() {
		return this.evaluators;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

}
