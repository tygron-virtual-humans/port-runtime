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

public class DBExportPreferences {
	public enum Pref {
		exportbeliefs, exportpercepts, exportmailbox, exportgoals, separatefiles, openaftersave, exportBrowseDir, rememberLastUsedExportDir
	}

	private static Map<String, Object> preferences;

	public static void initPrefs(Map<String, Object> init) {
		if (init == null) {
			preferences = new TreeMap<>();
		} else {
			preferences = init;
		}

		init(Pref.exportbeliefs, true);
		init(Pref.exportmailbox, true);
		init(Pref.exportpercepts, true);
		init(Pref.exportgoals, true);
		init(Pref.separatefiles, true);
		init(Pref.openaftersave, false);
		init(Pref.rememberLastUsedExportDir, true);
		init(Pref.exportBrowseDir, System.getProperty("user.dir"));
	}

	public static Map<String, Object> getPrefs() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * get preference for export of beliefs
	 *
	 * @return true if user wants beliefs to be exported
	 */
	public static boolean getExportBeliefs() {
		return (Boolean) get(Pref.exportbeliefs);
	}

	/**
	 * get preference for export of mailbox
	 *
	 * @return true if user wants mailbox to be exported (included with beliefs)
	 */
	public static boolean getExportMailbox() {
		return (Boolean) get(Pref.exportmailbox);
	}

	/**
	 * get preference for export of percepts
	 *
	 * @return true if user wants percepts to be exported (included with
	 *         beliefs)
	 */
	public static boolean getExportPercepts() {
		return (Boolean) get(Pref.exportpercepts);
	}

	/**
	 * get preference for export of goals
	 *
	 * @return true if user wants goals to be exported
	 */
	public static boolean getExportGoals() {
		return (Boolean) get(Pref.exportgoals);
	}

	/**
	 * get preference for export of beliefs and goals in separate files
	 *
	 * @return true if user wants beliefs and beliefs in separate files
	 */
	public static boolean getExportSeparateFiles() {
		return (Boolean) get(Pref.separatefiles);
	}

	/**
	 * get preference for opening files after exporting
	 *
	 * @return true is user wants exported files to be opened after save.
	 */
	public static boolean getOpenAfterSave() {
		return (Boolean) get(Pref.openaftersave);
	}

	/**
	 * check if last used directory should be remembered. Defaults to true.
	 *
	 * @return true iff last used directory should be remembered.
	 */
	public static boolean getRememberLastUsedExportDir() {
		return (Boolean) get(Pref.rememberLastUsedExportDir);
	}

	/**
	 * Get the path used when the user wants to export a database. Default is
	 * user's home dir as returned by {@link System#getProperty("user.home")} <br>
	 *
	 * @return browse path for export. Default is user.home
	 */
	public static String getExportBrowsePath() {
		return (String) get(Pref.exportBrowseDir);
	}

	/**
	 * set preference for export of beliefs
	 */
	public static void setExportBeliefs(boolean exportbeliefs) {
		put(Pref.exportbeliefs, exportbeliefs);
	}

	/**
	 * set preference for export of mailbox
	 */
	public static void setExportMailbox(boolean exportmailbox) {
		put(Pref.exportmailbox, exportmailbox);
	}

	/**
	 * set preference for export of percepts
	 */
	public static void setExportPercepts(boolean exportpercepts) {
		put(Pref.exportpercepts, exportpercepts);
	}

	/**
	 * set preference for export of goals
	 */
	public static void setExportGoals(boolean exportgoals) {
		put(Pref.exportgoals, exportgoals);
	}

	/**
	 * set preference for export of beliefs and goals in separate files
	 */
	public static void setExportSeparateFiles(boolean separatefiles) {
		put(Pref.separatefiles, separatefiles);
	}

	/**
	 * set preference for opening files after exporting
	 */
	public static void setOpenAfterSave(boolean openaftersave) {
		put(Pref.openaftersave, openaftersave);
	}

	/**
	 * if the last used directory should be remembered
	 */
	public static void setRememberLastUsedExportDir(
			boolean rememberLastUsedExportDir) {
		put(Pref.rememberLastUsedExportDir, rememberLastUsedExportDir);
	}

	/**
	 * Adjust the export browse path. This function will not correctly call back
	 * the GUI panes used in PMPrefPanel. That means that things may display
	 * incorrectly if this function is called while having the PMPrefPanel open.
	 *
	 * @param newpath
	 *            is the new export browse path to be used. Should be an
	 *            absolute path to a directory.
	 */
	public static void setExportBrowsePath(String newpath) {
		put(Pref.exportBrowseDir, newpath);
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
	private DBExportPreferences() {
	}
}
