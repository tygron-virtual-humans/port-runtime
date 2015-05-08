package goal.core.runtime.service.agent.events;

import goal.core.agent.Agent;

public class RemovedLocalAgent extends LocalAgentServiceEvent {
	public RemovedLocalAgent(Agent agent) {
		super(agent);
	}
}
