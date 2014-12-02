package goal.tools.unittest.testsection;

import goal.core.program.actions.ActionCombo;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.ActionResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionResult;

/**
 * Action rule for test program. Executes an {@link ActionCombo}.
 *
 * @author mpkorstanje
 */
public class DoActionSection implements TestSection {

	/**
	 * @return the action combo to execute
	 */
	public ActionCombo getAction() {
		return action;
	}

	@Override
	public String toString() {
		return "Action [action=" + action + "]";
	}

	private final ActionCombo action;

	/**
	 * Constructs a new action invocation.
	 *
	 * @param action
	 *            the action to invoke. Should be closed.
	 */
	public DoActionSection(ActionCombo action) {
		this.action = action;
	}

	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws TestSectionFailed {
		runState.startCycle(false);
		Result result = action.run(runState, runState.getMentalState()
				.getKRLanguage().getEmptySubstitution(), false);
		return new ActionResult(this, result);

	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
