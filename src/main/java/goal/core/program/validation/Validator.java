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

package goal.core.program.validation;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Basic structure for a validator. The intended structure for subclasses is as
 * follows:<br>
 * - Create a subclass for each type of object to be validated, with custom code
 * for {@link #doValidate} in each of them.<br>
 * - If an object to be validated contains (links to) other objects to be
 * validated, call {@link #validate} on a validator for that object. Then call
 * {@link #reportToSuperior} to copy over any found errors and warnings.<br>
 * - It is usually preferred not to stop once such a subordinate validator
 * encountered errors, but not mandatory.<br>
 * - Try to call validators for child-/sub-objects in such an order that the
 * reason an error occurred may only be because of earlier validated objects.
 *
 * @param <T>
 *            The validator type.
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public abstract class Validator<T> {

	/**
	 * The list of errors encountered in the last validation run.
	 */
	private List<ValidatorError> errors = null;
	/**
	 * The list of warnings encountered in the last validation run.
	 */
	private List<ValidatorWarning> warnings = null;
	/**
	 * The list with all reported errors and warnings, in the order they are
	 * reported.
	 */
	private List<ValidatorMessage> allReports = null;
	/**
	 * True if we are validating a mod2g (or other external) file
	 */
	protected boolean mod2g = false;

	/**
	 * Create a new {@link Validator}. Does nothing; the error and warning lists
	 * are initialized by calling {@link #validate} (or {@link #initialize},
	 * which is called by {@link #validate}).
	 */
	protected Validator() {
	}

	/**
	 * Initializes this {@link Validator} for a new {@link #doValidate} call.
	 * Intended use is for in {@link #validate} only.
	 */
	private void initialize() {
		this.errors = new LinkedList<>();
		this.warnings = new LinkedList<>();
		this.allReports = new LinkedList<>();
	}

	/**
	 * Checks whether the object this {@link Validator} last checked did not
	 * generate any errors.
	 *
	 * @return {@code true} if no errors have been found while checking the
	 *         object using {@link #validate()}.
	 */
	public final boolean isValid() {
		return this.errors.isEmpty();
	}

	/**
	 * Checks whether the object this {@link Validator} last checked did not
	 * generate any errors and/or warnings.
	 *
	 * @return {@code true} if no errors or warnings were generated during the
	 *         last call of {@link #validate}.
	 */
	public boolean isPerfect() {
		return this.allReports.isEmpty();
	}

	/**
	 * Validates a certain object.<br>
	 * Multiple objects can be validated with this validator, but information
	 * about previously validated objects is lost once a new one is validated.
	 *
	 * @param subject
	 *            The object to validate.
	 * @param mod2g
	 *            True if we are validating a mod2g (or other external) file
	 * @return <code>true</code> iff the given object is 'perfect'. That is, if
	 *         no errors or warnings were reported while validating the object.
	 *         Note that this also returns <code>false</code> if the given
	 *         object is valid but still has some warnings.
	 */
	public boolean validate(T subject, boolean mod2g) {
		initialize();
		this.mod2g = mod2g;
		doValidate(subject);
		return this.isPerfect();
	}

	/**
	 * Does the actual validation of an object. {@link #validate} also handles
	 * initialization and the 'perfectness'-check afterwards.
	 *
	 * @param subject
	 *            The object to validate.
	 */
	protected abstract void doValidate(T subject);

	/**
	 * Reports a problem {{@link ValidatorMessage}) that was found while
	 * validating.
	 *
	 * @param problem
	 *            The problem that was found while validating; type of the
	 *            problem reported must be either a {@link ValidatorWarning} or
	 *            a {@link ValidatorError}.
	 */
	protected void report(ValidatorMessage problem) {
		this.allReports.add(problem);
		if (problem instanceof ValidatorError) {
			this.errors.add((ValidatorError) problem);
		} else if (problem instanceof ValidatorWarning) {
			this.warnings.add((ValidatorWarning) problem);
		} else {
			throw new RuntimeException("Unrecognized validator message type: "
					+ problem.getClass());
		}
	}

	/**
	 * @return The resulting errors (if any).
	 */
	public List<ValidatorError> getErrors() {
		return errors;
	}

	/**
	 * @return The resulting warnings (if any).
	 */
	public List<ValidatorWarning> getWarnings() {
		return warnings;
	}

	/**
	 * @return The resulting errors and/or warnings (if any).
	 */
	public List<ValidatorMessage> getAllReports() {
		return allReports;
	}

	/**
	 * Reports any errors and warnings found to another validator.
	 *
	 * @param superior
	 *            The {@link Validator} to report the found errors and warnings
	 *            to.
	 */
	public void reportToSuperior(Validator<?> superior) {
		assert this.allReports != null : "Reports in " + this.getClass()
				+ " are used but have not been initialized.";

		for (ValidatorMessage ex : this.allReports) {
			superior.report(ex);
		}
	}

}
