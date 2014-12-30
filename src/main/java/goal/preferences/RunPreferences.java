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

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class RunPreferences {
	public enum Pref {
		middleware
	}

	private static Map<String, Object> preferences;

	public static void initPrefs(Map<String, Object> init) {
		if (init == null) {
			preferences = new TreeMap<>();
		} else {
			preferences = init;
		}

		init(Pref.middleware, "LOCAL");
	}

	public static Map<String, Object> getPrefs() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * Get middleware selection
	 *
	 * @return latest middleware selection. LOCAL is default value. Current
	 *         values are "JADE","RMI" or "LOCAL".
	 */
	public static String getUsedMiddleware() {
		return (String) get(Pref.middleware);
	}

	/**
	 * set middleware to use for running the agents.
	 *
	 * @param middlewarename
	 *            is middleware to run during startup. allowed values: NONE,
	 *            JADE, RMI.
	 */
	public static void setUsedMiddleware(String middlewarename) {
		put(Pref.middleware, middlewarename);
	}

	// 3 helper functions...
	private static Object get(Pref pref) {
		if (preferences == null) {
			Preferences.initializeAllPrefs();
		}
		return preferences.get(pref.name());
	}

	private static void put(Pref pref, Object value) {
		if (preferences == null) {
			Preferences.initializeAllPrefs();
		}
		preferences.put(pref.name(), value);
	}

	private static void init(Pref pref, Object defaultValue) {
		Object current = get(pref);
		if (current == null
				|| !current.getClass().equals(defaultValue.getClass())) {
			put(pref, defaultValue);
		}
	}

	/**
	 * Hide constructor.
	 */
	private RunPreferences() {
	}
}
