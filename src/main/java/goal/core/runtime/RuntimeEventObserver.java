package goal.core.runtime;

import goal.util.Observer;

/**
 * interface to observe events from the {@link RuntimeManager}.
 *
 * @author W.Pasman 11nov2013
 *
 */
public interface RuntimeEventObserver extends
Observer<RuntimeManager<?, ?>, RuntimeEvent> {
	/**
	 * This function is called when a {@link RuntimeEvent} occurs. To be
	 * implemented by the observer.
	 *
	 * @param source
	 *            the {@link RuntimeManager} creating the event
	 * @param evt
	 *            the {@link RuntimeEvent} that happened.
	 */
	@Override
	public void eventOccured(RuntimeManager<?, ?> source, RuntimeEvent evt);
}
