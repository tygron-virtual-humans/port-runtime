package goal.tools.unittest.result.testsection;

import goal.core.runtime.service.agent.Result;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.executors.DoActionExecutor;
import languageTools.program.agent.actions.ActionCombo;
import languageTools.program.test.testsection.DoActionSection;

/**
 * Result of evaluating the {@link DoActionSection} rule.
 *
 * @author mpkorstanje
 */
public class ActionResult implements TestSectionResult {

	@Override
	public String toString() {
		return "ActionResult [result=" + this.result + "]";
	}

	private final Result result;
	private final DoActionExecutor action;

	/**
	 * @return the result of the {@link ActionCombo}
	 */
	public Result getResult() {
		return this.result;
	}

	/**
	 * @return the evaluated action rule
	 */
	public DoActionExecutor getAction() {
		return this.action;
	}

	/**
	 * Constructs a new action result.
	 *
	 * @param action
	 *            evaluated
	 * @param result
	 *            produced
	 */
	public ActionResult(DoActionExecutor action, Result result) {
		this.result = result;
		this.action = action;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
