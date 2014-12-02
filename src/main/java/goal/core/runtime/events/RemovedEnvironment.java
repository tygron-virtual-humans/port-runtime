package goal.core.runtime.events;

import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

/**
 * A local environment disappeared in a RuntimeServiceManager.
 *
 * @author W.Pasman 14nov2013
 */
public class RemovedEnvironment implements RemoteRuntimeEvent {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1971050703490461278L;
	private final MessageBoxId messageBoxId;

	/**
	 * the MessageBoxId is the environmentport box id.
	 *
	 * @param id
	 *            The MessageBoxId
	 */
	public RemovedEnvironment(MessageBoxId id) {
		messageBoxId = id;
	}

	/**
	 * @return The associated MessageBoxId
	 */
	public MessageBoxId getMessageBoxId() {
		return messageBoxId;
	}
}
