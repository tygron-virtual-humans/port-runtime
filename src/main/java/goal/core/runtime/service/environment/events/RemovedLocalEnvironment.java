package goal.core.runtime.service.environment.events;

import goal.core.runtime.service.environment.LocalMessagingEnvironment;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

/**
 * Indicates removed local environment.
 *
 * @author W.Pasman 14nov2013
 *
 */
public class RemovedLocalEnvironment extends EnvironmentServiceEvent {
	private final MessageBoxId boxId;

	/**
	 * @param messageBoxId
	 *            the {@link MessageBoxId} of the
	 *            {@link LocalMessagingEnvironment} that was removed.
	 */
	public RemovedLocalEnvironment(MessageBoxId messageBoxId) {
		this.boxId = messageBoxId;
	}

	/**
	 * @return the {@link MessageBoxId} of the {@link LocalMessagingEnvironment}
	 *         that was removed.
	 */
	public MessageBoxId getMessageBoxId() {
		return this.boxId;
	}
}