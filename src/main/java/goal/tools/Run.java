package goal.tools;

import goal.tools.logging.Loggers;
import goal.tools.unittest.result.UnitTestResult;
import goal.tools.unittest.result.UnitTestResultFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import krTools.errors.exceptions.ParserException;
import languageTools.program.test.AgentTest;
import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.Messaging;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import rmimessaging.RmiMessaging;

/**
 * Command line utility to run multi-agent systems and tests. Will accept one or
 * more .mas2g and .test2g files and run them. Options can be used to repeat
 * runs and enable learning between runs.
 *
 * Each program will be ran until all the agents have terminated or the
 * environment is terminated or the program times out, which ever happens first.
 *
 * <pre>
 * {@code
 * usage: goal.tools.Run [options] [[file|directory]]
 *  -d,--debug               Display out put from debugger while running
 *                           agent
 *  -e                       Print messages from error
 *  -h,--help                Displays this help
 *  -i                       Print messages from info
 *     --license             Shows the license
 *  -p                       Print messages from parser
 *  -r,--repeats <number>    Number of times to repeat running all episodes
 *     --recursive           Recursively search for mas files
 *     --rmi <host>          Use RMI messaging middleware. Host is the
 *                           location of the RMI server. Using "localhost" will
 *                           initialize a RMI server
 *  -v,--verbose             Print all messages
 *     --version             Shows the current version
 *  -w                       Print messages from warning
 *
 *
 * }
 * </pre>
 *
 * @author mpkorstanje
 * @modified K.Hindriks
 */
public class Run {
	private static final String OPTION_HELP = "help";
	private static final String OPTION_HELP_SHORT = "h";

	private static final String OPTION_LICENSE = "license";
	private static final String OPTION_VERSION = "version";

	private static final String OPTION_RECURSIVE = "recursive";

	private static final String OPTION_RMI_MESSAGING = "rmi";

	private static final String OPTION_DEBUG = "debug";
	private static final char OPTION_DEBUG_SHORT = 'd';

	private static final String OPTION_VERBOSE = "verbose";
	private static final char OPTION_VERBOSE_SHORT = 'v';
	private static final char OPTION_VERBOSE_PARSER = 'p';
	private static final char OPTION_VERBOSE_ERROR = 'e';
	private static final char OPTION_VERBOSE_WARNING = 'w';
	private static final char OPTION_VERBOSE_INFO = 'i';

	private static final String OPTION_REPEATS = "repeats";
	private static final char OPTION_REPEATS_SHORT = 'r';

	private static final Options options = createOptions();

