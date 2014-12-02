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

package goal.core.mas;

import eis.iilang.Parameter;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the parsed environment information from the MAS file.
 *
 * Contains info on the environment to be used.
 *
 * TODO: add support for multiple environments (.jar files, etc) LATER; mixing
 * the logic where we assume only a single environment with that where we allow
 * for multiple environments is confusing...
 *
 * @author W.Pasman 1mar2010
 * @modified K.Hindriks
 */

public class EnvironmentInfo extends ParsedObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 9213631222046223053L;
	/**
	 * Environment reference. If reference is to a file, it should be a .jar
	 * file.
	 */
	private String environmentName = null;
	/**
	 * DOC
	 */
	private File environmentFile = null;
	/**
	 * The initialization parameters which are to be sent to the environment for
	 * initialization.
	 */
	private Map<String, Parameter> initParameters = null;

	/**
	 * @param source
	 *            The source code location of this info
	 */
	public EnvironmentInfo(InputStreamPosition source) {
		super(source);
	}

	/**
	 * Returns the name of the environment.
	 *
	 * @return The name of the environment, or {@code null} if no name is
	 *         available (no environment set). TODO: CHeck: This object should
	 *         not be created in case there is no environment section in a MAS
	 *         program...
	 */
	protected String getName() {
		return this.environmentName;
	}

	/**
	 * Sets the name of the environment. Only allowed when there also is a jar
	 * file.
	 *
	 * @param name
	 *            The environment name.
	 * @throws IllegalArgumentException
	 *             if name is provided in an inappropriate situation FIXME:
	 *             parameter is never used??
	 */
	public void setName(String name) {
		if (environmentName == null) {
			throw new IllegalArgumentException(
					"Cannot set name: no environment provided.");
		}
		String envname = environmentName;
		if (!envname.endsWith(".jar")) {
			throw new IllegalArgumentException(
					"Cannot set name: the environment '"
							+ envname
							+ "' is not a .jar file and therefore is loaded and named remotely");
		}
	}

	/**
	 * Adds a given string (should be a .jar file name) to the list of
	 * environments.
	 *
	 * @param newjar
	 *            DOC
	 * @todo Why is this called 'addJar' instead of 'setJar'? And isn't setName
	 *       the same thing only with additional (uneccessary) checks? -Vincent
	 */
	public void addJar(String newjar) {
		this.environmentName = newjar;
	}

	/**
	 * @return The environment file.
	 */
	public File getEnvironmentFile() {
		return this.environmentFile;
	}

	/**
	 * @param environmentFile
	 *            The environment file.
	 * @todo This function is confusing in respect to addJar (or setJar); this
	 *       is only used as a temporary store in the MASProgramValidator?!
	 */
	public void setEnvironmentFile(File environmentFile) {
		this.environmentFile = environmentFile;
	}

	/**
	 * Returns the environment init command as specified in the mas file, or
	 * plain 'init' command if nothing has been specified.
	 *
	 * @return The full map of init commands
	 */
	public Map<String, Parameter> getInitParameters() {
		if (initParameters != null) {
			return initParameters;
		} else {
			return new HashMap<>(0);
		}
	}

	/**
	 * Sets the environment initialization command.
	 *
	 * @param params
	 *            The command's contents.
	 */
	public void setInitParameters(Map<String, Parameter> params) {
		initParameters = params;
	}

	@Override
	public String toString() {
		return "Environments[" + environmentName + "," + initParameters + "]";
	}
}