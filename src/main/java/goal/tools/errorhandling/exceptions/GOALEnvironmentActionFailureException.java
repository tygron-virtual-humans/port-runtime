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

import eis.exceptions.ActException;

/**
 * Use this when the environment failed to perform an action and throws an
 * {@link ActException#FAILURE} or an {@link ActException#NOTSPECIFIC}. In these
 * cases we currently have no useful information about what has gone wrong but
 * the cause of the problem should not be something we did wrong. We therefore
 * should handle this case gently from an agent perspective and not start
 * throwing {@link GOALActionFailedException}s which would kill the agent that
 * requested to perform the action.
 *
 * @author K.Hindriks
 */
public class GOALEnvironmentActionFailureException extends GOALUserError {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = 6909663255788741525L;

	public GOALEnvironmentActionFailureException(String message) {
		super(message);
		assert message != null;
	}

	public GOALEnvironmentActionFailureException(String message, Throwable cause) {
		super(message, cause);
		assert message != null;
	}

	@Override
	public String toString() {
		return "Environment failed to perform action: " + this.getMessage();
	}
}
