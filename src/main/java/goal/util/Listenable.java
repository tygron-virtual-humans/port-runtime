/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class to support listenable objects
 *
 * @param ListenerType
 *            is the class name of the Listener object
 * @param EventType
 *            is the class name of the Event type being passed.
 * @author WW.Pasman 23june2010
 */
public abstract class Listenable<ListenerType, EventType> {
	private final List<ListenerType> myListeners = new LinkedList<>();

	public void addListener(ListenerType listener) {
		getMyListeners().add(listener);
	}

	public void removeListener(ListenerType listener) {
		getMyListeners().remove(listener);
	}

	/**
	 * <p>
	 * you must override this. Typically, it is something like <br>
	 * <tt>
	 * for(listener: myListeners) { listener.blaEventOccured(..); }
	 * </tt>
	 * </p>
	 */
	public abstract void notifyListeners(EventType e);

	/**
	 * Get all current listeners.
	 *
	 * @return the list of the current listeners.
	 */
	public List<ListenerType> getMyListeners() {
		return myListeners;
	}
}