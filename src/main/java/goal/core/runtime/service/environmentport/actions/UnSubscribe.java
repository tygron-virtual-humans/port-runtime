package goal.core.runtime.service.environmentport.actions;

import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

public class UnSubscribe extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -824064754869457154L;

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws Exception {
		return messages2Environment.invoke(this);
	}
}