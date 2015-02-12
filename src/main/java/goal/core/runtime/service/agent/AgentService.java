package goal.core.runtime.service.agent;

import goal.core.agent.Agent;
import goal.core.agent.AgentFactory;
import goal.core.agent.GOALInterpreter;
import goal.core.runtime.service.agent.events.AddedLocalAgent;
import goal.core.runtime.service.agent.events.AddedRemoteAgent;
import goal.core.runtime.service.agent.events.AgentServiceEvent;
import goal.core.runtime.service.agent.events.RemovedLocalAgent;
import goal.core.runtime.service.agent.events.RemovedRemoteAgent;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.core.runtime.service.environmentport.environmentport.events.DeletedEntityEvent;
import goal.core.runtime.service.environmentport.environmentport.events.EnvironmentEvent;
import goal.core.runtime.service.environmentport.environmentport.events.FreeEntityEvent;
import goal.core.runtime.service.environmentport.environmentport.events.NewEntityEvent;
import goal.preferences.EnvironmentPreferences;
import goal.preferences.PMPreferences;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.InfoLog;

import java.io.File;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import languageTools.program.mas.Launch;
import languageTools.program.mas.LaunchRule;
import languageTools.program.mas.MASProgram;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

/**
 * This class keeps track of agents in the system and creating new local agents
 * when a new entity appears.
 * <p>
 * This class must be thread safe.
 *
 * @param <D>
 *            The debugger type
 * @param <C>
 *            The interpreter type
 */
public class AgentService<D extends Debugger, C extends GOALInterpreter<D>> {
	private final MASProgram masProgram;
	private final Map<File, AgentProgram> agentPrograms;
	private final Agents agents = new Agents();
	private final AgentFactory<D, C> factory;
	private final List<AgentServiceEventObserver> observers = new LinkedList<>();
	private final Map<LaunchRule, Integer> applicationCount = new HashMap<>();

	private class Agents {
		/**
		 * Maps agent id's to their corresponding GOAL agent. This means these
		 * are the <i>local</i> agents as we can only associate agent id's with
		 * real agents if these agents are constructed and running locally (on
		 * this JVM) here by this service manager.
		 */
		private final Map<AgentId, Agent<C>> local = new HashMap<>();

		private final Set<AgentId> all = new HashSet<>();

		public Set<AgentId> allId() {
			return this.all;
		}

		public void addLocal(Agent<C> agent) {
			this.local.put(agent.getId(), agent);
			this.all.add(agent.getId());
		}

		public void add(AgentId id) {
			this.all.add(id);
		}

		public Collection<Agent<C>> local() {
			return this.local.values();
		}

		public Agent<C> getLocal(AgentId id) {
			return this.local.get(id);
		}

		public boolean containsLocal(AgentId id) {
			return this.local.containsKey(id);
		}

		public void remove(AgentId id) {
			this.all.remove(id);
			this.local.remove(id);
		}
	}

	/**
	 * DOC
	 *
	 * @param program
	 * @param factory
	 * @throws GOALLaunchFailureException
	 */
	public AgentService(MASProgram program, Map<File, AgentProgram> agents,
			AgentFactory<D, C> factory) {
		this.masProgram = program;
		this.agentPrograms = agents;
		this.factory = factory;
	}

	/**
	 * Launches multi-agent system.
	 *
	 * @throws GOALLaunchFailureException
	 *             DOC
	 */
	public synchronized void start() throws GOALLaunchFailureException {
		for (LaunchRule multilaunch : this.masProgram.getLaunchRules()) {
			for (Launch launch : multilaunch.getInstructions()) {
				for (int i = 0; i < launch.getNumberOfAgentsToLaunch(); i++) {
					launchAgent(launch, null, null);
				}
			}
		}
	}

	/**
	 * DOC
	 */
	public synchronized void shutDown() {
		// Kill any remaining agents (not connected to an entity).
		// CHECK these are ALL agents, right?
		for (Agent<C> agent : getAgents()) {
			agent.stop();
		}
	}

