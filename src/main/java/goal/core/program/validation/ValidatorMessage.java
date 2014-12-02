package goal.core.program.validation;

import goal.parser.IParsedObject;
import goal.parser.InputStreamPosition;

public abstract class ValidatorMessage {
	public interface ValidatorMessageType {
		public String toReadableString(String... args);
	}

	protected final ValidatorMessageType type;
	protected InputStreamPosition pos;
	protected String[] args;

	public ValidatorMessage(ValidatorMessageType type, InputStreamPosition pos,
			String... args) {
		this.type = type;
		this.pos = pos;
		this.args = args;
	}

	public ValidatorMessage(ValidatorMessageType type, IParsedObject obj,
			String... args) {
		this(type, obj.getSource(), args);
	}

	public ValidatorMessage(ValidatorMessageType type) {
		this(type, (InputStreamPosition) null, new String[0]);
	}

	public ValidatorMessage(ValidatorMessageType type, String... args) {
		this(type, (InputStreamPosition) null, args);
	}

	public ValidatorMessageType getType() {
		return this.type;
	}

	public InputStreamPosition getPosition() {
		return this.pos;
	}

	public void setPosition(InputStreamPosition pos) {
		this.pos = pos;
	}

	public String[] getArguments() {
		return this.args;
	}

	public void setArguments(String... args) {
		this.args = args;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof ValidatorMessage)) {
			return false;
		} else {
			ValidatorMessage other = (ValidatorMessage) obj;
			if (this.pos == null) {
				if (other.getPosition() != null) {
					return false;
				}
			} else if (!this.pos.equals(other.getPosition())) {
				return false;
			}
			if (this.type == null) {
				if (other.getType() != null) {
					return false;
				}
			} else if (!this.type.equals(other.getType())) {
				return false;
			}
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public String toShortString() {
		final String[] shortargs = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			if (arg.length() > 200) {
				shortargs[i] = arg.substring(0, 200) + "...";
			} else {
				shortargs[i] = arg;
			}
		}
		return this.type.toReadableString(shortargs);
	}

	@Override
	public abstract String toString();
}
