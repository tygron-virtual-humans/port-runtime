package goal.tools.unittest.testsection.testconditions;

import krTools.language.Substitution;
import krTools.language.Var;
import languageTools.program.agent.msc.MentalStateCondition;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base for any test condition. Test conditions are evaluated in the
 * context of a running agent and need to provide an evaluator that can do so.
 *
 * @author mpkorstanje
 */
public abstract class TestCondition {
	/**
	 * The mental state condition of the query
	 */
	protected final MentalStateCondition query;
	/**
	 * An optional nested condition (... -> ...)
	 */
	protected TestCondition nested;
	/**
	 * Null when a condition is not nested; Empty set when a condition is nested
	 * but not active (e.g. condition not met); Filled set when a condition is
	 * nested and active (condition met) > all substitutions in the set need to
	 * be evaluated.
	 */
	protected Set<Substitution> isNested;
	/**
	 * The (unbound) variables that were bound whilst evaluating this condition
	 */
	protected final Set<Var> boundByMe;
	/**
	 * The actual evaluator for the conditions
	 */
	protected TestConditionEvaluator evaluator;

	/**
	 * @return the mental state condition of the query
	 */
	public MentalStateCondition getQuery() {
		return this.query;
	}

	/**
	 * @return the nested condition (... -> ...) if it is present (null
	 *         otherwise)
	 */
	public TestCondition getNestedCondition() {
		return this.nested;
	}

	/**
	 * @return true when a nested condition is present
	 */
	public boolean hasNestedCondition() {
		return this.nested != null;
	}

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
	 * Creates a {@link TestCondition} using the mental state condition.
	 *
	 * @param query
	 *            A mental state condition.
	 */
	public TestCondition(MentalStateCondition query) {
		this.query = query;
		this.boundByMe = new HashSet<>();
	}

	/**
	 * Defines a nested condition (when ... -> ...)
	 *
	 * @param nested
	 *            The nested TestCondition.
	 */
	public void setNestedCondition(TestCondition nested) {
		if (this.isNested == null) {
			nested.setNested(new HashSet<Substitution>(0));
			this.nested = nested;
		}
	}

	/**
	 * Update the state of a nested condition (which we are)
	 *
	 * @param nested
	 *            The substitution set to use in our next evaluation.
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
				throw new TestConditionFailedException(
						"Nested condition did not hold before next evaluation",
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

}
