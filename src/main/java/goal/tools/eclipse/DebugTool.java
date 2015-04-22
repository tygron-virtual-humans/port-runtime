package goal.tools.eclipse;

import goal.core.runtime.RuntimeManager;
import goal.preferences.LoggingPreferences;
import goal.preferences.Preferences;
import goal.tools.IDEDebugger;
import goal.tools.IDEGOALInterpreter;
import goal.tools.LaunchManager;
import goal.tools.PlatformManager;
import goal.tools.logging.InfoLog;
import goal.tools.logging.Loggers;

import java.io.File;

import languageTools.program.mas.MASProgram;
import languageTools.program.test.UnitTest;

public class DebugTool {

	public static void main(final String[] args) {
		try {
			final File prefs = new File(args[0]);
			Preferences.changeSettingsFile(prefs);
			Loggers.addConsoleLogger();

			if (LoggingPreferences.getEclipseDebug()) {
				new InfoLog("Initializing debug for " + args[1] + "...");
			}

			final PlatformManager platform = PlatformManager.createNew();
			final File executable = new File(args[1]);
			RuntimeManager<IDEDebugger, IDEGOALInterpreter> runtime;
			if (executable.getName().endsWith("mas2g")) {
				final MASProgram program = platform.parseMASFile(executable);
				runtime = LaunchManager.createNew().launchMAS(program,
						platform.getParsedAgentPrograms());
			} else {
				final UnitTest test = platform.parseUnitTestFile(executable);
				runtime = LaunchManager.createNew().launchTest(test);
			}

			if (args.length > 2) {
				GoalBreakpointManager.loadAll(args[2]);
				for (final File f : platform.getAllGOALFiles()) {
					setFileBreaks(platform, f);
				}
			}

			final EclipseEventObserver observer = new EclipseEventObserver();
			final InputReaderWriter readerwriter = new InputReaderWriter(
					System.in, System.out, runtime, observer);
			readerwriter.start();
			runtime.addObserver(observer);
			runtime.start(false);
			if (LoggingPreferences.getEclipseDebug()) {
				new InfoLog("Started debugging for " + args[1] + "!");
			}
		} catch (final Exception e) {
			InputReaderWriter.logFatal(e);
			System.exit(-1);
		}
	}

	public static void setFileBreaks(final PlatformManager platform,
			final File f) {
		final GoalBreakpointManager manager = GoalBreakpointManager
				.getGoalBreakpointManager(f);
		if (manager != null) {
			platform.getBreakpointManager().setBreakpoints(f,
					manager.getBreakPoints());
		}
		for (final File subf : platform.getImportedFiles(f)) {
			setFileBreaks(platform, subf);
		}
	}
}