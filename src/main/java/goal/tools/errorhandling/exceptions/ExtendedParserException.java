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

import java.io.File;

import org.antlr.runtime.IntStream;

/**
 * An extension to {@link ParserException}, also indicating the file (or other
 * source) where the error originated from. It can also contain an inner
 * exception that caused this exception.
 *
 * @author N.Kraayenbrink
 * @modified W.Pasman namechange as this is not a GOALException.
 *
 */
public class ExtendedParserException extends ParserException {

	/** auto-generated serial version UID */
	private static final long serialVersionUID = 546114942769689181L;

	/**
	 * The exception causing this exception to be thrown.
	 */
	private Exception innerException;
	/**
	 * A flag indicating whether or not to show the stack trace of the inner
	 * exception in the toString method.
	 */
	private boolean showIEStack;

	/**
	 * Creates a new {@link ExtendedParserException}, with a given input source,
	 * a reason for the exception to have occurred, and a name or description of
	 * the file (or other source) where the exception originated from.
	 *
	 * @param input
	 *            A reference to the faulty stream, at the position of the error
	 *            made by the user. May be null, in which case that info can be
	 *            set later using {@link #setInputStream(IntStream)}.
	 * @param reason
	 *            Why the new exception occurred.
	 * @param sourceFile
	 *            In what file (or other source) the exception occurred.
	 */
	public ExtendedParserException(IntStream input, String reason,
			File sourceFile) {
		this(input, reason, sourceFile, null, false);
	}

	/**
	 * Creates a new {@link ExtendedParserException}, with a given input source,
	 * a reason for the exception to have occurred, and the exception that
	 * triggered the new exception.
	 *
	 * @param input
	 *            A reference to the faulty stream, at the position of the error
	 *            made by the user. May be null, in which case that info can be
	 *            set later using {@link #setInputStream(IntStream)}.
	 * @param reason
	 *            Why the new exception occurred.
	 * @param innerException
	 *            The {@link Exception} triggering the new
	 *            {@link ExtendedParserException}.
	 * @param showStack
	 *            If the {@link #toString()} method should print the stack trace
	 *            of the inner exception (if present).
	 */
	public ExtendedParserException(IntStream input, String reason,
			Exception innerException, boolean showStack) {
		this(input, reason, null, innerException, showStack);
	}

	/**
	 * Creates a new {@link ExtendedParserException}, with a given input source,
	 * a reason for the exception to have occurred, the exception triggering the
	 * new exception, and the file (or other source) in which the original
	 * exception occurred.
	 *
	 * @param input
	 *            A reference to the faulty stream, at the position of the error
	 *            made by the user. May be null, in which case that info can be
	 *            set later using {@link #setInputStream(IntStream)}.
	 * @param reason
	 *            Why the new exception occurred.
	 * @param sourceFile
	 *            In what file (or other source) the exception occurred.
	 * @param innerException
	 *            The {@link Exception} triggering the new
	 *            {@link ExtendedParserException}.
	 * @param showStack
	 *            If the {@link #toString()} method should print the stack trace
	 *            of the inner exception (if present).
	 */
	public ExtendedParserException(IntStream input, String reason,
			File sourceFile, Exception innerException, boolean showStack) {
		super(input, reason);
		this.setSourceFile(sourceFile);
		this.setInnerException(innerException, showStack);
	}

	/**
	 * @return The exception that triggered this exception. May be null if none
	 *         was given.
	 */
	public Exception getInnerException() {
		return this.innerException;
	}

	/**
	 * Sets the exception that triggered this exception.
	 *
	 * @param innerException
	 *            The exception that triggered this exception. May be null.
	 * @param showStack
	 *            If the stack trace of the given exception should be printed in
	 *            this exception's {@link #toString()} method.
	 */
	public void setInnerException(Exception innerException, boolean showStack) {
		this.innerException = innerException;
		this.showIEStack = showStack;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("GOAL parse error");
		this.appendAtLinePositionString(builder);
		this.appendSourceInfo(builder);
		builder.append(": ");
		builder.append(this.getReason());
		this.appendInnerException(builder);
		builder.append(".");

		return builder.toString();
	}

	/**
	 * Appends info about the file (or other source) where this exception
	 * occurred, but only if that info was set.
	 *
	 * @param builder
	 *            To which builder to append the info.
	 */
	public void appendSourceInfo(StringBuilder builder) {
		if (this.sourceFile != null) {
			builder.append(" in ");
			builder.append(this.sourceFile.getName());
		}
	}

	/**
	 * Appends info about the exception that caused this exception to be
	 * created, but only if that info was set.<br>
	 * If showStack has been set to true, also the stack trace of the inner
	 * exception (when present) is printed. Each element is printed on a new
	 * line with a tab character in front.
	 *
	 * @param builder
	 *            To which builder to append the info.
	 */
	public void appendInnerException(StringBuilder builder) {
		if (this.innerException != null) {
			builder.append(", which was caused by: ");
			builder.append(this.innerException.toString());
			if (this.showIEStack) {
				for (StackTraceElement ste : this.innerException
						.getStackTrace()) {
					builder.append("\n\t");
					builder.append(ste.toString());
				}
				builder.append("\n");
			}
		}
	}
}
