package goal.core.runtime.service.agent.events;

import languageTools.program.agent.AgentId;

/**
 * superclass for events from the AgentService
 *
 * @author W.Pasman
 */
public class RemoteAgentServiceEvent extends AgentServiceEvent {
	private final AgentId agentId;

	public RemoteAgentServiceEvent(AgentId id) {
		this.agentId = id;
	}

	public AgentId getAgentId() {
		return this.agentId;
	}
}
