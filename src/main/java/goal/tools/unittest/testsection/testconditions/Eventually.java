package goal.tools.unittest.testsection.testconditions;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.EvaluateIn;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.msc.MentalStateCondition;

/**
 * Eventually operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold at some point during the
 * execution of the actions in the EvaluateIn section.
 *
 * @author mpkorstanje
 */
public class Eventually extends TestCondition {
	private boolean nestedOnce = false;

	/**
	 * Constructs a new Eventually operator
	 *
	 * @param query
	 *            to evaluate at the end
	 */
	public Eventually(MentalStateCondition query) {
		super(query);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public String toString() {
		return "Eventually [query=" + query + "]";
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return Eventually.class.getSimpleName() + "Evaluator";
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
					} else if (hasNestedCondition()) {
						if (!nestedOnce) {
							final Set<Substitution> evaluation = evaluate(
									runstate, substitution, getQuery());
							if (!evaluation.isEmpty()) {
								getNestedCondition().setNested(evaluation);
								nestedOnce = true;
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
				if (hasNestedCondition()) {
					final TestConditionEvaluator nested = getNestedCondition()
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
}
