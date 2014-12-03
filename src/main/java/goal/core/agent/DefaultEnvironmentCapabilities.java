package goal.core.agent;

import eis.exceptions.EnvironmentInterfaceException;
import eis.iilang.Percept;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.actions.UserSpecAction;
import goal.core.runtime.service.environmentport.EnvironmentPort;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import nl.tudelft.goal.messaging.exceptions.MessagingException;

/**
 * Provides the Agents {@link EnvironmentCapabilities} through an
 * {@link EnvironmentPort}. The environment port is shared between multiple
 * agents and provides more functionality then needed by the agent. This hides
 * these details from the agent.
 *
 * @author mpkorstanje
 */
public class DefaultEnvironmentCapabilities implements EnvironmentCapabilities {
	/**
	 * The percept buffer of the {@link AgentMesg}.
	 */
	private final Queue<Percept> perceptBuffer = new LinkedList<>();

	private final EnvironmentPort environment;

	private final AgentId id;

	/**
	 * Constructs the default environment capabilities.
	 *
	 * @param agentId
	 *            of the agent
	 * @param environmentPort
	 *            used to talk to the environment
	 */
	public DefaultEnvironmentCapabilities(AgentId agentId,
			EnvironmentPort environmentPort) {
		this.id = agentId;
		this.environment = environmentPort;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see goal.core.agent.Capabilities#getReward()
	 */
	@Override
	public Double getReward() throws MessagingException,
	EnvironmentInterfaceException {
		return environment.getReward(id.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see goal.core.agent.Capabilities#getPercepts()
	 */
	@Override
	public Set<Percept> getPercepts() throws MessagingException,
	EnvironmentInterfaceException {
		Set<Percept> percepts = new LinkedHashSet<>();

		percepts.addAll(perceptBuffer);
		perceptBuffer.clear();

		// Only request percepts from environment if we are connected to an
		// environment and that environment is running;
		// otherwise, return the empty list.
		percepts.addAll(environment.getPercepts(id.getName()));

		return percepts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goal.core.agent.Capabilities#performAction(languageTools.program.agent.actions.
	 * UserSpecAction)
	 */
	@Override
	public void performAction(UserSpecAction action) throws MessagingException,
	EnvironmentInterfaceException {
		Collection<Percept> percepts = environment.performAction(id.getName(),
				action.convert());
		perceptBuffer.addAll(percepts);
	}

	@Override
	public void dispose() throws MessagingException,
	EnvironmentInterfaceException {
		environment.freeAgent(id.getName());
	}
}
