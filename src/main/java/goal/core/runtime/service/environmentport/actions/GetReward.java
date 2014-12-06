package goal.core.runtime.service.environmentport.actions;

import eis.exceptions.QueryException;
import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * get the reward that the environment currently gives for the current state.
 * returns a Double, or null if no reward available. The assumption here is that
 * each agent is connected only to 1 entity and that EIS can figure it out.
 *
 * Throws QueryException if environment does not support getReward.
 *
 * @author W.Pasman. Modified 13may2014 #3052
 */
public class GetReward extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 1545965461986234836L;
	private final String agentName;

	/**
	 * get reward for given entity name.
	 *
	 * @param agentName
	 *            name of agent requesting this action.
	 */
	public GetReward(String agentName) {
		this.agentName = agentName;

	}

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws QueryException {
		return messages2Environment.getReward(this.agentName);
	}
}