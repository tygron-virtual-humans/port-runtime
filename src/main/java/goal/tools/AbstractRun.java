package goal.tools;

import eis.exceptions.EnvironmentInterfaceException;
import goal.core.agent.Agent;
import goal.core.agent.AgentFactory;
import goal.core.agent.GOALInterpreter;
import languageTools.program.mas.LaunchRule;
import languageTools.program.mas.MASProgram;
import goal.core.runtime.MessagingService;
import goal.core.runtime.RemoteRuntimeService;
import goal.core.runtime.RuntimeManager;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.environment.EnvironmentService;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALCommandCancelledException;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.InfoLog;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

/**
 * Abstract run can be used to run a {@link MASProgram}. The run will setup the
 * environment, launch the agents. When all agents have terminated the
 * environment will be terminated as well. It is also possible to terminate the
 * mas after a certain duration.
 *
 * The result of the run can be inspected by setting a {@link ResultInspector}.
 *
 * The exact agents used depend on the subclass. Subclasses can provide an
 * {@link AgentFactory} by implementing the
 * {@link #buildAgentFactory(MessagingService)} method.
 *
 *
 * @author mpkorstanje
 *
 * @param <D>
 *            class of the Debugger used by the GOALInterpreter
 * @param <C>
 *            class of the GOALInterpreter
 */
public abstract class AbstractRun<D extends Debugger, C extends GOALInterpreter<D>> {

	/**
	 * Time to wait for the first agent to be created. Depending on the
	 * environment used agents may not appear directly after initializing the
	 * environment. This is the maximum duration the system will wait.
	 */
	public static final long TIMEOUT_FIRST_AGENT_SECONDS = 10;
	/**
	 * True if the agent should log their debugger output. Subclasses should use
	 * this to create agents that provide it.
	 */
	protected boolean debuggerOutput;

	private ResultInspector<C> resultInspector = null;
	private final MASProgram program;
	private Messaging messaging = new LocalMessaging();
	private String messagingHost = "localhost";

	/**
	 * Constructs a new abstract run of the MASProgram.
	 *
	 * @param program
	 *            to run
	 */
	public AbstractRun(MASProgram program) {
		this.program = program;
	}

	/**
	 * Returns the messaging host.
	 *
	 * @return the messaging host
	 */
	public String getMessagingHost() {
		return messagingHost;
	}

	/**
	 * Sets the messaging host.
	 *
	 * @param messagingHost
	 *            the messagingHost to set
	 */
	public void setMessagingHost(String messagingHost) {
		this.messagingHost = messagingHost;
	}

	/**
	 * Set the messaging used.
	 *
	 * @param messaging
	 *            the messaging to use
	 */
	public void setMessaging(Messaging messaging) {
		this.messaging = messaging;
	}

	/**
	 * Returns true if the {@link MASProgram} will be started with a logging
	 * debugger.
	 *
	 * @return true if the {@link MASProgram} will be started with a logging
	 *         debugger
	 */
	public boolean isDebugerOutput() {
		return this.debuggerOutput;
	}

	/**
	 * Set to true to start the {@link MASProgram} with a logging debugger.
	 *
	 * @param debuggerOutput
	 *            true if a logging debugger should be used.
	 */
	public void setDebuggerOutput(boolean debuggerOutput) {
		this.debuggerOutput = debuggerOutput;
	}

	/**
	 * Gets the {@link ResultInspector} used to inspect the agent states at the
	 * end of a run.
	 *
	 * @return the {@link ResultInspector} used to inspect the agent states at
	 *         the end of a run.
	 */
	public ResultInspector<C> getResultInspector() {
		return resultInspector;
	}

	/**
	 * Sets the {@link ResultInspector} used to inspect the agent states at the
	 * end of a run.
	 *
	 *
	 * @param resultInspector
	 *            used to inspect the agent states at the end of a run.
	 */
	public void setResultInspector(ResultInspector<C> resultInspector) {
		this.resultInspector = resultInspector;
	}

