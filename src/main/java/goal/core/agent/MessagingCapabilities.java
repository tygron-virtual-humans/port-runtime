package goal.core.agent;

import java.util.Set;

import languageTools.program.agent.msg.Message;

/**
 * Provides and abstract representation of the ability of the agent to
 * communicate with other agents.
 *
 * An agent needs to be able to exchanges message with other agents.Implementing
 * classes can provide this functionality as they see fit.
 *
 * @author mpkorstanje
 *
 */
public interface MessagingCapabilities {

	/**
	 * Resets any state stored in agent capabilities.
	 */
	public abstract void reset();

	/**
	 * Get all messages from the queue and empties the queue.
	 *
	 * @return all messages in the queue
	 */
	public abstract Set<Message> getAllMessages();

	/**
	 * Posts a message to another agent.
	 *
	 * @param message
	 *            Message added to the out queue.
	 */
	public abstract void postMessage(Message message);

	/**
	 * Release any resources held.
	 */
	public abstract void dispose();

}