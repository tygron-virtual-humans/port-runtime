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
 * Always operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should always hold during the execution
 * of the actions in the EvaluateIn rule.
 *
 * @author mpkorstanje
 */
public class Always extends TestCondition {

	/**
	 * Constructs a new always operator for the mental state condition.
	 *
	 * @param query
	 *            mental state condition to test
	 */
	public Always(MentalStateCondition query) {
		super(query);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public String toString() {
		return "Always [query=" + this.query + "]";
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return Always.class.getSimpleName() + "Evaluator";
			}

			@Override
			public void firstEvaluation() {
				notifyBreakpointHit(null);
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				if (isNested()) {
					for (final Substitution substitution : getNested()) {
						final Set<Substitution> evaluation = evaluate(runstate,
								substitution, getQuery());
						if (evaluation.isEmpty()) {
							setPassed(false);
							throw new TestConditionFailedException(
									"Always condition did not hold for "
											+ substitution, this);
						}
					}
				} else if (hasNestedCondition()) {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (!evaluation.isEmpty()) {
						getNestedCondition().setNested(evaluation);
					}
				} else {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (evaluation.isEmpty()) {
						setPassed(false);
						throw new TestConditionFailedException(
								"Always condition did not hold", this);
					}
				}
			}

			@Override
			public void lastEvaluation() {
				try {
					notifyBreakpointHit(null);
					setPassed(true);
				} catch (TestConditionFailedException e) {
					// setPassed(false)
				}
				if (hasNestedCondition()) {
					final TestConditionEvaluator nested = getNestedCondition()
							.provideEvaluator(runstate, substitution);
					nested.lastEvaluation();
					setPassed(nested.isPassed());
				}
			}

			@Override
			public <T> T accept(ResultFormatter<T> formatter) {
				return formatter.visit(this);
			}
		};
	}
}
