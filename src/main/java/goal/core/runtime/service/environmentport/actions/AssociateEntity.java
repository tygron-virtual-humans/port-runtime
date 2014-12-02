package goal.core.runtime.service.environmentport.actions;

import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * Executes the EIS associateEntity command.
 *
 * @author W.Pasman
 */
public class AssociateEntity extends Action {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = -2868288779730939560L;

	private final String agentName;
	private final String entity;

	public AssociateEntity(String agentName, String entity) {
		this.agentName = agentName;
		this.entity = entity;
	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws Exception {
		return messages2Environment.invoke(this);
	}
}