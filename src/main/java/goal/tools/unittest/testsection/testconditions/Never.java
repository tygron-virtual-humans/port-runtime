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
 * Never operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should never hold during the execution
 * of the actions in the EvaluateIn rule.
 *
 * @author V.Koeman
 */
public class Never extends TestCondition {

	/**
	 * Constructs a new never operator for the mental state condition.
	 *
	 * @param query
	 *            mental state condition to test
	 */
	public Never(MentalStateCondition query) {
		super(query);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public String toString() {
		return "Never [query=" + this.query + "]";
	}

	@Override
	public void setNestedCondition(TestCondition nested) {
		throw new IllegalArgumentException(
				"Never-condition cannot have a nested condition");
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return Never.class.getSimpleName() + "Evaluator";
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
						if (!evaluation.isEmpty()) {
							setPassed(false);
							throw new TestConditionFailedException(
									"Never condition did not hold for "
											+ substitution, this);
						}
					}
				} else {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (!evaluation.isEmpty()) {
						setPassed(false);
						throw new TestConditionFailedException(
								"Never condition did not hold", this);
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
			}

			@Override
			public <T> T accept(ResultFormatter<T> formatter) {
				return formatter.visit(this);
			}
		};
	}
}
