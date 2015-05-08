package goal.core.runtime.service.environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import nl.tudelft.goal.messaging.Message;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.exceptions.CommunicationFailureException;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId.Type;
import nl.tudelft.goal.messaging.messagebox.MessageBoxListener;
import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.EnvironmentListener;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.EntityException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.QueryException;
import eis.exceptions.RelationException;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.core.runtime.service.environmentport.actions.Action;
import goal.core.runtime.service.environmentport.actions.AssociateEntity;
import goal.core.runtime.service.environmentport.actions.ExecuteAction;
import goal.core.runtime.service.environmentport.actions.FreeAgent;
import goal.core.runtime.service.environmentport.actions.GetPercepts;
import goal.core.runtime.service.environmentport.actions.Kill;
import goal.core.runtime.service.environmentport.actions.Pause;
import goal.core.runtime.service.environmentport.actions.RegisterAgent;
import goal.core.runtime.service.environmentport.actions.Reset;
import goal.core.runtime.service.environmentport.actions.Start;
import goal.core.runtime.service.environmentport.actions.Subscribe;
import goal.core.runtime.service.environmentport.actions.UnSubscribe;
import goal.core.runtime.service.environmentport.environmentport.events.DeletedEntityEvent;
import goal.core.runtime.service.environmentport.environmentport.events.EnvironmentEvent;
import goal.core.runtime.service.environmentport.environmentport.events.FreeEntityEvent;
import goal.core.runtime.service.environmentport.environmentport.events.NewEntityEvent;
import goal.core.runtime.service.environmentport.environmentport.events.NewPerceptEvent;
import goal.core.runtime.service.environmentport.environmentport.events.StateChangeEvent;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;
import goal.tools.logging.InfoLog;

/**
 * <p>
 * Main responsibility of an environment connector is to link the environment
 * (by using the Environment Interface Standard, EIS) to a GOAL MAS.
 * </p>
 * <p>
 * Connects an {@link EnvironmentInterfaceStandard} to the {@link Messaging}
 * system. This is a server, running the {@link EnvironmentInterfaceStandard}
 * environment in a separate thread. It enables agents and RSM to connect the
 * environment that runs on top of the {@link Messaging} system.
 * </p>
 * <p>
 * It allows agents to communicate with the environment via {@link Message}s to
 * get percepts and execute actions.
 *
 * DOC/check... And the {@link MonitoringService} to manage the environment
 * using {@link Message}s.
 * <p>
 * To request command execution, construct a {@link EnvOnMsgAction} and send it
 * to us. All incoming command requests are strictly serialized by queing them
 * in a BlockingQueue and handling them in incoming order.
 * <p>
 * To receive callbacks on environmentListener events, you have to subscribe to
 * us, using the {@link Subscribe} command.
 * <p>
 * Assumptions: the caller of this server sends messages using a
 * {@link MessageBox} that has a {@link MessageBoxId} that exactly matches the
 * actual agent's name in EIS. This allows us to use the
 * {@link Message#getSender()} as the EIS agent name.
 * <p>
 *
 * @author W.Pasman 22mar2012
 * @modified #2385 put this completely on top of {@link Messaging}.
 * @modified K.Hindriks
 */
public class LocalMessagingEnvironment {
	private final Thread messages2EnvironmentThread;

	private final Messages2Environment messages2Environment = new Messages2Environment();

	public class Messages2Environment implements Runnable, MessageBoxListener {
		private final BlockingQueue<Action> requests = new LinkedBlockingQueue<>();

