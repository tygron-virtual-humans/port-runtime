package goal.core.agent;

import goal.core.kr.KRlanguage;
import goal.core.program.actions.LogAction;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

/**
 * Generic representation of an agent in the GOAL runtime environment.
 *
 * An agent consist of an identity, the ability to act and perceive its an
 * environment through and the ability to communicate with other agents. What
 * the agent does is controlled by a {@link Controller}.
 *
 * The {@link Controller} starts a process that can be used by subclasses to
 * drive the agents actions. This process can be stopped, started and reset
 * through the agent. During these operations the agent should stay connected to
 * to the environment and messaging.
 *
 * At the end of the Agents life the dispose method should be called to clean up
 * any resources claimed by the agent.
 *
 *
 * @param <CONTROLLER>
 *            a subclass of {@link Controller} used to control the agent.
 */
public class Agent<CONTROLLER extends Controller> {
	private final CONTROLLER controller;
	private final EnvironmentCapabilities environment;
	private final AgentId id;
	private final MessagingCapabilities messaging;
	private final LoggingCapabilities logging;

	/**
	 * Creates new agent.
	 *
	 * @param id
	 *            unique identity of the agent
	 * @param environment
	 *            capabilities for the environment in which the agent is
	 *            situated
	 * @param messaging
	 *            capabilities of the agent
	 * @param controller
	 *            that controls the agent
	 * @param logger
	 *            a logger for {@link LogAction}s.
	 *
	 * @throws KRInitFailedException
	 *             when the agent could not initialize the KRLanguage
	 */
	public Agent(AgentId id, EnvironmentCapabilities environment,
			MessagingCapabilities messaging, LoggingCapabilities logger,
			CONTROLLER controller) throws KRInitFailedException {
		this.id = id;
		this.environment = environment;
		this.messaging = messaging;
		this.logging = logger;
		this.controller = controller;
		this.controller.initalizeController(this);
	}

	/**
	 * Waits for the agents process to terminate.
	 *
	 * @throws InterruptedException
	 *             when interrupted while waiting for the agent to terminate
	 */
	public void awaitTermination() throws InterruptedException {
		controller.awaitTermination();
	}

	/**
	 * Disposes any resources held by the agent.
	 */
	public void dispose() {
		try {
			controller.dispose();
		} catch (Exception e) {
			new Warning(Resources.get(WarningStrings.FAILED_FREE_AGENT), e);
		}
		messaging.dispose();
		try {
			environment.dispose();
		} catch (Exception e) {
			new Warning(Resources.get(WarningStrings.FAILED_FREE_AGENT), e);
		}
		logging.dispose();
	}

	/**
	 *
	 * @return the agents controller
	 */
	public CONTROLLER getController() {
		return controller;
	}

	/**
	 * @return the environment capabilities of the agent.
	 */
	public EnvironmentCapabilities getEnvironment() {
		return environment;
	}

	/**
	 * Returns the name of the {@link Agent}.
	 *
	 * @return The name of the agent.
	 */
	public AgentId getId() {
		return id;
	}

	/**
	 * @return the messaging capabilities of the agent.
	 */

	public MessagingCapabilities getMessaging() {
		return messaging;
	}

	/**
	 * @return the agent's {@link LoggingCapabilities}.
	 */
	public LoggingCapabilities getLogging() {
		return logging;
	}

	/**
	 * Checks if agents process is running. Returns {@code true} if it has been
	 * started and is running.
	 *
	 * @return {@code true} if the agents proccess has been started and is
	 *         running
	 */

	public boolean isRunning() {
		return controller.isRunning();
	}

	/**
	 * Resets the agents controller. The agents procces is stopped, once the
	 * agent has stopped its internal state will be reset.
	 *
	 * @throws InterruptedException
	 *             when interrupted while waiting for the agent to stop
	 * @throws KRInitFailedException
	 *             when failing to reset the {@link KRlanguage}.
	 */
	public void reset() throws InterruptedException, KRInitFailedException {
		controller.reset();
	}

	/**
	 * Starts the agents process.
	 */
	public void start() {
		controller.run();
	}

	/**
	 * Stops the agents process.
	 */
	public void stop() {
		controller.terminate();
	}

	/**
	 * Returns the name of the agent. Useful for debugging purposes.
	 *
	 * @return the name of the agent
	 */
	@Override
	public String toString() {
		return getId().getName();
	}
}