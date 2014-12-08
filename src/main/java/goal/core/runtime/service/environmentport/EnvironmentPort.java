/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.core.runtime.service.environmentport;

import eis.exceptions.ActException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Percept;
import goal.core.agent.Agent;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.environment.LocalMessagingEnvironment;
import goal.core.runtime.service.environmentport.actions.AssociateEntity;
import goal.core.runtime.service.environmentport.actions.ExecuteAction;
import goal.core.runtime.service.environmentport.actions.FreeAgent;
import goal.core.runtime.service.environmentport.actions.GetPercepts;
import goal.core.runtime.service.environmentport.actions.GetReward;
import goal.core.runtime.service.environmentport.actions.Kill;
import goal.core.runtime.service.environmentport.actions.Pause;
import goal.core.runtime.service.environmentport.actions.RegisterAgent;
import goal.core.runtime.service.environmentport.actions.Reset;
import goal.core.runtime.service.environmentport.actions.Start;
import goal.core.runtime.service.environmentport.actions.Subscribe;
import goal.core.runtime.service.environmentport.environmentport.events.EnvironmentEvent;
import goal.core.runtime.service.environmentport.environmentport.events.StateChangeEvent;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import nl.tudelft.goal.messaging.Message;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId.Type;
import nl.tudelft.goal.messaging.messagebox.MessageBoxListener;

/**
 * Tool to handle the connection with an {@link LocalMessagingEnvironment}. It
 * runs on the client side and communicates with a
 * {@link LocalMessagingEnvironment} which runs on another computer.
 * <p>
 * Basic functionality is to track the run state of the {@link EnvMsgConnector}.
 * <p>
 * This also handles events like newEntity and passes them on to the
 * {@link AgentService} if necessary.
 *
 * @author W.Pasman
 * @modified #2385 put this entirely on top of messaging system.
 */
public class EnvironmentPort {
	/**
	 * to communicate with the remote environment, we also need our own
	 * messagebox.
	 */
	private final MessageBox messagebox;
	private final MessageBoxId environmentMessageBoxId;
	protected volatile EnvironmentState environmentState = EnvironmentState.PAUSED;

	private final List<EnvironmentPortObserver> observers = new LinkedList<>();

	private final MessageBoxListener messageboxlistener = new MessageBoxListener() {
		@Override
		public boolean newMessage(Message message) {
			// Only listen to messages send from the environment we
			// we are subscribed to.
			if (!message.getSender().equals(
					EnvironmentPort.this.environmentMessageBoxId)) {
				return false;
			}

			// We don't accept any messages that are send to us in reply.
			// They're send by blocking sends.
			if (message.repliesToId() != null) {
				return false;
			}

			// Sync on parent object.
			synchronized (EnvironmentPort.this) {
				if (!(message.getContent() instanceof EnvironmentEvent)) {
					return false;
				}

				if (message.getContent() instanceof StateChangeEvent) {
					EnvironmentPort.this.environmentState = ((StateChangeEvent) message
							.getContent()).getState();
				}

				notifyObservers((EnvironmentEvent) message.getContent());
				return true;
			}
		}
	};

	/**
	 * The {@link Agent}s are our clients. We provide each of them with a
	 * separate messagebox for communication with us.
	 */
	private final HashMap<String, MessageBox> clients = new HashMap<>();
	private final MessagingService messaging;

	/**
	 * <p>
	 * Create environment port to control environment.
	 * </p>
	 * <p>
	 * The EnvironmentPort must be immediately ready to handle state requests,
	 * even if no environment has been connected yet.
	 * </p>
	 *
	 * TODO: looks like a round about construction to get environment info in...
	 *
	 * @param envMessageBoxId
	 *            The message box of the environment that this port should
	 *            connect to.
	 * @param messaging
	 *            The messaging service to use.
	 * @throws GOALLaunchFailureException
	 *             If we failed to create a message box
	 */
	public EnvironmentPort(MessageBoxId envMessageBoxId,
			MessagingService messaging) throws GOALLaunchFailureException {
		// Store reference to environment connector.
		this.environmentMessageBoxId = envMessageBoxId;
		this.messaging = messaging;

		// Get unique ID for this environment port.
		MessageBoxId id;
		try {
			id = messaging.getNewUniqueID("environmentport", //$NON-NLS-1$
					Type.ENVIRONMENTPORT);
			this.messagebox = messaging.getNewMessageBox(id);
		} catch (MessagingException e1) {
			throw new GOALLaunchFailureException("can't get new messagebox id", //$NON-NLS-1$
					e1);
		}
	}

