package goal.tools.eclipse;

import eis.iilang.EnvironmentState;
import goal.core.agent.Agent;
import goal.core.runtime.RuntimeManager;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.preferences.LoggingPreferences;
import goal.tools.BreakpointManager;
import goal.tools.IDEDebugger;
import goal.tools.IDEGOALInterpreter;
import goal.tools.PlatformManager;
import goal.tools.eclipse.DebugCommand.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;

public class InputReaderWriter extends Thread {
	private final BufferedReader input;
	private final BufferedWriter output;
	private final RuntimeManager<IDEDebugger, IDEGOALInterpreter> runtime;
	private final EclipseEventObserver observer;

	protected InputReaderWriter(final InputStream is, final OutputStream os,
			final RuntimeManager<IDEDebugger, IDEGOALInterpreter> runtime,
			final EclipseEventObserver observer) {
		this.input = new BufferedReader(new InputStreamReader(is));
		this.output = new BufferedWriter(new OutputStreamWriter(os));
		this.runtime = runtime;
		observer.setWriter(this);
		this.observer = observer;
	}

	public synchronized void write(final DebugCommand c) {
		write(c.toString());
	}

	public synchronized void write(final Exception e) {
		write(e.getMessage().replace('\n', ' '));
	}

	public synchronized void write(final String s) {
		try {
			this.output.write(s);
			this.output.newLine();
			this.output.flush();
		} catch (final Exception e) {
			logFatal(e);
		}
	}

	public static void logFatal(final Exception e) {
		if (LoggingPreferences.getLogToFile()) {
			final File f = new File(LoggingPreferences.getLogDirectory()
					+ File.separator + "exceptions.log");
			try (final PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
				e.printStackTrace(pw);
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		} else {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (this.runtime != null) {
			try {
				String line = this.input.readLine();
				if (line != null) {
					final DebugCommand read = DebugCommand.fromString(line);
					final boolean handled = processCommand(read);
					if (!handled) {
						throw new Exception("Unhandled command: " + read);
					}
				}
			} catch (final Exception e) {
				write(e);
			}
		}
		try {
			this.input.close();
			this.output.close();
		} catch (final Exception ignore) {
		}
	}

	private boolean processCommand(final DebugCommand command) {
		switch (command.getCommand()) {
		case PAUSE: // TODO: environment is never paused?!
			for (final Agent<IDEGOALInterpreter> agent : this.runtime
					.getAgents()) {
				try {
					if (agent.getId().equals(command.getAgent())) {
						final IDEGOALInterpreter controller = agent
								.getController();
						if (controller.isRunning()) {
							agent.getController().getDebugger().finestep();
						}
						return true;
					}
				} catch (final Exception e) {
					write(e);
				}
			}
			return false;
		case ENV_PAUSE:
			for (final EnvironmentPort env : this.runtime.getEnvironmentPorts()) {
				try {
					if (env.getMessageBoxId().getName()
							.equals(command.getEnvironment().getName())) {
						env.pause();
						return true;
					}
				} catch (final Exception e) {
					write(e);
				}
			}
			return false;
		case RUN:
			for (final Agent<IDEGOALInterpreter> agent : this.runtime
					.getAgents()) {
				try {
					if (agent.getId().equals(command.getAgent())) {
						final IDEGOALInterpreter controller = agent
								.getController();
						controller.getDebugger().run();
						if (!controller.isRunning()) {
							try {
								agent.reset();
							} catch (final Exception e) {
								write(e);
							}
							this.observer.getObserver(agent).suspendAtSource();
						}
						return true;
					}
				} catch (final Exception e) {
					write(e);
				}
			}
			return false;
		case ENV_RUN:
			for (final EnvironmentPort env : this.runtime.getEnvironmentPorts()) {
				try {
					if (env.getMessageBoxId().getName()
							.equals(command.getEnvironment().getName())) {
						env.start();
						Thread.sleep(100); // TODO: env.start is aysnc?!
						if (!env.getEnvironmentState().equals(
								EnvironmentState.RUNNING)) {
							try {
								env.reset();
							} catch (final Exception e) {
								write(e);
							}
						}
						return true;
					}
				} catch (final Exception e) {
					write(e);
				}
			}
			return false;
		case STEP:
			for (final Agent<IDEGOALInterpreter> agent : this.runtime
					.getAgents()) {
				try {
					if (agent.getId().equals(command.getAgent())) {
						final IDEGOALInterpreter controller = agent
								.getController();
						if (controller.isRunning()) {
							controller.getDebugger().step();
						} else {
							this.observer.getObserver(agent).suspendAtSource();
						}
						return true;
					}
				} catch (final Exception e) {
					write(e);
				}
			}
			return false;
		case EVAL:
			String result = "";
			if (command.getAgent() == null) { // watch expression
				for (final Agent<IDEGOALInterpreter> agent : this.runtime
						.getAgents()) {
					result += agent.getId().getName() + ": ";
					try {
						final QueryTool query = new QueryTool(agent);
						result += query.doquery(command.getData()).replace(
								'\n', ' ');
					} catch (final Exception e) {
						result += e.getMessage().replace('\n', ' ');
					}
					result += "\n";
				}
				write(new DebugCommand(Command.EVAL, result));
			} else { // interactive console
				for (final Agent<IDEGOALInterpreter> agent : this.runtime
						.getAgents()) {
					if (agent.getId().equals(command.getAgent())) {
						try {
							final QueryTool query = new QueryTool(agent);
							try {
								result = query.doaction(command.getData());
							} catch (final Exception ignore) {
								result = query.doquery(command.getData());
							}
							result = result.replace('\n', ' ');
						} catch (final Exception e) {
							result = e.getMessage().replace('\n', ' ');
						}
						break;
					}
				}
				write(new DebugCommand(Command.EVAL, command.getAgent(), result));
			}
			return true;
		case BREAKS:
			final PlatformManager platform = PlatformManager.getCurrent();
			GoalBreakpointManager.loadAll(command.getData());
			for (final File f : platform.getAllGOALFiles()) {
				DebugTool.setFileBreaks(platform, f);
			}
			final BreakpointManager breaks = platform.getBreakpointManager();
			for (final Agent<IDEGOALInterpreter> agent : this.runtime
					.getAgents()) {
				try {
					final Set<IParsedObject> newbreakpoints = breaks
							.getBreakpoints(agent.getController().getProgram()
									.getSource().getSourceFile());
					agent.getController().getDebugger()
							.setBreakpoints(newbreakpoints);
				} catch (final Exception e) {
					write(e);
				}
			}
			return true;
		case STOP:
			try {
				runtime.shutDown();
			} catch (final Exception ignore) {
			} finally {
				System.exit(0);
			}
			return true;
		default:
			return false;
		}
	}
}
