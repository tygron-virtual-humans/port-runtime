package goal.core.program.validation.agentfile;

import goal.core.program.validation.ValidatorWarning.ValidatorWarningType;

import java.util.MissingFormatArgumentException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum GOALWarning implements ValidatorWarningType {
	MODULE_EMPTY_PROGRAMSECTION,
	MODULE_NEVER_USED,
	ACTION_NEVER_USED,
	EXPRESSION_NEVER_USED,
	MACRO_NEVER_USED,
	MACRO_PARAMETERS_NOTIN_DEFINITION;

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("goal.core.program.validation.agentfile.GOALWarningMessages");

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
