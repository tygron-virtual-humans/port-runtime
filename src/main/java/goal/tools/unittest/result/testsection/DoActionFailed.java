package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.executors.DoActionExecutor;

/**
 * @author M.P.Korstanje
 */
public class DoActionFailed extends TestSectionFailed {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 2119184965021739086L;
	private final DoActionExecutor doAction;

	public DoActionExecutor getDoAction() {
		return this.doAction;
	}

	/**
	 * @param evaluateIn
	 * @param evaluators
	 */
	public DoActionFailed(DoActionExecutor doAction) {
		this(doAction, null);
	}

	/**
	 * @param evaluateIn
	 * @param evaluators
	 * @param exception
	 */
	public DoActionFailed(DoActionExecutor doAction, Exception exception) {
		super(exception);
		this.doAction = doAction;

	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
