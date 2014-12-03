package goal.tools.unittest.testsection.testconditions;

import krTools.language.Substitution;
import languageTools.program.agent.Module;
import languageTools.program.agent.msc.MentalStateCondition;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.EvaluateIn;

import java.util.Set;

/**
 * AtEnd operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold after the execution of the
 * actions in the EvaluateIn rule.
 *
 * @author mpkorstanje
 */
public class AtEnd extends TestCondition {
	private final Module module;

	/**
	 * Constructs a new AtEnd operator
	 *
	 * @param query
	 *            to evaluate at the end of a module
	 * @param module
	 *            the module (optionally null)
	 */
	public AtEnd(MentalStateCondition query, Module module) {
		super(query);
		this.module = module;
	}

	/**
	 * @return A textual representation of the module associated with this
	 *         operator (empty string if none)
	 */
	public String getModuleName() {
		return (this.module == null) ? "" : ("[" + module.getName() + "]");
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public String toString() {
		return "AtEnd [query=" + query + ", module=" + getModuleName() + "]";
	}

	@Override
	public TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		return new TestConditionEvaluator(this) {
			@Override
			public String getObserverName() {
				return AtEnd.class.getSimpleName() + "Evaluator";
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
							throw new TestConditionFailedException("AtEnd"
									+ getModuleName()
									+ " condition did not hold", this);
						}
					}
					setPassed(true);
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
						throw new TestConditionFailedException("AtEnd"
								+ getModuleName() + " condition did not hold",
								this);
					} else {
						setPassed(true);
					}
				}
			}

			@Override
			public void notifyBreakpointHit(DebugEvent event) {
				if (module != null && event != null && !isPassed()) {
					switch (event.getChannel()) {
					case EVENT_MODULE_EXIT:
					case MAIN_MODULE_EXIT:
					case INIT_MODULE_EXIT:
					case USER_MODULE_EXIT:
						if (module.equals(event.getAssociatedObject())) {
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
				if (module == null) {
					evaluation();
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
