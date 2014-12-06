package goal.core.runtime.service.environmentport.actions;

import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * Execute the EIS RegisterAgent command.
 *
 * @author W.Pasman
 */
public class RegisterAgent extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -2868288779730939560L;
	private final String agentName;

	/**
	 * @param agentName
	 *            The agent.
	 */
	public RegisterAgent(String agentName) {
		this.agentName = agentName;
	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return this.agentName;
	}

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws Exception {
		return messages2Environment.invoke(this);
	}
}