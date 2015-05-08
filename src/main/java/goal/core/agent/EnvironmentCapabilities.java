package goal.core.agent;

import java.util.Set;

import nl.tudelft.goal.messaging.exceptions.MessagingException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.iilang.Action;
import eis.iilang.Percept;

/**
 * Provides and abstract representation of the capabilities of the agent in the
 * environment.
 *
 * An agent needs to be able to retrieve percepts and rewards from an
 * environment and an agent needs to be able to execute actions in the
 * environment.
 *
 * Implementing classes can provide this functionality as they see fit.
 *
 *
 * @author mpkorstanje
 *
 */
public interface EnvironmentCapabilities {

	/**
	 * Get the reward that the environment provides to this agent.
	 *
	 * @return Double containing number between 0 and 1 (usually), or
	 *         {@code null} if the environment did not provide a reward.
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public abstract Double getReward() throws MessagingException,
	EnvironmentInterfaceException;

	/**
	 * Sends a user-specified action to the environment in which it should be
	 * executed.
	 *
	 * @param action
	 *            the action to be executed in the environment
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public abstract void performAction(Action action)
			throws MessagingException, EnvironmentInterfaceException;

	/**
	 * Collects percepts from the environment. When no percepts could be
	 * collected this method should return an empty set.
	 *
	 * @return the set of percepts received from the environment
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public abstract Set<Percept> getPercepts() throws MessagingException,
	EnvironmentInterfaceException;

	/**
	 * Releases any resources held.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public abstract void dispose() throws MessagingException,
	EnvironmentInterfaceException;

}