	/**
	 * Call the {@link LocalMessagingEnvironment} to execute the action. Server
	 * calls can return an exception, which needs to be checked. This function
	 * checks and throws the exception as passed from the server.
	 * <p>
	 * Notice that EIS can throw ANY unchecked exception. However if it happens,
	 * it is the environment implementation that threw it. The only official EIS
	 * unchecked exception is {@link NoEnvironmentException}, which is also
	 * important here. All other ones have no meaning to us, but we need to deal
	 * with them. For now, we just print a warning and ignore these.
	 * <p>
	 *
	 * @param action
	 * @return the serializable reply
	 * @throws NoEnvironmentException
	 * @throws EnvironmentInterfaceException
	 * @throws MessagingException
	 */
	private Serializable callRemoteAction(String agentName,
			goal.core.runtime.service.environmentport.actions.Action action)
			throws NoEnvironmentException, EnvironmentInterfaceException,
			MessagingException {
		/*
		 * FIXME To handle the call, this function is using a temporary
		 * messagebox. not clear why this is done. Older comments here indicate
		 * it is to provide thread safety, but all functions here are already
		 * synchronized so this makes no sense.
		 */
		MessageBox clientMessageBox = getClientMessageBox(agentName);
		Message result = clientMessageBox.blockingSend(clientMessageBox
				.createMessage(this.environmentMessageBoxId, action, null));
		checkResult(result);
		return result.getContent();
	}

	private static void checkResult(Message result)
			throws NoEnvironmentException, EnvironmentInterfaceException {
		Serializable content = result.getContent();
		if (content instanceof RuntimeException) {
			if (content instanceof NoEnvironmentException) {
				throw (NoEnvironmentException) content;
			} else {
				new Warning(Resources.get(WarningStrings.FAILED_ENV_CALL),
						(RuntimeException) content);
			}
		} else if (content instanceof Exception) {
			if (content instanceof EnvironmentInterfaceException) {
				throw (EnvironmentInterfaceException) content;
			} else {
				throw new GOALBug("Environment threw unknown exception", //$NON-NLS-1$
						(Exception) content);
			}
		}
	}

	/**
	 * Shut down the environment port. This just removes our connection with the
	 * environment, but does not close the remote environment. A reset command
	 * is sent to the remote environment though, in order to support batches of
	 * systems/tests properly.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void shutDown() throws MessagingException,
			EnvironmentInterfaceException {
		try {
			reset();
		} catch (Exception ignore) {
		}

		this.messagebox.removeListener(this.messageboxlistener);
		this.messaging.deleteMessageBox(this.messagebox);
		for (MessageBox box : this.clients.values()) {
			this.messaging.deleteMessageBox(box);
		}
	}

	public synchronized void startPort() throws MessagingException {
		this.messagebox.addListener(this.messageboxlistener);

		Message result = this.messagebox.blockingSend(this.messagebox
				.createMessage(this.environmentMessageBoxId, new Subscribe(),
						null));

		@SuppressWarnings("unchecked")
		List<EnvironmentEvent> events = (List<EnvironmentEvent>) result
				.getContent();

		for (EnvironmentEvent event : events) {
			notifyObservers(event);
		}
	}

	/**
	 * requests the environment to start. NOTE: The env may not be running when
	 * this call returns.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void start() throws MessagingException,
			EnvironmentInterfaceException {
		if (this.environmentState != EnvironmentState.RUNNING) {
			Message result = this.messagebox.blockingSend(this.messagebox
					.createMessage(this.environmentMessageBoxId, new Start(),
							null));
			checkResult(result);
		}
	}

	/**
	 * requests the environment to pause. NOTE: the env may not yet be paused
	 * when the call returns.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void pause() throws MessagingException,
			EnvironmentInterfaceException {
		if (this.environmentState != EnvironmentState.PAUSED) {
			Message result = this.messagebox.blockingSend(this.messagebox
					.createMessage(this.environmentMessageBoxId, new Pause(),
							null));
			checkResult(result);
		}
	}

	/**
	 * kill the environment. NOTE: the env may not yet be killed when this call
	 * returns.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void kill() throws MessagingException,
			EnvironmentInterfaceException {
		if (this.environmentState != EnvironmentState.KILLED) {
			Message result = this.messagebox.blockingSend(this.messagebox
					.createMessage(this.environmentMessageBoxId, new Kill(),
							null));
			checkResult(result);
		}
	}

	/**
	 * Reset the environment. Note, this is a parameterless reset because the
	 * EnvMsgConnector remembers the original init parameters and re-uses them.
	 * See {@link EnvMsgConnector#reset()}. Note2: the env may not yet be reset
	 * when this call returns.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void reset() throws MessagingException,
			EnvironmentInterfaceException {
		Message result = this.messagebox
				.blockingSend(this.messagebox.createMessage(
						this.environmentMessageBoxId, new Reset(), null));
		checkResult(result);
	}

	/**
	 * Support to register agent with environment? TODO: why? we want to
	 * associate agent with entity or leave environment alone?
	 *
	 * @param agentName
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void registerAgent(String agentName)
			throws MessagingException, EnvironmentInterfaceException {
		Message result = this.messagebox.blockingSend(this.messagebox
				.createMessage(this.environmentMessageBoxId, new RegisterAgent(
						agentName), null));
		checkResult(result);
	}

	/**
	 * Frees an agent from the agents-entities-relation.
	 *
	 * @param agentName
	 *            agent name in EIS.
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public void freeAgent(String agentName) throws MessagingException,
			EnvironmentInterfaceException {
		try {
			callRemoteAction(agentName, new FreeAgent(agentName));
			// Clean up after agent frees itself.
			MessageBox clientMessageBox = getClientMessageBox(agentName);
			this.messaging.deleteMessageBox(clientMessageBox);
		} catch (EnvironmentInterfaceException e) {
			throw e;
		}
	}

	/**
	 * Support to associate agent to entity.
	 *
	 * @param agentName
	 *            The agent.
	 * @param newEntity
	 *            The entity.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 */
	public synchronized void associateEntity(String agentName, String newEntity)
			throws MessagingException, EnvironmentInterfaceException {
		Message result = this.messagebox.blockingSend(this.messagebox
				.createMessage(this.environmentMessageBoxId,
						new AssociateEntity(agentName, newEntity), null));
		checkResult(result);
	}

