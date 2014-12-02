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

package goal.tools.debugger;

import goal.core.runtime.service.agent.AgentService;
import goal.tools.errorhandling.exceptions.GOALBug;

import java.io.File;

/**
 * stores information about a breakpoint that was placed in the code manually by
 * the user. These are shown in the editor with the 'markers'.
 *
 * @author W.Pasman 18apr2012
 *
 */
public class BreakPoint {
	/**
	 * The file that this breakpoint is in
	 */
	private final File file;
	/**
	 * The type of this breakpoint
	 */
	private final Type type;
	/**
	 * The line on which this breakpoint is set
	 */
	private final int linenumber;

	public enum Type {
		/**
		 * always stop when breakpoint reached
		 */
		ALWAYS,
		/**
		 * stop only when the condition holds. Used in GOAL for
		 * if-condition-then rules
		 */
		CONDITIONAL;
	}

	/**
	 * Creates new breakpoint. You still need to tell the relevant services
	 * (e.g., {@link AgentService}, about it.
	 *
	 * @param file
	 *            is the file that this breakpoint is in.
	 * @param linenumber
	 *            is the line number where the breakpoint is. 0 is first line.
	 * @param type
	 *            is the type of this breakpoint.
	 */
	public BreakPoint(File file, int linenumber, Type type) {
		if (file == null || type == null) {
			throw new GOALBug("file and type should be not null");
		}
		this.file = file;
		this.type = type;
		this.linenumber = linenumber;
	}

	/**
	 * get the type of this breakpoint
	 *
	 * @return The breakpoint type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * get the line number where this breakpoint is placed
	 *
	 * @return line holding this breakpoint. 0 is first line.
	 */
	public int getLine() {
		return linenumber;
	}

	/**
	 * get the file that this breakpoint is in
	 *
	 * @return the file that this breakpoint is in
	 */
	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return "Breakpoint[" + file.getName() + "," + linenumber + "," + type
				+ "]";
	}
}