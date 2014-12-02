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

package goal.tools.errorhandling.exceptions;

import goal.parser.InputStreamPosition;

import java.io.File;

import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.MissingTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.UnwantedTokenException;

/**
 * An extension to {@link RecognitionException}, where the input stream may be
 * set after creation. Also a single reason may be given why the exception
 * occurred.
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman namechange as this is not a GOAL Exception.
 */
// this is not a GOALException; since we want them to be RecognitionExceptions
// and always have the same severity.
public class ParserException extends RecognitionException {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = -645378627711874741L;

	/**
	 * The reason why this exception occurred.
	 */
	private final String reason;
	/**
	 * A flag indicating if info about the source of the error, using
	 * {@link #setInputStream(IntStream)}, has been set. Used by
	 * {@link #toString()}.
	 */
	private boolean inputInfoSet = false;
	/**
	 * Where the exception came from. Usually either 'GOAL' or 'Prolog'.
	 */
	private String sourceType = "GOAL";
	/**
	 * From what part of the parsing process the exception came from. Should be
	 * either parser or lexer.
	 */
	protected String parserLexer = "parser";
	/**
	 * The source of this error. Will not be printed when null.
	 */
	protected File sourceFile = null;

	/**
	 * Creates a new {@link ParserException} for some reason, without any link
	 * to where the source of the error lies. Info may be added later by calling
	 * {@link #setInputStream(IntStream)}.
	 *
	 * @param reason
	 *            Why the new exception occurred.
	 */
	public ParserException(String reason) {
		this.reason = reason;
	}

	/**
	 * Creates a new {@link ParserException} for some reason, without any link
	 * to where the source of the error lies. Info may be added later by calling
	 * {@link #setInputStream(IntStream)}.
	 *
	 * @param reason
	 *            Why the new exception occurred.
	 * @param sourceType
	 *            The type of the source of the exception. Usually 'GOAL' or
	 *            'Prolog'. Used when converting the exception to a string.
	 *            Default is 'GOAL'.
	 */
	public ParserException(String reason, String sourceType) {
		this(reason);
		this.sourceType = sourceType;
	}

	/**
	 * Creates a new {@link ParserException} for some reason, with info where
	 * the source of the error lies.
	 *
	 * @param input
	 *            A reference to the faulty stream, at the position of the error
	 *            made by the user.
	 * @param reason
	 *            Why the new exception occurred.
	 */
	public ParserException(IntStream input, String reason) {
		this(reason);
		if (input != null) {
			this.extractInfoFromInput(input);
		}
	}

	/**
	 * Creates a new {@link ParserException} for some reason, with info where
	 * the source of the error lies.
	 *
	 * @param source
	 *            A pointer to where in the input stream this exception
	 *            originated.
	 * @param reason
	 *            Why the new exception occurred.
	 */
	public ParserException(InputStreamPosition source, String reason) {
		this(reason);
		if (source != null) {
			this.extractInfoFromInput(source);
		}
	}

	/**
	 * Creates a new {@link ParserException} for some reason, with info where
	 * the source of the error lies.
	 *
	 * @param input
	 *            A reference to the faulty stream, at the position of the error
	 *            made by the user.
	 * @param reason
	 *            Why the new exception occurred.
	 * @param sourceType
	 *            The type of the source of the exception. Usually 'GOAL' or
	 *            'Prolog'. Used when converting the exception to a string.
	 *            Default is 'GOAL'.
	 */
	public ParserException(IntStream input, String reason, String sourceType) {
		this(input, reason);
		this.sourceType = sourceType;
	}

	/**
	 * Creates a new {@link ParserException} for some reason, with info where
	 * the source of the error lies.
	 *
	 * @param source
	 *            A pointer to where in the input stream this exception
	 *            originated.
	 * @param reason
	 *            Why the new exception occurred.
	 * @param sourceType
	 *            The type of the source of the exception. Usually 'GOAL' or
	 *            'Prolog'. Used when converting the exception to a string.
	 *            Default is 'GOAL'.
	 */
	public ParserException(InputStreamPosition source, String reason,
			String sourceType) {
		this(source, reason);
		this.sourceType = sourceType;
	}

