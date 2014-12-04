package goal.tools.unittest.testsection;

import krTools.language.Substitution;
import languageTools.program.agent.AgentProgram;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebugObserver;
import goal.tools.debugger.DebuggerKilledException;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.EvaluateInFailed;
import goal.tools.unittest.result.testsection.EvaluateInInterrupted;
import goal.tools.unittest.result.testsection.EvaluateInResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;
import goal.tools.unittest.testsection.testconditions.TestBoundaryException;
import goal.tools.unittest.testsection.testconditions.TestCondition;
import goal.tools.unittest.testsection.testconditions.TestConditionEvaluator;
import goal.tools.unittest.testsection.testconditions.TestConditionFailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluate-in statement. The evaluate-in statement evaluates a list of
 * {@link TestCondition}s while executing an action or module.
 *
 * The queries in the evaluate-in statement are evaluated by a
 * {@link TestConditionEvaluator}. Each {@link TestCondition} provides an
 * evaluator that can be used to evaluate that condition.
 *
 * When an test condition is evaluated, the corresponding evaluator is run as
 * debugger. The evaluators are {@link DebugObserver}s that listen to
 * {@link Channel#ACTION_EXECUTED_BUILTIN} and
 * {@link Channel#ACTION_EXECUTED_USERSPEC}.
 *
 * Evaluation of the test conditions happens in three phases.
 *
 * <ol>
 * <li>Install evaluators.
 * <li>Trigger {@link TestConditionEvaluator#firstEvaluation()}.
 * <li>Execute the action associated with {@link EvaluateIn} using the installed
 * Evaluators. Whenever an action is executed the test condition is evaluated.
 * <li>Trigger {@link TestConditionEvaluator#lastEvaluation()}.
 * <li>Uninstall evaluators.
 * <ol>
 *
 * @author mpkorstanje
 */
public class EvaluateIn implements TestSection {

	@Override
	public String toString() {
		return "EvaluateIn [conditions= " + conditions + ", action=" + action
				+ ", boundary=" + boundary + "]";
	}

	private final List<TestCondition> conditions;
	private final DoActionSection action;
	private final TestCondition boundary;
	private final Substitution substitution;

	/**
	 * Constructs a new EvaluateIn rule.
	 *
	 * @param queries
	 *            to execute
	 * @param action
	 *            on which to evaluate queries
	 * @param boundary
	 *            an optional boundary on the evaluation (until/while)
	 * @param program
	 *            the AgentProgram source
	 */
	public EvaluateIn(List<TestCondition> queries, DoActionSection action,
			TestCondition boundary, AgentProgram program) {
		this.conditions = queries;
		this.action = action;
		this.boundary = boundary;
		this.substitution = program.getKRInterface().getSubstitution(null);
	}

	/**
	 * Returns the {@link TestCondition}s evaluated in this section.
	 *
	 * @return the test conditions evaluated in this section.
	 */
	public List<TestCondition> getQueries() {
		return conditions;
	}

	/**
	 * Returns the action or module on which queries are evaluated.
	 *
	 * @return the action or module on which queries are evaluated
	 */
	public TestSection getAction() {
		return action;
	}

	/**
	 * Returns the boundary condition on which queries are evaluated.
	 *
	 * @return the boundary condition on which queries are evaluated
	 */
	public TestCondition getBoundary() {
		return boundary;
	}

	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed {
		/*
		 * Installs the condition evaluators on the debugger. Conditions will be
		 * evaluated after every action has been executed.
		 */
		ObservableDebugger debugger = runState.getDebugger();
		final int add = (this.boundary == null) ? 0 : 1;
		TestCondition[] conditions = new TestCondition[this.conditions.size()
				+ add];
		if (this.boundary != null) {
			conditions[0] = this.boundary;
		}
		for (int i = add; i < (this.conditions.size() + add); i++) {
			conditions[i] = this.conditions.get(i - add);
		}
		List<TestConditionEvaluator> evaluators = installEvaluators(runState,
				substitution, debugger, conditions);

		/*
		 * Initial evaluation of conditions before action is executed.
		 */
		runState.startCycle(false);
		for (TestConditionEvaluator evaluator : evaluators) {
			try {
				evaluator.firstEvaluation();
			} catch (TestConditionFailedException e) {
				uninstallEvaluators(debugger, evaluators);
				throw new EvaluateInFailed(this, evaluators, e);
			}
		}

		/*
		 * Evaluates the action. While being evaluated the conditions installed
		 * on the debugger may throw a failed test condition exception.
		 */
		try {
			action.run(runState);
		} catch (TestConditionFailedException e) {
			uninstallEvaluators(debugger, evaluators);
			throw new EvaluateInFailed(this, evaluators, e);
		} catch (DebuggerKilledException e) {
			throw new EvaluateInInterrupted(this, evaluators, e);
		} catch (TestBoundaryException e) {
			// continue silently
		}

		/*
		 * After the action has been done all conditions are evaluated one more
		 * time.
		 */
		for (TestConditionEvaluator evaluator : evaluators) {
			evaluator.lastEvaluation();
		}

		uninstallEvaluators(debugger, evaluators);

		/*
		 * If any of the queries failed we fail this test section.
		 */
		for (TestConditionEvaluator evaluator : evaluators) {
			if (!evaluator.isPassed()) {
				throw new EvaluateInFailed(this, evaluators);
			}
		}

		return new EvaluateInResult(this, evaluators);
	}

	private static void uninstallEvaluators(ObservableDebugger debugger,
			List<TestConditionEvaluator> evaluators) {
		for (TestConditionEvaluator evaluator : evaluators) {
			debugger.unsubscribe(evaluator);
		}
	}

	private static List<TestConditionEvaluator> installEvaluators(
			RunState<? extends ObservableDebugger> runState,
			Substitution substitution, ObservableDebugger debugger,
			TestCondition[] queries) {
		List<TestConditionEvaluator> evaluators = new ArrayList<>(
				queries.length);
		for (TestCondition query : queries) {
			TestConditionEvaluator evaluator = query.provideEvaluator(runState,
					substitution);
			evaluators.add(evaluator);
			debugger.subscribe(evaluator, Channel.ACTIONCOMBO_FINISHED);
			debugger.subscribe(evaluator, Channel.EVENT_MODULE_ENTRY);
			debugger.subscribe(evaluator, Channel.EVENT_MODULE_EXIT);
			debugger.subscribe(evaluator, Channel.INIT_MODULE_ENTRY);
			debugger.subscribe(evaluator, Channel.INIT_MODULE_EXIT);
			debugger.subscribe(evaluator, Channel.MAIN_MODULE_ENTRY);
			debugger.subscribe(evaluator, Channel.MAIN_MODULE_EXIT);
			debugger.subscribe(evaluator, Channel.USER_MODULE_ENTRY);
			debugger.subscribe(evaluator, Channel.USER_MODULE_EXIT);
		}
		return evaluators;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
