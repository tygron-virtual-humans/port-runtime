package goal.core.agent;

import goal.core.runtime.service.agent.RunState;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.Warning;
import krTools.errors.exceptions.KRInitFailedException;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import goal.tools.logging.InfoLog;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Interpreter for {@link AgentProgram}.
 *
 * The interpreter can be provided with a {@link Debugger} that will be called
 * at specific points during the interpretation.
 *
 * A {@link Learner} will be consulted during the Adaptive sections of a
 * AgentProgram.
 *
 * @author mpkorstanje
 *
 * @param <DEBUGGER>
 *            class of the Debugger used by the interpreter.
 */
public class GOALInterpreter<DEBUGGER extends Debugger> extends Controller {
	/**
	 * The {@link RunState} of this agent. Records the current state of the
	 * interpreter in the GOAL Program.
	 */
	protected RunState<DEBUGGER> runState;
	/**
	 * Program ran by the interpreter.
	 */
	protected final AgentProgram program;
	/**
	 * Debugger used while running the interpreter.
	 */
	protected final DEBUGGER debugger;
	/**
	 * Learner consulted during adaptive sections of the program.
	 */
	protected final Learner learner;

	/**
	 * Constructs a new interpreter.
	 *
	 * @param program
	 *            to run
	 * @param debugger
	 *            used to debug the program
	 * @param learner
	 *            used evaluate adaptive rules
	 */
	public GOALInterpreter(AgentProgram program, DEBUGGER debugger,
			Learner learner) {
		this.program = program;
		this.debugger = debugger;
		this.learner = learner;
	}

	/**
	 * Returns the current run state of the interpreter.
	 *
	 * @return the current run state of the interpreter
	 */
	public RunState<DEBUGGER> getRunState() {
		return runState;
	}

	@Override
	protected void initalizeController(Agent<? extends Controller> agent)
			throws KRInitFailedException {
		super.initalizeController(agent);
		program.getKRInterface().initialize(/*program, agent.getId().getName()*/);
		this.runState = new RunState<>(agent.getId(), agent.getEnvironment(),
				agent.getMessaging(), agent.getLogging(), program, debugger,
				learner);
	}

	@Override
	public void onReset() throws InterruptedException, KRInitFailedException {
		runState.reset();
		debugger.reset();
	}

	@Override
	public void onTerminate() {
		debugger.kill();
	}

	@Override
	protected Runnable getRunnable(final Executor pool,
			final Callable<Callable<?>> in) {
		return new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					Callable<Callable<?>> call = in;
					if (call == null) {
						// Create the initial call (run the main module)
						debugger.breakpoint(Channel.REASONING_CYCLE_SEPARATOR,
								0, "%s has been started", agent.getId());
						runState.startCycle(false);
						call = runState.getMainModule().execute(runState,
								program.getKRInterface().getEmptySubstitution());
					}
					Callable<Callable<?>> out = null;
					if (call != null) {
						// Run the current task
						out = (Callable<Callable<?>>) call.call();
					}
					if (out != null && isRunning()) {
						// Submit the next task (when any)
						pool.execute(getRunnable(pool, out));
					} else {
						// Clean-up (terminate/dispose)
						learner.terminate(runState.getMentalState(),
								runState.getReward());
						setTerminated();
						new InfoLog(agent.getId() + " terminated successfully.");
					}
				} catch (final Exception e) {
					throwable = e;
					setTerminated();
				}
			}
		};
	}

	/**
	 * Returns the program ran by the interpreter.
	 *
	 * @return the program ran by the interpreter
	 */
	public AgentProgram getProgram() {
		return program;
	}

	/**
	 * Returns the debugger used while running the interpreter.
	 *
	 * @return the debugger used while running the interpreter
	 */
	public DEBUGGER getDebugger() {
		return debugger;
	}

	/**
	 * Makes agent aware of existence of other agent with id {@link AgentId}.
	 *
	 * @param id
	 *            Id of other agent that is introduce to this agent.
	 * @param available
	 *            {@code true} if agent is available and needs to be added;
	 *            {@code false} if agent is no longer available and needs to be
	 *            removed.
	 */
	public void updateAgentAvailability(AgentId id, boolean available) {
		// FIXME: This should be two methods. One to add knowledge of agents.
		// Another to remove it. Both should be placed in the run state.

		// if it's me don't worry; I'll take care of myself.
		if (id.equals(agent.getId())) {
			return;
		} else if (available) {
			try {
				runState.getMentalState().addAgentModel(id, debugger);
			} catch (Exception e) {
				new Warning(e.getMessage(), e.getCause());
			}
		} else {
			try {
				runState.getMentalState().removeAgentModel(id);
			} catch (Exception e) {
				new Warning(e.getMessage(), e.getCause());
			}
		}
	}

	@Override
	public void dispose() throws InterruptedException {
		super.dispose();
		debugger.dispose();
		runState.dispose();
		// agent is disposed by the AgentService, which is in turn disposed by
		// the RuntimeManager, so we don't need to do that here
	}
}
