package goal.core.runtime.events;

import languageTools.program.agent.AgentId;

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
		this.agentId = id;
	}

	public AgentId getAgentId() {
		return this.agentId;
	}
}
