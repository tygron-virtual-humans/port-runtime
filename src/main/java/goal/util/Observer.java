package goal.util;

/**
 * Observer of events of type OBJ coming from SRC,
 *
 * @author wouter
 *
 * @param <SRC>
 *            the source of the events.
 * @param <OBJ>
 */
public interface Observer<SRC, OBJ> {
	/**
	 * Called when an event OBJ occurs in SRC.
	 *
	 * @param source
	 * @param evt
	 */
	public void eventOccured(SRC source, OBJ evt);
}
