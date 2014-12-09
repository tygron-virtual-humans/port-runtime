package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.Eventually;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Eventually operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold at some point during the
 * execution of the actions in the EvaluateIn section.
 *
 * @author mpkorstanje
 */
public class EventuallyExecutor extends TestConditionExecutor {
	private final Eventually eventually;
	private boolean nestedOnce;

	public EventuallyExecutor(Eventually eventually) {
		this.eventually = eventually;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.eventually);
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return EventuallyExecutor.class.getSimpleName() + "Evaluator";
			}

			@Override
			public void firstEvaluation() {
				notifyBreakpointHit(null);
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				if (!isPassed()) {
					if (isNested()) {
						boolean passed = true;
						for (final Substitution substitution : getNested()) {
							final Set<Substitution> evaluation = evaluate(
									runstate, substitution, getQuery());
							if (evaluation.isEmpty()) {
								passed = false;
								break;
							}
						}
						if (passed) {
							setPassed(true);
						}
					} else if (hasNestedExecutor()) {
						if (!EventuallyExecutor.this.nestedOnce) {
							final Set<Substitution> evaluation = evaluate(
									runstate, substitution, getQuery());
							if (!evaluation.isEmpty()) {
								getNestedExecutor().setNested(evaluation);
								EventuallyExecutor.this.nestedOnce = true;
							}
						}
					} else {
						final Set<Substitution> evaluation = evaluate(runstate,
								substitution, getQuery());
						if (!evaluation.isEmpty()) {
							setPassed(true);
						}
					}
				}
			}

			@Override
			public void lastEvaluation() {
				notifyBreakpointHit(null);
				if (hasNestedExecutor()) {
					final TestConditionEvaluator nested = getNestedExecutor()
							.provideEvaluator(runstate, substitution);
					nested.lastEvaluation();
					setPassed(nested.isPassed());
				} else if (!isPassed()) {
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
		return this.eventually;
	}
}
