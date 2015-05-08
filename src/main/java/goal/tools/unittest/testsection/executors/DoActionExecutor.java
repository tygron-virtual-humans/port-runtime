package goal.tools.unittest.testsection.executors;

import goal.core.executors.ActionComboExecutor;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.ActionResult;
import goal.tools.unittest.result.testsection.DoActionFailed;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;
import languageTools.program.test.testsection.DoActionSection;

public class DoActionExecutor extends TestSectionExecutor {
	private final DoActionSection doaction;

	public DoActionExecutor(DoActionSection doaction) {
		this.doaction = doaction;
	}

	@Override
	public DoActionSection getSection() {
		return this.doaction;
	}

	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed {
		try {
			runState.startCycle(false);
			ActionComboExecutor action = new ActionComboExecutor(
					this.doaction.getAction());
			Result result = action.run(runState, runState.getMentalState()
					.getOwner().getKRInterface().getSubstitution(null), true);
			return new ActionResult(this, result);
		} catch (GOALActionFailedException e) {
			throw new DoActionFailed(this, e);
		}

	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.doaction);
	}

}
