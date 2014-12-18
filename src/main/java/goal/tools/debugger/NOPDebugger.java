package goal.tools.debugger;

import krTools.parser.SourceInfo;
import languageTools.program.agent.AgentId;

public class NOPDebugger implements Debugger {
	private final String id;
	private boolean killed = false;

	public NOPDebugger(AgentId id) {
		this(id.getName());
	}

	public NOPDebugger(String id) {
		this.id = id;
	}

	@Override
	public void breakpoint(Channel channel, Object associateObject,
			SourceInfo associateSource, String message, Object... args) {
		if (this.killed) {
			throw new DebuggerKilledException();
		}
	}

	@Override
	public String getName() {
		return this.id;
	}

	@Override
	public void kill() {
		this.killed = true;
	}

	@Override
	public void reset() {
		this.killed = false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
