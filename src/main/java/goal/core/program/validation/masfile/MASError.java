package goal.core.program.validation.masfile;

import goal.core.program.validation.ValidatorError.ValidatorErrorType;

import java.util.MissingFormatArgumentException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum MASError implements ValidatorErrorType {
	ENVIRONMENT_COULDNOT_OPEN,
	ENVIRONMENT_CANNOT_FIND,
	INIT_DUPLICATE_PARAMETER,
	INIT_UNRECOGNIZED_PARAMETER,
	AGENTFILES_DUPLICATE_NAME,
	AGENTFILE_DUPLICATE_PARAMETER,
	AGENTFILE_CANNOT_FIND,
	AGENTFILE_NONEXISTANT_REFERENCE,
	AGENTFILE_UNRECOGNIZED_PARAMETER,
	LAUNCH_DUPLICATE,
	LAUNCH_INVALID_WILDCARD,
	LAUNCH_INVALID_DEFINITION,
	CONSTRAINT_DUPLICATE,
	CONSTRAINT_INVALID_DEFINITION;

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("goal.core.program.validation.masfile.MASErrorMessages");

	@Override
	public String toReadableString(String... args) {
		try {
			return String.format(BUNDLE.getString(name()), (Object[]) args);
		} catch (MissingResourceException e1) {
			if (args.length > 0) {
				return args[0];
			} else {
				return name();
			}
		} catch (MissingFormatArgumentException e2) {
			return BUNDLE.getString(name());
		}
	}
}
