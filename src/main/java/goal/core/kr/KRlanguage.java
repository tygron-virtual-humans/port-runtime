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
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalState;
import goal.core.program.GOALProgram;
import goal.core.program.dependencygraph.DependencyGraph;
import goal.core.program.expressiongraph.ExpressionGraph;
import goal.core.program.literals.MentalStateCond;
import goal.parser.EmbeddedKRParser;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.exceptions.ExtendedParserException;
import goal.tools.errorhandling.exceptions.GOALParseException;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;

/**
 * The knowledge representation (KR) service. A knowledge representation service
 * provides the following:
 * <ul>
 * <li>Its name.</li>
 * <li>A method for initializing the KR to enable it to process a GOAL agent
 * program.</li>
 * <li>A method for creating a database.</li>
 * <li>A method for resetting the KR service.</li>
 * <li>A compiler for compiling GOAL rules into the KR language.</li>
 * <li>A parser for parsing expression from the KR language.</li>
 * <li>A method for parsing individual updates.</li>
 * <li>A few other language related methods.</li>
 * </ul>
 *
 * @author K.Hindriks
 * @modified W.Pasman 6sep2011 removed enumerated KR language set.
 * @modified W.Pasman 20mar2012 added reset, changed to interface.
 * @modified K.Hindriks
 */
public interface KRlanguage {

	/**
	 * @return the name of the KR service.
	 */
	String getName();

	/**
	 * Performs knowledge representation specific initializations BEFORE the
	 * knowledge representation technology can be used by a particular GOAL
	 * agent to set up its mental state, if needed.
	 *
	 * @param program
	 *            the GOAL agent program that contains information necessary for
	 *            initialization.
	 * @param agentname
	 *            the name of the GOAL agent
	 * @throws KRInitFailedException
	 *             if initialization of kr technology fails.
	 */
	void initialize(GOALProgram program, String agentname)
			throws KRInitFailedException;

	/**
	 * Creates new database using the content. It is the responsibility of the
	 * KR technology to differentiate databases (e.g. by associating unique
	 * identifiers with a database).
	 *
	 * @param type
	 *            database type, i.e. belief base, goal base, mailbox, percept
	 *            base.
	 * @param content
	 *            set of formulas to be inserted to database.
	 * @param agent
	 *            agent that requests the database. This name is used as
	 *            database identifier, and should have a unique name for each
	 *            new agent.
	 *
	 * @return database that has been created.
	 *
	 * @throws KRInitFailedException
	 */
	Database makeDatabase(BASETYPE type, Collection<DatabaseFormula> content,
			String agent) throws KRInitFailedException;

	/**
	 * Reset the language. Clears the databases and stops all queries.
	 *
	 * @throws KRInitFailedException
	 */
	void reset() throws KRInitFailedException;

	/**
	 * @return KRCompiler, or null if not available. If available, GOAL must use
	 *         it to compile rules.
	 */
	KRCompiler getCompiler();

	/**
	 * get a parser for the embedded KR language and initialize the parser for
	 * use.
	 *
	 * @param cs
	 *            is the {@link ANTLRReaderStream} to be used for parsing,
	 * @param sourceFile
	 *            is the source file
	 * @throws ExtendedParserException
	 *             if initialization fails
	 *
	 * @return the parser
	 */
	EmbeddedKRParser getParser(ANTLRReaderStream cs, File sourceFile)
			throws ExtendedParserException;

	/**
	 * <p>
	 * Convert string to Update. Used at run time to verify that percepts
	 * received from the environment can be inserted or retracted into the
	 * percept base.
	 * </p>
	 * <p>
	 * Performs all relevant checks to verify that the string provided is an
	 * update.
	 * </p>
	 *
	 * @param pStr
	 *            to be converted to DatabaseFormula
	 * @return Update corresponding to given string.
	 * @throws GOALParseException
	 *             if string is no good update in our language.
	 */
	Update parseUpdate(String pStr) throws GOALParseException;

	/**
	 * Needed in {@link MentalStateCond#evaluate(MentalState, SteppingDebugger)}
	 * and in
	 * {@link MentalState#queryLiteral(goal.core.program.literals.MentalLiteral, SteppingDebugger)}
	 * .
	 *
	 * @return The empty substitution.
	 */
	public Substitution getEmptySubstitution();

	/**
	 * Creates a language-specific Term containing an (ordered) list of Terms.
	 * Assumes the given Terms are for this KR language.
	 *
	 * @param termList
	 *            The list of Terms to convert to a single Term.
	 *
	 * @return A Term containing the given Terms as a list.
	 */
	Term makeList(List<Term> termList);

	/**
	 * Creates an initial, language-specific {@link ExpressionGraph}.
	 *
	 * @throws UnsupportedOperationException
	 *             When this KRLanguage does not support building a graph (or
	 *             the functionality has been turned off).
	 * @deprecated
	 */
	@Deprecated
	ExpressionGraph makeExpressionGraph();

	/**
	 * Creates an empty, language specific {@link DependencyGraph}.
	 *
	 * @return Empty dependency graph for this {@link KRlanguage}.
	 */
	DependencyGraph<?> createDependencyGraph();

}
