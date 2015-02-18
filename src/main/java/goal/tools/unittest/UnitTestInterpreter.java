package goal.tools.unittest;

import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Channel;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.unittest.result.AgentTestResult;
import goal.tools.unittest.result.UnitTestInterpreterResult;
import goal.tools.unittest.testsection.executors.TestExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import languageTools.program.agent.AgentProgram;
import languageTools.program.test.AgentTest;

/**
 * Interpreter that will run test programs. Once completed test results can be
 * retrieved. When no test is provided the AgentProgram will be interpreted as
 * normal instead.
 *
 * @author M.P. Korstanje
 * @param <D>
 *            class of the debugger used
 *
 */
public class UnitTestInterpreter<D extends ObservableDebugger> extends
GOALInterpreter<ObservableDebugger> {
	private final AgentTest test;
	private AgentTestResult agentTestResult;

	/**
	 * Constructs a new test interpreter.
	 *
	 * @param program
	 *            of the agent under test
	 * @param test
	 *            to run, may be null when no tests should be ran
	 * @param debugger
	 *            used when running the program or test
	 * @param learner
	 *            used evaluate adaptive rules
	 */
	public UnitTestInterpreter(AgentProgram program, AgentTest test,
			D debugger, Learner learner) {
		super(program, debugger, learner);
		this.test = test;
	}

	/**
	 * @return the test
	 */
	public AgentTest getTest() {
		return this.test;
	}

	/**
	 * Returns the results of the tests executed by the interpreter. The test
	 * results are not valid until the interpreter has stopped.
	 *
	 * @return the testResults.
	 */
	public UnitTestInterpreterResult getTestResults() {
		return new UnitTestInterpreterResult(this.test, this.agent.getId(),
				this.agentTestResult, getUncaughtThrowable());
	}

	@Override
	protected Runnable getRunnable(final Executor pool,
			final Callable<Callable<?>> in) {
		if (this.test == null) {
			// Just run the agent itself when no test for it is present;
			// it might just be there for another agent in the MAS.
			return super.getRunnable(pool, in);
		} else {
			return new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					try {
						// Run the whole test
						UnitTestInterpreter.this.debugger.breakpoint(
								Channel.REASONING_CYCLE_SEPARATOR, 0, null,
								"%s test has been started",
								UnitTestInterpreter.this.agent.getId());
						UnitTestInterpreter.this.agentTestResult = new TestExecutor(
								UnitTestInterpreter.this.test)
								.run((Agent<UnitTestInterpreter<ObservableDebugger>>) UnitTestInterpreter.this.agent);
					} catch (final Exception e) {
						UnitTestInterpreter.this.throwable = e;
					} finally {
						// Clean-up
						UnitTestInterpreter.this.debugger.kill();
						setTerminated();
					}
				}
			};
		}
	}
}
