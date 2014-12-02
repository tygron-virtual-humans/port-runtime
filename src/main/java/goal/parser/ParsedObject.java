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

import java.io.Serializable;

/**
 * <p>
 * Object that originates from a parser. E.g., the result of parsing a file.
 * </p>
 * <p>
 * Saves a marker where the object was defined in the parser's stream. This
 * enables a {@link Validator} to indicate where a parsing issue was found.
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman 27jul10 to implement Serializable so that Actions can be
 *           sent over middleware.
 * @modified K.Hindriks
 */
public class ParsedObject implements Serializable, Comparable<ParsedObject>,
		IParsedObject {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 6954798838175069605L;
	/**
	 * The (start) location in the input stream handled by the parser where the
	 * object was found.
	 */
	private InputStreamPosition source;

	/**
	 * Creates a new {@link ParsedObject}.
	 *
	 * @param source
	 *            Where in its source stream the definition of the new object
	 *            starts. May be null if not created by a parser.
	 */
	public ParsedObject(InputStreamPosition source) {
		this.source = source;
	}

	/**
	 * Sets the numerical identifier for this {@link ParsedObject}. If this is
	 * not called, the value will be -1. The ID is propagated to the underlying
	 * {@link InputStreamPosition}.
	 *
	 * @param id
	 *            The numerical identifier.
	 */
	public void setID(int id) {
		if (this.source != null) {
			this.source.setID(id);
		}
	}

	/**
	 * @return The numerical identifier for this {@link ParsedObject}. The
	 *         default value is -1. The value is retrieved from the underlying
	 *         {@link InputStreamPosition}.
	 */
	public int getID() {
		if (this.source == null) {
			return -1;
		} else {
			return this.source.getID();
		}
	}

	/**
	 * Returns the source code location in the input stream of the object that
	 * was parsed.
	 *
	 * @return The {@link InputStreamPosition}, i.e., location of this object's
	 *         definition in the stream where it was found. May return
	 *         {@code null} which usually indicates that the object has been
	 *         inserted manually, and not by a parser.
	 */
	@Override
	public InputStreamPosition getSource() {
		return this.source;
	}

	/**
	 * Sets the location of this object's definition in the stream it was
	 * defined in. Intended use is only from within GOALParser.
	 */
	@Override
	public void setSource(InputStreamPosition source) {
		this.source = source;
	}

	@Override
	public int compareTo(ParsedObject other) {
		// the object without source should be after the object with source
		if (this.source == null) {
			if (other.getSource() == null) {
				return 0;
			} else {
				return 1;
			}
		}
		if (other.getSource() == null) {
			return -1;
		}
		return this.getSource().compareTo(other.getSource());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		ParsedObject other = (ParsedObject) obj;
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		return true;
	}

}