		@Override
		public boolean newMessage(final Message message) {
			if (!(message.getContent() instanceof Action)) {
				// Simply ignore messages that are not intended for us...
				return false;
			}

			if (!LocalMessagingEnvironment.this.running) {
				// Message is for us but we don't currently accept messages...
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_LISTENER_OFFLINE),
						this.toString(), message.toString()));
				return false;
			}

			Action request = (Action) message.getContent();
			request.setMessage(message);

			/*
			 * TODO: All requests could be handled on the calling thread if EIS
			 * was thread-safe. EIS specifications make no such guarantees. So
			 * we treat all operations as if they are not thread safe and queue
			 * them up.
			 * 
			 * On Thread-Safe implementations of EIS this creates somewhat of a
			 * bottleneck because all agents are now waiting for the
			 * environment.
			 */

			this.requests.add(request);
			return true;
		}

		@Override
		public void run() {
			new InfoLog("Messages->Environment started."); //$NON-NLS-1$

			try {
				while (LocalMessagingEnvironment.this.running) {
					Action action = this.requests.take();
					handleAction(action);
				}
			} catch (InterruptedException e1) {
				// Breaking out of loop.
			} catch (Throwable e) {
				new Warning(Resources.get(WarningStrings.FAILED_MSG_ENV), e);
			}

			new InfoLog(Resources.get(WarningStrings.FAILED_MSG_ENV_STOPPED));
		}

		private void handleAction(Action action) {
			// Execute action.
			Serializable result;
			try {
				result = action.invoke(this);
			} catch (Exception e) { // report all errors to caller
				result = e;
			}

			try {
				Message replyMsg = LocalMessagingEnvironment.this.messageBox
						.createMessage(action.getSender(), result,
								action.getMessage());
				LocalMessagingEnvironment.this.messageBox.send(replyMsg);
			} catch (MessagingException e) {
				new Warning(
						String.format(Resources
								.get(WarningStrings.FAILED_REPLY_AFTER_ACTION),
								action.toString()), e);
			}
		}

		/**
		 * get the reward of given entity.
		 *
		 * @param agentName
		 *            the agent's name (as known by EIS).
		 * @return reward of given entity. Consult environment's manual for the
		 *         precise meaning of getReward and for the evaluation time of
		 *         getReward in a multi-threading situation.
		 *
		 * @throws QueryException
		 *             if getReward is not supported by the environmment,
		 *             environment does not return a floating point number, etc.
		 */
		public Serializable getReward(String agentName) throws QueryException {
			String value;

			value = LocalMessagingEnvironment.this.eis
					.queryProperty("REWARD " + agentName); //$NON-NLS-1$
			if (value == null) {
				return null;
			}

			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new QueryException(
						"getReward returned a non-number value " + value, e); //$NON-NLS-1$
			}
		}

		public Serializable invoke(FreeAgent freeAgent)
				throws RelationException, EntityException {
			LocalMessagingEnvironment.this.eis.freeAgent(freeAgent
					.getAgentName());
			return null;
		}

		public Serializable invoke(GetPercepts getPercepts)
				throws PerceiveException, NoEnvironmentException {
			/**
			 * The default implementation of EIS returns all percepts for the
			 * entities associated with the agent if no entities are passed as
			 * arguments.
			 */
			String eisAgentName = getPercepts.getAgentName();
			LinkedList<Percept> percepts = new LinkedList<>();

			// In case collecting percepts from the environment fails, we return
			// an empty map.
			Map<String, Collection<Percept>> eispercepts = new HashMap<>();
			eispercepts = LocalMessagingEnvironment.this.eis
					.getAllPercepts(eisAgentName);

			// Collect percepts obtained for each entity that the agent is
			// connected to.
			for (Collection<Percept> perceptsOfOneEntity : eispercepts.values()) {
				percepts.addAll(perceptsOfOneEntity);
			}

			return percepts;
		}

		public Serializable invoke(Kill kill) throws ManagementException {
			LocalMessagingEnvironment.this.eis.kill();
			return null;
		}

		public Serializable invoke(Pause pause) throws ManagementException {
			LocalMessagingEnvironment.this.eis.pause();
			return null;
		}

		public Serializable invoke(ExecuteAction executeAction)
				throws ActException {
			Map<String, Percept> map = LocalMessagingEnvironment.this.eis
					.performAction(executeAction.getAgentName(),
							executeAction.getAction());
			return new ArrayList<>(map.values());
		}

		public Serializable invoke(AssociateEntity associateEntity)
				throws RelationException {
			LocalMessagingEnvironment.this.eis
					.associateEntity(associateEntity.getAgentName(),
							associateEntity.getEntity());
			return null;
		}

		public Serializable invoke(RegisterAgent registerAgent)
				throws AgentException {
			LocalMessagingEnvironment.this.eis.registerAgent(registerAgent
					.getAgentName());
			return null;
		}

		public Serializable invoke(Reset reset) throws ManagementException {
			LocalMessagingEnvironment.this.eis
					.reset(LocalMessagingEnvironment.this.initialization);
			return null;
		}

		public Serializable invoke(Start start) throws ManagementException {
			// FIXME: Eis should not complain about transitions from start to
			// start.
			if (LocalMessagingEnvironment.this.eis.getState() != EnvironmentState.RUNNING) {
				LocalMessagingEnvironment.this.eis.start();
			}
			return null;
		}

		public Serializable invoke(Subscribe subscribe) {
			LinkedList<EnvironmentEvent> events = new LinkedList<>();

			// Already subscribed, quietly ignore.
			if (LocalMessagingEnvironment.this.subscribedEnvironmentPorts
					.contains(subscribe.getSender())) {
				return events;
			}

			// Add environment port as listener.
			LocalMessagingEnvironment.this.subscribedEnvironmentPorts
					.add(subscribe.getSender());

			try {
				for (String entity : LocalMessagingEnvironment.this.eis
						.getFreeEntities()) {
					String type = null;
					try {
						type = LocalMessagingEnvironment.this.eis
								.getType(entity);
					} catch (EntityException e) {
						new Warning(
								Resources
										.get(WarningStrings.FAILED_EIS_GETTYPE),
								e);
					}
					events.add(new NewEntityEvent(entity, type));
				}

				// Inform listener about current state of environment by
				// requesting
				// EIS for state information.
				events.add(new StateChangeEvent(
						LocalMessagingEnvironment.this.eis.getState()));

			} catch (NullPointerException e) {
				// FIXME: BW4T can throw nullpointers when getFreeEntities() or
				// getState() is called before init().
			}
			return events;
		}

		public Serializable invoke(UnSubscribe unSubscribe) {
			LocalMessagingEnvironment.this.subscribedEnvironmentPorts
					.remove(unSubscribe.getSender());
			return null;
		}
	}

	private final Thread environment2MessagesThread;

	private final Environment2Messages environment2Messages = new Environment2Messages();

	private class Environment2Messages implements Runnable, AgentListener,
			EnvironmentListener {
		private final BlockingQueue<EnvironmentEvent> events = new LinkedBlockingQueue<>();

		/**
		 * Notify all listeners of event.
		 *
		 * @param event
		 *            The event the listeners are being notified of.
		 */
		private void notifyAll(EnvironmentEvent event) {
			for (MessageBoxId listener : LocalMessagingEnvironment.this.subscribedEnvironmentPorts) {
				Message msg;
				try {
					msg = LocalMessagingEnvironment.this.messageBox
							.createMessage(listener, event, null);
				} catch (MessagingException e) {
					new Warning(
							String.format(
									Resources
											.get(WarningStrings.FAILED_CREATE_MSG_TO_INFORM_ENVPORT),
									event.toString(), listener.toString()), e);
					return;
				}
				try {
					LocalMessagingEnvironment.this.messageBox.send(msg);
				} catch (MessagingException e) {
					new Warning(String.format(Resources
							.get(WarningStrings.FAILED_MSG_DELIVER_TO_ENVPORT),
							msg.toString(), listener.toString()), e);
				}
			}
		}

		@Override
		public void handlePercept(String agent, Percept percept) {
			this.events.add(new NewPerceptEvent(agent, percept));
		}

		@Override
		public void handleStateChange(EnvironmentState newState) {
			this.events.add(new StateChangeEvent(newState));
		}

		@Override
		public void handleFreeEntity(String entity, Collection<String> agents) {
			// FIXME: handleFreeEntity should also have type parameter...
			// Now we have to catch exceptions here which we cannot handle...
			// Rien: Seems to me eis.getType should just return null. What ever
			// thread we're on just came out of eis so we can assume that the
			// entity is present.
			String type = null;
			try {
				type = LocalMessagingEnvironment.this.eis.getType(entity);
			} catch (EntityException e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_EIS_GETTYPE1),
						entity), e);
			}
			this.events.add(new FreeEntityEvent(entity, agents, type));
		}

		@Override
		public void handleDeletedEntity(String entity, Collection<String> agents) {
			this.events.add(new DeletedEntityEvent(entity, agents));
		}

		@Override
		public void handleNewEntity(String entity) {
			// FIXME: handleFreeEntity should also have type parameter...
			// Now we have to catch exceptions here which we cannot handle...
			String type = null;
			try {
				type = LocalMessagingEnvironment.this.eis.getType(entity);
			} catch (EntityException e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_EIS_GETTYPE1),
						entity), e);
			}
			this.events.add(new NewEntityEvent(entity, type));
		}

		@Override
		public void run() {
			try {
				new InfoLog("Environment->Messages started."); //$NON-NLS-1$

				while (LocalMessagingEnvironment.this.running) {

					EnvironmentEvent event = this.events.take();
					notifyAll(event);

				}
			} catch (InterruptedException e) {
				// Breaking out of loop
			} catch (Throwable e) {
				new GOALRuntimeErrorException(
						"Environment->Messages encountered an exception", e); //$NON-NLS-1$
			}

			new InfoLog("Environment->Messages stopped."); //$NON-NLS-1$
		}
	}

	/**
	 * The environment interface and initialization parameters managed by this
	 * environment connector.
	 */
	private final EnvironmentInterfaceStandard eis;
	private final Map<String, eis.iilang.Parameter> initialization;
	/**
	 * Executor service for running the environment connector's run method.
	 */

	private final MessagingService messagingService;
	private MessageBox messageBox;
	/**
	 * {@link EnvironmentPort}s are listeners of this
	 * {@link LocalMessagingEnvironment}.
	 */
	private final List<MessageBoxId> subscribedEnvironmentPorts = new CopyOnWriteArrayList<>();
	// needed for take down.

	private boolean running = true;

	/**
	 * Launch an environment and put it on top of the messaging system. This
	 * does call the INIT call but the user may still want to
	 * <ul>
	 * <li>subscribe listeners to environment
	 * <li>handle current free entities
	 * </ul>
	 *
	 * @param eis
	 *            The interface to connect with the environment.
	 * @param environmentName
	 *            The name of the environment.
	 * @param initialization
	 *            Initialization parameters for initializing the environment.
	 * @param messagingService
	 *            The messaging service used to communicate with clients of
	 *            environment.
	 * @throws GOALLaunchFailureException
	 *             If environment interface could not be loaded; If connecting
	 *             to messaging infrastructure failed; If initializing
	 *             environment failed.
	 */
	public LocalMessagingEnvironment(EnvironmentInterfaceStandard eis,
			String environmentName, Map<String, Parameter> initialization,
			MessagingService messagingService)
			throws GOALLaunchFailureException {
		this.initialization = initialization;

		this.messagingService = messagingService;

		this.eis = eis;

		// Establish a connection with the environment.

		try {
			MessageBoxId id = messagingService.getNewUniqueID(environmentName,
					Type.ENVIRONMENT);
			this.messageBox = messagingService.getNewMessageBox(id);
		} catch (CommunicationFailureException e1) {
			throw new GOALLaunchFailureException(
					"can't get new message box id", e1); //$NON-NLS-1$
		} catch (MessagingException e) {
			throw new GOALLaunchFailureException(
					"Failure while establishing a connection with the environment", //$NON-NLS-1$
					e);
		}
		// Attach this environment connector as listener to message box (to be
		// able to respond to requests from clients).
		// TODO: should not register listener in constructor -
		// http://www.ibm.com/developerworks/library/j-jtp07265/
		this.messageBox.addListener(this.messages2Environment);
		// Attach this environment connector as listener to environment
		// interface.
		eis.attachEnvironmentListener(this.environment2Messages);

		this.messages2EnvironmentThread = new Thread(this.messages2Environment,
				"Messages->Environment"); //$NON-NLS-1$
		this.messages2EnvironmentThread.start();
		this.environment2MessagesThread = new Thread(this.environment2Messages,
				"Environment->Messages"); //$NON-NLS-1$
		this.environment2MessagesThread.start();
	}

	/**
	 * <p>
	 * Shuts down the environment.
	 * </p>
	 * <p>
	 * TODO: check. This goes indirectly because there may be pending requests
	 * that have to be answered, to avoid blocking of the caller that is still
	 * awaiting the reply of the request. All refs to this object should be
	 * discarded after calling this.
	 * </p>
	 *
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws ManagementException
	 */
	public synchronized void shutDown() throws InterruptedException,
			MessagingException, ManagementException {
		this.running = false;
		this.messages2EnvironmentThread.interrupt();
		this.environment2MessagesThread.interrupt();

		this.messages2EnvironmentThread.join();
		this.environment2MessagesThread.join();

		this.eis.detachEnvironmentListener(this.environment2Messages);
		this.messageBox.removeListener(this.messages2Environment);

		if (!this.eis.getState().equals(EnvironmentState.KILLED)) {
			this.eis.kill();
		}

		// FIXME: Should block here (instead of looping) until signal from
		// environment is
		// received.
		// Now we should await the kill before we continue (otherwise we might
		// even be
		// trying to start up the same environment again in batch run mode while
		// the old
		// instance is still getting killed and we cannot assume the environment
		// to be
		// thread-safe... E.g., Wumpus Environment hangs if we don't wait here.)

		// FIXME: This is really getting nasty now...; we're running in the AWT
		// thread if we're
		// running GOAL using the GUI, but we're not in the AWT thread if
		// we're running from command line... So here we now need to distinguish
		// since otherwise the code here will block the AWT thread if we're
		// running
		// this in that thread and prevent the execution of the
		// SwingUtilities.invokeLater(new Runnable())
		// in the kill() method...
		// We really should not be running this method ever in the AWT thread...
		// instead we should
		// have the RuntimeServiceManager run in its own thread.

		// FIXME: All calls to Runtime and friends should be done in a
		// SwingWorker. That should avoid problems such as these.

		if (!SwingUtilities.isEventDispatchThread()) {

			while (this.eis.getState() != EnvironmentState.KILLED) {
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					// TODO: throw a GOALRuntimeException?
				}
			}

		}
		if (this.eis.getState() == EnvironmentState.KILLED) {
			new InfoLog("done."); //$NON-NLS-1$
		}

		this.messagingService.deleteMessageBox(this.messageBox);
	}

	/**
	 * Get the message box for the environment. Used to connect agents.
	 *
	 * @return the message box for the environment
	 */
	public MessageBoxId getMessageBoxId() {
		return this.messageBox.getId();
	}

	/**
	 * Returns string of the form "environment <name>".
	 */
	@Override
	public String toString() {
		return this.messageBox.getId().toString();
	}

	/**
	 * @throws ManagementException
	 */
	public synchronized void initialize() throws ManagementException {
		try {
			this.eis.init(this.initialization);
		} catch (RuntimeException e) {
			// FIXME: BW4T throws RuntimeExceptions.
			// This messes up clean up in failure cases.
			throw new ManagementException("Failed to initalize environment", e); //$NON-NLS-1$
		}
	}
}
