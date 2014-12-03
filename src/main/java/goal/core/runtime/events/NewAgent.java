package goal.core.runtime.events;

import languageTools.program.agent.AgentId;

/**
 * A new agent appeared in a RuntimeServiceManager.
 *
 * @author W.Pasman 11nov2013
 */
public class NewAgent implements RemoteRuntimeEvent {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -454019727437979527L;
	private final AgentId agentId;

	public NewAgent(AgentId id) {
		agentId = id;
	}

	public AgentId getAgentId() {
		return agentId;
	}
}
