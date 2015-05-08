package goal.core.runtime.service.environment.events;

import goal.core.runtime.service.environmentport.EnvironmentPort;

public class EnvironmentPortRemovedEvent extends EnvironmentServiceEvent {
	private final EnvironmentPort port;

	public EnvironmentPortRemovedEvent(EnvironmentPort port) {
		this.port = port;
	}

	/**
	 * @return the port
	 */
	public EnvironmentPort getPort() {
		return this.port;
	}
}