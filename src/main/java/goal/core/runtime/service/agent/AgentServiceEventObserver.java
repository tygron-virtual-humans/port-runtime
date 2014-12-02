package goal.core.runtime.service.agent;

import goal.core.runtime.service.agent.events.AgentServiceEvent;

/**
 * Inform listeners about events in the {@link AgentService}.
 *
 * @author W.Pasman
 *
 */
public interface AgentServiceEventObserver {

	/**
	 * this is called when an event occurs in the {@link AgentService}.
	 *
	 * @param rs
	 *            The agent responsible service.
	 * @param evt
	 *            The new event from the service.
	 */
	public void agentServiceEvent(AgentService<?, ?> rs, AgentServiceEvent evt);
}
