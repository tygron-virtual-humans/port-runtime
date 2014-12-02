package goal.core.runtime.events;

import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

/**
 * indicates that a new {@link Runtime} has been launched
 *
 * @author W.Pasman
 */
public class RuntimeLaunched implements RemoteRuntimeEvent {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -6468336181039289965L;
	private final MessageBoxId newId;

	public RuntimeLaunched(MessageBoxId newRuntimeBox) {
		newId = newRuntimeBox;
	}

	public MessageBoxId getMessageBoxId() {
		return newId;
	}
}
