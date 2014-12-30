package goal.tools;

import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.preferences.LoggingPreferences;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebugObserver;
import goal.tools.debugger.DebugSettingSynchronizer;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.logging.InfoLog;
import krTools.parser.SourceInfo;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;

public class IDEDebugger extends ObservableDebugger {
	private final DebugSettingSynchronizer observer;
	private volatile boolean firstObserver = false;

	/**
	 * @param id
	 *            the {@link AgentId} that this debugger controls.
	 * @param program
	 *            the {@link AgentProgram} that we are running (used for
	 *            breakpoints).
	 * @param env
	 *            The current environment (if any), used when the 'new agents
	 *            copy environment run state' option is enabled.
	 */
	public IDEDebugger(AgentId id, AgentProgram program, EnvironmentPort env) {
		super(id, env);
		this.observer = new DebugSettingSynchronizer(this);
		setBreakpoints(PlatformManager.getCurrent().getBreakpointManager()
				.getAllBreakpoints(program.getSourceFile()));
	}

	@Override
	public RunMode getInitialRunMode() {
		return RunMode.STEPPING;
	}

	@Override
	public void breakpoint(Channel channel, Object associateObject,
			SourceInfo associateSource, String message, Object... args) {
		while (!this.firstObserver) {
			try {
				if (LoggingPreferences.getEclipseDebug()) {
					new InfoLog("Waiting for first observer...");
				}
				Thread.sleep(10);
			} catch (final Exception ignore) {
			}
		}
		super.breakpoint(channel, associateObject, associateSource, message,
				args);
	}

	@Override
	public void subscribe(DebugObserver observer, Channel channel) {
		super.subscribe(observer, channel);
		this.firstObserver = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		this.observer.stop();
	}
}
