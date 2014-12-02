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

package goal.core.kr;

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Update;
import goal.tools.errorhandling.exceptions.ParserException;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;

public interface Parser {

	/**
	 * Parses an update from a given stream. Expects an ANTLR reader stream.
	 *
	 * @param stream
	 *            An ANTLR reader stream.
	 * @return Update The {@link Update} obtained by parsing the stream input.
	 * @throws ParserException
	 *             If parsing failed for some reason; use {@link #getErrors()}
	 *             to get all errors.
	 */
	public Update parseUpdate(ANTLRReaderStream stream) throws ParserException;

	/**
	 * Parses the KR specific content of various GOAL program components from a
	 * given stream. Expects an ANTLR reader stream.
	 *
	 * @param stream
	 *            An ANTLR reader stream
	 * @return A list of {@link DatabaseFormula}s.
	 * @throws ParserException
	 *             If parsing failed for some reason; use {@link #getErrors()}
	 *             to get all errors.
	 */
	ArrayList<DatabaseFormula> parseProgram(ANTLRReaderStream stream)
			throws ParserException;

	/**
	 * @return The list of all errors that occurred while parsing.
	 */
	List<ParserException> getErrors();
}