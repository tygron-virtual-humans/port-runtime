package goal.tools.unittest.testcondition.executors;

import goal.core.executors.MentalStateConditionExecutor;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.NOPDebugger;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Term;
import languageTools.program.agent.actions.UserSpecAction;
import languageTools.program.test.TestMentalStateCondition;
import languageTools.program.test.testcondition.Always;
import languageTools.program.test.testcondition.AtEnd;
import languageTools.program.test.testcondition.Eventually;
import languageTools.program.test.testcondition.Never;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testcondition.Until;
import languageTools.program.test.testcondition.While;

/**
 * Abstract base for any test condition. Test conditions are evaluated in the
 * context of a running agent and need to provide an evaluator that can do so.
 *
 * @author mpkorstanje
 */
public abstract class TestConditionExecutor {
	/**
	 * A test condition can have one out of three evaluations:
	 * {@link TestConditionEvaluation#PASSED},
	 * {@link TestConditionEvaluation#FAILED}, or
	 * {@link TestConditionEvaluation#UNKNOWN}.
	 *
	 * @author K.Hindriks
	 */
	public enum TestConditionEvaluation {
		/**
		 * PASSED means that the test condition has been passed (holds).
		 */
		PASSED,
		/**
		 * FAILED means that the test condition failed during the test (did not
		 * hold).
		 */
		FAILED,
		/**
		 * UNKNOWN means that the test has not yet been completed and the test
		 * condition has not yet been completely evaluated.
		 */
		UNKNOWN;

		@Override
		public String toString() {
			switch (this) {
			case FAILED:
				return "failed";
			case PASSED:
				return "passed";
			case UNKNOWN:
				// if we don't know whether test condition was passed or not,
				// this means the test must have been interrupted.
				return "interrupted";
			default:
				// should never happen...
				return "unknown";
			}
		}
	}

	public enum TestEvaluationChannel {
		MODULE_ENTRY, MODULE_EXIT, ACTION_EXECUTED, STOPTEST;

		public static TestEvaluationChannel fromDebugChannel(Channel debug) {
			switch (debug) {
			case ACTION_EXECUTED_BUILTIN:
			case ACTION_EXECUTED_USERSPEC:
				return ACTION_EXECUTED;
			case INIT_MODULE_ENTRY:
			case EVENT_MODULE_ENTRY:
			case MAIN_MODULE_ENTRY:
			case USER_MODULE_ENTRY:
				return MODULE_ENTRY;
			case INIT_MODULE_EXIT:
			case EVENT_MODULE_EXIT:
			case MAIN_MODULE_EXIT:
			case USER_MODULE_EXIT:
				return MODULE_EXIT;
			default:
				return STOPTEST;
			}
		}
	}

	private TestConditionEvaluation passed = TestConditionEvaluation.UNKNOWN;
	private final Substitution substitution;
	protected final RunState<? extends Debugger> runstate;
	protected final EvaluateInExecutor parent;
	protected DebugEvent current;

	public TestConditionExecutor(Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		this.substitution = substitution;
		this.runstate = runstate;
		this.parent = parent;
	}

	public Substitution getSubstitution() {
		return this.substitution;
	}

	/**
	 * Get the parsed {@link TestCondition}.
	 *
	 * @return {@link TestCondition}
	 */
	abstract public TestCondition getCondition();

