package goal.core.runtime;

import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;
import goal.tools.logging.InfoLog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.client.MessagingClient;
import nl.tudelft.goal.messaging.exceptions.CommunicationFailureException;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId.Type;

/**
 * Provides the following messaging services:
 * <ul>
 * <li>Launching a messaging service.</li>
 * <li>Shutting down the messaging service.</li>
 * </ul>
 *
 * DOC what does this add to the Messaging interface?
 *
 * @author K.Hindriks
 */
public class MessagingService {
	private final Messaging messaging;
	private final MessagingClient messagingClient;
	/**
	 * Store of all IDs for entities using the messaging service that are
	 * managed locally.
	 */
	private final Set<MessageBoxId> localIDs = new LinkedHashSet<>();

	/**
	 * DOC
	 *
	 * @param host
	 * @param messaging
	 *
	 * @throws GOALLaunchFailureException
	 */
	public MessagingService(String host, Messaging messaging)
			throws GOALLaunchFailureException {
		this.messaging = messaging;

		// If the host needs to be started locally, start a server that
		// provides the infrastructure to run agents on.
		if (host.equals("localhost")) {
			new InfoLog("Launching messaging server...");
			try {
				messaging.runServer();
			} catch (MessagingException e) {
				throw new GOALLaunchFailureException(
						"Starting messaging server failed", e);
			}
			new InfoLog("OK.");
		}

		// Only ask for a messaging client after a server has been started.
		new InfoLog("Connecting to messaging server...");
		try {
			this.messagingClient = messaging.getClient(new URI(host));
		} catch (MessagingException e) {
			throw new GOALLaunchFailureException(
					"Failed to connect to messaging server", e);
		} catch (URISyntaxException e) {
			throw new GOALLaunchFailureException(
					"Failed to connect to messaging server", e);
		}

		new InfoLog("OK.");
	}

	/**
	 * TODO: hide the messaging client... and remove this method.
	 *
	 * @return The messaging client.
	 */
	public MessagingClient getClient() {
		return this.messagingClient;
	}

	/**
	 * Checks whether ID is locally managed.
	 *
	 * @param id
	 *            The ID to be checked.
	 * @return {@code true} if the ID is locally managed.
	 */
	public boolean isLocal(MessageBoxId id) {
		return this.localIDs.contains(id);
	}

	/**
	 * Returns a new, unique ID.
	 *
	 * Hides getNewUniqueId method from {@link MessagingClient}.
	 *
	 * @param name
	 *            A suggested name to base the ID on.
	 * @param type
	 *            The type of entity for which the ID is generated.
	 * @return A new, unique ID.
	 * @throws CommunicationFailureException
	 */
	public MessageBoxId getNewUniqueID(String name, Type type)
			throws CommunicationFailureException {
		MessageBoxId id = this.messagingClient.getNewUniqueID(name, type);
		this.localIDs.add(id);
		return id;
	}

	/**
	 * Returns a new message box for a GOAL agent.
	 *
	 * Hides createMessageBox method.
	 *
	 * @param id
	 *            The unique id of the GOAL agent.
	 * @return A new message box for a GOAL agent.
	 * @throws MessagingException
	 *             DOC
	 */
	public MessageBox getNewMessageBox(MessageBoxId id)
			throws MessagingException {
		return this.messagingClient.createMessageBox(id);
	}

	/**
	 * Removes the message box from the messaging infrastructure. Is a local
	 * service only, so ID of message box should be known loccally.
	 *
	 * @param messageBox
	 *            The message box to be removed.
	 * @throws GOALRuntimeErrorException
	 *             If message box could not be removed.
	 */
	public void deleteMessageBox(MessageBox messageBox) {
		if (this.localIDs.contains(messageBox.getId())) {
			try {
				this.messagingClient.deleteMessageBox(messageBox);
				this.localIDs.remove(messageBox.getId());
			} catch (MessagingException e) {
				throw new GOALRuntimeErrorException(
						"Failed to disconnect " + messageBox.getId()
						+ " from messaging infrastructure", e);
			}
		}
	}

	/**
	 * DOC If we're hosting the messaging server, we also bring down this
	 * server.
	 */
	public void shutDown() {
		this.localIDs.clear();
		if (this.messaging != null) {
			try {
				this.messagingClient.dispose();
				this.messaging.stopServer();
			} catch (MessagingException e) {
				new Warning(
						Resources
						.get(WarningStrings.FAILED_SHUTDOWN_MSG_SERVER),
						e);
			}
		}
	}
}
