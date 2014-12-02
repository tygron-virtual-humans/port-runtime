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

import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;

/**
 * An extension to {@link ParserException}, mimicking the behaviour of
 * {@link FailedPredicateException}.
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman namechange as this is not a GOALException
 */
public class InvalidSemanticsException extends ParserException {
	/** auto-generated serial version UID */
	private static final long serialVersionUID = -6040231378277316947L;

	/**
	 * The text that did not match the expected format.
	 */
	private final String invalidText;

	/**
	 * Creates a new {@link InvalidSemanticsException}, indicating that some
	 * input from the user did not match the expected format.<br>
	 * Information on the location of the faulty predicate may be given later
	 * using {@link #setInputStream(IntStream)}.
	 *
	 * @param ruleName
	 *            The rule that made the input from the user invalid.
	 * @param faultyPredicate
	 *            What the user provided that conflicts with the given rule.
	 */
	public InvalidSemanticsException(String ruleName, String faultyPredicate) {
		this((IntStream) null, ruleName, faultyPredicate);
	}

	/**
	 * Creates a new {@link InvalidSemanticsException}, indicating that some
	 * input from the user did not match the expected format.
	 *
	 * @param input
	 *            A reference to the faulty stream, at the position of the
	 *            faulty predicate. May be null, in which case the information
	 *            on the location can be set later using
	 *            {@link #setInputStream(IntStream)}.
	 * @param ruleName
	 *            The rule that made the input from the user invalid.
	 * @param faultyPredicate
	 *            What the user provided that conflicts with the given rule.
	 */
	public InvalidSemanticsException(IntStream input, String ruleName,
			String faultyPredicate) {
		super(input, ruleName);
		this.invalidText = faultyPredicate;
	}

	/**
	 * Creates a new {@link InvalidSemanticsException}, indicating that some
	 * input from the user did not match the expected format.
	 *
	 * @param source
	 *            A pointer to where in the input stream this exception
	 *            originated
	 * @param ruleName
	 *            The rule that made the input from the user invalid.
	 * @param faultyPredicate
	 *            What the user provided that conflicts with the given rule.
	 */
	public InvalidSemanticsException(InputStreamPosition source,
			String ruleName, String faultyPredicate) {
		super(source, ruleName);
		this.invalidText = faultyPredicate;
	}

	/**
	 * Creates a new {@link InvalidSemanticsException}, indicating that some
	 * input from the user did not match the expected format.
	 *
	 * @param source
	 *            A pointer to where in the input stream this exception
	 *            originated
	 * @param ruleName
	 *            The rule that made the input from the user invalid.
	 * @param faultyPredicate
	 *            What the user provided that conflicts with the given rule.
	 * @param sourceType
	 *            the name of the source, typically "GOAL" or "Prolog".
	 */
	public InvalidSemanticsException(InputStreamPosition source,
			String ruleName, String faultyPredicate, String sourceType) {
		super(source, ruleName, sourceType);
		this.invalidText = faultyPredicate;
	}

	/**
	 * Creates a new {@link InvalidSemanticsException}, based on a
	 * {@link FailedPredicateException}.<br>
	 * Main use is to print the ANTLR FPE in the same way as GOAL ISEs.
	 *
	 * @param fpe
	 *            the {@link FailedPredicateException} the new exception should
	 *            be based upon.
	 */
	public InvalidSemanticsException(FailedPredicateException fpe) {
		this(fpe.input, fpe.ruleName, fpe.predicateText);
		// the input stream may not be at the correct position any more;
		// copy the values found at creation.
		this.copyInfoFrom(fpe);
	}

	/**
	 * Creates a new {@link InvalidSemanticsException}, based on a
	 * {@link FailedPredicateException}.<br>
	 * Main use is to print the ANTLR FPE in the same way as GOAL ISEs.
	 *
	 * @param fpe
	 *            the {@link FailedPredicateException} the new exception should
	 *            be based upon.
	 * @param parserLexer
	 *            From what part of the parsing process the error came. Should
	 *            be either parser or lexer.
	 */
	public InvalidSemanticsException(FailedPredicateException fpe,
			String parserLexer) {
		this(fpe);
		this.parserLexer = parserLexer;
	}

	/**
	 * @return What the user provided that conflicted with the rule described in
	 *         {@link #getReason()}.
	 */
	public String getFaultyPredicate() {
		return this.invalidText;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(getSourceType() + " " + getParserLexer() + " error: ");
		builder.append(this.getReason());
		builder.append("; \'");
		builder.append(this.getFaultyPredicate());
		builder.append("\'");
		this.appendAtLinePositionString(builder);
		builder.append(".");

		return builder.toString();
	}
}
