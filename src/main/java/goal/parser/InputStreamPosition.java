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

package goal.parser;

import goal.tools.debugger.BreakPoint;

import java.io.File;
import java.io.Serializable;

/**
 * Describes a single position in a file or stream by saving its line number and
 * position on that line.
 *
 * @author N.Kraayenbrink
 *
 * @modified W.Pasman 27jul10 to implement Serializable so that this can be sent
 *           over the middleware
 */
public class InputStreamPosition implements Comparable<InputStreamPosition>,
Serializable {

	/** Auto-generated serial version ID */
	private static final long serialVersionUID = 1639395508526942987L;

	/**
	 * On what line the input stream's pointer is located (0-based)
	 */
	private int lineNumber;
	/**
	 * At which character on the given line the input stream's pointer is
	 * located (0-based)
	 */
	private final int characterPosition;
	/**
	 * The current (token) startindex of the stream's pointer
	 */
	private int startIndex;
	/**
	 * The current (token) stopindex of the stream's pointer
	 */
	private int stopIndex;
	/**
	 * The name or description of the file or other stream this
	 * {@link InputStreamPosition} points in.
	 */
	private final File sourceFile;

	/**
	 * An identifier for parsed objects. Can be set, such that it can be used
	 * for efficiently handling user-defined breakpoints. The default value is
	 * -1. This MUST be set properly for all objects used to define user-defined
	 * {@link BreakPoint}s.
	 */
	private int id = -1;

	public InputStreamPosition(org.antlr.v4.runtime.Token start,
			org.antlr.v4.runtime.Token stop, File sourceFile) {
		this(start.getLine(), start.getCharPositionInLine(), start
				.getStartIndex(), stop.getStopIndex(), sourceFile);
	}

	public InputStreamPosition(org.antlr.runtime.Token token, int index,
			File sourceFile) {
		this(token.getLine(), token.getCharPositionInLine(), index, token
				.getText() == null ? index : index + token.getText().length(),
						sourceFile);
	}

	public InputStreamPosition(int lineNumber, int characterPosition,
			int startIndex, int stopIndex, File sourceFile) {
		this.sourceFile = sourceFile;
		this.lineNumber = lineNumber;
		this.characterPosition = characterPosition;
		this.startIndex = startIndex;
		this.stopIndex = stopIndex;
	}

	/**
	 * @return The line number this marker marks.
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * @return The index of the character in its line that this marker marks.
	 */
	public int getCharacterPosition() {
		return this.characterPosition;
	}

	/**
	 * @return The (token) startindex of the character that this marker marks.
	 */
	public int getStartIndex() {
		return this.startIndex;
	}

	/**
	 * @return The (token) stopindex of the character that this marker marks.
	 */
	public int getStopIndex() {
		return this.stopIndex;
	}

	/**
	 * @return The file this {@link InputStreamPosition} is located in. May be
	 *         null if it was generated from a string.
	 */
	public File getSourceFile() {
		return this.sourceFile;
	}

	/**
	 * Sets the numerical identifier for this {@link InputStreamPosition}. If
	 * this is not called, the value will be -1.
	 *
	 * @param id
	 *            The numerical identifier
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * @return The numerical identifier for this {@link InputStreamPosition}.
	 *         The default value is -1.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * @return A short representation of this {@link InputStreamPosition}. The
	 *         returned value is of the format <code>L&lt;LINE&gt;,
	 * C&lt;COL&gt;</code>.
	 */
	public String toShortString() {
		return "L" + this.lineNumber + ", C" + this.characterPosition;
	}

	/**
	 * Determines if this {@link InputStreamPosition} is located after the given
	 * location. WARNING: the line number is 0-based, while the line number in
	 * this {@link InputStreamPosition} is 1-based.
	 *
	 * @param file
	 *            The referenced file.
	 * @param lineNumber
	 *            The referenced line number.
	 * @return {@code true} iff this {@link InputStreamPosition} is located in
	 *         the given file, after or at the start of the given line.
	 */
	public boolean definedAfter(File file, int lineNumber) {
		if (!file.equals(this.sourceFile)) {
			return false;
		}
		return this.lineNumber >= lineNumber + 1;
	}

	public InputStreamPosition add(InputStreamPosition other) {
		if (other != null) {
			this.lineNumber += other.getLineNumber() - 1;
			this.startIndex += other.getStartIndex();
			this.stopIndex += other.getStopIndex();
		}
		return this;
	}

	public InputStreamPosition end(InputStreamPosition end) {
		if (end != null) {
			this.stopIndex = end.getStopIndex();
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("line ");
		builder.append(this.lineNumber);
		builder.append(", position ");
		builder.append(this.characterPosition);
		if (this.sourceFile != null) {
			builder.append(" in ");
			builder.append(this.sourceFile.getName());
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		int hash = (31 * lineNumber) << 16 + characterPosition;
		if (this.sourceFile != null) {
			hash += this.sourceFile.hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (!(other instanceof InputStreamPosition)) {
			return false;
		}
		InputStreamPosition that = (InputStreamPosition) other;

		if (this.lineNumber != that.lineNumber) {
			return false;
		} else if (this.characterPosition != that.characterPosition) {
			return false;
		}

		if (this.sourceFile == null) {
			return that.sourceFile == null;
		} else {
			return this.sourceFile.getAbsoluteFile().equals(
					that.sourceFile.getAbsoluteFile());
		}
	}

	@Override
	public int compareTo(InputStreamPosition o) {
		// ASSUMES the two ISPs being compared are in the same file.

		// first order by line number
		if (this.lineNumber < o.lineNumber) {
			return -1;
		} else if (this.lineNumber > o.lineNumber) {
			return 1;
		} else
			// then by character position
			if (this.characterPosition < o.characterPosition) {
				return -1;
			} else if (this.characterPosition > o.characterPosition) {
				return 1;
			} else {
				return 0;
			}
	}

}
