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

package goal.util;

import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * Wrapper for the data from an #import-command. Stores the path as stored in
 * the agent file, as well as the actual referenced file, along with a record of
 * where the command is located in the agent file.
 *
 * @author N.Kraayenbrink
 */
public class ImportCommand extends ParsedObject {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6585210374293631622L;
	private final String givenPath;
	private File importedFile;

	/**
	 * DOC
	 *
	 * @param baseFile
	 * @param givenPath
	 * @param extension
	 * @param source
	 */
	public ImportCommand(File baseFile, String givenPath, Extension extension,
			InputStreamPosition source) {
		super(source);
		this.givenPath = givenPath;
		if (givenPath.endsWith(extension.getExtension())) {
			this.importedFile = new File(FilenameUtils.concat(
					baseFile.getParent(), givenPath));
		} else {
			this.importedFile = new File(FilenameUtils.concat(
					baseFile.getParent(), givenPath + extension.getExtension()));
		}
	}

	/**
	 * @return The actual referenced file.
	 */
	public File getFile() {
		return this.importedFile;
	}

	/**
	 * @return The (relative) path to the file as provided in the agent file.
	 */
	public String getGivenPath() {
		return this.givenPath;
	}

	@Override
	public String toString() {
		return "#import \"" + this.givenPath + "\".";
	}
}
