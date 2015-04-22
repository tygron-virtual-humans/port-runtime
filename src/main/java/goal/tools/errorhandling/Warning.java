package goal.tools.errorhandling;

import goal.tools.debugger.Debugger;

/**
 * Stub for the old Warning class. Now just maps into ExceptionLogRecord. TODO
 * remove this entirely, use ExceptionLogRecord straight away.
 * 
 * @author W.Pasman 5feb15
 *
 */
public class Warning extends ExceptionLogRecord {

	public Warning(Debugger debugger, String warning, Throwable cause) {
		super(debugger, warning, cause);
	}

	public Warning(String warning, Throwable error) {
		super(warning, error);
	}

	public Warning(String warning) {
		super(warning, null);
	}

	public Warning(Debugger debugger, String warning) {
		super(debugger, warning, null);
	}
}