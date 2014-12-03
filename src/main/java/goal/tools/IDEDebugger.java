package goal.tools;

import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.preferences.LoggingPreferences;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebugObserver;
import goal.tools.debugger.DebugSettingSynchronizer;
import goal.tools.debugger.ObservableDebugger;
import goal.tools.logging.InfoLog;

import java.io.File;

public class IDEDebugger extends ObservableDebugger {
	private final DebugSettingSynchronizer observer;
	private volatile boolean firstObserver = false;

	/**
	 * @param id
	 *            the {@link AgentId} that this debugger controls.
	 * @param program
	 *            the {@link AgentProgram} that we are running (used for
	 *            breakpoints).
	 * @param goalProgramFile
	 *            file behind program.
	 * @param env
	 *            The current environment (if any), used when the 'new agents
	 *            copy environment run state' option is enabled.
	 */
	public IDEDebugger(AgentId id, AgentProgram program, File goalProgramFile,
			EnvironmentPort env) {
		super(id, env);
		observer = new DebugSettingSynchronizer(this);
		setBreakpoints(PlatformManager.getCurrent().getBreakpointManager()
				.getAllBreakpoints(goalProgramFile));
	}

	@Override
	public RunMode getInitialRunMode() {
		return RunMode.STEPPING;
	}

	@Override
	public void breakpoint(Channel channel, Object associate, String message,
			Object... args) {
		while (!this.firstObserver) {
			try {
				if (LoggingPreferences.getEclipseDebug()) {
					new InfoLog("Waiting for first observer...");
				}
				Thread.sleep(10);
			} catch (final Exception ignore) {
			}
		}
		super.breakpoint(channel, associate, message, args);
	}

	@Override
	public void subscribe(DebugObserver observer, Channel channel) {
		super.subscribe(observer, channel);
		this.firstObserver = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		observer.stop();
	}
}
