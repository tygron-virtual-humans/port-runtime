package goal.core.program.validation;

import goal.parser.IParsedObject;
import goal.parser.InputStreamPosition;

public class ValidatorError extends ValidatorMessage {
	public interface ValidatorErrorType extends ValidatorMessageType {
	}

	public ValidatorError(ValidatorErrorType type, InputStreamPosition pos,
			String... args) {
		super(type, pos, args);
	}

	public ValidatorError(ValidatorErrorType type, IParsedObject obj,
			String... args) {
		this(type, obj.getSource(), args);
	}

	public ValidatorError(ValidatorErrorType type) {
		super(type, (InputStreamPosition) null, new String[0]);
	}

	public ValidatorError(ValidatorErrorType type, String... args) {
		super(type, (InputStreamPosition) null, args);
	}

	@Override
	public ValidatorErrorType getType() {
		return (ValidatorErrorType) this.type;
	}

	@Override
	public String toString() {
		if (this.pos != null) {
			return "Error at " + this.pos.toString() + ": "
					+ this.toShortString();
		} else {
			return "Error: " + this.toShortString();
		}
	}
}