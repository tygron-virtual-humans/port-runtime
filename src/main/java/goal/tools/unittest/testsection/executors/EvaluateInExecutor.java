package goal.tools.unittest.testsection.executors;

import goal.core.executors.ModuleCallActionExecutor;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.DebugObserver;
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
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;

import java.util.HashSet;
import java.util.Set;

import krTools.KRInterface;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

public class EvaluateInExecutor extends TestSectionExecutor implements
DebugObserver {
	private final EvaluateIn evaluatein;
	private Set<TestConditionExecutor> executors;

	public EvaluateInExecutor(EvaluateIn evaluatein) {
		this.evaluatein = evaluatein;
	}

	@Override
	public EvaluateIn getSection() {
		return this.evaluatein;
	}

	public void add(TestConditionExecutor executor) {
		this.executors.add(executor);
	}

	public void remove(TestConditionExecutor executor) {
		this.executors.remove(executor);
	}

	public TestConditionExecutor[] getExecutors() {
		return this.executors.toArray(new TestConditionExecutor[this.executors
		                                                        .size()]);
	}

	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runstate)
			throws TestSectionFailed {
		KRInterface kr = runstate.getMainModule().getKRInterface();
		TestCondition boundary = this.evaluatein.getBoundary();
		Set<TestCondition> conditions = this.evaluatein.getQueries();

		/*
		 * Installs the condition evaluators on the debugger. Conditions will be
		 * evaluated after every action has been executed.
		 */
		ObservableDebugger debugger = runstate.getDebugger();
		// Main event we listen to (executing actions)
		debugger.subscribe(this, Channel.ACTION_EXECUTED_BUILTIN);
		debugger.subscribe(this, Channel.ACTION_EXECUTED_USERSPEC);
		// Module entries might also add a set of beliefs/goals
		// Note: event module entry handles percept/mails as well
		debugger.subscribe(this, Channel.INIT_MODULE_ENTRY);
		debugger.subscribe(this, Channel.EVENT_MODULE_ENTRY);
		debugger.subscribe(this, Channel.MAIN_MODULE_ENTRY);
		debugger.subscribe(this, Channel.USER_MODULE_ENTRY);
		// For atend conditions specifically
		// Note: main module exit means agent exit
		debugger.subscribe(this, Channel.INIT_MODULE_EXIT);
		debugger.subscribe(this, Channel.EVENT_MODULE_EXIT);
		debugger.subscribe(this, Channel.MAIN_MODULE_EXIT);
		debugger.subscribe(this, Channel.USER_MODULE_EXIT);

		this.executors = new HashSet<>();
		if (boundary != null) {
			this.executors.add(TestConditionExecutor.getTestConditionExecutor(
					boundary, kr.getSubstitution(null), runstate, this));
		}
		for (TestCondition condition : conditions) {
			this.executors.add(TestConditionExecutor.getTestConditionExecutor(
					condition, kr.getSubstitution(null), runstate, this));
		}

		/*
		 * Evaluates the action. While being evaluated the conditions installed
		 * on the debugger may throw a failed test condition exception.
		 */
		ModuleCallActionExecutor module = new ModuleCallActionExecutor(
				this.evaluatein.getAction());
		try {
			runstate.startCycle(false);
			module.run(runstate, kr.getSubstitution(null),
					runstate.getDebugger(), true);
		} catch (TestConditionFailedException e) {
			throw new EvaluateInFailed(this, this.executors, e);
		} catch (DebuggerKilledException e) {
			throw new EvaluateInInterrupted(this, this.executors, e);
		} catch (GOALActionFailedException e) {
			throw new EvaluateInInterrupted(this, this.executors,
					new DebuggerKilledException("Module failed to execute", e));
		} catch (TestBoundaryException e) {
			// continue silently
		}

		/*
		 * Allow conditions to clean-up after themselves (determine final
		 * results, e.g. fail eventually or succeed always/never conditions)
		 */
		for (TestConditionExecutor executor : getExecutors()) {
			try {
				executor.evaluate(null);
			} catch (TestConditionFailedException e) {
				throw new EvaluateInFailed(this, this.executors, e);
			} catch (DebuggerKilledException e) {
				throw new EvaluateInInterrupted(this, this.executors, e);
			} catch (TestBoundaryException e) {
				// continue silently
			}
		}

		/*
		 * Uninstall the evaluators
		 */
		debugger.unsubscribe(this);

		/*
		 * If any of the queries failed we fail this test section.
		 */
		for (TestConditionExecutor executor : getExecutors()) {
			if (!executor.isPassed()) {
				throw new EvaluateInFailed(this, this.executors);
			}
		}

		/*
		 * We succeeded :)
		 */
		return new EvaluateInResult(this.evaluatein, this.executors);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.evaluatein);
	}

	@Override
	public String getObserverName() {
		return getClass().getSimpleName();
	}

	@Override
	public void notifyBreakpointHit(DebugEvent event) {
		for (TestConditionExecutor executor : getExecutors()) {
			executor.evaluate(event);
		}
	}
}
