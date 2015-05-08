package goal.core.runtime;

import goal.core.runtime.events.RemoteRuntimeEvent;
import nl.tudelft.goal.messaging.Message;
import nl.tudelft.goal.messaging.exceptions.CommunicationFailureException;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;
import nl.tudelft.goal.messaging.messagebox.MessageBoxListener;

/**
 * This class handles the {@link RuntimeManager}'s messagebox. It basically
 * connects with other {@link RuntimeManager}s to exchange events.
 *
 * @author wouter
 */
public abstract class RuntimeMessageBox implements MessageBoxListener {
	MessageBox messageBox = null;
	MessagingService messagingService;

	public RuntimeMessageBox(MessageBox mbox)
			throws CommunicationFailureException, MessagingException {
		this.messageBox = mbox;
		this.messageBox.addListener(this);

	}

	public void shutDown() {
	}

	@Override
	public boolean newMessage(Message message) {
		if (message.repliesToId() != null) {
			return false; // ignore replies to RPC calls
		}

		// can we assume that this call will be handled quickly?
		RemoteRuntimeEvent event = (RemoteRuntimeEvent) message.getContent();
		runtimeEventOccured(event);
		return true;
	}

	/**
	 * To be overridden in the {@link RuntimeManager} to handle the event.
	 */
	abstract public void runtimeEventOccured(RemoteRuntimeEvent evt);

}
