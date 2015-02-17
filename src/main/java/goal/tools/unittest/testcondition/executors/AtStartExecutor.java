package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.Module;
import languageTools.program.test.testcondition.AtStart;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * AtStart operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold after the execution of the
 * specified module in the EvaluateIn rule.
 *
 * @author K.Hindriks
 */
public class AtStartExecutor extends TestConditionExecutor {
	private final AtStart atstart;

	public AtStartExecutor(AtStart atstart) {
		this.atstart = atstart;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.atstart);
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public void firstEvaluation() {
				if (AtStartExecutor.this.atstart.getModule() == null) {
					evaluation();
				}
			}

			private void evaluation() throws TestConditionFailedException {
				if (isNested()) {
					for (final Substitution substitution : getNested()) {
						final Set<Substitution> evaluation = evaluate(runstate,
								substitution, getQuery());
						if (evaluation.isEmpty()) {
							setPassed(false);
							throw new TestConditionFailedException(
									"The nested condition "
											+ AtStartExecutor.this.atstart
											+ " did not hold.", this);
						}
					}
					setPassed(true);
				} else if (hasNestedExecutor()) {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					getNestedExecutor().setNested(evaluation);
					if (!evaluation.isEmpty()) {
						setPassed(true);
					}
				} else {
					final Set<Substitution> evaluation = evaluate(runstate,
							substitution, getQuery());
					if (evaluation.isEmpty()) {
						setPassed(false);
						throw new TestConditionFailedException("The condition "
								+ AtStartExecutor.this.atstart
								+ " did not hold.", this);
					} else {
						setPassed(true);
					}
				}
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				if (AtStartExecutor.this.atstart.getModule() != null
						&& event != null && !isPassed()) {
					switch (event.getChannel()) {
					case EVENT_MODULE_ENTRY:
					case MAIN_MODULE_ENTRY:
					case INIT_MODULE_ENTRY:
					case USER_MODULE_ENTRY:
						Module test = ((Module) event.getAssociatedObject());
						if (AtStartExecutor.this.atstart.getModule().equals(
								test)) {
							break;
						} else {
							return;
						}
					default:
						return;
					}
					evaluation();
				}
			}

			@Override
			public void lastEvaluation() {
				if (hasNestedExecutor()) {
					final TestConditionEvaluator nested = getNestedExecutor()
							.provideEvaluator(runstate, substitution);
					nested.lastEvaluation();
					if (nested.getPassed() == TestConditionEvaluation.UNKNOWN) {
						nested.setPassed(true);
					}
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
		return this.atstart;
	}
}
