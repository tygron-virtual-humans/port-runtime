package goal.core.runtime.service.environment;

import goal.core.runtime.service.environment.events.EnvironmentServiceEvent;

/**
 * observer for EnvironmentService
 *
 * @author wouter
 */
public interface EnvironmentServiceObserver {
	/**
	 * Called when an event occurs
	 *
	 * @param environmentService
	 *            the {@link EnvironmentService}
	 * @param evt
	 *            the {@link EnvironmentServiceEvent}
	 */
	public void environmentServiceEventOccured(
			EnvironmentService environmentService, EnvironmentServiceEvent evt);
}