	/**
	 * Creates a {@link ParserException} based on a {@link RecognitionException}
	 * .<br>
	 * Main use is to print a RE in a similar way as a GOAL RE.
	 *
	 * @param re
	 *            The {@link RecognitionException} to base the new exception on.
	 * @param tokenNames
	 *            The names of the tokens the given RE is referencing to.
	 */
	public ParserException(RecognitionException re, String[] tokenNames) {
		this(re.input, ParserException.getReasonFrom(re, tokenNames));
		// the input stream may not be at the correct position any more; '
		// copy the values found at creation.
		this.copyInfoFrom(re);
	}

	/**
	 * Creates a {@link ParserException} based on a {@link RecognitionException}
	 * .<br>
	 * Main use is to print a RE in a similar way as a GOAL RE.
	 *
	 * @param re
	 *            The {@link RecognitionException} to base the new exception on.
	 * @param tokenNames
	 *            The names of the tokens the given RE is referencing to.
	 * @param sourceType
	 *            The type of the source of the exception. Usually 'GOAL' or
	 *            'Prolog'. Used when converting the exception to a string.
	 *            Default is 'GOAL'.
	 */
	public ParserException(RecognitionException re, String[] tokenNames,
			String sourceType) {
		this(re, tokenNames);
		this.sourceType = sourceType;
	}

	/**
	 * Creates a {@link ParserException} based on a {@link RecognitionException}
	 * .<br>
	 * Main use is to print a RE in a similar way as a GOAL RE.
	 *
	 * @param re
	 *            The {@link RecognitionException} to base the new exception on.
	 * @param tokenNames
	 *            The names of the tokens the given RE is referencing to.
	 * @param sourceType
	 *            The type of the source of the exception. Usually 'GOAL' or
	 *            'Prolog'. Used when converting the exception to a string.
	 *            Default is 'GOAL'.
	 * @param parserLexer
	 *            From what part of the parsing process the error came. Should
	 *            be either parser or lexer.
	 */
	public ParserException(RecognitionException re, String[] tokenNames,
			String sourceType, String parserLexer) {
		this(re, tokenNames);
		this.sourceType = sourceType;
		this.parserLexer = parserLexer;
	}

	/**
	 * Extracts things like line number and character index from the input. Does
	 * this by creating a new {@link RecognitionException} and extracting the
	 * info from there.
	 *
	 * @param input
	 *            The input stream at the position causing this exception
	 */
	private void extractInfoFromInput(IntStream input) {
		RecognitionException rex = new RecognitionException(input);
		this.input = input;
		this.copyInfoFrom(rex);

		this.inputInfoSet = true;
	}

	/**
	 * Extracts the line number and character position from a given pointer
	 *
	 * @param source
	 *            A pointer to where in the input stream this exception
	 *            originated
	 */
	private void extractInfoFromInput(InputStreamPosition source) {
		this.input = null;
		this.charPositionInLine = source.getCharacterPosition();
		this.line = source.getLineNumber();
		this.sourceFile = source.getSourceFile();

		this.inputInfoSet = true;
	}

	/**
	 * Copies info about the line and character position from an ANTLR
	 * recognition exception, so we do not have to try and do it ourselves.
	 *
	 * @param rex
	 *            The {@link RecognitionException} that possibly contains
	 *            information about the position of an error.
	 */
	protected void copyInfoFrom(RecognitionException rex) {
		this.index = rex.index;
		this.token = rex.token;
		this.line = rex.line;
		this.charPositionInLine = rex.charPositionInLine;
		this.c = rex.c;
		this.node = rex.node;
		this.approximateLineInfo = rex.approximateLineInfo;
	}

	/**
	 * @return The reason why this exception occurred.
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Sets the input stream for this exception, and extracts the info from it
	 * that normally only a constructor would do.
	 *
	 * @param input
	 *            The input stream at the position causing this exception.
	 */
	public void setInputStream(IntStream input) {
		this.extractInfoFromInput(input);
	}

	/**
	 * Sets the source of this exception.
	 *
	 * @param sourceFile
	 *            the source file
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the source of this exception.
	 */
	public File getSourceFile() {
		return this.sourceFile;
	}

	/**
	 * @return The source type of this exception. (Example: 'GOAL' or 'Prolog')
	 */
	public String getSourceType() {
		return this.sourceType;
	}

	/**
	 * Sets the source type of this exception. Example: 'GOAL' or 'Prolog'.
	 *
	 * @param newSourceType
	 *            The new source type of this exception.
	 */
	public void setSourceType(String newSourceType) {
		this.sourceType = newSourceType;
	}

