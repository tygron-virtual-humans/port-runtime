package goal.core.runtime;

import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.core.runtime.events.DeadAgent;
import goal.core.runtime.events.NewAgent;
import goal.core.runtime.events.RemoteRuntimeEvent;
import goal.core.runtime.events.RemoteRuntimeListener;
import goal.core.runtime.events.RuntimeLaunched;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.logging.InfoLog;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import nl.tudelft.goal.messaging.Message;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxListener;

/**
 * Handles messaging between the local run time and remote runtime.
 *
 * Can be used to inform other runtimes that we've added or removed agents. Will
 * listen for the same events on other runtimes.
 *
 * @author mpkorstanje
 *
 * @param <D>
 *            debugger used by the agents.
 * @param <C>
 *            controller used by the agents.
 */
public class RemoteRuntimeService<D extends Debugger, C extends GOALInterpreter<D>> {

	/**
	 * This catches messages in our messagebox, pumps them into a blocking queue
	 * and handles them in strict incoming order.
	 */
	private class Messages2Runtime implements Runnable, MessageBoxListener {

		private final BlockingQueue<RemoteRuntimeEvent> messages = new LinkedBlockingQueue<>();

		/**
		 * Handle next action on the queue
		 *
		 * @param action
		 */
		private void handleEvent(RemoteRuntimeEvent event) {
			for (RemoteRuntimeListener l : RemoteRuntimeService.this.listeners) {
				l.remoteRuntimeEventOccured(event);
			}
		}

		@Override
		public boolean newMessage(Message message) {
			if (!(message.getContent() instanceof RemoteRuntimeEvent)) {
				// ignore messages that are not intended for us
				return false;
			}

			this.messages.add((RemoteRuntimeEvent) message.getContent());
			return true;

		}

		@Override
		public void run() {
			new InfoLog("Messages->Runtime started.");

			try {
				while (true) { // interrupt this thread to stop.
					RemoteRuntimeEvent evt = this.messages.take();
					handleEvent(evt);
				}
			} catch (InterruptedException e) {
				// This happens if someone tries to stop this thread.
			} catch (Throwable e) {
				new Warning(Resources.get(WarningStrings.FAILED_MSG_RUNTIME), e);
			}

			new InfoLog("Messages->Runtime stopped.");
		}

	}

	private class Runtime2Messages implements Runnable {
		private final BlockingQueue<RemoteRuntimeEvent> events = new LinkedBlockingQueue<>();

		public void add(RemoteRuntimeEvent event) {
			this.events.add(event);
		}

		/**
		 * broadcast to all other {@link Runtime}s. Gives only a warning for
		 * failures.
		 *
		 * @param message
		 */
		private void broadcast(RemoteRuntimeEvent event) {
			List<MessageBoxId> rsmboxes;
			try {
				rsmboxes = RemoteRuntimeService.this.messagingService
						.getClient().getMessageBoxes(
								MessageBoxId.Type.RUNTIMESERVICE, null);
			} catch (MessagingException e1) {
				new Warning(
						Resources.get(WarningStrings.FAILED_UPDATE_INTERNAL),
						e1);
				return; // FIXME this is fatal. Should throw up.
			}
			rsmboxes.remove(RemoteRuntimeService.this.messageBox.getId());
			for (MessageBoxId receiver : rsmboxes) {
				try {
					Message msg = RemoteRuntimeService.this.messageBox
							.createMessage(receiver, event, null);
					RemoteRuntimeService.this.messageBox.send(msg);
				} catch (MessagingException e) {
					new Warning(String.format(
							Resources.get(WarningStrings.FAILED_BROADCAST),
							event.toString()), e);
				}
			}
		}

		@Override
		public void run() {
			new InfoLog("Runtime->Messages started.");

			try {
				while (true) { // interrupt this thread to stop.
					RemoteRuntimeEvent event = this.events.take();
					broadcast(event);
				}
			} catch (InterruptedException e) {
				// This happens if someone tries to stop this thread.
			} catch (Throwable e) {
				new Warning(Resources.get(WarningStrings.FAILED_RUNTIME_MSG), e);
			}

			new InfoLog("Runtime->Messages stopped.");
		}

	}

	private MessageBox messageBox;

	private Thread messages2RuntimeThread;

	private Thread runtime2MessagesThread;

	private final MessagingService messagingService;

	private final Messages2Runtime messages2Runtime = new Messages2Runtime();

	private final Runtime2Messages runtime2Messages = new Runtime2Messages();

	private final List<RemoteRuntimeListener> listeners = new LinkedList<>();

	/**
	 * Constructs a new RemoteRuntimeService.
	 *
	 * @param messagingService
	 *            used to contact other run times
	 */
	public RemoteRuntimeService(MessagingService messagingService) {

		this.messagingService = messagingService;

	}

	/**
	 * Adds a listener for remote runtime events.
	 *
	 * @param listener
	 *            to add
	 */
	public void addListener(RemoteRuntimeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Awaits termination of the service.
	 *
	 * @throws InterruptedException
	 *             when interupted while waiting
	 */
	public void awaitTermination() throws InterruptedException {
		this.messages2RuntimeThread.join();
		this.runtime2MessagesThread.join();
	}

	/**
	 * Notifies all remote runtime services that we've lost a local agent.
	 *
	 * @param agent
	 *            that has been removed
	 */
	public void broadCastDeadAgent(Agent<?> agent) {
		this.runtime2Messages.add(new DeadAgent(agent.getId()));
	}

	/**
	 * Notifies all remote runtime services that a local agent has been added.
	 *
	 * @param agent
	 *            that has been added
	 */
	public void broadCastNewAgent(Agent<?> agent) {
		this.runtime2Messages.add(new NewAgent(agent.getId()));
	}

	/**
	 * Notify all remote runtime services that a new runtime has launched.
	 */
	public void broadcastRuntimeLaunched() {
		this.runtime2Messages.add(new RuntimeLaunched(this.messageBox.getId()));
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener
	 *            to remove
	 */
	public void removeListener(RemoteRuntimeListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Shuts down the service.
	 */
	public void shutDown() {
		this.messages2RuntimeThread.interrupt();
		this.runtime2MessagesThread.interrupt();
	}

	/**
	 * Starts the service.
	 *
	 * @throws MessagingException
	 *             when unable to setup messaging.
	 */

	public void start() throws MessagingException {

		MessageBoxId id = this.messagingService.getClient().getNewUniqueID(
				"Runtime Manager", MessageBoxId.Type.RUNTIMESERVICE);
		this.messageBox = this.messagingService.getClient()
				.createMessageBox(id);
		this.messageBox.addListener(this.messages2Runtime);

		this.messages2RuntimeThread = new Thread(this.messages2Runtime,
				"Messages -> Runtime");
		this.messages2RuntimeThread.start();

		this.runtime2MessagesThread = new Thread(this.runtime2Messages,
				"Runtime -> Messages");
		this.runtime2MessagesThread.start();
	}

}