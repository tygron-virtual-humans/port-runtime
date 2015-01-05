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

package goal.core.runtime;

import eis.exceptions.EnvironmentInterfaceException;
import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.core.runtime.RuntimeEvent.EventType;
import goal.core.runtime.events.DeadAgent;
import goal.core.runtime.events.NewAgent;
import goal.core.runtime.events.RemoteRuntimeEvent;
import goal.core.runtime.events.RemoteRuntimeListener;
import goal.core.runtime.events.RuntimeLaunched;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.agent.AgentServiceEventObserver;
import goal.core.runtime.service.agent.events.AddedLocalAgent;
import goal.core.runtime.service.agent.events.AgentServiceEvent;
import goal.core.runtime.service.agent.events.RemoteAgentServiceEvent;
import goal.core.runtime.service.agent.events.RemovedLocalAgent;
import goal.core.runtime.service.environment.EnvironmentService;
import goal.core.runtime.service.environment.EnvironmentServiceObserver;
import goal.core.runtime.service.environment.events.EnvironmentPortAddedEvent;
import goal.core.runtime.service.environment.events.EnvironmentPortRemovedEvent;
import goal.core.runtime.service.environment.events.EnvironmentServiceEvent;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.core.runtime.service.environmentport.EnvironmentPortObserver;
import goal.core.runtime.service.environmentport.environmentport.events.EnvironmentEvent;
import goal.core.runtime.service.environmentport.environmentport.events.StateChangeEvent;
import goal.tools.AbstractRun;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.InfoLog;
import goal.util.DefaultObservable;
import goal.util.Observable;

import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.activation.UnknownObjectException;
import java.util.Collection;
import java.util.HashMap;

import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import languageTools.program.agent.AgentId;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.client.MessagingClient;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId.Type;

/**
 * <p>
 * The RuntimeServiceManager is a LOCAL controller. It provides a functionality
 * for managing the GOAL runtime at a high level and can be observed through
 * event listeners.
 * </p>
 * <p>
 * A RuntimeSerManager manages the life cycle of a {@link AgentService},
 * {@link MessagingService} and {@link EnvironmentService}. It also passes
 * events from each service to the other interested servers and any observers of
 * the RuntimeServiceManager itself.
 * </p>
 * <p>
 * External observer are are provided with {@link RuntimeEvent}s to inform them
 * of events in the Runtime.
 * </p>
 * <p>
 * A distributed system can contain several RuntimeServiceManagers. These each
 * have their own set of agents. Communication between Agents is handled through
 * the {@link MessagingService}. The {@link EnvironmentService} provides agents
 * with access to the environment.
 * </p>
 * <p>
 * This class is an important node rerouting various events in the system. The
 * rerouting here looks like this:
 * <ul>
 * <li>RemoteRuntimeManager -- (AgentBorn| AgentRemoved|RuntimeLaunched) -->
 * AgentService
 * <li>RemoteRuntimeManager -- Environment(Added|Removed)(MsgBoxId) -->
 * EnvironmentService
 * <li>EnvironmentService -- EnvironmentPort(Added|Removed) --> (Add|Remove)
 * EnvironmentPort2RuntimeManager Listener.
 * <li>EnvironmentService -- EnvironmentPort(Added|Removed) --> (Add|Remove)
 * EnvironmentPort2Observers Listener.
 * <li>AgentService -- (AgentBornAndReady|LocalAgentRemoved) --> Observers
 * <li>AgentService -- (AgentBornAndReady|LocalAgentRemoved) -->
 * RemoteRuntimeService
 * </ul>
 * <p>
 * The {@link RuntimeManager} has two types of 'observers':
 * <ul>
 * <li>direct observers within this JVM. see
 * {@link #addObserver(RuntimeEventObserver)}.
 * <li>remote observers, working through the {@link Messaging} system. These are
 * not registered here as observers but called directly using the
 * {@link MessagingClient#getMessageBoxes(Type, String)} call.
 * </ul>
 *
 * @author K.Hindriks
 * @author W.Pasman major code reorganization 25jan2011. All runtime code from
 *         the platform manager is now contained here.
 * @param <D>
 *            subclass of {@link Debugger} that agents in this runtime will use.
 * @param <C>
 *            subclass of {@link GOALInterpreter} that agents in this runtime
 *            will use.
 * @modified W.Pasman 19apr2012 #2108 conditional breakpoints
 * @modified W.Pasman 7feb2013 environment completely on messaging
 * @modified K.Hindriks
 * @modified M.P. Korstanje sep2013
 */
