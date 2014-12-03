package goal.tools.debugger;

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
	public void breakpoint(Channel channel, Object associate, String message,
			Object... args) {
		if (killed) {
			throw new DebuggerKilledException();
		}
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public void kill() {
		killed = true;
	}

	@Override
	public void reset() {
		killed = false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
