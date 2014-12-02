package goal.core.runtime.service.environment.events;

import goal.core.runtime.service.environmentport.EnvironmentPort;

public class EnvironmentPortAddedEvent extends EnvironmentServiceEvent {
	private final EnvironmentPort port;

	public EnvironmentPortAddedEvent(EnvironmentPort port) {
		this.port = port;
	}

	/**
	 * @return the port
	 */
	public EnvironmentPort getPort() {
		return port;
	}
}