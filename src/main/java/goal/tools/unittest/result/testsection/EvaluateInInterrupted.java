package goal.tools.unittest.result.testsection;

import goal.tools.debugger.DebuggerKilledException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

/**
 * @author M.P.Korstanje
 */
public class EvaluateInInterrupted extends TestSectionInterupted {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 2119184965021739086L;
	private final Set<TestConditionExecutor> evaluators;

	/**
	 * @param evaluateIn
	 * @param evaluators
	 * @param exception
	 */
	public EvaluateInInterrupted(EvaluateInExecutor evaluateIn,
			Set<TestConditionExecutor> evaluators,
			DebuggerKilledException exception) {
		super(evaluateIn, exception);
		this.evaluators = evaluators;
	}

	/**
	 * @return the evaluators of the interrupted test section.
	 */
	public Set<TestConditionExecutor> getEvaluators() {
		return this.evaluators;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