	/**
	 * Starts a single run of the mas program.
	 *
	 * @param timeout
	 *            duration to wait before terminating the program. When zero
	 *            this will wait until all agents have finished.
	 * @param timeUnit
	 *            of the duration to wait
	 * @throws MessagingException
	 * @throws GOALCommandCancelledException
	 * @throws GOALParseException
	 * @throws GOALLaunchFailureException
	 * @throws InterruptedException
	 * @throws EnvironmentInterfaceException
	 */
	// FIXME: This amount of exceptions is ridiculous. Clean this up.
	@SuppressWarnings("unchecked")
	public void run() throws MessagingException, GOALCommandCancelledException,
	GOALParseException, GOALLaunchFailureException,
	InterruptedException, EnvironmentInterfaceException {
		RuntimeManager<? extends D, ? extends C> runtimeManager = null;
		try {
			runtimeManager = buildRuntime(program);

			// Start the environment.
			// This will also start the multi-agent system
			runtimeManager.startEnvironment();

			/*
			 * Wait for at least one agent to show up. Not all environments
			 * start agents directly in response to init.
			 */
			if (runtimeManager.awaitFirstAgent(TIMEOUT_FIRST_AGENT_SECONDS,
					TimeUnit.SECONDS)) {

				// Wait for system to end.
				awaitTermination(runtimeManager);

				/*
				 * Using unknown collection type here. Java can't resolve the
				 * generic types in generics.
				 */
				Collection<?> agents = runtimeManager.getAgents();

				/*
				 * Show the results inspector what we did. Agent can still be
				 * running.
				 */
				if (resultInspector != null) {
					resultInspector.handleResult((Collection<Agent<C>>) agents);
				}
			}
		} finally {
			if (runtimeManager != null) {
				runtimeManager.shutDown();
			}
		}
	}

	/**
	 * Blocks until the agent system is terminated or times out.
	 *
	 * Subclasses can implement their own termination criterea here.
	 *
	 * @param timeout
	 * @param timeUnit
	 * @param runtimeManager
	 * @throws InterruptedException
	 */
	protected void awaitTermination(
			RuntimeManager<? extends D, ? extends C> runtimeManager)
			throws InterruptedException {
		runtimeManager.awaitTermination();
	}

	/**
	 * Builds the {@link RuntimeManager} that will be used to run the
	 * MASProgram.
	 *
	 * @param program
	 *            to run
	 * @param messagingHost
	 *            used by messaging
	 * @return a new run time service manager.
	 * @throws GOALLaunchFailureException
	 *             when the program could not be validated
	 */
	protected RuntimeManager<D, C> buildRuntime(MASProgram program)
			throws GOALLaunchFailureException {

		// FIXME: find a better way to prevent launching a MASFile with
		// errors...
		if (!program.isValidated()) {
			throw new GOALLaunchFailureException("Cannot launch MAS " + program
					+ " because it (or a child) has errors.");
		}

		// FIXME: we are still using "parsed" objects; therefore we need to
		// reset 'runtime' objects such as the launchrule objects here...
		for (LaunchRule launchRule : program.getLaunchRules()) {
			launchRule.resetApplicationCount();
		}

		// Launch the multi-agent system. and start the runtime environment.
		new InfoLog("Launching MAS " + program.getMASFile() + ".");

		// init MessagingFactory and get our messaging system

		MessagingService messagingService = new MessagingService(messagingHost,
				messaging);

		EnvironmentService environmentService = new EnvironmentService(program,
				messagingService);

		AgentFactory<D, C> agentFactory = buildAgentFactory(messagingService);
		AgentService<D, C> runtimeService = new AgentService<>(program,
				agentFactory);

		RemoteRuntimeService<D, C> remoteRuntimeService = new RemoteRuntimeService<>(
				messagingService);

		return new RuntimeManager<>(messagingService, runtimeService,
				environmentService, remoteRuntimeService);
	}

	/**
	 * Provides an agent factory used to created agents by the run time.
	 * Subclasses can implement this method to provide their own agents.
	 *
	 * @param messaging
	 *            the messaging service used for communication
	 * @return an agent factory
	 */
	protected abstract AgentFactory<D, C> buildAgentFactory(
			MessagingService messaging);

}