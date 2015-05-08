package goal.core.runtime.service.environmentport.actions;

import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

/**
 * Executes the EIS reset() command.
 *
 * @author W.Pasman
 * @modified K.Hindriks
 */
public class Reset extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -2868288779730939560L;

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws Exception {
		return messages2Environment.invoke(this);
	}
}