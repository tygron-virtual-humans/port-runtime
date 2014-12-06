package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.AssertTest;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.msc.MentalStateCondition;

/**
 * Stores the result of a {@link MentalStateTeste}. A test produces a set of
 * {@link Substitution}.
 *
 * The test is considered passed if the set of substitutions is not empty (e.g.
 * the mental state test had a solution).
 *
 * @author M.P. Korstanje
 */
public class AssertTestResult implements TestSectionResult {
	private final boolean passsed;
	private final Set<Substitution> result;
	private final AssertTest test;

	public AssertTestResult(AssertTest mentalStateTest, Set<Substitution> result) {
		this.test = mentalStateTest;
		this.result = result;
		this.passsed = checkPassed();
	}

	private boolean checkPassed() {
		return !this.result.isEmpty();
	}

	/**
	 * @return the message to display should this test fail.
	 */
	public String getMessage() {
		return this.test.getMessage();
	}

	/**
	 * @return the mental state query that produced this result.
	 */
	public MentalStateCondition getMentalStateTest() {
		return this.test.getMentalStateTest();
	}

	/**
	 * Returns true if the test passed. A test is considered passed if the set
	 * of substitutions is not empty (e.g. the mental state test had a
	 * solution).
	 *
	 * @return true if the test passed.
	 */
	public boolean isPassed() {
		return this.passsed;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MentalStateTestResult [test=" + this.test + ", passsed="
				+ this.passsed + "]";
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}
}
