package goal.tools.unittest.testsection.testconditions;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.DebugObserver;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.NOPDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.EvaluateIn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.msc.MentalStateCondition;

/**
 * Base to evaluate {@link TestCondition}s in the context of a running agent.
 * The Evaluator is installed on the debugger and receives a call back whenever
 * an action has been executed.
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

	private final TestCondition condition;
	private TestConditionEvaluation passed = TestConditionEvaluation.UNKNOWN;

	/**
	 * Creates an evaluator.
	 *
	 * @param condition
	 *            to evaluate
	 */
	public TestConditionEvaluator(TestCondition condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return getObserverName() + " [TestCondition= " + condition
				+ ", passed=" + passed + "]";
	}

	/**
	 * @return query used by the {@link TestCondition}.
	 */
	public MentalStateCondition getQuery() {
		return condition.getQuery();
	}

	/**
	 * @return {@link TestCondition} being evaluated.
	 */
	public TestCondition getCondition() {
		return condition;
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
			Substitution substitution, MentalStateCondition query) {
		// Using a NOPDebugger here to avoid endless recursion on observer
		Debugger debugger = new NOPDebugger(getObserverName());
		// If this condition bound a variable to the substitution,
		// reset that variable so we can set it again, but store it
		// so we can put it back when the evaluation below failed
		final Map<Var, Term> removed = new HashMap<>();
		for (final Var pre : condition.getBoundByMe()) {
			Term term = substitution.get(pre);
			removed.put(pre, term);
			substitution.remove(pre);
		}
		// Run the query
		Set<Substitution> result = new HashSet<>(0);
		try {
			result = query.applySubst(substitution).evaluate(
					runState.getMentalState(), debugger);
		} catch (Exception e) {
			result = query.evaluate(runState.getMentalState(), debugger);

		}
		// Update the substitution when needed,
		// registering any new variables this condition has bounded,
		// and restoring any deleted ones when the evaluation failed
		if (result.isEmpty()) {
			for (final Var var : removed.keySet()) {
				substitution.addBinding(var, removed.get(var));
			}
		} else {
			Substitution apply = result.iterator().next();
			for (final Var var : apply.getVariables()) {
				final Term term = apply.get(var);
				if (term.isClosed() && condition.addBoundVar(var)) {
					substitution.addBinding(var, term);
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
