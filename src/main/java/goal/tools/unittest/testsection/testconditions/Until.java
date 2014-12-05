package goal.tools.unittest.testsection.testconditions;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import krTools.language.Substitution;
import languageTools.program.agent.msc.MentalStateCondition;

/**
 * Until operator. When the mental state condition evaluated by this operator
 * holds at some point during the execution of the actions in the EvaluateIn
 * section, the corresponding agent is terminated.
 *
 * @author V.Koeman
 */
public class Until extends TestCondition {
	private boolean first = false;

	/**
	 * Constructs a new StopWhen operator
	 *
	 * @param query
	 *            to evaluate
	 */
	public Until(MentalStateCondition query) {
		super(query);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public String toString() {
		return "Until [query=" + query + "]";
	}

	@Override
	public void setNestedCondition(TestCondition nested) {
		throw new IllegalArgumentException(
				"Boundaries cannot have a nested condition");
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runState,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return Until.class.getSimpleName() + "Evaluator";
			}

			@Override
			public void firstEvaluation() {
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				if (!first) {
					first = true;
				} else if (!evaluate(runState, substitution, getQuery())
						.isEmpty()) {
					setPassed(true);
					throw new TestBoundaryException("Until boundary reached");
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
}
