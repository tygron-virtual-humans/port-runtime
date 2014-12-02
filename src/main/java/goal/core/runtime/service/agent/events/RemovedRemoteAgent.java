package goal.core.runtime.service.agent.events;

import goal.core.agent.AgentId;

public class RemovedRemoteAgent extends RemoteAgentServiceEvent {
	public RemovedRemoteAgent(AgentId id) {
		super(id);
	}
}
