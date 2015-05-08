package goal.core.runtime.service.agent.events;

import goal.core.agent.Agent;

public class AddedLocalAgent extends LocalAgentServiceEvent {
	public AddedLocalAgent(Agent<?> agent) {
		super(agent);
	}
}
