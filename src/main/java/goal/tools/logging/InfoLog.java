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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Replacement class for System.out calls. Create a new InfoLog instead of
 * calling System.out.print(). (note that you have to end lines manually)<br>
 * Logs all created {@link InfoLog}s to a logger, which is available using
 * {@link #getLogger()}.
 *
 * @author N.Kraayenbrink
 */
public class InfoLog extends StringsLogRecord {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6467059802392960544L;

	/**
	 * @return The {@link Logger} logging all {@link InfoLog} instances.
	 */
	public static GOALLogger getLogger() {
		return Loggers.getInfoLogger();
	}

	/**
	 * Creates and logs a new InfoLog with some formatted message.
	 *
	 * @param message
	 *            The format of the message (or the message itself) to log.
	 * @param messageParams
	 *            The parameters of the message to log.
	 */
	public InfoLog(String message, Object... messageParams) {
		super(Level.INFO, message, messageParams);
		getLogger().log(this);
	}
}
