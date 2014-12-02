package goal.core.program.validation;

import goal.parser.IParsedObject;
import goal.parser.InputStreamPosition;

/**
 *
 *
 */
public class ValidatorWarning extends ValidatorMessage {

	public interface ValidatorWarningType extends ValidatorMessageType {
	}

	public ValidatorWarning(ValidatorWarningType type, InputStreamPosition pos,
			String... args) {
		super(type, pos, args);
	}

	public ValidatorWarning(ValidatorWarningType type, IParsedObject obj,
			String... args) {
		this(type, obj.getSource(), args);
	}

	public ValidatorWarning(ValidatorWarningType type) {
		super(type, (InputStreamPosition) null, new String[0]);
	}

	public ValidatorWarning(ValidatorWarningType type, String... args) {
		super(type, (InputStreamPosition) null, args);
	}

	@Override
	public ValidatorWarningType getType() {
		return (ValidatorWarningType) this.type;
	}

	@Override
	public String toString() {
		if (this.pos != null) {
			return "Warning at " + this.pos.toString() + ": "
					+ this.toShortString();
		} else {
			return "Warning: " + this.toShortString();
		}
	}
}
