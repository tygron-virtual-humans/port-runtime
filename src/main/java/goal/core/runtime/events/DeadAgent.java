package goal.core.runtime.events;

import goal.core.agent.AgentId;

/**
 * A agent died in a RuntimeServiceManager.
 *
 * @author W.Pasman 11nov2013
 */
public class DeadAgent implements RemoteRuntimeEvent {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -5110072977997364014L;
	private final AgentId agentId;

	public DeadAgent(AgentId id) {
		agentId = id;
	}

	public AgentId getAgentId() {
		return agentId;
	}
}
