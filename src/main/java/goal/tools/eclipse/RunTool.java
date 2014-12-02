package goal.tools.eclipse;

import goal.preferences.LoggingPreferences;
import goal.preferences.Preferences;
import goal.tools.PlatformManager;
import goal.tools.SingleRun;
import goal.tools.UnitTestRun;
import goal.tools.UnitTestRunResultInspector;
import goal.tools.logging.InfoLog;
import goal.tools.logging.Loggers;
import goal.tools.unittest.UnitTest;
import goal.tools.unittest.result.UnitTestResultFormatter;

import java.io.File;

public class RunTool {
	public static void main(final String[] args) {
		try {
			final File prefs = new File(args[0]);
			Preferences.changeSettingsFile(prefs);
			Loggers.addConsoleLogger();

			if (LoggingPreferences.getEclipseDebug()) {
				new InfoLog("Initializing run for " + args[1] + "...");
			}
			final File file = new File(args[1]);
			if (PlatformManager.isMASFile(file)) {
				final SingleRun run = new SingleRun(file);
				run.setDebuggerOutput(true);
				run.run();
			} else if (PlatformManager.isTestFile(file)) {
				final PlatformManager platform = PlatformManager.createNew();
				final UnitTest test = platform.parseUnitTestFile(file);
				final UnitTestRun run = new UnitTestRun(test);
				final UnitTestRunResultInspector inspector = new UnitTestRunResultInspector(
						test);
				run.setDebuggerOutput(true);
				run.setResultInspector(inspector);
				run.run();
				final UnitTestResultFormatter formatter = new UnitTestResultFormatter();
				System.out.println(formatter.visit(inspector.getResults()));
			} else {
				throw new Exception("Unrecognized file: " + file);
			}
			System.exit(0);
		} catch (final Exception e) {
			InputReaderWriter.logFatal(e);
			System.exit(-1);
		}
	}
}