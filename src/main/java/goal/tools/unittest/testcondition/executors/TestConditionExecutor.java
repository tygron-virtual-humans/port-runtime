package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Var;
import languageTools.program.test.testcondition.Always;
import languageTools.program.test.testcondition.AtEnd;
import languageTools.program.test.testcondition.AtStart;
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
	 * Null when a condition is not nested; Empty set when a condition is nested
	 * but not active (e.g. condition not met); Filled set when a condition is
	 * nested and active (condition met) > all substitutions in the set need to
	 * be evaluated.
	 */
	private Set<Substitution> isNested;
	/**
	 * The (unbound) variables that were bound whilst evaluating this condition
	 */
	private final Set<Var> boundByMe = new HashSet<>();
	/**
	 * The actual evaluator for the conditions
	 */
	private TestConditionEvaluator evaluator;

	/**
	 * @return true when this condition is a nested condition
	 */
	public boolean isNested() {
		return this.isNested != null;
	}

	/**
	 * @return Get the substitutions to use for the nested condition (if we are
	 *         one; null otherwise)
	 */
	public Set<Substitution> getNested() {
		return this.isNested;
	}

	/**
	 * Get the parsed {@link TestCondition}.
	 *
	 * @return {@link TestCondition}
	 */
	abstract public TestCondition getCondition();

	protected boolean hasNestedExecutor() {
		return getTestConditionExecutor(getCondition().getNestedCondition()) != null;
	}

	protected TestConditionExecutor getNestedExecutor() {
		return getTestConditionExecutor(getCondition().getNestedCondition());
	}

	/**
	 * Update the state of a nested condition (which we are)
	 *
	 * @param nested
	 *            The substitution set to use in our next evaluation .
	 */
	public void setNested(Set<Substitution> nested) {
		final boolean empty = (this.isNested == null)
				|| (this.isNested.isEmpty());
		this.isNested = nested;
		if (this.evaluator != null) {
			this.evaluator.lastEvaluation();
			if (empty || this.evaluator.isPassed()) {
				this.evaluator.reset();
			} else {
				throw new TestConditionFailedException("The nested condition "
						+ getCondition()
						+ " did not get evaluated before next evaluation",
						this.evaluator);
			}
		}
	}

	/**
	 * Register any variable this condition bounds during its evaluation.
	 *
	 * @param var
	 *            The variable that this condition has bounded
	 * @return True if we are a nested condition and the variable was registered
	 *         successfully.
	 */
	public boolean addBoundVar(Var var) {
		if (this.isNested == null) {
			this.boundByMe.add(var);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the (unbound) variables that were bound during the last
	 *         evaluation of this condition
	 */
	public Set<Var> getBoundByMe() {
		return Collections.unmodifiableSet(this.boundByMe);
	}

	/**
	 * Provides an evaluator capable of evaluating the mental state query on a
	 * running agent. The runstate is provided as an argument because it is not
	 * available through the {@link ObservableDebugger}. This function uses
	 * {@link createEvaluator} to create the initial evaluator initially, and
	 * returns the existing instance otherwise.
	 *
	 * @param runstate
	 *            of the agent
	 * @param substitution
	 *            the substitution to share
	 * @return a new evaluator
	 */
	public final TestConditionEvaluator provideEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution) {
		if (this.evaluator == null) {
			this.evaluator = createEvaluator(runstate, substitution);
		}
		return this.evaluator;
	}

	/**
	 * Provides an evaluator capable of evaluating the mental state query on a
	 * running agent. The runstate is provided as an argument because it is not
	 * available through the {@link ObservableDebugger}. This function should
	 * actually create the evaluator's implementation.
	 *
	 * @param runstate
	 *            of the agent
	 * @param substitution
	 *            the substitution to share
	 * @return a new evaluator
	 */
	public abstract TestConditionEvaluator createEvaluator(
			final RunState<? extends ObservableDebugger> runstate,
			final Substitution substitution);

	/**
	 * Uses double dispatch to call the formatter.
	 *
	 * @param formatter
	 *            to call
	 * @return result of the formatter
	 */
	public abstract <T> T accept(ResultFormatter<T> formatter);

	private static Map<TestCondition, TestConditionExecutor> executors = new HashMap<>();

	public static TestConditionExecutor getTestConditionExecutor(
			TestCondition condition) {
		TestConditionExecutor returned = executors.get(condition);
		if (returned == null) {
			if (condition instanceof Always) {
				returned = new AlwaysExecutor((Always) condition);
			} else if (condition instanceof AtEnd) {
				returned = new AtEndExecutor((AtEnd) condition);
			} else if (condition instanceof AtStart) {
				returned = new AtStartExecutor((AtStart) condition);
			} else if (condition instanceof Eventually) {
				returned = new EventuallyExecutor((Eventually) condition);
			} else if (condition instanceof Never) {
				returned = new NeverExecutor((Never) condition);
			} else if (condition instanceof Until) {
				returned = new UntilExecutor((Until) condition);
			} else if (condition instanceof While) {
				returned = new WhileExecutor((While) condition);
			}
			executors.put(condition, returned);
		}
		return returned;
	}
}
