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

public class PMPreferences {
	public enum Pref {
		/**
		 * always run middleware local
		 */
		middlewareLocal,
		/**
		 * when user browses for an agent file, start browsing here
		 */
		agentsBrowseDir,
		/**
		 * true if we should remember which directory on file system user used
		 * last for loading or saving agents
		 */
		rememberLastUsedAgentDir,
		/**
		 * default KRInterface name to use
		 */
		defaultKRInterface,
		/**
		 * sleep agents when they receive same percepts and do same actions all
		 * the time
		 */
		sleepRepetetiveAgent,
		/**
		 * remove agent from the platform when it is killed
		 */
		removeKilledAgent,
		/**
		 * new agents copy environment run state (or run if no environment)
		 */
		agentCopyEnvRunState,
		/**
		 * Preference for indicating whether MAS name will be prefixed to agent
		 * name or not.
		 */
		useMASNameAsAgentPrefix,
		/**
		 * Amount of threads (e.g. cores) to use for running agents; uses
		 * everything that is available by default.
		 */
		threadPoolSize
	}

	private static Map<String, Object> preferences;

	/**
	 * Initializes the preference settings. If no initial preference settings
	 * are provided, the default preference settings are used (as if user did
	 * not change any settings).
	 *
	 * @param init
	 *            The settings for initializing the preferences.
	 */
	public static void initPrefs(Map<String, Object> init) {
		if (init == null) {
			preferences = new TreeMap<>();
		} else {
			preferences = init;
		}
		String runIn = System.getProperty("user.dir");

		init(Pref.middlewareLocal, false);
		init(Pref.rememberLastUsedAgentDir, true);
		init(Pref.removeKilledAgent, false);
		init(Pref.agentCopyEnvRunState, false);
		init(Pref.sleepRepetetiveAgent, false);
		init(Pref.defaultKRInterface, "swiprolog");
		init(Pref.agentsBrowseDir, runIn + "/GOALagents");
		init(Pref.useMASNameAsAgentPrefix, false);
		init(Pref.threadPoolSize, 0);
	}

	public static Map<String, Object> getPrefs() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * check if middleware should be launched locally always
	 *
	 * @return true iff middleware should be launched locally always.
	 */
	public static boolean getAlwaysMiddlewareLocal() {
		return (Boolean) get(Pref.middlewareLocal);
	}

	/**
	 * check if last used directory should be remembered. Defaults to true.
	 *
	 * @return true iff last used directory should be remembered.
	 */
	public static boolean getRememberLastUsedAgentDir() {
		return (Boolean) get(Pref.rememberLastUsedAgentDir);
	}

	/**
	 * check if killed agents should be removed from the platform
	 *
	 * @return {@code true} if killed agents should be removed entirely.
	 */
	public static boolean getRemoveKilledAgent() {
		return (Boolean) get(Pref.removeKilledAgent);
	}

	/**
	 * Checks whether MAS name should be used as prefix for agent name.
	 *
	 * @return {@code true} if MAS name should be used as prefix.
	 */
	public static boolean getUseMASNameAsAgentPrefix() {
		return (Boolean) get(Pref.useMASNameAsAgentPrefix);
	}

	/**
	 * Check if the agent should copy its run state from the environment. So if
	 * env is running, the agent should go to running. For other environment
	 * states, agents goes to pause mode. If there is no environment, the agent
	 * should be set to running. Default value is false.
	 *
	 * @return true if agents should run automatically, else false.
	 */
	public static boolean getAgentCopyEnvRunState() {
		return (Boolean) get(Pref.agentCopyEnvRunState);
	}

	/**
	 * check if agents that repeat actions should be put to sleep till their
	 * percept input changes
	 *
	 * @return true if such agents should sleep, false else. Default is true.
	 */
	public static boolean getSleepRepeatingAgent() {
		return (Boolean) get(Pref.sleepRepetetiveAgent);
	}

	/**
	 * get the Default KR Language to use
	 *
	 * @return default KR language, typically "swiprolog".
	 */
	public static String getDefaultKRInterface() {
		return (String) get(Pref.defaultKRInterface);
	}

	/**
	 * Get the path used when the user wants to browse for agents. Default is
	 * user's home dir as returned by {@link System#getProperty("user.home")}.
	 * This path may be set by the installer when the examples are installed in
	 * a user selected location. <br>
	 * Note that this path is not relevant for opening agents or MAS file, since
	 * these are stored by ABSOLUTE PATH anyway.
	 *
	 * @return browse path for agents.
	 */
	public static String getAgentBrowsePath() {
		return (String) get(Pref.agentsBrowseDir);
	}

	/**
	 * @return The amount of threads (e.g. cores) to use for running agents.
	 */
	public static int getThreadPoolSize() {
		return (Integer) get(Pref.threadPoolSize);
	}

	/**
	 * if middleware should be launched locally always
	 */
	public static void setAlwaysMiddlewareLocal(boolean middlewareLocal) {
		put(Pref.middlewareLocal, middlewareLocal);
	}

	/**
	 * if last used directory should be remembered
	 */
	public static void setRememberLastUsedAgentDir(
			boolean rememberLastUsedAgentDir) {
		put(Pref.rememberLastUsedAgentDir, rememberLastUsedAgentDir);
	}

	/**
	 * if killed agents should be removed from the platform
	 */
	public static void setRemoveKilledAgent(boolean removeKilledAgent) {
		put(Pref.removeKilledAgent, removeKilledAgent);
	}

	/**
	 * If the agent should copy its run state from the environment. So if env is
	 * running, the agent should go to running. For other environment states,
	 * agents goes to pause mode. If there is no environment, the agent should
	 * be set to running.
	 */
	public static void setAgentCopyEnvRunState(boolean agentCopyEnvRunState) {
		put(Pref.agentCopyEnvRunState, agentCopyEnvRunState);
	}

	/**
	 * if agents that repeat actions should be put to sleep till their percept
	 * input changes
	 */
	public static void setSleepRepeatingAgent(boolean sleepRepetetiveAgent) {
		put(Pref.sleepRepetetiveAgent, sleepRepetetiveAgent);
	}

	/**
	 * set the Default KR Language to use
	 */
	public static void setDefaultKRInterface(String defaultKRInterface) {
		put(Pref.defaultKRInterface, defaultKRInterface);
	}

	/**
	 * Set the path used when the user wants to browse for agents. This path may
	 * be set by the installer when the examples are installed in a user
	 * selected location. <br>
	 * Note that this path is not relevant for opening agents or MAS file, since
	 * these are stored by ABSOLUTE PATH anyway.
	 */
	public static void setAgentBrowsePath(String agentsBrowseDir) {
		put(Pref.agentsBrowseDir, agentsBrowseDir);
	}

	public static void setPrefixAgentNameWithMASName(boolean useMASName) {
		put(Pref.useMASNameAsAgentPrefix, useMASName);
	}

	/**
	 * Set the amount of threads (e.g. cores) to use for running agents; this is
	 * everything that is available (on the current machine) by default.
	 */
	public static void setThreadPoolSize(int size) {
		put(Pref.threadPoolSize, size);
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
	private PMPreferences() {
	}
}