public class RuntimeManager<D extends Debugger, C extends GOALInterpreter<D>>
		implements
		Observable<RuntimeEventObserver, RuntimeManager<?, ?>, RuntimeEvent> {
	// wrapper pattern for implementing Observable.
	private final DefaultObservable<RuntimeEventObserver, RuntimeManager<?, ?>, RuntimeEvent> myObservable = new DefaultObservable<>();

	private final static String GOAL_RELAY = "http://ii.tudelft.nl:8080/glrly-1";

	/**
	 * Connects {@link StateChangeEvent}s from the {@link EnvironmentPort} to
	 * external Observers.
	 *
	 */
	private final class EnvironmentPort2Observers implements
			EnvironmentPortObserver {
		@Override
		public void EnvironmentPortEventOccured(
				EnvironmentPort environmentPort, EnvironmentEvent event) {
			if (event instanceof StateChangeEvent) {
				RuntimeManager.this.myObservable.notifyObservers(
						RuntimeManager.this, new RuntimeEvent(
								EventType.ENVIRONMENT_RUNMODE_CHANGED,
								environmentPort));
			}
		}
	}

	/**
	 * Connects {@link EnvironmentEvent}s from an {@link EnvironmentPort} to the
	 * {@link AgentService}.
	 */
	private final class EnvironmentPort2Runtime implements
			EnvironmentPortObserver {
		@Override
		public void EnvironmentPortEventOccured(
				EnvironmentPort environmentPort, EnvironmentEvent event) {
			// FIXME: Handling the event should be done here. RuntimeService
			// should just apply the launch rules for what it is given.
			// RuntimeService may ofcourse provide convenient methods for this.
			RuntimeManager.this.agentService.handleEnvironmentEvent(event,
					environmentPort);
		}
	}

	/**
	 * When ever an {@link EnvironmentPort} is created or removed this will add
	 * or remove a connection from that EnvironmentPort to external Observers.
	 */
	private final class EnvironmentService2Observers implements
			EnvironmentServiceObserver {
		private final HashMap<EnvironmentPort, EnvironmentPortObserver> observers = new HashMap<>();

		private void handle(EnvironmentPortAddedEvent event) {
			EnvironmentPort environmentPort = event.getPort();
			EnvironmentPortObserver observer = new EnvironmentPort2Observers();
			this.observers.put(environmentPort, observer);
			environmentPort.addObserver(observer);

			RuntimeManager.this.myObservable.notifyObservers(
					RuntimeManager.this, new RuntimeEvent(
							EventType.ENVIRONMENT_LAUNCHED, event.getPort()));
		}

		private void handle(EnvironmentPortRemovedEvent event) {
			EnvironmentPort environmentPort = event.getPort();
			EnvironmentPortObserver observer = this.observers
					.remove(environmentPort);
			environmentPort.deleteObserver(observer);
			RuntimeManager.this.myObservable.notifyObservers(
					RuntimeManager.this, new RuntimeEvent(
							EventType.ENVIRONMENT_KILLED, event.getPort()));
		}

		@Override
		public void environmentServiceEventOccured(
				EnvironmentService environmentService,
				EnvironmentServiceEvent event) {
			if (event instanceof EnvironmentPortAddedEvent) {
				handle((EnvironmentPortAddedEvent) event);
			} else if (event instanceof EnvironmentPortRemovedEvent) {
				handle((EnvironmentPortRemovedEvent) event);
			}
		}
	}

	/**
	 * When ever an {@link EnvironmentPort} is created or removed this will add
	 * or remove a connection from that EnvironmentPort to the
	 * {@link AgentService}.
	 */
	private final class EnvironmentService2Runtime implements
			EnvironmentServiceObserver {
		private final HashMap<EnvironmentPort, EnvironmentPortObserver> observers = new HashMap<>();

		private void handle(EnvironmentPortAddedEvent event) {
			EnvironmentPort environmentPort = event.getPort();
			EnvironmentPortObserver observer = new EnvironmentPort2Runtime();
			this.observers.put(environmentPort, observer);
			environmentPort.addObserver(observer);
		}

		private void handle(EnvironmentPortRemovedEvent event) {
			EnvironmentPort environmentPort = event.getPort();
			EnvironmentPortObserver observer = this.observers
					.remove(environmentPort);
			environmentPort.deleteObserver(observer);
		}

		@Override
		public void environmentServiceEventOccured(
				EnvironmentService environmentService,
				EnvironmentServiceEvent event) {
			if (event instanceof EnvironmentPortAddedEvent) {
				handle((EnvironmentPortAddedEvent) event);
			} else if (event instanceof EnvironmentPortRemovedEvent) {
				handle((EnvironmentPortRemovedEvent) event);
			}
		}
	}

	/**
	 * Connects Agents added / removed events from the {@link AgentService} to
	 * external Observers.
	 */
	private final class Runtime2Observers implements
			goal.core.runtime.service.agent.AgentServiceEventObserver {
		@Override
		public void agentServiceEvent(AgentService runtimeService,
				AgentServiceEvent evt) {
			if (evt instanceof goal.core.runtime.service.agent.events.AddedLocalAgent) {
				RuntimeManager.this.myObservable
						.notifyObservers(
								RuntimeManager.this,
								new RuntimeEvent(
										EventType.AGENT_IS_LOCAL_AND_READY,
										((goal.core.runtime.service.agent.events.AddedLocalAgent) evt)
												.getAgent()));
			} else if (evt instanceof goal.core.runtime.service.agent.events.RemovedLocalAgent) {
				RuntimeManager.this.myObservable
						.notifyObservers(
								RuntimeManager.this,
								new RuntimeEvent(
										// removed, AGENT_DIED events go through
										// the debugger
										EventType.AGENT_REMOVED,
										((goal.core.runtime.service.agent.events.RemovedLocalAgent) evt)
												.getAgent().getId().getName()));
			} else if (evt instanceof goal.core.runtime.service.agent.events.AddedRemoteAgent) {
				RuntimeManager.this.myObservable
						.notifyObservers(
								RuntimeManager.this,
								new RuntimeEvent(
										EventType.AGENT_BORN,
										((goal.core.runtime.service.agent.events.AddedRemoteAgent) evt)
												.getAgentId().getName()));
			} else if (evt instanceof goal.core.runtime.service.agent.events.RemovedRemoteAgent) {
				RuntimeManager.this.myObservable
						.notifyObservers(
								RuntimeManager.this,
								new RuntimeEvent(
										EventType.AGENT_REMOVED,
										((goal.core.runtime.service.agent.events.RemovedRemoteAgent) evt)
												.getAgentId().getName()));
			} else {
				throw new IllegalArgumentException("unknown event " + evt);
			}
		}
	}

	/**
	 * Connects Agents added / removed events from the {@link AgentService} to
	 * other {@link RuntimeManager}s.
	 */
	private final class AgentService2RemoteRuntime implements
			AgentServiceEventObserver {
		@Override
		public void agentServiceEvent(AgentService runtimeService,
				AgentServiceEvent evt) {
			if (evt instanceof AddedLocalAgent) {
				AddedLocalAgent added = (AddedLocalAgent) evt;
				RuntimeManager.this.remoteRuntimeService
						.broadCastNewAgent(added.getAgent());
			} else if (evt instanceof RemovedLocalAgent) {
				RemovedLocalAgent removed = (RemovedLocalAgent) evt;
				RuntimeManager.this.remoteRuntimeService
						.broadCastDeadAgent(removed.getAgent());
			} else if (evt instanceof RemoteAgentServiceEvent) {
				// Remote runtimes don't need to be notified of remote agent
				// events. These are broadcasted by the runtime that removed
				// their local agents.
				return;
			} else {
				throw new IllegalArgumentException("unknown event " + evt);
			}
		}
	}

	private final class RemoteRuntime2AgentService implements
			RemoteRuntimeListener {
		/**
		 * Generated serialVersionUID
		 */
		private static final long serialVersionUID = 8037450487007633074L;

		@Override
		public void remoteRuntimeEventOccured(RemoteRuntimeEvent event) {
			if (event instanceof NewAgent) {
				NewAgent newAgent = (NewAgent) event;
				RuntimeManager.this.agentService.handleAgentCreated(newAgent
						.getAgentId());
			} else if (event instanceof DeadAgent) {
				DeadAgent deadAgent = (DeadAgent) event;
				RuntimeManager.this.agentService.handleAgentRemoved(deadAgent
						.getAgentId());
			} else if (event instanceof RuntimeLaunched) {
				handleRuntimeLaunched();
			} else {
				throw new IllegalArgumentException("unknown event " + event);
			}
		}

		private void handleRuntimeLaunched() {
			for (Agent<C> agent : RuntimeManager.this.agentService.getAgents()) {
				RuntimeManager.this.remoteRuntimeService
						.broadCastNewAgent(agent);
			}
		}
	}

	private final EnvironmentService environmentService;

	private final MessagingService messagingService;

	private final AgentService<D, C> agentService;

	private final RemoteRuntimeService<D, C> remoteRuntimeService;

	/**
	 * Creates a new runtime service manager to manage a multi-agent system.
	 *
	 * @param messagingService
	 *            used to facilitate communication between agents and between
	 *            agents.
	 * @param agentService
	 *            used to manage agents in the multi-agent system.
	 * @param environmentService
	 *            used to manage the environment.
	 * @param remoteRuntimeService
	 *            the remote runtime service.
	 * @throws GOALLaunchFailureException
	 *             when the system could not be launched.
	 */
	public RuntimeManager(MessagingService messagingService,
			AgentService<D, C> agentService,
			EnvironmentService environmentService,
			RemoteRuntimeService<D, C> remoteRuntimeService)
			throws GOALLaunchFailureException {
		this.messagingService = messagingService;
		this.agentService = agentService;
		this.environmentService = environmentService;
		this.remoteRuntimeService = remoteRuntimeService;

		reportGoalUsage();

		// Get start time.
		long start = ManagementFactory.getThreadMXBean().getThreadCpuTime(
				Thread.currentThread().getId());

		// RemoteRuntime -- (AgentBorn| AgentRemoved|RuntimeLaunched) -->
		// AgentService
		RemoteRuntime2AgentService remoteRuntime2AgentService = new RemoteRuntime2AgentService();
		remoteRuntimeService.addListener(remoteRuntime2AgentService);

		// AgentService -- (AgentAdded | AgentRemoved ) --> RemoteRuntime
		AgentService2RemoteRuntime agentservice2remote = new AgentService2RemoteRuntime();
		agentService.addObserver(agentservice2remote);

		// EnvironmentService -- EnvironmentPort(Added|Removed) --> (Add|Remove)
		// EnvironmentPort2RuntimeService.
		environmentService.addObserver(new EnvironmentService2Runtime());

		// EnvironmentService -- EnvironmentPort(Added|Removed) --> (Add|Remove)
		// EnvironmentPort2Observer.
		environmentService.addObserver(new EnvironmentService2Observers());

		// RuntimeService -- (AgentBornAndReady|LocalAgentRemoved) --> Observers
		agentService.addObserver(new Runtime2Observers());

		// agentService.start();

		try {
			remoteRuntimeService.start();
		} catch (MessagingException e) {
			agentService.shutDown();
			throw new GOALLaunchFailureException(
					"Could not enable connection to remote runtime", e);
		}

		try {
			environmentService.start();
		} catch (Exception e) {
			remoteRuntimeService.shutDown();
			agentService.shutDown();
			throw new GOALLaunchFailureException(
					"EIS failed to start environment", e);
		}

		// inform other Runtimes about this launch
		remoteRuntimeService.broadcastRuntimeLaunched();
		// Measure time it took to launch the MAS.
		long end = ManagementFactory.getThreadMXBean().getThreadCpuTime(
				Thread.currentThread().getId());
		new InfoLog("Took " + (end - start) / 1000000
				+ " milliseconds to load MAS.");

		// RuntimeServiceManager -- MASBorn --> Observers
		this.myObservable.notifyObservers(this, new RuntimeEvent(
				EventType.MAS_BORN, agentService));
	}

	/**
	 * reports this usage of GOAL to the GOAL usage (relay) server. Runs in
	 * separate thread and ignores any failures.
	 *
	 */
	private static void reportGoalUsage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				reportGoalUsage1();
			}
		}).start();
	}

	/**
	 * reports this usage of GOAL to the GOAL server.
	 */
	private static void reportGoalUsage1() {
		try {
			String id = System.getProperty("user.name") + "@"
					+ System.getProperty("os.name") + ":"
					+ System.getProperty("os.version") + " java"
					+ System.getProperty("java.version");
			URL url = new URL(GOAL_RELAY + "?id="
					+ URLEncoder.encode(id, "UTF-8"));

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			// int c = conn.getResponseCode();
		} catch (Throwable e) {
			// ignore completely. reporting is optional.
		}
	}

	/**
	 * Await the launch of the first agent. This method will return once an
	 * agent has been launched.
	 *
	 * @param timeout
	 *            the point in time at which we should stop waiting; 0 for never
	 *            (wait forever).
	 * @return true if an agent launched before the time out ended.
	 * @throws InterruptedException
	 */
	public boolean awaitFirstAgent(long timeout) throws InterruptedException {
		return this.agentService.awaitFirstAgent(timeout);

	}

	/**
	 * Waits for all agents to die.
	 *
	 * @param timeout
	 *            the number of seconds we should wait for the runtime to
	 *            terminate; 0 for indefinite.
	 * @throws InterruptedException
	 *             DOC
	 */
	public void awaitTermination(long timeout) throws InterruptedException {
		this.agentService.awaitTermination(timeout);
	}

	/**
	 * Returns the agent with the given <code>id</code>.
	 *
	 * @param id
	 *            the id of the agent
	 * @return the agent with the given <code>id</code> or <code>null</code>
	 *         when no such agent is available
	 */
	public Agent<C> getAgent(AgentId id) {
		return this.agentService.getAgent(id);
	}

	/**
	 * Returns the agents that are part of the LOCAL MAS runtime environment.
	 *
	 * @return an array containing all agents that have been launched LOCALLY.
	 *         Some agents might be dead but not yet removed.
	 */
	public Collection<Agent<C>> getAgents() {
		return this.agentService.getAgents();
	}

	/**
	 * Returns all local agents that are running. This collection is
	 * thread-safe.
	 *
	 * @return all local agents that are running.
	 */
	public Collection<Agent<C>> getAliveAgents() {
		return this.agentService.getAliveAgents();
	}

	/**
	 * @return True if there is any local agent running.
	 */
	public synchronized boolean hasAliveLocalAgents() {
		return this.agentService.hasAliveLocalAgents();
	}

	/**
	 * Returns all local agents that are dead. This collection is thread-safe.
	 *
	 * @return all local agents that are dead.
	 */
	public Collection<Agent<C>> getDeadAgents() {
		return this.agentService.getDeadAgents();
	}

	/**
	 * Returns a list of available environments. This collection is thread-safe.
	 *
	 * @return a list of available environments.
	 */
	public Collection<EnvironmentPort> getEnvironmentPorts() {
		return this.environmentService.getEnvironmentPorts();
	}

	/**
	 * Resets the internal state of an agent.
	 *
	 * @param id
	 *            of agent to reset
	 * @deprecated use {@link Agent#reset()} instead
	 */
	@Deprecated
	public void resetAgent(AgentId id) {
		try {
			this.agentService.getAgent(id).reset();
		} catch (KRInitFailedException | KRDatabaseException
				| KRQueryFailedException | UnknownObjectException e) {
			new Warning(Resources.get(WarningStrings.FAILED_RESET_AGENT), e);
		} catch (InterruptedException e) {
			new Warning(Resources.get(WarningStrings.INTERRUPT_RESET_AGENT), e);
		}
	}

	/**
	 * Restarts an agent. For now only resets the agent.
	 *
	 * @param id
	 *            of agent to restart.
	 * @deprecated use {@link Agent#reset()} instead
	 */
	@Deprecated
	public void restartAgent(AgentId id) {
		resetAgent(id);
	}

	/**
	 * Shuts down all runtime services, kills and cleans all agents.
	 *
	 */
	public void shutDown() {
		new InfoLog("Shutting down MAS.");

		// shut down our message box handler
		this.remoteRuntimeService.shutDown();
		try {
			this.remoteRuntimeService.awaitTermination();
		} catch (InterruptedException e) {
			// Some one wants us to hurry up. Okay...
			new Warning(Resources.get(WarningStrings.INTERRUPT_STOP_RUNTIME), e);
		}

		// Shut down MAS.
		this.agentService.shutDown();

		// FIXME Shut down KR languages.

		// Wait for agents to finish
		try {
			this.agentService
					.awaitTermination(AbstractRun.TIMEOUT_FIRST_AGENT_SECONDS);
		} catch (InterruptedException e) {
			// Some one wants us to hurry up. Okay...
			new Warning(Resources.get(WarningStrings.INTERRUPT_STOP_RUNTIME), e);
		}

		// Cleanup mental states of any remaining agents.
		this.agentService.dispose();

		// Shut down environment.
		try {
			this.environmentService.shutDown();
		} catch (MessagingException e) {
			new Warning(Resources.get(WarningStrings.FAILED_STOP_ENV_SERV), e);
		} catch (EnvironmentInterfaceException e) {
			new Warning(Resources.get(WarningStrings.FAILED_STOP_ENV), e);
		} catch (InterruptedException e) {
			new Warning(Resources.get(WarningStrings.INTERRUPT_STOP_RUNTIME), e);
		}

		// Shut down messaging infrastructure.
		this.messagingService.shutDown();

		new InfoLog("The multi-agent system " + this.agentService.toString()
				+ " has been terminated.");

		// TODO: providing runtime service after MAS died looks like a silly
		// thing to do...
		this.myObservable.notifyObservers(this, new RuntimeEvent(
				EventType.MAS_DIED, this));
	}

	/**
	 * Starts all environment that were launched paused.
	 *
	 * @throws MessagingException
	 *             when it was not possible to connect to the environment.
	 * @throws EnvironmentInterfaceException
	 *             when the environment could not be started.
	 * @throws GOALLaunchFailureException
	 */
	public void startEnvironment() throws MessagingException,
	EnvironmentInterfaceException, GOALLaunchFailureException {
		Collection<EnvironmentPort> ports = this.environmentService
				.getEnvironmentPorts();
		if (ports.isEmpty()) {
			this.agentService.startWithoutEnv();
		} else {
			for (EnvironmentPort port : ports) {
				port.start();
			}
		}
		new InfoLog("running.");
	}

	/**
	 * Stops agent with the given <code>id</code>.
	 *
	 * @param id
	 *            of the agent to stop
	 */
	public void stopAgent(AgentId id) {
		this.agentService.stopAgent(id);
	}

	/**
	 * Name of the multi-agent system being run.
	 */
	@Override
	public String toString() {
		return this.agentService.toString();
	}

	/*****************************************/
	/**************** observer ***************/
	/*****************************************/
	/**
	 * Must override but this function should never be used externally.
	 * Therefore we throw. Internally we use {@link #myObservable}
	 */
	@Override
	public void notifyObservers(RuntimeManager<?, ?> e, RuntimeEvent evt) {
		throw new GOALBug("illegal use of RuntimeManager#notifyObservers");
	}

	/**
	 * {@inheritDoc}.
	 *
	 * Adds observer. New observers will immediately be notified of the state of
	 * the runtime through a late listener pattern.
	 */
	@Override
	public void addObserver(RuntimeEventObserver observer) {
		this.myObservable.addObserver(observer);

		// implement late-listening pattern #1444
		observer.eventOccured(this, new RuntimeEvent(EventType.MAS_BORN,
				this.agentService));

		for (EnvironmentPort envport : this.environmentService
				.getEnvironmentPorts()) {
			observer.eventOccured(this, new RuntimeEvent(
					EventType.ENVIRONMENT_LAUNCHED, envport));
		}

		// also forward the remote agents. FIXME AGENT_BORN is not exactly the
		// right event but that's what the process panel needs now.
		for (AgentId agentId : this.agentService.getAll()) {
			if (!this.agentService.isLocal(agentId)) {
				observer.eventOccured(this, new RuntimeEvent(
						EventType.AGENT_BORN, agentId.getName()));
			}
		}

		for (Agent<?> agent : this.agentService.getAgents()) {
			observer.eventOccured(this, new RuntimeEvent(
					EventType.AGENT_IS_LOCAL_AND_READY, agent));
		}
	}

	@Override
	public void removeObserver(RuntimeEventObserver observer) {
		this.myObservable.removeObserver(observer);
	}
}
