package goal.tools.unittest.testcondition.executors;

import goal.core.executors.MentalStateConditionExecutor;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugObserver;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.NOPDebugger;
import goal.tools.unittest.result.ResultFormatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.actions.UserSpecAction;
import languageTools.program.agent.msc.MentalStateCondition;
import languageTools.program.test.TestMentalStateCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Base to evaluate {@link TestConditionExecutor}s in the context of a running
 * agent. The Evaluator is installed on the debugger and receives a call back
 * whenever an action has been executed.
 *
 * @author mpkorstanje
 */
public abstract class TestConditionEvaluator implements DebugObserver {

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
	}

	private final TestConditionExecutor executor;
	private TestConditionEvaluation passed = TestConditionEvaluation.UNKNOWN;

	/**
	 * Creates an evaluator.
	 *
	 * @param executor
	 *            to evaluate
	 */
	public TestConditionEvaluator(TestConditionExecutor executor) {
		this.executor = executor;
	}

	/**
	 * @return query used by the {@link TestConditionExecutor}.
	 */
	public TestMentalStateCondition getQuery() {
		return this.executor.getCondition().getQuery();
	}

	/**
	 * @return {@link TestConditionExecutor} being evaluated.
	 */
	public TestConditionExecutor getConditionExecutor() {
		return this.executor;
	}

	/**
	 * Evaluates a mental state query on the agent's {@link RunState}.
	 *
	 * @param runState
	 *            of the agent.
	 * @param substitution
	 *            the current substitution.
	 * @param query
	 *            the mental state query.
	 * @return result of evaluating the mental state query.
	 */
	protected Set<Substitution> evaluate(RunState<? extends Debugger> runState,
			Substitution substitution, TestMentalStateCondition testquery) {
		// Using a NOPDebugger here to avoid endless recursion on observer
		Debugger debugger = new NOPDebugger(getObserverName());
		// If this condition bound a variable to the substitution,
		// reset that variable so we can set it again, but store it
		// so we can put it back when the evaluation below failed
		final Map<Var, Term> removed = new HashMap<>();
		for (final Var pre : this.executor.getBoundByMe()) {
			Term term = substitution.get(pre);
			removed.put(pre, term);
			substitution.remove(pre);
		}

		final Set<Substitution> result = new HashSet<>();
		Substitution temp = substitution.clone();
		for (UserSpecAction action : testquery.getActions()) {
			Substitution check = (runState.getLastAction() == null) ? null
					: action.mgu(runState.getLastAction());
			if (check == null) {
				return result;
			} else {
				temp = temp.combine(check);
			} // TODO: what about multiple actions in one rule (combo)?
		}
		for (MentalStateCondition query : testquery.getConditions()) {
			try {
				Set<Substitution> res = new MentalStateConditionExecutor(
						query.applySubst(temp)).evaluate(
								runState.getMentalState(), debugger);
				result.addAll(res);
			} catch (Exception e) {
				// FIXME: this exception can occur (and is expected)
				Set<Substitution> res = new MentalStateConditionExecutor(query)
						.evaluate(runState.getMentalState(), debugger);
				result.addAll(res);
			}
		}

		// Update the substitution when needed,
		// registering any new variables this condition has bounded,
		// and restoring any deleted ones when the evaluation failed
		if (result.isEmpty()) {
			for (final Var var : removed.keySet()) {
				substitution.addBinding(var, removed.get(var));
			}
		} else {
			for (final Substitution apply : result) {
				for (final Var var : apply.getVariables()) {
					final Term term = apply.get(var);
					if (term.isClosed() && this.executor.addBoundVar(var)) {
						try {
							substitution.addBinding(var, term);
						} catch (final RuntimeException ignore) {
						}
					}
				}
			}
		}
		return result;
	}

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
		} else {
			this.passed = TestConditionEvaluation.FAILED;
		}
	}

	/**
	 * Use this method for clearing the current test condition evaluation
	 */
	protected void reset() {
		this.passed = TestConditionEvaluation.UNKNOWN;
	}

	/**
	 * @return true iff the test condition is passed
	 */
	public boolean isPassed() {
		return this.passed == TestConditionEvaluation.PASSED;
	}

	public TestConditionEvaluation getPassed() {
		return this.passed;
	}

	/**
	 * @return report that says test condition "passed", "failed", or was
	 *         "interrupted".
	 */
	public String getSummaryReport() {
		switch (this.passed) {
		case FAILED:
			return "failed";
		case PASSED:
			return "passed";
		case UNKNOWN:
			// if we don't know whether test condition was passed or not, this
			// means the test must have been interrupted.
			return "interrupted";
		default:
			// should never happen...
			return "unknown";
		}
	}

	@Override
	public String getObserverName() {
		return "TestCondition";
	}

	/**
	 * Last evaluation, called after the action of {@link EvaluateIn} has
	 * completed.
	 */
	public abstract void lastEvaluation();

	/**
	 * Accepts a ResultFormatter and calls its visit method.
	 *
	 * @param formatter
	 *            to format results
	 * @return result of calling the formatters visit method
	 */
	public abstract <T> T accept(ResultFormatter<T> formatter);

	/**
	 * First evaluation, called before the action of {@link EvaluateIn} has is
	 * executed.
	 */
	public abstract void firstEvaluation();
}
