package goal.core.agent;

import eis.iilang.Percept;
import goal.core.executors.ActionExecutor;
import goal.core.executors.ModuleExecutor;
import goal.core.runtime.service.agent.RunState;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.DebuggerKilledException;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.logging.InfoLog;

import java.rmi.activation.UnknownObjectException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import krTools.language.DatabaseFormula;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.MentalAction;
import languageTools.program.agent.actions.UserSpecAction;
import vh3.goalgamygdala.GoalGamygdala;

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

    private final GoalGamygdala goalGamygdala;

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
        this.goalGamygdala = GoalGamygdala.getInstance();
	}

	/**
	 * Returns the current run state of the interpreter.
	 *
	 * @return the current run state of the interpreter
	 */
	public RunState<DEBUGGER> getRunState() {
		return this.runState;
	}

	@Override
	protected void initalizeController(Agent<? extends Controller> agent)
			throws KRInitFailedException {
		super.initalizeController(agent);
		this.program.getKRInterface().initialize();
		this.runState = new RunState<>(this, agent.getId(),
				agent.getEnvironment(), agent.getMessaging(),
				agent.getLogging(), this.program, this.debugger, this.learner);
        goalGamygdala.createAgent(agent.getId().getName());
	}

	@Override
	public void onReset() throws InterruptedException, KRInitFailedException,
			KRDatabaseException, KRQueryFailedException, UnknownObjectException {
		this.runState.reset();
		this.debugger.reset();
	}

	@Override
	public void onTerminate() {
		this.debugger.kill();
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
						GOALInterpreter.this.debugger.breakpoint(
								Channel.REASONING_CYCLE_SEPARATOR, 0, null,
								"%s has been started",
								GOALInterpreter.this.agent.getId());
						GOALInterpreter.this.runState.startCycle(false);
						call = new ModuleExecutor(
								GOALInterpreter.this.runState.getMainModule())
								.execute(GOALInterpreter.this.runState,
										GOALInterpreter.this.program
												.getKRInterface()
												.getSubstitution(null));
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
						GOALInterpreter.this.learner.terminate(
								GOALInterpreter.this.runState.getMentalState(),
								GOALInterpreter.this.runState.getReward());
						setTerminated();
						new InfoLog(GOALInterpreter.this.agent.getId()
								+ " terminated successfully.");
					}

				} catch (final Exception e) { // Thread failure handling
					GOALInterpreter.this.throwable = e;
					Exception e1 = e;
					if (e instanceof DebuggerKilledException) {
						// "normal" forced termination by the debugger.
						// re-wrap to avoid bug report for this way to kill
						e1 = new ExecutionException(
								"Agent was killed by debugger", e);
					}
					new Warning(GOALInterpreter.this.agent.getId()
							+ " was terminated", e1);
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
		return this.program;
	}

	/**
	 * Returns the debugger used while running the interpreter.
	 *
	 * @return the debugger used while running the interpreter
	 */
	public DEBUGGER getDebugger() {
		return this.debugger;
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
	 * @throws UnknownObjectException
	 * @throws KRQueryFailedException
	 * @throws KRDatabaseException
	 * @throws KRInitFailedException
	 */
	public void updateAgentAvailability(AgentId id, boolean available)
			throws KRInitFailedException, KRDatabaseException,
			KRQueryFailedException, UnknownObjectException {
		// FIXME: This should be two methods. One to add knowledge of agents.
		// Another to remove it. Both should be placed in the run state.

		// if it's me don't worry; I'll take care of myself.
		if (id.equals(this.agent.getId())) {
			return;
		}
		if (available) {
			this.runState.getMentalState().addAgentModel(id, this.debugger);
		} else {
			this.runState.getMentalState().removeAgentModel(id);
		}
	}

	/**
	 * Executes an action.
	 *
	 * Sends a user-specified action through the given middleware/messaging
	 * system to be executed in the Environment.
	 *
	 * @param action
	 *            The action to be executed in the environment.
	 * @throws GOALActionFailedException
	 */
	public void doPerformAction(Action<?> action)
			throws GOALActionFailedException {
		if (action instanceof MentalAction) {
			DEBUGGER debugger = this.runState.getDebugger();
			if (debugger instanceof SteppingDebugger) {
				((SteppingDebugger) debugger).setKeepRunning(true);
			}
			ActionExecutor.getActionExecutor(action, null).run(this.runState,
					this.program.getKRInterface().getSubstitution(null),
					debugger, false);
			if (debugger instanceof SteppingDebugger) {
				((SteppingDebugger) debugger).setKeepRunning(false);
			}
		} else if (action instanceof UserSpecAction) {
			UserSpecAction userspec = (UserSpecAction) action;
			this.runState.doPerformAction(userspec);
		}
	}

	/**
	 * Processes {@link Percept}s received from the agent's environment.
	 * Converts EIS {@link Percept}s to {@link DatabaseFormula}s and inserts new
	 * and removes old percepts from the percept base.
	 * <p>
	 * Note that the agent's percept buffer is not used for this.
	 * </p>
	 *
	 * @param newPercepts
	 *            The percepts received from the agent's environment that need
	 *            to be processed.
	 * @param previousPercepts
	 *            the received percepts from last cycle.
	 */
	public void processPercepts(Set<Percept> newPercepts,
			Set<Percept> previousPercepts) {
		this.runState.processPercepts(newPercepts, previousPercepts);
	}

	@Override
	public void dispose() throws InterruptedException {
		super.dispose();
		this.debugger.dispose();
		this.runState.dispose();
		// agent is disposed by the AgentService, which is in turn disposed by
		// the RuntimeManager, so we don't need to do that here
	}
}
