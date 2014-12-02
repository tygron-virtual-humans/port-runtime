package goal.core.runtime.events;

import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

/**
 * A new local environment appeared in a RuntimeServiceManager.
 *
 * @author W.Pasman 14nov2013
 */
public class NewEnvironment implements RemoteRuntimeEvent {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 1971050703490461278L;
	private final MessageBoxId messageBoxId;

	public NewEnvironment(MessageBoxId id) {
		messageBoxId = id;
	}

	public MessageBoxId getMessageBoxId() {
		return messageBoxId;
	}
}
