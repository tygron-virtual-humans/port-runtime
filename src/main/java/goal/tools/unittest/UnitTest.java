package goal.tools.unittest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import languageTools.program.mas.MASProgram;
import languageTools.program.test.AgentTest;

/**
 * UnitTest for GOAL. A unit test consists of a list of tests
 *
 * @author mpkorstanje
 */
public class UnitTest {
	private final Map<File, AgentTest> tests = new HashMap<>();
	private final MASProgram masProgram;
	private final File unitTestFile;
	private final long timeout;

	/**
	 * Constructs a new unit test with no agent tests.
	 *
	 * @param unitTestFile
	 *            file containing the unit test
	 * @param masProgram
	 *            of the system under test
	 */
	public UnitTest(File unitTestFile, MASProgram masProgram) {
		this(unitTestFile, masProgram, new ArrayList<AgentTest>(0));
	}

	/**
	 * Constructs a new unit test.
	 *
	 * @param unitTestFile
	 *            file containing the unit test
	 * @param masProgram
	 *            of the system under test
	 * @param tests
	 *            to run
	 */
	public UnitTest(File unitTestFile, MASProgram masProgram,
			List<AgentTest> tests) {
		this(unitTestFile, masProgram, tests, 0);
	}

	/**
	 * Constructs a new unit test.
	 *
	 * @param unitTestFile
	 *            file containing the unit test
	 * @param masProgram
	 *            of the system under test
	 * @param tests
	 *            to run
	 * @param timeout
	 *            duration in ms before test times out
	 */
	public UnitTest(File unitTestFile, MASProgram masProgram,
			List<AgentTest> tests, long timeout) {
		this.unitTestFile = unitTestFile;
		this.masProgram = masProgram;
		for (AgentTest t : tests) {
			this.tests.put(t.getSourceFile(), t);
		}
		this.timeout = timeout;
	}

	/**
	 * Constructs a new unit test.
	 *
	 * @param unitTestFile
	 *            file containing the unit test
	 * @param masProgram
	 *            of the system under test
	 * @param timeout
	 *            duration in ms before test times out
	 */
	public UnitTest(File unitTestFile, MASProgram masProgram, Long timeout) {
		this(unitTestFile, masProgram, new ArrayList<AgentTest>(0), timeout);
	}

	public Collection<AgentTest> getTests() {
		return tests.values();
	}

	public MASProgram getMasProgram() {
		return masProgram;
	}

	/**
	 * Returns a test for the agent with the given base name or null when the
	 * agent has no test associated with it.
	 *
	 * @param agentFile
	 *            to find test for
	 * @return a test or null
	 */
	public AgentTest getTest(File agentFile) {
		return tests.get(agentFile);
	}

	public File getFile() {
		return unitTestFile;
	}

	public long getTimeout() {
		return timeout;
	}
}
