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

package goal.tools.errorhandling.exceptions;

import goal.core.agent.Agent;
import goal.core.runtime.service.environmentport.actions.GetPercepts;

/**
 * Represents an exception that occurred while getting the percepts from the
 * environment for an {@link Agent}. See also: {@link GetPercepts}.
 *
 * @author K.Hindriks.
 */
public class GOALGetPerceptsFailedException extends GOALRuntimeErrorException {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 953890011844171754L;

	/**
	 * @param string
	 * @param exception
	 */
	public GOALGetPerceptsFailedException(String string, Exception exception) {
		super(string, exception);
	}

	/**
	 * @param string
	 * @param cause
	 */
	public GOALGetPerceptsFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param string
	 */
	public GOALGetPerceptsFailedException(String string) {
		super(string);
	}
}
