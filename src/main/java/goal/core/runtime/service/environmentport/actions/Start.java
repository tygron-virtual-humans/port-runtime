package goal.core.runtime.service.environmentport.actions;

import eis.exceptions.ManagementException;
import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * execute the EIS start() command.
 *
 * @author W.Pasman
 */
public class Start extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -471133348919875660L;

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws ManagementException {
		return messages2Environment.invoke(this);
	}
}