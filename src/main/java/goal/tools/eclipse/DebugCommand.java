package goal.tools.eclipse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import languageTools.program.agent.AgentId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

public class DebugCommand {
	private final static String PREFIX = "DC";
	private final static String DELIMITER = "|";

	public enum Command {
		// received
		PAUSE, RUN, STEP, BREAKS, STOP,
		// received and/or sent
		EVAL,
		// sent
		RUNMODE, LOG, LAUNCHED, SUSPEND, INSERTED_BEL, DELETED_BEL, INSERTED_PERCEPT, DELETED_PERCEPT, INSERTED_MAIL, DELETED_MAIL, ADOPTED, DROPPED, FOCUS, DEFOCUS, RULE_EVALUATION, PRECOND_EVALUATION, MODULE_ENTRY, MODULE_EXIT, EXECUTED,
		// environment
		ENV_CREATED, ENV_STATE, ENV_PAUSE, ENV_RUN
	}

	private final Command command;
	private final AgentId agent;
	private final MessageBoxId environment;
	private final List<String> data;

	public DebugCommand(final Command command, final List<String> data) {
		this.command = command;
		this.agent = null;
		this.environment = null;
		this.data = data;
	}

	public DebugCommand(final Command command, final String data) {
		this(command, new ArrayList<String>(1));
		this.data.add(data);
	}

	public DebugCommand(final Command command, final AgentId agent,
			final List<String> data) {
		this.command = command;
		this.agent = agent;
		this.environment = null;
		this.data = data;
	}

	public DebugCommand(final Command command, final AgentId agent,
			final String data) {
		this(command, agent, new ArrayList<String>(1));
		this.data.add(data);
	}

	public DebugCommand(final Command command, final AgentId agent) {
		this(command, agent, new ArrayList<String>(0));
	}

	public DebugCommand(final Command command, final MessageBoxId environment,
			final List<String> data) {
		this.command = command;
		this.agent = null;
		this.environment = environment;
		this.data = data;
	}

	public DebugCommand(final Command command, final MessageBoxId environment,
			final String data) {
		this(command, environment, new ArrayList<String>(1));
		this.data.add(data);
	}

	public DebugCommand(final Command command, final MessageBoxId environment) {
		this(command, environment, new ArrayList<String>(0));
	}

	public Command getCommand() {
		return this.command;
	}

	public AgentId getAgent() {
		return this.agent;
	}

	public MessageBoxId getEnvironment() {
		return this.environment;
	}

	public List<String> getAllData() {
		return Collections.unmodifiableList(this.data);
	}

	public String getData(int index) {
		return this.data.get(index);
	}

	public String getData() {
		return getData(0);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || !(obj instanceof DebugCommand)) {
			return false;
		} else {
			final DebugCommand other = (DebugCommand) obj;
			if (this.agent == null) {
				if (other.agent != null) {
					return false;
				}
			} else if (!this.agent.equals(other.agent)) {
				return false;
			}
			if (this.environment == null) {
				if (other.environment != null) {
					return false;
				}
			} else if (!this.environment.equals(this.environment)) {
				return false;
			} else if (this.command != other.command) {
				return false;
			} else if (this.data == null) {
				if (other.data != null) {
					return false;
				}
			} else if (!this.data.equals(other.data)) {
				return false;
			}
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.agent == null) ? 0 : this.agent.hashCode());
		result = prime
				* result
				+ ((this.environment == null) ? 0 : this.environment.hashCode());
		result = prime * result
				+ ((this.command == null) ? 0 : this.command.hashCode());
		result = prime * result
				+ ((this.data == null) ? 0 : this.data.hashCode());
		return result;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer(PREFIX);
		final String agentName = (this.agent == null) ? "" : this.agent
				.getName();
		final String agent = agentName.replace('\n', ' ').replace(DELIMITER,
				"\\" + DELIMITER);
		final String envName = (this.environment == null) ? ""
				: this.environment.getName();
		final String environment = envName.replace('\n', ' ').replace(
				DELIMITER, "\\" + DELIMITER);
		buffer.append(DELIMITER).append(this.command.name()).append(DELIMITER)
				.append(agent).append(DELIMITER).append(environment)
				.append(DELIMITER).append(this.data.size());
		for (final String data : this.data) {
			final String d = data.replace('\n', ' ').replace(DELIMITER,
					"\\" + DELIMITER);
			buffer.append(DELIMITER).append(d);
		}
		return buffer.toString();
	}

	@SuppressWarnings("deprecation")
	public static DebugCommand fromString(final String string) throws Exception {
		if (string.startsWith(PREFIX)) {
			final String[] s = string.split("(?<!\\\\)\\" + DELIMITER);
			if (s.length >= 5) {
				final Command command = Command.valueOf(s[1]);
				final AgentId agent = s[2].isEmpty() ? null : new AgentId(
						s[2].replace("\\" + DELIMITER, DELIMITER));
				final MessageBoxId environment = s[3].isEmpty() ? null
						: new StubBoxId(s[3].replace("\\" + DELIMITER,
								DELIMITER));
				final int size = Integer.parseInt(s[4]);
				final List<String> data = new ArrayList<>(size);
				for (int i = 5; i < (size + 5); i++) {
					data.add(s[i].replace("\\" + DELIMITER, DELIMITER));
				}
				if (agent != null) {
					return new DebugCommand(command, agent, data);
				} else if (environment != null) {
					return new DebugCommand(command, environment, data);
				} else {
					return new DebugCommand(command, data);
				}
			} else {
				throw new Exception("Not a debug command: " + string);
			}
		} else {
			throw new Exception("Not a debug command: " + string);
		}
	}

	public static class StubBoxId extends MessageBoxId {
		/**
		 *
		 */
		private static final long serialVersionUID = 4910214653662191537L;
		private final String name;

		public StubBoxId(final String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public Type getType() {
			return Type.ENVIRONMENTPORT;
		}
	}
}
