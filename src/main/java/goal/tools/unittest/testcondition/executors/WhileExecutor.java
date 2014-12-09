package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestBoundaryException;
import krTools.language.Substitution;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testcondition.While;

/**
 * While operator. When the mental state condition evaluated by this operator
 * does not hold at some point during the execution of the actions in the
 * EvaluateIn section, the corresponding agent is terminated.
 *
 * @author V.Koeman
 */
public class WhileExecutor extends TestConditionExecutor {
	private final While _while;
	private boolean first = false;

	public WhileExecutor(While _while) {
		this._while = _while;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this._while);
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runState,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return WhileExecutor.class.getSimpleName() + "Evaluator";
			}

			@Override
			public void firstEvaluation() {
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				if (!WhileExecutor.this.first) {
					WhileExecutor.this.first = true;
				} else if (evaluate(runState, substitution, getQuery())
						.isEmpty()) {
					setPassed(true);
					throw new TestBoundaryException("While boundary reached");
				}
			}

			@Override
			public void lastEvaluation() {
				if (!isPassed()) {
					setPassed(false);
				}
			}

			@Override
			public <T> T accept(ResultFormatter<T> formatter) {
				return formatter.visit(this);
			}
		};
	}

	@Override
	public TestCondition getCondition() {
		return this._while;
	}
}
