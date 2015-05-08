package goal.tools.debugger;

import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

/**
 * DOC what is this exception ? It makes no sense to kill a debugger as a
 * debugger is a utility class for an agent?
 */
public class DebuggerKilledException extends GOALRuntimeErrorException {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 5945016622339822624L;

	public DebuggerKilledException() {
		super("Debugger terminated the agent");
	}

	public DebuggerKilledException(String string, Exception e) {
		super(string, e);
	}
}