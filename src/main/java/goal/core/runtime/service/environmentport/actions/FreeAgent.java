package goal.core.runtime.service.environmentport.actions;

import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

public class FreeAgent extends Action {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = -7531018081355537113L;

	private final String agentName;

	public FreeAgent(String agentName) {
		this.agentName = agentName;
	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return agentName;
	}

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws Exception {
		return messages2Environment.invoke(this);
	}
}