	public static void main(String[] args) {
		try {
			run(args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			showHelp();
		} catch (Exception e) {
			System.out.println("Exception during execution: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws ParseException
	 * @throws ParserException
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public static void run(String... args) throws ParseException,
	ParserException, FileNotFoundException, Exception {
		// Get start time.
		long startTime = System.nanoTime();

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		/*
		 * Handle general options.
		 */
		if (cmd.hasOption(OPTION_HELP)) {
			showHelp();
			return;
		}

		if (cmd.hasOption(OPTION_LICENSE)) {
			showLicense();
			return;
		}

		if (cmd.hasOption(OPTION_VERSION)) {
			showVersion();
			return;
		}

		// Verbose makes other verbose options irrelevant.
		if (cmd.hasOption(OPTION_VERBOSE_SHORT)) {
			Loggers.addConsoleLogger();
		} else {
			if (cmd.hasOption(OPTION_VERBOSE_INFO)) {
				Loggers.getInfoLogger().addConsoleLogger();
			}

			if (cmd.hasOption(OPTION_VERBOSE_WARNING)) {
				Loggers.getWarningLogger().addConsoleLogger();
			}

			if (cmd.hasOption(OPTION_VERBOSE_ERROR)) {
				Loggers.getRuntimeErrorLogger().addConsoleLogger();
			}

			if (cmd.hasOption(OPTION_VERBOSE_PARSER)) {
				Loggers.getParserLogger().addConsoleLogger();
			}
		}

		final boolean debuggerOutput = cmd.hasOption(OPTION_DEBUG);

		final Messaging messaging;
		final String host;
		if (cmd.hasOption(OPTION_RMI_MESSAGING)) {
			messaging = new RmiMessaging();
			host = cmd.getOptionValue(OPTION_RMI_MESSAGING);
		} else {
			messaging = new LocalMessaging();
			host = "localhost";
		}

		/*
		 * Run .mas2g files.
		 */
		List<File> masFiles = parseFileArguments(cmd.getArgs(),
				new MASProgramFilter(cmd.hasOption(OPTION_RECURSIVE)));

		BatchRun repeatedBatchRun = new BatchRun(masFiles);
		repeatedBatchRun.setDebuggerOutput(debuggerOutput);

		if (cmd.hasOption(OPTION_REPEATS)) {
			Number repeats = (Number) cmd.getParsedOptionValue(OPTION_REPEATS);
			repeatedBatchRun.setRepeats(repeats.longValue());
		}

		repeatedBatchRun.setMessagingHost(host);
		repeatedBatchRun.setMessaging(messaging);

		repeatedBatchRun.run();

		/*
		 * Run .test2g files.
		 */
		List<File> testFiles = parseFileArguments(cmd.getArgs(),
				new UnitTestFilter(cmd.hasOption(OPTION_RECURSIVE)));
		List<UnitTestResult> results = new ArrayList<>(testFiles.size());

		for (File unitTestFile : testFiles) {
			AgentTest unitTest = PlatformManager.createNew().parseUnitTestFile(
					unitTestFile);
			UnitTestRun testRun = new UnitTestRun(unitTest);
			UnitTestRunResultInspector inspector = new UnitTestRunResultInspector(
					unitTest);
			testRun.setDebuggerOutput(debuggerOutput);
			testRun.setMessaging(messaging);
			testRun.setResultInspector(inspector);
			testRun.setMessagingHost(host);
			testRun.run();
			results.add(inspector.getResults());

		}

		UnitTestResultFormatter formatter = new UnitTestResultFormatter();
		int passed = 0;
		for (UnitTestResult result : results) {
			System.out.println(formatter.visit(result));
			if (result.isPassed()) {
				passed += 1;
			}
		}

		Loggers.removeConsoleLogger();

		if (!results.isEmpty()) {
			System.out.println("Tests: " + results.size() + " passed: "
					+ passed + " failed: " + (results.size() - passed));
		}

		// Get elapsed time.
		long elapsedTime = (System.nanoTime() - startTime) / 1000000;
		System.out
		.println("Took " + elapsedTime + " milliseconds to run jobs.");
	}

	/**
	 * Parses any left over arguments as files.
	 *
	 * @param arguments
	 *            holding the unparsed arguments.
	 * @param function
	 *            that transforms a file into a list of T
	 *
	 * @return a list of T
	 * @throws ParseException
	 *             when no left over arguments were present
	 * @throws ParserException
	 *             when the file could not be parsed
	 * @throws FileNotFoundException
	 *             when the argument was not a file or directory
	 */
	private static List<File> parseFileArguments(String[] arguments,
			FileFilter filter) throws FileNotFoundException {
		if (arguments.length == 0) {
			throw new FileNotFoundException("Missing file or directory");
		}

		List<File> files = new LinkedList<>();
		for (String fileOrFolder : arguments) {
			File f = new File(fileOrFolder);
			if (f.isDirectory() || f.isFile()) {
				files.addAll(filter.proccess(f));
			} else {
				throw new FileNotFoundException(fileOrFolder
						+ " was neither a file nor a directory");
			}
		}
		return files;
	}

	private static interface FileFilter {
		public abstract List<File> proccess(File f);
	}

	private static class MASProgramFilter implements FileFilter {
		private final boolean recursive;

		public MASProgramFilter(boolean recursive) {
			this.recursive = recursive;
		}

		@Override
		public List<File> proccess(File f) {
			return PlatformManager.getMASFiles(f, this.recursive);
		}
	}

	private static class UnitTestFilter implements FileFilter {
		private final boolean recursive;

		public UnitTestFilter(boolean recursive) {
			this.recursive = recursive;
		}

		@Override
		public List<File> proccess(File f) {
			return PlatformManager.getUnitTestFiles(f, this.recursive);
		}
	}

	/**
	 * Creates the command line options.
	 *
	 * @return the command line options.
	 */
	private static Options createOptions() {
		Options options = new Options();

		OptionBuilder.withDescription("Print all messages");
		OptionBuilder.withLongOpt(OPTION_VERBOSE);
		options.addOption(OptionBuilder.create(OPTION_VERBOSE_SHORT));

		OptionBuilder.withDescription("Print messages from parser");
		options.addOption(OptionBuilder.create(OPTION_VERBOSE_PARSER));

		OptionBuilder.withDescription("Print messages from info");
		options.addOption(OptionBuilder.create(OPTION_VERBOSE_INFO));

		OptionBuilder.withDescription("Print messages from error");
		options.addOption(OptionBuilder.create(OPTION_VERBOSE_ERROR));

		OptionBuilder.withDescription("Print messages from warning");
		options.addOption(OptionBuilder.create(OPTION_VERBOSE_WARNING));

		options.addOption(new Option(OPTION_HELP_SHORT, OPTION_HELP, false,
				"Displays this help"));

		OptionBuilder.withDescription("Shows the license");
		OptionBuilder.withLongOpt(OPTION_LICENSE);
		options.addOption(OptionBuilder.create());

		OptionBuilder.withLongOpt(OPTION_VERSION);
		OptionBuilder.withDescription("Shows the current version");
		options.addOption(OptionBuilder.create());

		OptionBuilder.withLongOpt(OPTION_REPEATS);
		OptionBuilder.withArgName("number");
		OptionBuilder
		.withDescription("Number of times to repeat running all episodes");
		OptionBuilder.hasArg();
		OptionBuilder.withType(Number.class);
		options.addOption(OptionBuilder.create(OPTION_REPEATS_SHORT));

		OptionBuilder.withLongOpt(OPTION_RECURSIVE);
		OptionBuilder.withDescription("Recursively search for mas files");
		options.addOption(OptionBuilder.create());

		OptionBuilder.withLongOpt(OPTION_DEBUG);
		OptionBuilder
		.withDescription("Display out put from debugger while running agent");
		options.addOption(OptionBuilder.create(OPTION_DEBUG_SHORT));

		OptionBuilder.withLongOpt(OPTION_RMI_MESSAGING);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("host");
		OptionBuilder
		.withDescription("Use RMI messaging middleware. Host is the location of the RMI server. Using \"localhost\" will initialize a RMI server");
		options.addOption(OptionBuilder.create());

		return options;
	}

	/**
	 * Prints the version of GOAL used.
	 */
	private static void showVersion() {
		// TODO: GOAL currently uses some regex magic when building a release to
		// set the code correctly. This should be in a property file.
	}

	/**
	 * Prints the help for the command line options.
	 */
	private static void showHelp() {
		System.out.println("GOAL Copyright (C) 2014 GPLv3");
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(Run.class.getCanonicalName()
				+ " [options] [[file|directory]]", options);
	}

	/**
	 * Print the license; required by GPL v3.
	 */
	private static void showLicense() {
		System.out
		.println("GOAL interpreter that facilitates developing and executing GOAL multi-agent programs.\n"
				+ "Copyright (C) 2014  K.V. Hindriks\n\n"
				+ "This program is free software: you can redistribute it and/or modify\n"
				+ "it under the terms of the GNU General Public License as published by\n"
				+ "the Free Software Foundation, either version 3 of the License, or\n"
				+ "(at your option) any later version.\n\n"
				+ "This program is distributed in the hope that it will be useful,\n"
				+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
				+ "GNU General Public License for more details.\n\n"
				+ "You should have received a copy of the GNU General Public License\n"
				+ "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
	}

}
