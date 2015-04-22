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

/**
 * This exception is thrown when an action can not be executed due to a user
 * error.
 * 
 * @author W.Pasman 21jul11
 * @modified K.Hindriks now extends GOALRuntimeErrorException.
 * @modified W.Pasman 16feb2015 now extends GOALException as this is (should be)
 *           a user error #3013.
 *
 */
public class GOALActionFailedException extends GOALException {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 953890011844171754L;

	/**
	 * @param string
	 * @param exception
	 */
	public GOALActionFailedException(String string, Exception exception) {
		super(string, exception);
	}

	/**
	 * @param string
	 * @param exception
	 */
	public GOALActionFailedException(String string, Throwable exception) {
		super(string, exception);
	}

	/**
	 * @param string
	 */
	public GOALActionFailedException(String string) {
		super(string);
	}
}
