package goal.tools;

import goal.core.agent.Agent;
import languageTools.program.agent.AgentId;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.UnitTest;
import goal.tools.unittest.UnitTestInterpreter;
import goal.tools.unittest.result.UnitTestInterpreterResult;
import goal.tools.unittest.result.UnitTestResult;

import java.util.Collection;
import java.util.HashMap;

/**
 * Inspects the results of a {@link RunTest}. The results are provided as a
 * {@link UnitTestResult}. This inspector can be reused between tests with the
 * same {@link UnitTest}.
 *
 * @author M.P. Korstanje
 */
public class UnitTestRunResultInspector implements
ResultInspector<UnitTestInterpreter<ObservableDebugger>> {
	private final HashMap<AgentId, UnitTestInterpreterResult> results = new HashMap<>();
	private final UnitTest unitTest;

	public UnitTestRunResultInspector(UnitTest unitTest) {
		this.unitTest = unitTest;
	}

	/**
	 * Returns the results of running the unit test. Note that the test result
	 * is only populate after running the test.
	 *
	 * @return the unit test results.
	 */
	public UnitTestResult getResults() {
		return new UnitTestResult(unitTest, new HashMap<>(results));
	}

	@Override
	public void handleResult(
			Collection<Agent<UnitTestInterpreter<ObservableDebugger>>> agents) {
		results.clear();

		// Stop agents. Agents are either stopped or half way in a test. When in
		// a test the test will show the rule currently being executed as
		// interupted.
		for (Agent<?> agent : agents) {
			agent.stop();
		}

		// Wait for termination.
		try {
			for (Agent<?> agent : agents) {
				agent.awaitTermination();
			}

			// Extract results.
			for (Agent<UnitTestInterpreter<ObservableDebugger>> a : agents) {
				UnitTestInterpreterResult result = a.getController()
						.getTestResults();
				results.put(a.getId(), result);
			}
		} catch (InterruptedException e) {
			// Formatter should conclude from absence of results that the test
			// did not run or was interrupted.
		}
	}
}