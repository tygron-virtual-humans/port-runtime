package goal.core.runtime.service.agent.events;

import languageTools.program.agent.AgentId;

public class AddedRemoteAgent extends RemoteAgentServiceEvent {
	public AddedRemoteAgent(AgentId id) {
		super(id);
	}
}
