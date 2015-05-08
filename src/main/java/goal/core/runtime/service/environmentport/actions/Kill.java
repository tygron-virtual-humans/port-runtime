package goal.core.runtime.service.environmentport.actions;

import eis.exceptions.ManagementException;
import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * Executes the EIS kill() command.
 *
 * @author W.Pasman
 * @modified K.Hindriks
 */
public class Kill extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -2868288779730939560L;

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws ManagementException {
		return messages2Environment.invoke(this);
	}
}