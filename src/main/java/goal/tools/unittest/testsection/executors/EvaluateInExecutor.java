package goal.tools.unittest.testsection.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebuggerKilledException;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestBoundaryException;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;
import goal.tools.unittest.result.testsection.EvaluateInFailed;
import goal.tools.unittest.result.testsection.EvaluateInInterrupted;
import goal.tools.unittest.result.testsection.EvaluateInResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;
import goal.tools.unittest.testcondition.executors.TestConditionEvaluator;
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;
import languageTools.program.test.testsection.TestSection;

public class EvaluateInExecutor extends TestSectionExecutor {
	private final EvaluateIn evaluatein;
	private Substitution substitution;

	public EvaluateInExecutor(EvaluateIn evaluatein) {
		this.evaluatein = evaluatein;
	}

	@Override
	public TestSection getSection() {
		return this.evaluatein;
	}

	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed {
		TestCondition boundary = this.evaluatein.getBoundary();
		List<TestCondition> conditions = this.evaluatein.getQueries();
		if (this.substitution == null) {
			this.substitution = runState.getMainModule().getKRInterface()
					.getSubstitution(null);
		}

		/*
		 * Installs the condition evaluators on the debugger. Conditions will be
		 * evaluated after every action has been executed.
		 */
		ObservableDebugger debugger = runState.getDebugger();
		List<TestConditionExecutor> executors = new LinkedList<>();
		if (boundary != null) {
			executors.add(TestConditionExecutor
					.getTestConditionExecutor(boundary));
		}
		for (TestCondition condition : conditions) {
			for (TestCondition subcondition : getAllConditions(condition)) {
				executors.add(TestConditionExecutor
						.getTestConditionExecutor(subcondition));
			}
		}
		List<TestConditionEvaluator> evaluators = new ArrayList<>(
				executors.size());
		for (TestConditionExecutor executor : executors) {
			TestConditionEvaluator evaluator = executor.provideEvaluator(
					runState, this.substitution);
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

		/*
		 * Initial evaluation of conditions before action is executed.
		 */
		try {
			runState.startCycle(false);
		} catch (GOALActionFailedException e1) {
			throw new IllegalStateException(
					"Failed to startCycle, action is failing", e1);
		}
		for (TestConditionEvaluator evaluator : evaluators) {
			try {
				evaluator.firstEvaluation();
			} catch (TestConditionFailedException e) {
				throw new EvaluateInFailed(this, evaluators, e);
			}
		}

		/*
		 * Evaluates the action. While being evaluated the conditions installed
		 * on the debugger may throw a failed test condition exception.
		 */
		TestSectionExecutor action = TestSectionExecutor
				.getTestConditionExecutor(this.evaluatein.getAction());
		try {
			action.run(runState);
		} catch (TestConditionFailedException e) {
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
			try {
				evaluator.lastEvaluation();
			} catch (TestConditionFailedException e) {
				throw new EvaluateInFailed(this, evaluators, e);
			}
		}

		/*
		 * Uninstall the evaluators
		 */
		for (TestConditionEvaluator evaluator : evaluators) {
			debugger.unsubscribe(evaluator);
		}

		/*
		 * If any of the queries failed we fail this test section.
		 */
		for (TestConditionEvaluator evaluator : evaluators) {
			if (!evaluator.isPassed()) {
				throw new EvaluateInFailed(this, evaluators);
			}
		}

		/*
		 * We succeeded :)
		 */
		return new EvaluateInResult(this.evaluatein, evaluators);
	}

	private List<TestCondition> getAllConditions(TestCondition condition) {
		List<TestCondition> result = new LinkedList<>();
		result.add(condition);
		if (condition.hasNestedCondition()) {
			result.addAll(getAllConditions(condition.getNestedCondition()));
		}
		return result;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.evaluatein);
	}
}