	/**
	 * Awaits the termination of all agents.
	 *
	 * @param timeout
	 * @throws InterruptedException
	 *             when interrupted while waiting
	 */
	public void awaitTermination(long timeout) throws InterruptedException {
		List<Agent<C>> agents;
		while (!(agents = getAliveAgents()).isEmpty()) {
			Agent<C> agent = agents.get(0);
			agent.awaitTermination(timeout);
		}
	}

	/**
	 * A list of the agents currently tracked by the {@link AgentService}. The
	 * collection is a non-updating copy.
	 *
	 * @return a list of the agents
	 */
	public synchronized Collection<Agent<C>> getAgents() {
		return new ArrayList<>(this.agents.local());
	}

	/**
	 * get all agents, also remote ones.
	 *
	 * @return a set of all agents
	 */
	public Set<AgentId> getAll() {
		return new HashSet<>(this.agents.all);
	}

	/**
	 * Get the MAS program
	 *
	 * @return
	 */
	public MASProgram getMAS() {
		return this.masProgram;
	}

	/**
	 * Returns the GOAL agent with name agentName, if it runs locally.
	 *
	 * @param id
	 *            the agent to be found.
	 * @return GOAL agent with name agentName, or null if a GOAL agent with that
	 *         name does not exist locally on THIS JVM.
	 */
	public synchronized Agent<C> getAgent(AgentId id) {
		return this.agents.getLocal(id);
	}

	/**
	 * Returns all local agents that are running. This collection is
	 * thread-safe.
	 *
	 * @return all local agents that are running.
	 */
	public synchronized List<Agent<C>> getAliveAgents() {
		List<Agent<C>> aliveAgents = new LinkedList<>();
		for (Agent<C> agent : this.agents.local()) {
			if (agent.isRunning()) {
				aliveAgents.add(agent);
			}
		}
		return aliveAgents;
	}

	/**
	 * Returns all local agents that are dead. This collection is thread-safe.
	 *
	 * @return all local agents that are dead.
	 */
	public synchronized List<Agent<C>> getDeadAgents() {
		List<Agent<C>> deadAgents = new LinkedList<>();
		for (Agent<C> agent : this.agents.local()) {
			if (!agent.isRunning()) {
				deadAgents.add(agent);
			}
		}
		return deadAgents;
	}