	/**
	 * @return A String indicating from where in the parsing process this error
	 *         was generated. Usually 'Parser' or 'Lexer'.
	 */
	public String getParserLexer() {
		return this.parserLexer;
	}

	/**
	 * Sets the part of the parsing process this exception originated from.
	 * Usually 'Parser' or 'Lexer'
	 *
	 * @param newParserLexer
	 *            The new origin of this exception.
	 */
	public void setParserLexer(String newParserLexer) {
		this.parserLexer = newParserLexer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(this.sourceType);
		builder.append(" ");
		builder.append(this.parserLexer);
		builder.append(" error");
		this.appendAtLinePositionString(builder);
		builder.append(": ");
		builder.append(this.reason);
		builder.append(".");

		return builder.toString();
	}

	/**
	 * Appends info about the line number and location of the origin of this
	 * exception, but only if that information was set.
	 *
	 * @param builder
	 *            To which builder to append the info.
	 */
	public void appendAtLinePositionString(StringBuilder builder) {
		if (this.inputInfoSet) {
			// if line == 0 and pos == -1, we're at EOF
			if (this.line == 0 && this.charPositionInLine == -1) {
				builder.append(" at end of stream");
			} else {
				builder.append(" at line ");
				builder.append(this.line);
				builder.append(", position ");
				builder.append(this.charPositionInLine);
			}
		}
		if (this.sourceFile != null) {
			builder.append(" in ");
			builder.append(this.sourceFile.getName());
		}
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link RecognitionException} may have been thrown, in a way that the user
	 * can understand. Generates a custom message for most subclasses of
	 * {@link RecognitionException}, and others use a generic message (although
	 * these should still be precise enough in most cases).
	 *
	 * @param re
	 *            The {@link RecognitionException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given RE is referring to.
	 * @return A string describing the RE in an understandable way.
	 */
	public static String getReasonFrom(RecognitionException re,
			String[] tokenNames) {
		try {
			throw re;
		}

		catch (FailedPredicateException fpe) {
			return ParserException.getReasonFromFPE(fpe, tokenNames);
		}

		catch (MissingTokenException mte) {
			return ParserException.getReasonFromMTE(mte, tokenNames);
		} catch (UnwantedTokenException ute) {
			return ParserException.getReasonFromUTE(ute, tokenNames);
		} catch (MismatchedTokenException mte) {
			return ParserException.getReasonFromMTE(mte, tokenNames);
		}

		catch (NoViableAltException nvae) {
			return ParserException.getReasonFromNVAE(nvae, tokenNames);
		}

		catch (InvalidSemanticsException ise) {
			return ParserException.getReasonFromISE(ise, tokenNames);
		}

		// - A message for an EarlyExitException ( '(...)+ loop did not match' )
		// does not get clearer if deviated form the default message
		// - CHECK I do not think MismatchedRangeExceptions,
		// MismatchedSetExceptions
		// or MismatchedTreeNodeExceptions can occur in GOAL/Prolog. Printing
		// something possibly sensible instead
		catch (RecognitionException re2) {
			return ParserException.getReasonFromRE(re2, tokenNames);
		}
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link FailedPredicateException} may have been thrown, in a way that the
	 * user can understand.
	 *
	 * @param fpe
	 *            The {@link FailedPredicateException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given FPE is referring to.
	 * @return A string describing the FPE in an understandable way.
	 */
	// unused argument 'tokenNames'; keep it to keep it similar to the other
	// methods.
	private static String getReasonFromFPE(FailedPredicateException fpe,
			String[] tokenNames) {
		StringBuilder reason = new StringBuilder();
		reason.append("Invalid semantics; ");
		reason.append(fpe.ruleName);
		reason.append(", however '");
		reason.append(fpe.predicateText);
		reason.append("' was found.");
		return reason.toString();
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link MismatchedTokenException} may have been thrown, in a way that the
	 * user can understand.
	 *
	 * @param mte
	 *            The {@link MismatchedTokenException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given MTE is referring to.
	 * @return A string describing the MTE in an understandable way.
	 */
	private static String getReasonFromMTE(MismatchedTokenException mte,
			String[] tokenNames) {
		StringBuilder reason = new StringBuilder();
		reason.append("Mismatched token; expected ");
		if (mte.expecting == Token.EOF) {
			reason.append("'<End Of File>'");
		} else {
			if (tokenNames == null) {
				reason.append("<no tokens available>");
			} else if (mte.expecting < tokenNames.length) {
				reason.append(tokenNames[mte.expecting]);
			} else {
				reason.append("<unknown>");
			}
		}
		reason.append(" but got ");
		reason.append(ParserException.getUnexpectedTokenName(mte.token,
				tokenNames));
		return reason.toString();
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link MissingTokenException} may have been thrown, in a way that the
	 * user can understand.
	 *
	 * @param mte
	 *            The {@link MissingTokenException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given MTE is referring to.
	 * @return A string describing the MTE in an understandable way.
	 */
	private static String getReasonFromMTE(MissingTokenException mte,
			String[] tokenNames) {
		StringBuilder reason = new StringBuilder();
		reason.append("Missing token; expected ");
		if (mte.expecting == Token.EOF) {
			reason.append("'<End Of File>'");
		} else {
			if (tokenNames == null) {
				reason.append("<no tokens available>");
			} else if (mte.expecting < tokenNames.length) {
				reason.append(tokenNames[mte.expecting]);
			} else {
				reason.append("<unknown>");
			}
		}
		if (mte.token != null) {
			reason.append(" but got ");
			reason.append(ParserException.getUnexpectedTokenName(mte.token,
					tokenNames));
		}
		return reason.toString();
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link MissingTokenException} may have been thrown, in a way that the
	 * user can understand.
	 *
	 * @param mte
	 *            The {@link MissingTokenException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given MTE is referring to.
	 * @return A string describing the MTE in an understandable way.
	 */
	private static String getReasonFromUTE(UnwantedTokenException ute,
			String[] tokenNames) {
		StringBuilder reason = new StringBuilder();
		reason.append("Unwanted token; expected ");
		if (ute.expecting == Token.EOF) {
			reason.append("'<End Of File>'");
		} else {
			if (tokenNames == null) {
				reason.append("<no tokens available>");
			} else if (ute.expecting < tokenNames.length) {
				reason.append(tokenNames[ute.expecting]);
			} else {
				reason.append("<unknown>");
			}
		}
		if (ute.token != null) {
			reason.append(" but found ");
			reason.append(ParserException.getUnexpectedTokenName(ute.token,
					tokenNames));
		}
		return reason.toString();
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link NoViableAltException} may have been thrown, in a way that the user
	 * can understand.
	 *
	 * @param nvae
	 *            The {@link NoViableAltException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given NVAE is referring to.
	 * @return A string describing the NVAE in an understandable way.
	 */
	private static String getReasonFromNVAE(NoViableAltException nvae,
			String[] tokenNames) {
		StringBuilder reason = new StringBuilder();
		reason.append("Could not continue parsing at ");
		if (nvae.token == null) {
			reason.append(ParserException.getUnexpectedTokenName(
					nvae.getUnexpectedType(), tokenNames));
		} else {
			reason.append(ParserException.getUnexpectedTokenName(nvae.token,
					tokenNames));
		}
		reason.append(" (no viable alternative)");
		return reason.toString();
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link InvalidSemanticsException} may have been thrown, in a way that the
	 * user can understand.
	 *
	 * @param re
	 *            The {@link InvalidSemanticsException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given ISE is referring to.
	 * @return A string describing the ISE in an understandable way.
	 */
	private static String getReasonFromISE(RecognitionException re,
			String[] tokenNames) {
		return ("KR expression is not acceptable in the GOAL context:" + re);
	}

	/**
	 * Creates a string that describes the reason why a
	 * {@link RecognitionException} may have been thrown, in a way that the user
	 * can understand.
	 *
	 * @param re
	 *            The {@link RecognitionException} that has been thrown.
	 * @param tokenNames
	 *            Names of the tokens the given RE is referring to.
	 * @return A string describing the RE in an understandable way.
	 */
	private static String getReasonFromRE(RecognitionException re,
			String[] tokenNames) {
		StringBuilder reason = new StringBuilder();
		reason.append("Unexpected token ");
		if (re.token == null) {
			reason.append(ParserException.getUnexpectedTokenName(
					re.getUnexpectedType(), tokenNames));
		} else {
			reason.append(ParserException.getUnexpectedTokenName(re.token,
					tokenNames));
		}
		return reason.toString();
	}

	/**
	 * Gets the name of some unexpected token.
	 *
	 * @param t
	 *            The unexpected token
	 * @param tokenNames
	 *            The names of all valid tokens (the result of a
	 *            Parser.getTokenNames() call; not a list of expected tokens).
	 * @return A string-representation of the unexpected tokens. The returned
	 *         string contains single-quotes around the token.
	 */
	public static String getUnexpectedTokenName(Token t, String[] tokenNames) {
		if (t == null) {
			return "an unknown character";
		}
		String s = t.getText();
		if (s == null) {
			if (t.getType() == Token.EOF) {
				s = "<End Of File>";
			} else {
				if (tokenNames == null) {
					s += "<no token names available>";
				} else if (t.getType() < tokenNames.length) {
					s = "<" + tokenNames[t.getType()] + ">";
				} else {
					s = "<unknown>";
				}
			}
		}
		return ("'" + s + "'").replace("\n", "\\n").replace("\r", "\\r");
	}

	/**
	 * Gets the name of some unexpected token.
	 *
	 * @param t
	 *            The unexpected token type or char of the unexpected input
	 *            element
	 * @param tokenNames
	 *            The names of all valid tokens (the result of a
	 *            Parser.getTokenNames() call; not a list of expected tokens).
	 * @return A string-representation of the unexpected tokens. The returned
	 *         string contains single-quotes around the token.
	 */
	public static String getUnexpectedTokenName(int t, String[] tokenNames) {
		String s;
		if (t == -1) {
			s = "<End Of File>";
		} else if (t < 0) {
			s = "<Unknown token type '" + t + "'>";
		} else if (tokenNames != null && t < tokenNames.length) {
			s = tokenNames[t];
		} else {
			s = "'" + (char) t + "'";
		}
		return s.replace("\n", "\\n").replace("\r", "\\r");
	}

	/**
	 * Converts a {@link RecognitionException} into a message that can easily be
	 * read by the user. If the exception is not a (subclass of)
	 * {@link ParserException} already, it is converted into one and then the
	 * toString method is called.
	 *
	 * @param rex
	 *            The exception the user needs to be made aware of. Note that an
	 *            is assumed to be originating from a GOAL parser if it is a
	 *            {@link RecognitionException} and not a
	 *            {@link FailedPredicateException}. Call
	 *            {@link ParserException#GOALRecognitionException(RecognitionException, String[], String)}
	 *            to get a message for prolog exceptions.
	 * @param tokenNames
	 *            The names of the tokens possibly used by the exception.
	 * @param parserLexer
	 *            From what part of the parsing process the error came. Should
	 *            be either parser or lexer.
	 * @param sourceType
	 *            A string describing what part of the parse sequence the error
	 *            came from. Usually MAS or GOAL.
	 * @return A string-representation of the given exception.
	 */
	public static String getReadableErrorMessage(RecognitionException rex,
			String[] tokenNames, String parserLexer, String sourceType) {
		// try/catch: an ideal way to filter by type of exception?
		try {
			throw rex;
		} catch (ParserException grex) {
			// GOALParserExceptions already have a neat way of printing the
			// error
			return grex.toString();
		} catch (FailedPredicateException fpex) {
			// convert to GOAL FPE
			return new InvalidSemanticsException(fpex, parserLexer).toString();
		} catch (RecognitionException rex2) {
			// convert to GOAL RE
			// source type is GOAL, as prolog lexer/parser errors should've been
			// converted to GOAL REs when parsing GOAL file
			return new ParserException(rex2, tokenNames, sourceType,
					parserLexer).toString();
		}
	}

	/**
	 * Determines if this exception occurred before another parser exception by
	 * comparing the line and character position.
	 *
	 * @param other
	 *            Some other parser exception.
	 * @return <code>true</code> if this exception occurred before the other
	 *         given exception, or if the input position of the other exception
	 *         has not been set.
	 */
	public boolean occurredBefore(ParserException other) {
		if (!other.inputInfoSet) {
			return true;
		}
		if (!this.inputInfoSet) {
			return false;
		}

		if (this.line < other.line) {
			return true;
		}
		if (this.line > other.line) {
			return false;
		}

		return this.charPositionInLine < other.charPositionInLine;
	}
}
