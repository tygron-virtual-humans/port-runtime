package goal.tools.unittest.result;

import goal.tools.RunTest;
import goal.tools.unittest.UnitTest;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import languageTools.program.agent.AgentId;
import languageTools.program.mas.MASProgram;
import languageTools.program.test.AgentTest;

/**
 * The results of executing a {@link UnitTest}. A UnitTest can be ran using
 * {@link RunTest}.
 *
 * Because the UnitTest starts a MAS and binds {@link AgentTest}s to agents
 * using their base name there may be multiple {@link AgentTestResult}s for a
 * test. Likewise there may be agents that ran but did not have an associated
 * test.
 *
 * A Unit test is considered to be passed if all
 * {@link UnitTestInterpreterResult}s are passed and if for each
 * {@link AgentTest} defined in the UnitTest there is at least one result.
 *
 * @author M.P. Korstanje
 */
public class UnitTestResult {
	@Override
	public String toString() {
		return "UnitTestResult [unitTest=" + this.unitTest + ", results="
				+ this.results + ", passed=" + this.passed + "]";
	}

	private final UnitTest unitTest;
	private final Map<AgentTest, List<UnitTestInterpreterResult>> results;
	private final boolean passed;

	/**
	 * @param unitTest
	 * @param results
	 */
	public UnitTestResult(UnitTest unitTest,
			HashMap<AgentId, UnitTestInterpreterResult> results) {
		this.unitTest = unitTest;
		this.results = setResults(results.values());
		this.passed = checkPassed();
	}

	@SuppressWarnings("hiding")
	private Map<AgentTest, List<UnitTestInterpreterResult>> setResults(
			Collection<UnitTestInterpreterResult> collection) {
		Map<AgentTest, List<UnitTestInterpreterResult>> results = new HashMap<>();

		// Create entry for all tests.
		for (AgentTest test : this.unitTest.getTests()) {
			results.put(test, new LinkedList<UnitTestInterpreterResult>());
		}

		// Put entries we actually got.
		for (UnitTestInterpreterResult result : collection) {
			AgentTest test = result.getTest();

			// TestResults don't need to be associated with a test.
			// Extra agents can show up. These are expected not to crash.
			if (!results.containsKey(test)) {
				results.put(test, new LinkedList<UnitTestInterpreterResult>());
			}

			results.get(test).add(result);
		}

		return results;
	}

	/**
	 * A unit test is considered to be passed if all tests were successfully
	 * passed (and at least one result was obtained), and failed otherwise,
	 * i.e., if one of the tests failed, was interrupted, or an exception
	 * occurred.
	 *
	 * @return {@code true} if the unit test was passed, {@code false}
	 *         otherwise.
	 */
	private boolean checkPassed() {
		for (List<UnitTestInterpreterResult> tr : this.results.values()) {
			if (tr.isEmpty()) {
				return false;
			}
			for (UnitTestInterpreterResult r : tr) {
				if (!r.isPassed()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return the file containing the {@link UnitTest}
	 */
	public File getUnitTestFile() {
		return this.unitTest.getFile();
	}

	/**
	 * @return the file containing the {@link MASProgram}
	 */
	public File getMasFile() {
		return this.unitTest.getMasProgram().getSourceFile();
	}

	/**
	 * Returns a map of test and for each test the results. Results without a
	 * associated test are stored with the <code>null</code> key.
	 *
	 * @return the results of the test.
	 */
	public Map<AgentTest, List<UnitTestInterpreterResult>> getResults() {
		return this.results;
	}

	/**
	 * A Unit test is considered passed if all TestResults are passed and if for
	 * each test defined in the UnitTest there is at least one result.
	 *
	 * @return true if the test is passed.
	 */
	public boolean isPassed() {
		return this.passed;
	}

	/**
	 * @param formatter
	 * @return
	 */
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
