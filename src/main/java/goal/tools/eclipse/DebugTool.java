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
			final File mas2g = new File(args[1]);
			final MASProgram program = platform.parseMASFile(mas2g);

			if (args.length > 2) {
				GoalBreakpointManager.loadAll(args[2]);
				for (final File f : platform.getAllGOALFiles()) {
					setFileBreaks(platform, f);
				}
			}

			final RuntimeManager<IDEDebugger, IDEGOALInterpreter> runtime = LaunchManager
					.createNew().launchMAS(program);
			final EclipseEventObserver observer = new EclipseEventObserver();
			final InputReaderWriter readerwriter = new InputReaderWriter(
					System.in, System.out, runtime, observer);
			readerwriter.start();
			runtime.addObserver(observer);
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