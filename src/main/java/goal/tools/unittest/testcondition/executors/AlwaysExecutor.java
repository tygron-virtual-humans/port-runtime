package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.Always;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Always operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should always hold during the execution
 * of the actions in the EvaluateIn rule.
 *
 * @author mpkorstanje
 */
public class AlwaysExecutor extends TestConditionExecutor {
	private final Always always;

	public AlwaysExecutor(Always always) {
		this.always = always;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.always);
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
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
									"The nested condition "
											+ AlwaysExecutor.this.always
											+ " did not hold.", this);
						}
					}
				} else if (hasNestedExecutor()) {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (!evaluation.isEmpty()) {
						getNestedExecutor().setNested(evaluation);
					}
				} else {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (evaluation.isEmpty()) {
						setPassed(false);
						throw new TestConditionFailedException(
								"The condition " + AlwaysExecutor.this.always
										+ " did not hold.", this);
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
				if (hasNestedExecutor()) {
					final TestConditionEvaluator nested = getNestedExecutor()
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

	@Override
	public TestCondition getCondition() {
		return this.always;
	}
}
