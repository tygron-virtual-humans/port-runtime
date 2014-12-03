package goal.tools.unittest.result.testsection;

import languageTools.program.agent.actions.ActionCombo;
import goal.core.runtime.service.agent.Result;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.DoActionSection;

/**
 * Result of evaluating the {@link DoActionSection} rule.
 *
 * @author mpkorstanje
 */
public class ActionResult implements TestSectionResult {

	@Override
	public String toString() {
		return "ActionResult [result=" + result + "]";
	}

	private final Result result;
	private final DoActionSection action;

	/**
	 * @return the result of the {@link ActionCombo}
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * @return the evaluated action rule
	 */
	public DoActionSection getAction() {
		return action;
	}

	/**
	 * Constructs a new action result.
	 *
	 * @param action
	 *            evaluated
	 * @param result
	 *            produced
	 */
	public ActionResult(DoActionSection action, Result result) {
		this.result = result;
		this.action = action;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