	/**
	 * Evaluates a mental state query on the agent's {@link RunState}.
	 *
	 * @param runstate
	 *            of the agent.
	 * @param substitution
	 *            the current substitution.
	 * @param query
	 *            the mental state query.
	 * @return result of evaluating the mental state query.
	 */
	protected Set<Substitution> evaluate() {
		// Using a NOPDebugger here to avoid endless recursion on observer
		Debugger debugger = new NOPDebugger(getClass().getSimpleName());
		TestMentalStateCondition testquery = getCondition().getQuery();

		Substitution temp = this.substitution.clone();
		UserSpecAction prev = this.runstate.getLastAction();
		if (prev == null) {
			prev = new UserSpecAction("", new ArrayList<Term>(0), false, null,
					null, null, null);
		}

		Set<Substitution> result = new HashSet<>();
		if (testquery.isActionFirst()) {
			UserSpecAction action = testquery.getAction().getAction();
			Substitution check = action.getSignature().equals(
					prev.getSignature()) ? action.applySubst(temp).mgu(prev)
							: null;
					if (testquery.getAction().isPositive()) {
						if (check == null) {
							return new HashSet<Substitution>(0);
						} else {
							temp = temp.combine(check);
						}
					} else if (check != null) {
						return new HashSet<Substitution>(0);
					}
					if (testquery.getCondition() == null) {
						result.add(check);
						return result;
					}
		}
		if (testquery.getCondition() != null) {
			try {
				result = new MentalStateConditionExecutor(testquery
						.getCondition().applySubst(temp)).evaluate(
								this.runstate.getMentalState(), debugger);
			} catch (GOALDatabaseException  | NullPointerException e) { // FIXME #3487 
				try {
					result = new MentalStateConditionExecutor(
							testquery.getCondition()).evaluate(
									this.runstate.getMentalState(), debugger);
				} catch (GOALDatabaseException e1) {
					throw new IllegalStateException("testcondition evaluation of "+testquery+" fails",e1);
				}
			}
			if (!result.isEmpty() && testquery.getAction() != null) {
				Substitution[] copy = result.toArray(new Substitution[result
						.size()]);
				result.clear();
				for (Substitution sub : copy) {
					UserSpecAction action = testquery.getAction().getAction();
					Substitution check = action.getSignature().equals(
							prev.getSignature()) ? action.applySubst(sub).mgu(
							prev) : null;
					if (testquery.getAction().isPositive()) {
						if (check != null) {
											result.add(check);
						}
					} else if (check == null) {
						result.add(sub);
					}
				}
			}
		}

		return result;
	}

	public void evaluate(DebugEvent event) {
		this.current = event;
		TestEvaluationChannel channel = (event == null) ? TestEvaluationChannel.STOPTEST
				: TestEvaluationChannel.fromDebugChannel(event.getChannel());
		evaluate(channel);
	}

	protected abstract void evaluate(TestEvaluationChannel channel);

	/**
	 * Use this method for setting evaluation of test condition to either
	 * {@link TestConditionEvaluation#PASSED} or
	 * {@link TestConditionEvaluation#FAILED}.
	 *
	 * @param passed
	 *            {@code true} to set evaluation to
	 *            {@link TestConditionEvaluation#PASSED}; {@code false} to set
	 *            evaluation to {@link TestConditionEvaluation#FAILED}.
	 */
	protected void setPassed(boolean passed) {
		if (passed) {
			this.passed = TestConditionEvaluation.PASSED;
			this.parent.remove(this); // good == no more evaluation
		} else {
			this.passed = TestConditionEvaluation.FAILED;
		}
	}

	/**
	 * @return true iff the test condition is passed
	 */
	public boolean isPassed() {
		return this.passed == TestConditionEvaluation.PASSED;
	}

	public TestConditionEvaluation getState() {
		return this.passed;
	}

	/**
	 * Uses double dispatch to call the formatter.
	 *
	 * @param formatter
	 *            to call
	 * @return result of the formatter
	 */
	public abstract <T> T accept(ResultFormatter<T> formatter);

	public static TestConditionExecutor getTestConditionExecutor(
			TestCondition condition, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		if (condition instanceof Always) {
			return new AlwaysExecutor((Always) condition, substitution,
					runstate, parent);
		} else if (condition instanceof AtEnd) {
			return new AtEndExecutor((AtEnd) condition, substitution, runstate,
					parent);
		} else if (condition instanceof Eventually) {
			return new EventuallyExecutor((Eventually) condition, substitution,
					runstate, parent);
		} else if (condition instanceof Never) {
			return new NeverExecutor((Never) condition, substitution, runstate,
					parent);
		} else if (condition instanceof Until) {
			return new UntilExecutor((Until) condition, substitution, runstate,
					parent);
		} else if (condition instanceof While) {
			return new WhileExecutor((While) condition, substitution, runstate,
					parent);
		} else {
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getCondition() == null) ? 0 : getCondition().hashCode());
		result = prime
				* result
				+ ((this.substitution == null) ? 0 : this.substitution
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TestConditionExecutor other = (TestConditionExecutor) obj;
		if (getCondition() == null) {
			if (other.getCondition() != null) {
				return false;
			}
		} else if (!getCondition().equals(other.getCondition())) {
			return false;
		}
		if (this.substitution == null) {
			if (other.getSubstitution() != null) {
				return false;
			}
		} else if (!this.substitution.equals(other.getSubstitution())) {
			return false;
		}
		return true;
	}
}
