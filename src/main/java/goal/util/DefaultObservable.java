package goal.util;

import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements generic observable functionality. Thread safe.
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
public class DefaultObservable<OBS extends Observer<SRC, OBJ>, SRC, OBJ>
		implements Observable<OBS, SRC, OBJ> {
	Set<OBS> observers = new HashSet<>();

	@Override
	public synchronized void addObserver(OBS observer) {
		this.observers.add(observer);
	}

	@Override
	public synchronized void removeObserver(OBS observer) {
		this.observers.remove(observer);
	}

	/**
	 * returns a static copy of the current list of observers.
	 *
	 * @return the current list of observers
	 */
	private synchronized Set<OBS> getObservers() {
		return new HashSet<>(this.observers);
	}

	/**
	 * notify all our observers of some event. This notifies all observers
	 * available at the moment of the call. If the set of observers changes
	 * DURING the notifyAll handling, this change will be ignored.
	 * <p>
	 * IMPORTANT: notifyObservers should only be called by the class
	 * implementing this, and not by external users of the observable such as
	 * Observers. Can we enforce this?
	 */
	@Override
	public void notifyObservers(SRC src, OBJ obj) {
		for (OBS obs : getObservers()) {
			try {
				obs.eventOccured(src, obj);
			} catch (Throwable e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_CALLBACK_1),
						obs.toString()), e);
			}
		}
	}
}
