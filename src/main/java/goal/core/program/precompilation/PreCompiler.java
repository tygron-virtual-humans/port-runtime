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

package goal.core.program.precompilation;

import goal.core.kr.KRlanguage;
import goal.parser.InputStreamPosition;
import goal.tools.errorhandling.exceptions.GOALException;
import goal.tools.errorhandling.exceptions.GOALUserError;
import goal.tools.errorhandling.exceptions.GOALWarning;
import goal.tools.logging.GOALLogger;
import goal.tools.logging.StringsLogRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Basic structure for a precompilation class. The intended structure for
 * subclasses is as follows:<br>
 * <ul>
 * <li>Create a subclass for each type of object to be precompiled, with custom
 * code for {@link #doPrecompile} in each of them.</li>
 * <li>If an object to be precompiled contains (links to) other objects to be
 * precompiled, call {@link #doPrecompile} on a precompilator for that object.</li>
 * </ul>
 *
 * @author K.Hindriks
 *
 */
public abstract class PreCompiler<T> {
	/**
	 * The list of errors encountered in the last precompilation run.
	 */
	private List<GOALException> errors;
	/**
	 * The list of warnings encountered in the last precompilation run.
	 */
	private List<GOALWarning> warnings;
	/**
	 * The list with all reported errors and warnings, in the order they are
	 * reported.
	 */
	private List<GOALException> allReports;

	private KRlanguage language;

	/**
	 * Create a new {@link PreCompiler}. Does nothing; the error and warning
	 * lists are initialized by calling {@link #preCompile} (or
	 * {@link #initialize}, which is called by {@link #preCompile}).
	 */
	protected PreCompiler() {
	}

	/**
	 * Initializes this {@link PreCompiler} for a new {@link #doPreCompile}
	 * call. Intended use is for in {@link #preCompile} only.
	 */
	private void initialize() {
		this.errors = new LinkedList<>();
		this.warnings = new LinkedList<>();
		this.allReports = new LinkedList<>();
	}

	/**
	 * @return The KR language
	 */
	public KRlanguage getKRlanguage() {
		return language;
	}

	/**
	 * @param language
	 *            The KR language to use
	 */
	public void setKRlanguage(KRlanguage language) {
		this.language = language;
	}

	/**
	 * Checks if the object this {@link PreCompiler} checked is a valid
	 * instance.
	 *
	 * @return <code>true</code> iff no errors have been found while
	 *         precompiling objects using {@link #precompile()}.
	 */
	public final boolean isCompiledWithoutProblems() {
		return this.allReports.isEmpty();
	}

	/**
	 * Precompiles a certain object.<br>
	 * Multiple objects can be precompiled with this precompilator, but
	 * information about previously precompiled objects is lost once a new one
	 * is precompiled.
	 *
	 * @param subject
	 *            The object to precompile.
	 * @return <code>true</code> iff the given object is 'perfect'. That is, if
	 *         no errors or warnings were reported while precompiling the
	 *         object. Note that this also returns <code>false</code> if the
	 *         given object is valid but still has some warnings.
	 */
	public final boolean preCompile(T subject) {
		this.initialize();
		this.doPreCompile(subject);
		return this.isCompiledWithoutProblems();
	}

	/**
	 * Does the actual precompilation of an object. {@link #precompile} also
	 * handles initialization and the issue-free-check afterwards.
	 *
	 * @param subject
	 *            The object to precompile.
	 */
	protected abstract void doPreCompile(T subject);

	/**
	 * Report a certain exception that occurred when precompiling.
	 *
	 * @param ex
	 *            The exception that occurred during precompilation. Must be
	 *            either a {@link GOALWarning} or a {@link GOALUserError}.
	 */
	protected void report(GOALException ex) {
		this.allReports.add(ex);
		try {
			throw ex;
		} catch (GOALWarning warning) {
			this.warnings.add(warning);
		} catch (GOALUserError error) {
			this.errors.add(error);
		} catch (GOALException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Report an error that occurred.
	 *
	 * @param message
	 *            Description of what went wrong.
	 * @param messageParams
	 *            The optional parameters of the message.
	 */
	protected void userError(String message, Object... messageParams) {
		if (messageParams.length == 0) {
			this.report(new GOALUserError(message));
		} else {
			this.report(new GOALUserError(String.format(message, messageParams)));
		}
	}

	/**
	 * Report a warning without source.
	 *
	 * @param message
	 *            What the user did that needs his attention. (should be
	 *            specific enough to understand without pointer to where the
	 *            error was made). May be a formatted string.
	 * @param messageParams
	 *            The optional parameters of the message.
	 */
	protected void warning(String message, Object... messageParams) {
		this.warning(null, message, messageParams);
	}

	/**
	 * Report a warning the user made somewhere.
	 *
	 * @param source
	 *            A pointer to where the thing is the warning describes.
	 * @param message
	 *            What the user should be made aware of. May be a formatted
	 *            string.
	 * @param messageParams
	 *            The optional parameters of the message.
	 */
	protected void warning(InputStreamPosition source, String message,
			Object... messageParams) {
		if (messageParams.length == 0) {
			this.report(new GOALWarning(message, source));
		} else {
			this.report(new GOALWarning(String.format(message, messageParams),
					source));
		}
	}

	/**
	 * Logs all errors and warnings this {@link PreCompiler} encountered in the
	 * last precompilation run to a certain logger, in the order of their source
	 * location.
	 *
	 * @param logger
	 *            Where to print the errors and warnings to.
	 * @param errorPrefix
	 *            What to prefix every error with.
	 * @param warningPrefix
	 *            What to prefix every warning with.
	 */
	public void logAllSorted(GOALLogger logger, String errorPrefix,
			String warningPrefix) {
		if (this.allReports != null) {
			ArrayList<GOALException> allSorted = new ArrayList<>(
					this.allReports);
			Collections.sort(allSorted);
			String prefix;
			for (GOALException ex : allSorted) {
				if (ex instanceof GOALWarning) {
					prefix = warningPrefix;
				} else {
					prefix = errorPrefix;
				}
				logger.log(new StringsLogRecord(Level.WARNING, "%1$s %2$s",
						prefix, ex));
			}
		}
	}

	/**
	 * Output all errors and warnings of this PreCompiler to the logger.
	 *
	 * @param logger
	 *            is logger to dump the log text in.
	 */
	public void logAllToLogger(Logger logger) {
		logger.warning(logAllToString());
	}

	/**
	 * Collect all error messages and warnings of this precompilator in one
	 * string.
	 *
	 * @return string with all messages.
	 */
	public String logAllToString() {
		if (this.allReports != null) {
			ArrayList<GOALException> allSorted = new ArrayList<>(
					this.allReports);
			Collections.sort(allSorted);
			StringBuffer logstring = new StringBuffer();
			for (GOALException ex : allSorted) {
				if (ex instanceof GOALWarning) {
					logstring.append(ex.toString() + "\n");
				} else {
					logstring.append(ex.toString() + "\n");
				}
			}
			return logstring.toString();
		}
		return "";
	}

	/**
	 * Reports any errors and warnings found to another precompiler.
	 *
	 * @param superior
	 *            The {@link PreCompiler} to report the found errors and
	 *            warnings. to.
	 */
	protected void reportToSuperior(PreCompiler<?> superior) {
		if (this.allReports != null) {
			for (GOALException ex : this.allReports) {
				superior.report(ex);
			}
		}
	}

}
