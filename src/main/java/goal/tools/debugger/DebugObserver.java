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

package goal.tools.debugger;

/**
 * <p>
 * Observer of a {@link SteppingDebugger}. A debug observer receives debug
 * events from the debuggers it is subscribed to, by callbacks to
 * {@link #notifyBreakpointHit(DebugEvent)}. A default implementation is
 * available in the abstract class MyDebugObserver.
 * </p>
 * <p>
 * The owner or the class itself needs to
 * {@link SteppingDebugger#subscribe(DebugObserver)} and
 * {@link SteppingDebugger#unsubscribe(DebugObserver)} the observer with one or
 * more debuggers. Also it needs to enable the events it wishes to observe, see
 * {@link SteppingDebugger#addPause(DebugObserver, Channel)} and
 * {@link SteppingDebugger#subscribe(DebugObserver, Channel)}
 * </p>
 *
 * @author W.Pasman
 * @modified KH Observer maintains own run mode.
 * @modified W.Pasman 15feb2011 moved run mode back into the Debugger, see
 *           #1178.
 */
public interface DebugObserver {

	/**
	 * Returns the name of the debug observer. Must contain unique name,
	 * otherwise subscribe will throw exception.
	 *
	 * @return name of the debug observer.
	 */
	String getObserverName();

	/**
	 * <p>
	 * Handles debug events received from the debugger. A debugger calls this
	 * method whenever a debug event that needs to be reported occurs.
	 * </p>
	 * <p>
	 * IMPORTANT: this method is called by the thread that is being debugged.
	 * The method should return immediately to not block the calling thread. For
	 * pausing or stepping the (agent) thread that is being debugged, the
	 * corresponding debugger methods should be used.
	 * </p>
	 *
	 * @param event
	 *            debug event received from debugger.
	 */
	void notifyBreakpointHit(DebugEvent event);

	/**
	 * We DO use equals on DebugObservers. But the default for equals is: an
	 * object is only equal to itself. And this should work for normal debug
	 * observers.
	 */

}
