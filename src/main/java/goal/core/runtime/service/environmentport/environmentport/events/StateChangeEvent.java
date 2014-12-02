package goal.core.runtime.service.environmentport.environmentport.events;

import eis.iilang.EnvironmentState;

/**
 * event to indicate state change of environment.
 *
 * @author W.Pasman
 */
public class StateChangeEvent extends EnvironmentEvent {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6387717146943403919L;
	private final EnvironmentState newState;

	public StateChangeEvent(EnvironmentState newState) {
		this.newState = newState;
	}

	/** @return The environment state */
	public EnvironmentState getState() {
		return newState;
	}

}