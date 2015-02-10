package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.Never;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Never operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should never hold during the execution
 * of the actions in the EvaluateIn rule.
 *
 * @author V.Koeman
 */
public class NeverExecutor extends TestConditionExecutor {
	private final Never never;

	public NeverExecutor(Never never) {
		this.never = never;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.never);
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
						if (!evaluation.isEmpty()) {
							setPassed(false);
							throw new TestConditionFailedException(
									"The nested condition "
											+ NeverExecutor.this.never
											+ " did not hold.", this);
						}
					}
				} else {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (!evaluation.isEmpty()) {
						setPassed(false);
						throw new TestConditionFailedException("The condition "
								+ NeverExecutor.this.never + " did not hold.",
								this);
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

	@Override
	public TestCondition getCondition() {
		return this.never;
	}
}
