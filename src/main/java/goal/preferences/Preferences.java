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

import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

/**
 * @author V.Koeman
 */
public class Preferences {

	private static File settingsFile = new File(System.getProperty("user.dir") //$NON-NLS-1$
			+ "/settings.yaml"); //$NON-NLS-1$

	/**
	 * Returns the default settings file; possibly null when not
	 * loading/persisting from a file at all.
	 *
	 * @return File
	 */
	public static File getSettingsFile() {
		return settingsFile;
	}

	/**
	 * Change the default settings file. Use null to not load/persist settings
	 * from a file at all.
	 *
	 * @param file
	 *            A file object (preferably a .yaml file)
	 */
	public static void changeSettingsFile(File file) {
		settingsFile = file;
		initializeAllPrefs();
	}

	/**
	 * Erase ALL preference changes made by the user. This recursively calls
	 * initPrefs(null) of the preferences in this package and deletes the
	 * settingsfile if present (see settingsFile field for the filepath).
	 * <p>
	 * IMPORTANT: This function is also called by the GOAL Uninstaller. We do
	 * not throw here as it seems there is nothing we can do after showing the
	 * message to the user. Also not throwing is convenient for the uninstaller.
	 * </p>
	 */
	public static void resetToDefaultPreferences() {
		// Reset all preferences to their default values.
		CorePreferences.initPrefs(null);
		DBExportPreferences.initPrefs(null);
		DebugPreferences.initPrefs(null);
		EnvironmentPreferences.initPrefs(null);
		LoggingPreferences.initPrefs(null);
		PMPreferences.initPrefs(null);
		RunPreferences.initPrefs(null);

		if (settingsFile != null && settingsFile.exists()) {
			settingsFile.delete();
		}
	}

	/**
	 * Initializes all preferences in this package. If a file exist at the path
	 * of the settingsFile field here, this (YAML) file will be used (if valid).
	 * Otherwise, an empty file will be created and default properties will be
	 * set everywhere. This needs to be called before any preferences can be
	 * used, but all preference classes do this automatically, so calling this
	 * outside of this package is not needed in normal circumstances! This
	 * function also automatically registers a Java shutdown-hook to
	 * automatically save the preferences at any termination.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static void initializeAllPrefs() {
		Object loaded = null;
		if (settingsFile != null) {
			try {
				if (!settingsFile.exists()) {
					settingsFile.createNewFile();
				}
				try (FileReader reader = new FileReader(settingsFile)) {
					Yaml yaml = new Yaml();
					loaded = yaml.load(reader);
				}
			} catch (FileNotFoundException e) {
				throw new GOALRuntimeErrorException(
						Resources.get(WarningStrings.FAILED_SETTINGSFILE_FIND),
						e);
			} catch (IOException e) {
				throw new GOALRuntimeErrorException(
						Resources.get(WarningStrings.FAILED_SETTINGSFILE_READ),
						e);
			}
		}

		Map<String, Map<String, Object>> prefs = new LinkedHashMap<String, Map<String, Object>>();
		if (loaded != null && loaded instanceof Map) {
			prefs = (Map<String, Map<String, Object>>) loaded;
		}

		CorePreferences.initPrefs(prefs.get(CorePreferences.class
				.getSimpleName()));
		DBExportPreferences.initPrefs(prefs.get(DBExportPreferences.class
				.getSimpleName()));
		DebugPreferences.initPrefs(prefs.get(DebugPreferences.class
				.getSimpleName()));
		EnvironmentPreferences.initPrefs(prefs.get(EnvironmentPreferences.class
				.getSimpleName()));
		LoggingPreferences.initPrefs(prefs.get(LoggingPreferences.class
				.getSimpleName()));
		PMPreferences.initPrefs(prefs.get(PMPreferences.class.getSimpleName()));
		RunPreferences
				.initPrefs(prefs.get(RunPreferences.class.getSimpleName()));

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				persistAllPrefs();
			}
		});
	}

	/**
	 * This persists all preferences in this package to the file indicated by
	 * the settingsFile field. Notice that this is a full-save at all times (not
	 * incremental). Moreover, the initializeAllPrefs function automatically
	 * registers this function to be called at any termination, and thus this
	 * function does not need to be called manually per-se. Note that this will
	 * not throw exceptions when the file cannot be written (silent).
	 */
	public static void persistAllPrefs() {
		Map<String, Map<String, Object>> prefs = new LinkedHashMap<>();
		// Note: the order here determines the order of appearance in the file,
		// although the order does not matter for loading (change at will).
		// Note that the settings in classes themselves are sorted
		// alphabetically (by key), but again changing this order
		// (e.g. in the file itself) does not matter for loading them.
		prefs.put(PMPreferences.class.getSimpleName(), PMPreferences.getPrefs());
		prefs.put(EnvironmentPreferences.class.getSimpleName(),
				EnvironmentPreferences.getPrefs());
		prefs.put(RunPreferences.class.getSimpleName(),
				RunPreferences.getPrefs());
		prefs.put(CorePreferences.class.getSimpleName(),
				CorePreferences.getPrefs());
		prefs.put(DebugPreferences.class.getSimpleName(),
				DebugPreferences.getPrefs());
		prefs.put(LoggingPreferences.class.getSimpleName(),
				LoggingPreferences.getPrefs());
		prefs.put(DBExportPreferences.class.getSimpleName(),
				DBExportPreferences.getPrefs());

		if (settingsFile != null) {
			DumperOptions options = new DumperOptions();
			options.setPrettyFlow(true);
			options.setLineBreak(LineBreak.WIN);
			try {
				if (!settingsFile.exists()) {
					settingsFile.createNewFile();
				}
				Yaml yaml = new Yaml(options);
				try (FileWriter writer = new FileWriter(settingsFile)) {
					yaml.dump(prefs, writer);
				}
			} catch (IOException e) {
				System.out.println("Failed to write preferences:"+e);
			}
		}
	}

	/**
	 * Hide constructor.
	 */
	private Preferences() {
	}
}
