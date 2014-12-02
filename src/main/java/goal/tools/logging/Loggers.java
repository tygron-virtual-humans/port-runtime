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

package goal.tools.logging;

import goal.preferences.LoggingPreferences;
import goal.tools.errorhandling.GOALBugReport;
import goal.tools.errorhandling.Warning;

import java.util.logging.Logger;

/**
 * Class with references to various static loggers, such as the one for warnings
 * and the one for parser messages. Does not contain references to loggers that
 * are created for each agent's action log and other debug debug output.
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman aug2010 trac #1191 and #1163
 * @modified K.Hindriks
 */
public final class Loggers {
	// disable instantiation
	private Loggers() {
	}

	private static GOALLogger infoLogger = new GOALLogger(
			InfoLog.class.getName(), LoggingPreferences.getLogConsolesToFile());
	private static GOALLogger warningLogger = new GOALLogger(
			Warning.class.getName(), LoggingPreferences.getLogConsolesToFile());
	private static GOALLogger runtimeErrorLogger = new GOALLogger(
			GOALBugReport.class.getName(),
			LoggingPreferences.getLogConsolesToFile());
	private static GOALLogger parserLogger = new GOALLogger("goal.parser",
			LoggingPreferences.getLogConsolesToFile());

	/**
	 * Adds a console logger to all available loggers.
	 */
	public static void addConsoleLogger() {
		for (GOALLogger logger : getAllLoggers()) {
			logger.addConsoleLogger();
		}
	}

	/**
	 * Removes the console logger from all loggers.
	 */
	public static void removeConsoleLogger() {
		for (GOALLogger logger : getAllLoggers()) {
			logger.removeConsoleLogger();
		}
	}

	/**
	 * @return A {@link Logger} for parser messages
	 */
	public static GOALLogger getParserLogger() {
		return parserLogger;
	}

	/**
	 * @return A {@link Logger} for general information
	 */
	public static GOALLogger getInfoLogger() {
		return infoLogger;
	}

	/**
	 * @return The {@link Logger} that logs all {@link Warning}s.
	 */
	public static GOALLogger getWarningLogger() {
		return warningLogger;
	}

	/**
	 * @return The {@link Logger} that logs all {@link GOALBugReport}s.
	 */
	public static GOALLogger getRuntimeErrorLogger() {
		return runtimeErrorLogger;
	}

	/**
	 * @return An array with all {@link Logger}s obtainable via this class.
	 */
	public static GOALLogger[] getAllLoggers() {
		return new GOALLogger[] { getParserLogger(), getInfoLogger(),
				getWarningLogger(), getRuntimeErrorLogger() };
	}
}
