package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.Module;
import languageTools.program.test.testcondition.AtEnd;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * AtEnd operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold after the execution of the
 * actions in the EvaluateIn rule.
 *
 * @author mpkorstanje
 */
public class AtEndExecutor extends TestConditionExecutor {
	private final AtEnd atend;

	public AtEndExecutor(AtEnd atend) {
		this.atend = atend;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.atend);
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return AtEndExecutor.class.getSimpleName() + "Evaluator";
			}

			@Override
			public void firstEvaluation() {
				// Does nothing
			}

			private void evaluation() throws TestConditionFailedException {
				if (isNested()) {
					for (final Substitution substitution : getNested()) {
						final Set<Substitution> evaluation = evaluate(runstate,
								substitution, getQuery());
						if (evaluation.isEmpty()) {
							setPassed(false);
							throw new TestConditionFailedException(
									"AtEnd condition did not hold", this);
						}
					}
					setPassed(true);
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
								"AtEnd condition did not hold", this);
					} else {
						setPassed(true);
					}
				}
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				final Module module = AtEndExecutor.this.atend.getModule();
				if (module != null && event != null && !isPassed()) {
					switch (event.getChannel()) {
					case EVENT_MODULE_EXIT:
					case MAIN_MODULE_EXIT:
					case INIT_MODULE_EXIT:
					case USER_MODULE_EXIT:
						Module test = ((Module) event.getAssociatedObject());
						if (AtEndExecutor.this.atend.getModule().equals(test)) {
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
				if (AtEndExecutor.this.atend.getModule() == null) {
					evaluation();
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
		return this.atend;
	}
}
