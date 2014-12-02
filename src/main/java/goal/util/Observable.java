package goal.util;

/**
 * generic interface for observable functionality. Thread safe.
 *
 * @author W.Pasman 16jan2014 #2773
 *
 * @param <OBS>
 *            observer of OBJects coming from SRC
 * @param <SRC>
 *            the type of the event source of OBJs
 * @param <OBJ>
 *            the type of event objects created by this observable.
 */
public interface Observable<OBS extends Observer<SRC, OBJ>, SRC, OBJ> {

	/**
	 * Add a new observer. Nothing happens if observer already there.
	 *
	 * @param observer
	 */
	public void addObserver(OBS observer);

	/**
	 * Remove an observer. Nothing happens if observer not there.
	 *
	 * @param observer
	 */
	public void removeObserver(OBS observer);

	/**
	 * notify all our observers of some event. This notifies all observers
	 * available at the moment of the call. If the set of observers changes
	 * DURING the notifyAll handling, this change will be ignored.
	 */
	public void notifyObservers(SRC src, OBJ obj);
}
