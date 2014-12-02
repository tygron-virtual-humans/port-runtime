package goal.core.runtime.service.agent.events;

import goal.core.agent.Agent;

/**
 * superclass for events from the AgentService
 *
 * @author W.Pasman
 */
public class LocalAgentServiceEvent extends AgentServiceEvent {
	private final Agent<?> agent;

	public LocalAgentServiceEvent(Agent<?> agt) {
		this.agent = agt;
	}

	public Agent<?> getAgent() {
		return agent;
	}
}
