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

public class EnvironmentPreferences {
	public enum Pref {
		/**
		 * print new entities when they appear
		 */
		printNewEntities,
		/**
		 * The host address for the middleware server.
		 */
		registryhost
	}

	private static Map<String, Object> preferences;

	public static void initPrefs(Map<String, Object> init) {
		if (init == null) {
			preferences = new TreeMap<>();
		} else {
			preferences = init;
		}

		init(Pref.printNewEntities, true);
		init(Pref.registryhost, "");
	}

	public static Map<String, Object> getPrefs() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * check if new entities should be printed
	 *
	 * @return true if middleware should be launched locally always. default
	 *         true
	 */
	public static boolean getPrintEntities() {
		return (Boolean) get(Pref.printNewEntities);
	}

	public static String getMiddlewareHost() {
		return (String) get(Pref.registryhost);
	}

	/**
	 * if new entities should be printed
	 */
	public static void setPrintEntities(boolean printNewEntities) {
		put(Pref.printNewEntities, printNewEntities);
	}

	public static void setMiddlewareHost(String registryhost) {
		put(Pref.registryhost, registryhost);
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
	private EnvironmentPreferences() {
	}
}
