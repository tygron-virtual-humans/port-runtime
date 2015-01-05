/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package goal.core.program;

import static org.junit.Assert.assertTrue;
import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.core.executors.MentalStateConditionExecutor;
import goal.core.mentalstate.MentalState;
import goal.tools.PlatformManager;
import goal.tools.SingleRun;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.NOPDebugger;
import goal.tools.eclipse.QueryTool;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.Loggers;
import goalhub.krTools.KRFactory;

import java.io.File;
import java.util.Set;

import krTools.KRInterface;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.msc.MentalStateCondition;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * General utility functions for running tests using {@link SingleRun}
 * <p>
 * Basically this assumes that there is a GOAL mas2g program. There must be
 * exactly 1 agent in the MAS. There can be an environment.
 * </p>
 * <p>
 * The program runs the MAS till the agent is dead. This requires the program to
 * set up such that the main module of all agents exits when they are done with
 * the test.
 * </p>
 * <p>
 * Then the belief base of the agent is queried for @ code result(X)}. The known
 * states are {@code result(failure)} and {@code result(ok)}. If one of these is
 * found, {@link RunResult#OK} or {@link RunResult#FAILURE} is returned.
 * Otherwise {@link RunResult#UNKNOWN} is returned. Also errors may be thrown
 * depending on the case.
 * </p>
 *
 * @author W.Pasman
 */
public abstract class ProgramTest {
	public enum RunResult {
		OK, FAILURE, UNKNOWN;

		public static RunResult get(String value) {
			try {
				return RunResult.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException e) {
				return RunResult.UNKNOWN;
			}
		}
	}

	private static KRInterface language;

	@BeforeClass
	public static void setupBeforeClass() throws KRInitFailedException {
		Loggers.addConsoleLogger();
		language = KRFactory.getDefaultInterface();

	}

	@AfterClass
	public static void tearDownAfterClass() {
		Loggers.removeConsoleLogger();
	}

	@After
	public void cleanUp() throws KRInitFailedException {
		// language.reset();
	}

	/**
	 * Do a single run of given GOAL file. Checks after the run why the agent
	 * terminated. The agent should hold the belief "ok" or "failure", and that
	 * is the reason to return. If neither is holding, we return
	 * {@link RunResult#UNKNOWN}.
	 *
	 * @param goalFile
	 * @throws Exception
	 */
	protected RunResult runAgent(String goalFile) throws Exception {
		String id = "TestAgent";
		AgentProgram program = PlatformManager.createNew().parseGOALFile(
				new File(goalFile), language);
		assertTrue(program.isValid());

		Agent<GOALInterpreter<Debugger>> agent = buildAgent(id, program);
		agent.start();
		agent.awaitTermination(0); // TODO: timeout?!

		return inspectResult(agent);
	}

	protected abstract Agent<GOALInterpreter<Debugger>> buildAgent(String id,
			AgentProgram program) throws GOALLaunchFailureException,
			MessagingException, KRInitFailedException;

	protected RunResult inspectResult(Agent<GOALInterpreter<Debugger>> agent) {
		QueryTool buildQuery = new QueryTool(agent);
		MentalStateCondition mentalStateCondition;
		try {
			mentalStateCondition = buildQuery.parseMSC("bel(result(X))");
		} catch (Exception e) {
			throw new IllegalStateException(
					"Unexpected exception whilst building MSC", e);
		}

		MentalState mentalState = agent.getController().getRunState()
				.getMentalState();
		Set<Substitution> res = new MentalStateConditionExecutor(
				mentalStateCondition).evaluate(mentalState, new NOPDebugger(
				agent.getId()));

		// there should be exactly 1 substi.
		if (res.size() < 1) {
			throw new IllegalStateException(
					"Program failed: it did not set a result");
		}
		if (res.size() > 1) {
			throw new IllegalStateException(
					"Program failed: it set multiple results (ony 1 allowed)");
		}
		// and it should hold exactly 1 variable, our variable "X". Get its
		// value
		Substitution substitution = ((Substitution) res.toArray()[0]);
		Set<Var> variables = substitution.getVariables();
		if (variables.size() < 1) {
			throw new IllegalStateException(
					"Query failed: it did not set a result");
		}
		if (variables.size() > 1) {
			throw new IllegalStateException(
					"Query failed: it set multiple results (ony 1 allowed)");
		}
		Var var = (Var) variables.toArray()[0];
		Term value = substitution.get(var);

		return RunResult.get(value.toString());
	}
}