	/**
	 * @return True if there are any running local agents.
	 */
	public synchronized boolean hasAliveLocalAgents() {
		for (Agent<C> agent : this.agents.local()) {
			if (agent.isRunning()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return True if there are any local agents (running or not).
	 */
	public synchronized boolean hasLocalAgents() {
		return !this.agents.local().isEmpty();
	}

	/**
	 * Stop an agent.
	 *
	 * @param id
	 *            The agent to stop.
	 */
	public synchronized void stopAgent(AgentId id) {
		if (this.agents.containsLocal(id)) {
			this.agents.getLocal(id).stop();
		}
	}

	/**
	 * This is called by remote {@link Runtime} when it is notified that a new
	 * agent has been created. It can be remote or local agent. All local agents
	 * are notified of the new agent.
	 *
	 * @param id
	 *            of the created message box.
	 */
	public void handleAgentCreated(AgentId id) {
		synchronized (this) {
			this.agents.add(id);

			// We don't know if the id is remote ore local.
			// We just pass the message onto the agent.
			for (Agent<C> agent : this.agents.local()) {
				agent.getController().updateAgentAvailability(id, true);
			}
		}

		notifyObservers(new AddedRemoteAgent(id));
	}

	/**
	 * If the agent was a local agent it is removed and the observers are
	 * notified.
	 *
	 * Note that agents only remove their message box if
	 * {@link PMPreferences#getRemoveKilledAgent()} is true.
	 *
	 * @param id
	 *            of the created message box.
	 */
	public void handleAgentRemoved(AgentId id) {
		Agent<C> agent;
		synchronized (this) {
			// Fetch agent and remove
			agent = this.agents.getLocal(id);
			this.agents.remove(id);

			// Pass the message onto the other agent.
			for (Agent<C> a : this.agents.local()) {
				a.getController().updateAgentAvailability(id, true);
			}
		}

		// If agent was local notify observers.
		if (agent != null) {
			notifyObservers(new RemovedLocalAgent(agent));
		} else {
			notifyObservers(new RemovedRemoteAgent(id));
		}
	}

	/**
	 * Handles environment events routed via {@link MonitoringService}. FIXME
	 *
	 * @param event
	 *            The environment event
	 * @param receiver
	 *            The port to route the event to
	 * @return True if the event was handled; false otherwise
	 */
	public boolean handleEnvironmentEvent(EnvironmentEvent event,
			EnvironmentPort receiver) {
		if (event instanceof FreeEntityEvent) {
			FreeEntityEvent f = (FreeEntityEvent) event;
			handleFreeEntity(f.getEntity(), f.getAgents(), f.getType(),
					receiver);
		} else if (event instanceof NewEntityEvent) {
			NewEntityEvent ne = (NewEntityEvent) event;
			handleNewEntity(ne.getEntity(), ne.getType(), receiver);
		} else if (event instanceof DeletedEntityEvent) {
			DeletedEntityEvent de = (DeletedEntityEvent) event;
			new Warning(String.format(Resources
					.get(WarningStrings.ENV_DELETED_ENTITY), de.getEntity()
					.toString(), de.getAgents().toString()));
			handleDeletedEntity(de.getAgents());
		} else {
			return false; // not handled.
		}
		return true;
	}

	/**
	 * Launches an agent using the messaging system, when a new entity has
	 * appeared. ASSERTS that the {@link LaunchRule#incrementApplicationCount()}
	 * has been called, and {@link Launch} preconditions have been checked, so
	 * that the {@link Launch} rule is applicable. Also we assume that the new
	 * entity is coming from {@link #myRemoteEnvironment} since we should really
	 * hook up to only 1 environment. There may be more environments in the MAS
	 * file but we basically ignore those other environments here.
	 *
	 * ASSUMES that there is a messaging system, i.e., a {@link Messaging}
	 * service available.
	 *
	 * @param launch
	 *            The rule to be executed.
	 * @param newEntity
	 *            the entity name that triggered this launch. May be used for
	 *            the agent name.
	 * @param environment
	 *            the environment has the entity. May be null if the launch was
	 *            not triggered by an entity.
	 * @throws GOALLaunchFailureException
	 * @throws MessagingException
	 * @throws KRInitFailedException
	 */
	private Agent<C> launchAgent(Launch launch, String newEntity,
			EnvironmentPort environment) throws GOALLaunchFailureException {
		// TODO(Rien): Creating an agent ID should be done in a better fashion.
		// Would suggest the form BaseName-Number@HostName/EnvironmentName/UUID
		// The BaseName can be used for human identification.
		// The number can differentiate between instances with the same
		// name created in a single JVM.
		// The host name differentiates between different hosts
		// The UUID provides a high chance in ensuring that agents launched on
		// the same machine with different JVM's are not colliding.
		// This allows the ID's be be created once and used for everything.

		// Get base name for the agent. Either provided by the Launch or the
		// Environment. FIXME: application count?!
		String agentBaseName = launch.getGivenName(newEntity, 0);

		// Check whether agent name should be prefixed with name of MAS (file).
		// Useful when running multiple MAS files using the batch runner.
		// TODO: ad hoc to use "_" as separator...
		if (PMPreferences.getUseMASNameAsAgentPrefix()) {
			String prefix = "";
			// FIXME: Should be able to get MASFile from Launch.
			prefix = this.masProgram.getSourceFile().getName();
			prefix = prefix.substring(0, prefix.indexOf("."));
			agentBaseName = prefix + "_" + agentBaseName;
		}

		// FIXME: AgentProgram should have reference to its file.
		AgentProgram program = this.agentPrograms.get(launch.getAgentFile());
		Agent<C> agent;
		try {
			agent = this.factory.build(program, agentBaseName, environment);
		} catch (KRInitFailedException e) {
			throw new GOALLaunchFailureException("Could not create Agent", e);
		} catch (MessagingException e) {
			throw new GOALLaunchFailureException("Could not create Agent", e);
		}

		if (environment != null) {
			new InfoLog("connecting to entity " + newEntity + "...");
			try {
				environment.registerAgent(agent.getId().getName());
				environment.associateEntity(agent.getId().getName(), newEntity);
			} catch (Exception e) {
				agent.dispose();
				throw new GOALLaunchFailureException(
						"Could not register agent with environment", e);
			}
		}

		new InfoLog("OK.");

		// INFORM ABOUT EXISTENCE
		// We've created a new agent; inform that agent of the existence of all
		// other agents that we know of.
		synchronized (this) {
			for (AgentId otherId : this.agents.allId()) {
				agent.getController().updateAgentAvailability(otherId, true);
			}

			for (Agent<C> otherAgent : this.agents.local()) {
				otherAgent.getController().updateAgentAvailability(
						agent.getId(), true);
			}

			this.agents.addLocal(agent);
		}

		// Start agent. Agent now runs in its own thread. Be careful with
		// modifications. Moved to before notifyObservers #3060
		agent.start();

		synchronized (this) {
			// Wake up sleeping threads in awaitFirstAgent.
			notifyAll();
		}

		notifyObservers(new AddedLocalAgent(agent));

		return agent;
	}

	/**
	 * DOC
	 *
	 * @param newEntity
	 */
	private void applyLaunchRules(String newEntity, String type,
			EnvironmentPort port) {
		// Find the first applicable launch rule, launch rules are applied in
		// order.
		List<LaunchRule> launchrules = this.masProgram.getLaunchRules();

		// Initial hypothesis about what has gone wrong if no rule applies.
		String warning = Resources
				.get(WarningStrings.MISMATCH_ENTITY_TYPE_RULE);

		for (LaunchRule launchRule : launchrules) {
			// check whether type-condition exists and is satisfied
			if (!launchRule.getRequiredEntityType().equals("")
					&& !launchRule.getRequiredEntityType().equals(type)) {
				continue;
			}

			// Second hypothesis about what has gone wrong if no rule applies.
			warning = Resources.get(WarningStrings.MISMATCH_ENTITY_NAME_RULE);

			// check whether label condition exists and is not satisfied
			if (!launchRule.getRequiredEntityName().equals("")
					&& !launchRule.getRequiredEntityName().equals(newEntity)) {
				continue;
			}

			// Final hypothesis about what has gone wrong if no rule applies.
			warning = Resources.get(WarningStrings.MAX_REACHED_RULE_APPL);

			// check if the maximum number of times this rule should be fired
			// has been reached.
			int appCount = 0;
			if (this.applicationCount.containsKey(launchRule)) {
				appCount = this.applicationCount.get(launchRule);
			} else {
				this.applicationCount.put(launchRule, appCount);
			}
			int max = launchRule.getMaxNumberOfApplications();
			if (max < 1) {
				max = Integer.MAX_VALUE;
			}
			if (appCount < max) {
				this.applicationCount.put(launchRule, ++appCount);
				for (Launch launch : launchRule.getInstructions()) {
					try {
						launchAgent(launch, newEntity, port);
					} catch (Exception e) {
						new Warning(String.format(Resources
								.get(WarningStrings.FAILED_LAUNCH_AGENT),
								newEntity), e);
					}
				}
				break;
			}

			// None of the launch rules could be fired; give a warning.
			new Warning(String.format(
					Resources.get(WarningStrings.NO_APPLICABLE_LAUNCH_RULE),
					newEntity.toString(), type.toString(), warning));
		}
	}

	/**
	 * called when we receive update about a free entity. * Because
	 * applyLaunchRules is called as callback from EIS (via handleFreeEntity
	 * etc), and because the eis callback does not accept throws from its
	 * callback functions, we can't throw an exception here. Therefore we will
	 * just print a warning if problems happen.
	 *
	 * @param agents
	 *            String names of agents that were connected to entity.
	 */
	private void handleFreeEntity(String entity, Collection<String> agents,
			String type, EnvironmentPort environment) {
		// Check whether we should print reception
		if (EnvironmentPreferences.getPrintEntities()) {
			new InfoLog("Received free entity named " + entity + ".");
		}

		// Kill the agents that are no longer bound to the entity.
		for (String name : agents) {
			// Soft kill to be able to inspect mental state thereafter.
			stopAgent(new AgentId(name));
		}

		/*
		 * when an entity becomes free, we can apply the launch rules. This is
		 * TRICKY when we are taking down the runtime service manager.
		 */
		this.applyLaunchRules(entity, type, environment);
	}

	/**
	 * Called when we receive update about a new entity.
	 */
	private void handleNewEntity(String entity, String type,
			EnvironmentPort environment) {
		// Check whether we should print reception of new entities.
		if (EnvironmentPreferences.getPrintEntities()) {
			new InfoLog("Received new entity named " + entity + ".");
		}

		// (Re)connect (new) agent to entity.
		this.applyLaunchRules(entity, type, environment);
	}

	/**
	 * @param agents
	 *            Called when we receive update about deleted entities.
	 */
	private void handleDeletedEntity(Collection<String> agents) {
		for (String name : agents) {
			stopAgent(new AgentId(name));
		}
	}

	@Override
	public String toString() {
		return this.masProgram.toString();
	}

	/**
	 * Resets all local agents.
	 *
	 * @throws InterruptedException
	 * @throws KRInitFailedException
	 * @throws KRQueryFailedException
	 * @throws KRDatabaseException
	 * @throws UnknownObjectException
	 */
	public synchronized void reset() throws InterruptedException,
	KRInitFailedException, KRDatabaseException, KRQueryFailedException,
	UnknownObjectException {
		for (Agent<C> a : this.agents.local()) {
			a.reset();
		}
	}

	/**
	 * Returns true if the agent id belongs to an agent that is running on this
	 * system.
	 *
	 * @param id
	 *            the id of the agent to check
	 * @return true if the agent id belongs to a local agent
	 */
	public synchronized boolean isLocal(AgentId id) {
		return this.agents.getLocal(id) != null;
	}

	/**
	 * Disposes all agents and any resources held by them.
	 */
	public void dispose() {
		for (Agent<C> agent : getAgents()) {
			agent.dispose();
			this.agents.remove(agent.getId());
			notifyObservers(new RemovedLocalAgent(agent));
		}
	}

	/**
	 * Await the launch of the first agent. This method will return once an
	 * agent has been launched.
	 *
	 * @param timeout
	 * @return True when any agent has launched within the timeout.
	 * @throws InterruptedException
	 */
	public boolean awaitFirstAgent(long timeout) throws InterruptedException {
		if (timeout <= 0) {
			return awaitFirstAgent();
		} else {
			timeout = System.currentTimeMillis() + (timeout * 1000L);
		}

		synchronized (this) {
			while (!hasLocalAgents()) {
				// This will surrender the lock.
				// Wake up call is done in launchAgent().
				wait(100);

				// Woken up. Check if we need to sleep and how long.
				if (System.currentTimeMillis() > timeout) {
					return hasLocalAgents();
				}
			}
			return true;
		}
	}

	/**
	 * Await the launch of the first agent. This method will return once an
	 * agent has been launched.
	 *
	 * @return true if agents are present
	 *
	 * @throws InterruptedException
	 */
	public boolean awaitFirstAgent() throws InterruptedException {
		synchronized (this) {
			while (!hasLocalAgents()) {
				// This will surrender the lock.
				// Wake up call is done in launchAgent().
				wait();
			}
		}
		return true;
	}

	/******************************************/
	/******** observer handling **************/
	/******************************************/
	public synchronized void addObserver(AgentServiceEventObserver o) {
		this.observers.add(o);
	}

	public synchronized void notifyObservers(AgentServiceEvent evt) {
		for (AgentServiceEventObserver obs : this.observers) {
			try {
				obs.agentServiceEvent(this, evt);
			} catch (Exception e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_CALLBACK),
						obs.toString(), evt.toString()), e);
			}
		}
	}
}
