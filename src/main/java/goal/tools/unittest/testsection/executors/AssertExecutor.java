package goal.tools.unittest.testsection.executors;

import goal.core.executors.MentalStateConditionExecutor;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.AssertTestFailed;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testsection.AssertTest;

public class AssertExecutor extends TestSectionExecutor {
	private final AssertTest _assert;

	public AssertExecutor(AssertTest _assert) {
		this._assert = _assert;
	}

	@Override
	public AssertTest getSection() {
		return this._assert;
	}

	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed {
		MentalState ms = runState.getMentalState();
		Debugger debugger = runState.getDebugger();
		MentalStateConditionExecutor condition = new MentalStateConditionExecutor(
				this._assert.getMentalStateTest());
		Set<Substitution> subs;
		try {
			subs = condition.evaluate(ms, debugger);
		} catch (GOALDatabaseException e) {
			throw new IllegalStateException("precondition failed to evaluate",e);
		}
		if (subs.isEmpty()) {
			throw new AssertTestFailed(this);
		} else {
			return new AssertTestResult(this._assert, subs);
		}
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this._assert);
	}

}
