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

import goal.core.kr.KRFactory;
import goal.core.kr.KRlanguage;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.PlatformManager;

import java.io.File;

/**
 * An AgentFile contains one line in the MAS file agentfiles section, like this
 * <code>    "tictactoe.goal" [name = player, language=K].</code> The quoted
 * string refers to the filename. But it is not a full qualified filename, it
 * still needs to be resolved. See {@link #resolvePath(String)}. The name=X part
 * is an alias for this file, to be used in the launchpolicy section. The
 * language=K part specifies which KRLanguage is used in the agent.
 *
 * <p>
 * Note that the filename may not be resolvable. The file can be in multiple
 * locations (see {@link PlatformManager#resolveFileReference(String, String)}
 * .Therefore the unresolved string as given in the MAS file is stored here, as
 * well as the MAS file directory. Resolving is done on an as-needed basis.
 *
 *
 * An agent file encapsulates
 * <ul>
 * <li>The agent (.goal) file,</li>
 * <li>The name of the agent, and</li>
 * <li>The associated knowledge representation language.</li>
 * </ul>
 *
 * @author Tristan Behrens
 * @modified K.Hindriks
 * @modified W.Pasman 16jan2014 #2858
 */
public class AgentFile extends ParsedObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -8921950408599912150L;
	/**
	 */

	/**
	 * Directory of the MAS. FIXME use File. We use string now because
	 * {@link #resolvePath(String)} takes a String.
	 */
	private String masDirectory = "";

	/**
	 * Path to agent (.goal) file as specified in the MAS. To be resolved.
	 */
	private String path = null;
	/**
	 * The name of the agent. This is just a label/alias for the file. it does
	 * not tell us which file to use.
	 * <p>
	 * Used instead of full paths while instantiating agents for the sake of
	 * convenience. This reference is used in launches and launch-rules.
	 * </p>
	 */
	private String name = null;
	/**
	 * FIXME: Why does this class need a KRlanguage?? The knowledge
	 * representation language. Default is SWI Prolog.
	 */
	private KRlanguage krlang = KRFactory.getDefaultLanguage();

	/**
	 * Creates a new AgentFile descriptor.
	 *
	 * @param filename
	 *            the given filename in the MAS. May be absolute or relative
	 *            path.
	 * @param masdir
	 *            the directory of the MAS itself that contains this reference.
	 *            Needed to resolve the filename.
	 * @param source
	 *            Where in the .mas file the definition of the agent file
	 *            starts.
	 */
	public AgentFile(String filename, String masdir, InputStreamPosition source) {
		super(source);
		if (filename == null || masdir == null) {
			throw new NullPointerException("filename or masdir is null");
		}
		path = filename;
		masDirectory = masdir;
	}

	/**
	 * Returns the agent file. May return null if the file cannot be found.
	 *
	 * @return The agent file, if it exists, {@code null} otherwise.
	 */
	public File getAgentFile() {
		return PlatformManager.resolveFileReference(masDirectory, path);
	}

	/**
	 * The name of the agent. This is just an alias for the file, to be used in
	 * the launchrules section.
	 *
	 * @return The agent name alias.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the path to the agent file as specified in the MAS. This may be a
	 * relative or absolute path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the agent name.
	 *
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the KR technology specified in the MAS file, if any; otherwise it
	 * returns the default KR language.
	 *
	 * @return The KR technology associated with this file.
	 */
	public KRlanguage getKRLang() {
		if (krlang != null) {
			return krlang;
		} else {
			return KRFactory.getDefaultLanguage();
		}
	}

	/**
	 * Sets the KR-language.
	 *
	 * @param krlang
	 *            the language
	 */
	public void setKRLang(KRlanguage krlang) {
		this.krlang = krlang;
	}

	@Override
	public String toString() {
		return "AgentFile[" + path + "," + name + ","
				+ (krlang != null ? krlang.getName() : "-") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (this.name != null) {
			result = result * prime + this.name.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AgentFile other = (AgentFile) obj;
		String otherName = other.getName();
		if (this.name == null || otherName == null) {
			return false;
		} else {
			return this.name.equalsIgnoreCase(otherName);
		}
	}
}
