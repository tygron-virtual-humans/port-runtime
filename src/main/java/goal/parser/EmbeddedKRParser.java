package goal.parser;

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Query;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.tools.errorhandling.exceptions.InvalidSemanticsException;
import goal.tools.errorhandling.exceptions.ParserException;
import goal.util.ImportCommand;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;

/**
 *
 * This is the interface for embedded KR language parsers. The embedded parser
 * must be compatible with the ANTLR parser that GOAL uses, since we are using
 * hard linking of the parsers to the
 * {@link org.antlr.runtime.ANTLRReaderStream} that the constructor takes.
 *
 * The constructor usually takes this form:<br>
 * <code>public EmbeddedXXXParser(ANTLRReaderStream cs, File sourceFile)</code>
 *
 * <h1>Error handling</h1>
 * <p>
 * By default, antlr parsers catch and try to recover from
 * {@link org.antlr.runtime.RecognitionException}. However, embedded parsers
 * operate on a stream that fits more widely in a GOAL parser. Therefore, the
 * parser is expected to interpret unexpected tokens as if the end-of-input was
 * encountered. CHECK lexer hacks to deal with '}' '.' and ')'??
 *
 * <p>
 * GOAL provides a number of extensions to such
 * {@link org.antlr.runtime.RecognitionException}, eg {@link ParserException}
 * and {@link InvalidSemanticsException}. Such exceptions are to be caught by
 * the embedded parser (by setting up ANTLR properly).
 * {@link InvalidSemanticsException} occurs usually as a post-check on a parsed
 * segment and therefore often is generated <em>after</em> the ANTLR parser has
 * done its job. In that case this exception must still be caught and reported.
 *
 *
 * @author W.Pasman 15sep2011
 *
 */
public interface EmbeddedKRParser {

	/**
	 * Fetch the original input stream
	 *
	 * @return The used ANTLReaderStream
	 */
	public ANTLRReaderStream getSourceStream();

	/**
	 * Fetch all parser errors that were accumulated while parse() commands were
	 * called
	 *
	 * @return list of Parser exceptions that occured.
	 */
	public List<ParserException> getParserErrors();

	/**
	 * Fetch all lexer errors that were accumulated while parse() commands were
	 * called
	 *
	 * @return list of Lexer exceptions that occured.
	 */
	public List<ParserException> getLexerErrors();

	/**
	 * Get a list of the lexer's tokens
	 *
	 * @return list of token strings. This is deep into ANTLR, chec the manuals
	 */
	public String[] getTokenNames();

	/**
	 * Get all the import commands that this parser encountered during parse.
	 *
	 * @return list of import commands. CHECK should this be done this way???
	 */
	public ArrayList<ImportCommand> getAllImports();

	/**
	 * Parse a full program section (for the knowledge section or belief
	 * section)
	 *
	 * @return List of DatabaseFormulas
	 */
	public List<DatabaseFormula> parseProgram();

	/**
	 * Parse a section containing update terms that are suited for the goal
	 * section in GOAL>
	 *
	 * @return set of Updates
	 */
	public ArrayList<Update> parseGoals();

	/**
	 * Parse a single GOAL update
	 *
	 * @return single Update
	 */
	public Update parseGoal();

	/**
	 * Parse a belief update
	 *
	 * @return Update
	 */
	public Update parseBelief();

	/**
	 * Parse a possibly empty belief update (for the post condition.
	 *
	 * @return null if empty.
	 */
	public Update parseBeliefOrEmpty();

	/**
	 * parse a query
	 *
	 * @return Query
	 */
	public Query parseQuery();

	/**
	 * Parse a possibly empty query (for pre condition?)
	 *
	 * @return Query
	 */
	public Query parseQueryOrEmpty();

	/**
	 * Parse mood, for send
	 *
	 * @return mood formula. This is bit of hack, check details in the Prolog
	 *         parser code
	 */
	public DatabaseFormula parseMood();

	/**
	 * get list of terms
	 *
	 * @return a list of terms.
	 */
	public List<Term> parseTerms();

	/**
	 * Get a term.
	 *
	 * @return the variable as a Term.
	 */
	public Term parseTerm();
}