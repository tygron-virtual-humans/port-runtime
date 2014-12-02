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

import java.io.File;

/**
 * Extensions of files recognized by GOAL.
 * 
 * TODO: use these instead of string constants in code, rethink and relate with
 * file node enum type. All constants should now have been replaced by this
 * enum.
 * 
 * @author K.Hindriks
 */
public enum Extension {
	MAS(".mas2g"), GOAL(".goal"), PROLOG(".pl"), MODULES(".mod2g"), LEARNING(
			".lrn"), TEST(".test2g");

	private String extension;

	private Extension(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return this.extension;
	}

	/**
	 * Determines the {@link Extension} of a certain file.
	 * 
	 * @param filename
	 *            The name or path to a file to get the extension of
	 * @return The {@link Extension} of the given file, or <code>null</code> if
	 *         the file has no known extension.
	 */
	public static Extension getFileExtension(String filename) {
		String lower = filename.toLowerCase();
		for (Extension ex : Extension.values()) {
			if (lower.endsWith(ex.getExtension())) {
				return ex;
			}
		}
		return null;
	}

	/**
	 * Determines the {@link Extension} of a certain file.
	 * 
	 * @param file
	 *            The file to get the extension of.
	 * @return The {@link Extension} of the given file, or <code>null</code> if
	 *         the file has no known extension.
	 */
	public static Extension getFileExtension(File file) {
		return Extension.getFileExtension(file.getName());
	}

	@Override
	public String toString() {
		return this.getExtension();
	}

}
