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
package goal.preferences;

import goal.tools.debugger.Channel;
import goal.tools.debugger.Channel.ChannelState;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TODO
 *
 */
public class DebugPreferences {
	private static List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
	private static Map<String, Object> preferences;

	public static void initPrefs(Map<String, Object> init) {
		if (init == null) {
			preferences = new TreeMap<>();
		} else {
			preferences = init;
		}

		for (Channel channel : Channel.values()) {
			init(channel, channel.getDefaultState());
		}
	}

	public static Map<String, Object> getPrefs() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * Gets the preferred {@link ChannelState} for the given {@link Channel}.
	 *
	 * @param channel
	 *            The {@link Channel} to get the state for.
	 * @return The preferred {@link ChannelState} of the given {@link Channel},
	 *         or the default state for the given channel if there is no
	 *         preference.
	 */
	public static ChannelState getChannelState(Channel channel) {
		return get(channel);
	}

	/**
	 * Sets the {@link ChannelState} for the given {@link Channel}. Only the
	 * non-hidden channels can be changed.
	 *
	 * @param channel
	 *            The channel to set the state for.
	 * @param state
	 *            The desired state of the channel.
	 */
	public static void setChannelState(Channel channel, ChannelState state) {
		// Get previous and store new settings for the channel.
		if (channel.getDefaultState().isHidden()) {
			return;
		}
		String oldValue = getChannelState(channel).name();
		put(channel, state);

		// Notify listeners of changes.
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(channel, channel
					.toString(), oldValue, state.toString()));
		}
	}

	/**
	 * Subscribe a listener to the Debug preferences, so that it will get a
	 * notification via a call to when the preferences are changed.
	 *
	 * @param listener
	 */
	public static void addChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * unsubscribe as listener
	 *
	 * @param listener
	 */
	public static void removeChangeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	// 3 helper functions...
	private static ChannelState get(Channel pref) {
		if (preferences == null) {
			Preferences.initializeAllPrefs();
		}
		String get = (String) preferences.get(pref.name());
		if (get == null || get.isEmpty()) {
			return null;
		} else {
			return ChannelState.valueOf(get);
		}
	}

	private static void put(Channel pref, ChannelState value) {
		if (preferences == null) {
			Preferences.initializeAllPrefs();
		}
		preferences.put(pref.name(), value.name());
	}

	private static void init(Channel pref, ChannelState defaultValue) {
		Object current = get(pref);
		if (current == null
				|| !current.getClass().equals(defaultValue.getClass())) {
			put(pref, defaultValue);
		}
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(pref, pref
					.toString(), current, get(pref)));
		}
	}

	/**
	 * Hide constructor.
	 */
	private DebugPreferences() {
	}

}