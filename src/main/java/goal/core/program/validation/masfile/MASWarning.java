package goal.core.program.validation.masfile;

import goal.core.program.validation.ValidatorWarning.ValidatorWarningType;

import java.util.MissingFormatArgumentException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum MASWarning implements ValidatorWarningType {
	ENVIRONMENT_NO_REFERENCE,
	AGENTFILES_NO_AGENTS,
	AGENTFILE_OTHEREXTENSION,
	AGENTFILE_UNUSED,
	LAUNCH_NONEFOUND;

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("goal.core.program.validation.masfile.MASWarningMessages");

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