	/**
	 * string that is also used in GOAL in the Process panel. FIXME it's not
	 * nice to have the name of env as string for this {@link EnvironmentPort}.
	 */
	@Override
	public String toString() {
		return this.environmentMessageBoxId.getName();
	}

	@SuppressWarnings("unchecked")
	public Collection<Percept> performAction(String agentName, Action action)
			throws MessagingException, ActException {
		// FIXME: All methods are synchronized because we're using a single
		// message box. This should be up to the environment.
		Serializable result;
		try {
			result = callRemoteAction(agentName, new ExecuteAction(agentName,
					action));
		} catch (ActException e) {
			throw e;
		} catch (EnvironmentInterfaceException e) {
			throw new GOALBug("unexpected EIS exception", e); //$NON-NLS-1$
		}
		if (result instanceof Collection<?>) {
			return (Collection<Percept>) result;
		} else {
			throw new MessagingException("Invalid result returned: " + result); //$NON-NLS-1$
		}
	}

	/**
	 * Provides a message box for the client agent. This allows multiple agents
	 * to concurrently request actions and percepts from the environment. It is
	 * assumed that individual agents do not execute actions concurrently.
	 *
	 * Note: {@link LocalMessagingEnvironment} handles all incoming request
	 * sequentially.
	 *
	 * @param agentName
	 *            name of the agents.
	 * @return a message box that can be used to execute agent specification.
	 * @throws MessagingException
	 */
	private synchronized MessageBox getClientMessageBox(String agentName)
			throws MessagingException {
		if (!this.clients.containsKey(agentName)) {
			MessageBoxId id = this.messaging.getNewUniqueID(agentName,
					Type.ENVIRONMENTPORT);
			MessageBox messageBox = this.messaging.getNewMessageBox(id);
			this.clients.put(agentName, messageBox);
		}
		return this.clients.get(agentName);
	}

	@SuppressWarnings("unchecked")
	public Collection<Percept> getPercepts(String agentName)
			throws MessagingException, EnvironmentInterfaceException {
		Serializable result;
		try {
			result = callRemoteAction(agentName, new GetPercepts(agentName));
		} catch (EnvironmentInterfaceException e) {
			throw e;
		} catch (MessagingException e) {
			throw e;
		}
		if (result instanceof Collection<?>) {
			return (Collection<Percept>) result;
		} else {
			throw new MessagingException("Invalid result returned: " + result); //$NON-NLS-1$
		}
	}

	public synchronized Double getReward(String entityName)
			throws MessagingException, EnvironmentInterfaceException {
		Message result = this.messagebox.blockingSend(this.messagebox
				.createMessage(this.environmentMessageBoxId, new GetReward(
						entityName), null));
		if (result.getContent() == null) {
			return null;
		} else if (result.getContent() instanceof Double) {
			return (Double) result.getContent();
		} else {
			throw (EnvironmentInterfaceException) result.getContent();
		}
	}

	/**
	 * @return The current state of the environment.
	 */
	public EnvironmentState getEnvironmentState() {
		return this.environmentState;
	}

	/**
	 * Get the message box id of this environmentport.
	 *
	 * @return The MessageBoxId.
	 */
	public MessageBoxId getMessageBoxId() {
		return this.environmentMessageBoxId;
	}

	/**********************************************/
	/*********** observer handler *****************/
	/**********************************************/

	/**
	 * notify all our observers of an event
	 *
	 * @param e
	 *            The event to notify.
	 */
	public void notifyObservers(EnvironmentEvent e) {
		for (EnvironmentPortObserver obs : this.observers) {
			try {
				obs.EnvironmentPortEventOccured(this, e);
			} catch (Exception e1) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_CALLBACK),
						obs.toString(), e.toString()), e1);
			}
		}
	}

	/**
	 * add new observer to this port.
	 *
	 * @param o
	 *            The observer to add.
	 */
	public void addObserver(EnvironmentPortObserver o) {
		this.observers.add(o);
	}

	/**
	 * remove an observer from this port. Nothing happens if given observer was
	 * not one of our observers
	 *
	 * @param o
	 *            The observer to delete.
	 */
	public void deleteObserver(EnvironmentPortObserver o) {
		this.observers.remove(o);
	}
}