package goal.core.runtime.service.environmentport;

import goal.core.runtime.service.environmentport.environmentport.events.EnvironmentEvent;

/**
 * Listener for events on an environment port
 *
 * @author W.Pasman 11nov2013
 *
 */
public interface EnvironmentPortObserver {

	/**
	 * Called when an {@link EnvironmentPort} event occurs.
	 *
	 * @param environmentPort
	 *            The port on which the event occurred.
	 * @param e
	 *            The event.
	 */
	public void EnvironmentPortEventOccured(EnvironmentPort environmentPort,
			EnvironmentEvent e);
}
