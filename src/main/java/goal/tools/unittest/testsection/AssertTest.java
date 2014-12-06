package goal.tools.unittest.testsection;

import goal.core.executors.MentalStateConditionExecutor;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testsection.AssertTestFailed;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.result.testsection.TestSectionResult;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.msc.MentalStateCondition;

/**
 * A mental state test is executed on the agents {@link RunState}. This can be
 * used to check if a certain condition holds once the agent has executed the
 * modules under test.
 *
 * @author M.P. Korstanje
 */
public class AssertTest implements TestSection {
	private final String message;
	private final MentalStateCondition condition;

	/**
	 * Constructs a new assert test.
	 *
	 * @param condition
	 *            to test
	 * @param message
	 *            to display when test fails.
	 */
	public AssertTest(MentalStateCondition condition, String message) {
		this.condition = condition;
		this.message = message;
	}

	/**
	 * Constructs a new assert test.
	 *
	 * @param condition
	 *            to test
	 */
	public AssertTest(MentalStateCondition condition) {
		this(condition, "");
	}

	/**
	 * @return the message to display if the test does not pass.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the query that is tested.
	 */
	public MentalStateCondition getMentalStateTest() {
		return condition;
	}

	/**
	 * Runs the mental state test against the agents run state. This will query
	 * the agents belief base.
	 *
	 * @param runState
	 *            of the agent under test
	 * @return result of this test
	 * @throws AssertTestFailed
	 *             when the mental state condition did not hold
	 */
	@Override
	public TestSectionResult run(RunState<? extends ObservableDebugger> runState)
			throws AssertTestFailed {
		MentalState ms = runState.getMentalState();
		Debugger debugger = runState.getDebugger();
		Set<Substitution> subs = new MentalStateConditionExecutor(condition)
			.evaluate(ms, debugger);
		if (subs.isEmpty()) {
			throw new AssertTestFailed(this);
		}
		return new AssertTestResult(this, subs);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MentalStateTest [condition=" + condition + ", message="
				+ message + "]";
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
