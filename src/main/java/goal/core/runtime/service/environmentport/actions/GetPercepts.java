package goal.core.runtime.service.environmentport.actions;

import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * Executes the EIS getAllPercepts command.
 *
 * @return All percepts for the given agent, or {@code null} if agent is unknown
 *         to EIS.
 *
 * @author W.Pasman
 * @modified K.Hindriks
 */
public class GetPercepts extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -471133348919875660L;
	private final String agentName;

	public GetPercepts(String agentName) {
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
			throws PerceiveException, NoEnvironmentException {
		return messages2Environment.invoke(this);
	}
}