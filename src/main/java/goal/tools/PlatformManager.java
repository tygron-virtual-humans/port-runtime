/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.tools;

import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.logging.GOALLogger;
import goal.tools.logging.Loggers;
import goal.tools.logging.StringsLogRecord;
import goal.util.Extension;
import goalhub.krTools.KRFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import krTools.KRInterface;
import krTools.errors.exceptions.ParserException;
import languageTools.analyzer.agent.AgentValidator;
import languageTools.analyzer.mas.MASValidator;
import languageTools.analyzer.test.TestValidator;
import languageTools.errors.Message;
import languageTools.program.Program;
import languageTools.program.agent.AgentProgram;
import languageTools.program.mas.MASProgram;
import languageTools.program.test.UnitTest;

import org.apache.commons.io.IOUtils;

/**
 * FIXME: This class should not be needed. {@link MASProgram}s,
 * {@link AgentProgram}s and {@link UnitTest}s should contain internally
 * references to all their relevant files. Removing this class allows a large
 * amount of static state to be removed from the GOAL core.
 *
 * <p>
 * Keeps track of all files and their associated parsed objects by maintaining a
 * static map that contains of file-parsed object pairs. This provides a unique
 * store of file references and their associated parsed objects that can be
 * accessed by other classes that need access to the result of parsing a file.
 * </p>
 * <p>
 * The {@link PlatformManager} is a <i>utility class</i> that provides
 * functionality mainly for and related to <i>parsing</i> file types recognized
 * by the GOAL platform (@see {@link Extension}).
 * </p>
 *
 * @since 23jul09: PlatformManager extends Observable (see TRAC #696). Inspired
 *        by Jade (PlatformController.Listener) the PM sends platformEvent
 *        objects to its listeners.
 * @since 27jul09: PlatformManager merges JADE platform events with
 *        DebugObserver platform events (Agent.SLEEPS, etc).
 * @author W.Pasman modified 20jul09.
 * @modified KH
 * @modified W.Pasman removed rmi host stuff. Use preferences and middleware
 *           init call for that.
 * @modified W.Pasman 28jun2011 made singleton, as there can be only one
 *           platform manager in the system
 * @modified K.Hindriks Now a utility class that provides static access to all
 *           files and their associated parsed objects.
 */
public class PlatformManager {
	/**
	 * The current PlatformManager
	 */
	private static PlatformManager current;
	/**
	 * The associated BreakpointManager (lazily loaded)
	 */
	private BreakpointManager breaks;
	/**
	 * All files with associated parsed objects.
	 */
	private final Map<File, Program> parsedFiles;

	public static PlatformManager getCurrent() {
		if (current == null) {
			return createNew();
		} else {
			return current;
		}
	}

	public static PlatformManager createNew() {
		current = new PlatformManager();
		for (final String language : KRFactory.getSupportedInterfaces()) {
			/*
			 * try { FIXME: no longer necessary/possible?!
			 * KRFactory.getInterface(language).reset(); } catch (final
			 * KRInitFailedException e) { new Warning(String.format(
			 * Resources.get(WarningStrings.INTERNAL_PROBLEM),
			 * "PlatformManager::createNew()", language), e); }
			 */
		}
		return current;
	}

	private PlatformManager() {
		this.parsedFiles = new HashMap<>();
	}

	public BreakpointManager getBreakpointManager() {
		if (this.breaks == null) {
			this.breaks = new BreakpointManager(this);
		}
		return this.breaks;
	}

	/**
	 * Adds a file and associated parsed object pair to the store of such
	 * objects maintained by the {@link PlatformManager}.
	 *
	 * @param file
	 * @param program
	 */
	public void addParsedProgram(File file, Program program) {
		this.parsedFiles.put(file, program);
	}

	/**
	 * Remove a file from the map of parsed objects {@link #parsedFiles}.
	 *
	 * @param file
	 */
	public void removeParsedProgram(File file) {
		this.parsedFiles.remove(file);
	}

	public Map<File, AgentProgram> getParsedAgentPrograms() {
		Map<File, AgentProgram> returned = new HashMap<>();
		for (File file : this.parsedFiles.keySet()) {
			Program program = this.parsedFiles.get(file);
			if (program instanceof AgentProgram) {
				returned.put(file, (AgentProgram) program);
			}
		}
		return returned;
	}

	/**
	 * DOC
	 *
	 * @param file
	 * @return
	 */
	public MASProgram getMASProgram(File file) {
		if (isMASFile(file)) {
			return (MASProgram) this.parsedFiles.get(file);
		} else {
			return null;
		}
	}

	/**
	 * DOC
	 *
	 * @param agentFile
	 *            The agent file.
	 * @return a set of MAS programs that use the given agent file.
	 */
	public Set<MASProgram> getMASProgramsThatUseFile(File agentFile) {
		Set<MASProgram> masPrograms = new HashSet<>();
		for (File file : this.parsedFiles.keySet()) {
			if (isMASFile(file)) {
				MASProgram masProgram = (MASProgram) this.parsedFiles.get(file);
				if (masProgram.getAgentFiles().contains(agentFile)) {
					masPrograms.add(masProgram);
				}
			}
		}
		return masPrograms;
	}

	/**
	 * Checks whether file is a MAS file.
	 *
	 * @param file
	 *            The file to check.
	 * @return {@code true} if extension of the file is {@link Extension#MAS}.
	 */
	public static boolean isMASFile(File file) {
		return Extension.getFileExtension(file) == Extension.MAS;
	}

	/**
	 * Checks whether file is a test file.
	 *
	 * @param file
	 *            The file to check.
	 * @return {@code true} if extension of the file is {@link Extension#MAS}.
	 */
	public static boolean isTestFile(File file) {
		return Extension.getFileExtension(file) == Extension.TEST;
	}

	/**
	 * Returns all files that have been parsed and are managed by this
	 * {@link PlatformManager}.
	 *
	 * @return The set of all files that have been parsed.
	 *
	 *         TODO: remove this method... Only used by
	 *         {@link BreakpointManager}.
	 */
	public Set<File> getAllGOALFiles() {
		Set<File> agentFiles = new HashSet<>();
		for (File file : this.parsedFiles.keySet()) {
			if (isGOALFile(file)) {
				agentFiles.add(file);
			}
		}
		return agentFiles;
	}

	/**
	 * Returns the agent program that is the result of parsing the given file.
	 *
	 * @param file
	 *            The file that is the source for the agent program.
	 * @return The agent program, if the file is an agent (.goal) file;
	 *         {@code null} otherwise.
	 */
	public AgentProgram getAgentProgram(File file) {
		if (isGOALFile(file)) {
			return (AgentProgram) this.parsedFiles.get(file);
		} else {
			return null;
		}
	}

	/**
	 * Checks whether file is a GOAL file.
	 *
	 * @param file
	 *            The file to check.
	 * @return {@code true} if extension of the file is {@link Extension#GOAL}.
	 */
	private static boolean isGOALFile(File file) {
		return Extension.getFileExtension(file) == Extension.GOAL;
	}

	/**
	 * If argument is a mas2g file it will be added. If the argument is a folder
	 * all mas2g files in it will be added. If <code>recursive</code> is
	 * {@code true}, mas2g files from all sub-folders (and sub-sub-folders,
	 * etc.) will also be added.
	 *
	 * @param fileOrFolder
	 *            File or folder to load mas2g file(s) from.
	 * @param recursive
	 *            If {@code true} all mas2g files in subfolders will also be
	 *            loaded.
	 * @return List of the MAS files that were loaded.
	 */
	public static List<File> getMASFiles(File fileOrFolder, boolean recursive) {
		List<File> masFiles = new LinkedList<>();

		if (fileOrFolder.isFile() && isMASFile(fileOrFolder)) {
			masFiles.add(fileOrFolder);
			return masFiles;
		}

		if (fileOrFolder.isDirectory()) {
			for (File file : fileOrFolder.listFiles()) {
				if (file.isFile()) {
					masFiles.addAll(getMASFiles(file, recursive));
				}

				if (file.isDirectory() && recursive) {
					masFiles.addAll(getMASFiles(file, recursive));
				}
			}
		}

		return masFiles;
	}

	/**
	 * If argument is a test2g file it will be added. If the argument is a
	 * folder all test2g files in it will be added. If <code>recursive</code> is
	 * {@code true}, test2g files from all sub-folders (and sub-sub-folders,
	 * etc.) will also be added.
	 *
	 * @param fileOrFolder
	 *            File or folder to load test2g file(s) from.
	 * @param recursive
	 *            If {@code true} all test2g files in subfolders will also be
	 *            loaded.
	 * @return List of the test2g files that were loaded.
	 */
	public static List<File> getUnitTestFiles(File fileOrFolder,
			boolean recursive) {
		List<File> files = new LinkedList<>();

		if (fileOrFolder.isFile() && isTestFile(fileOrFolder)) {
			files.add(fileOrFolder);
			return files;
		}

		if (fileOrFolder.isDirectory()) {
			for (File file : fileOrFolder.listFiles()) {
				if (file.isFile()) {
					files.addAll(getUnitTestFiles(file, recursive));
				}

				if (file.isDirectory() && recursive) {
					files.addAll(getUnitTestFiles(file, recursive));
				}
			}
		}

		return files;
	}

	public UnitTest parseUnitTestFile(File file) throws ParserException {
		// Logger to report issues found during parsing and validation.
		GOALLogger parserLogger = Loggers.getParserLogger();
		parserLogger.log(new StringsLogRecord(Level.INFO, "Parsing test file " //$NON-NLS-1$
				+ file.getPath()));

		TestValidator validator = new TestValidator(file.getPath());
		validator.validate();
		UnitTest test = validator.getProgram();
		if (test == null) {
			throw new ParserException("Invalid Test file"); //$NON-NLS-1$
		}
		addParsedProgram(file, test);

		// Report any errors encountered during parsing.
		boolean hasErrors = false, hasWarnings = false;
		for (Message error : validator.getSyntaxErrors()) {
			hasErrors = true;
			parserLogger.log(new StringsLogRecord(Level.SEVERE, error
					.toString()));
		}
		for (Message error : validator.getErrors()) {
			hasErrors = true;
			parserLogger.log(new StringsLogRecord(Level.SEVERE, error
					.toString()));
		}
		for (Message warning : validator.getWarnings()) {
			hasWarnings = true;
			parserLogger.log(new StringsLogRecord(Level.WARNING, warning
					.toString()));
		}

		if (hasErrors) {
			parserLogger.log(new StringsLogRecord(Level.INFO,
					"Parsing completed with errors.")); //$NON-NLS-1$
			// Also output warning to IDE console.
			Loggers.getWarningLogger().logln(
					String.format(Resources.get(WarningStrings.PARSING_ERRORS),
							file.getPath()));
			return test;
		} else {
			// Build message for final log report.
			StringBuilder message = new StringBuilder(35);
			message.append(String.format(
					Resources.get(WarningStrings.PARSING_COMPLETED),
					file.getPath()));
			if (hasWarnings) {
				message.append(Resources.get(WarningStrings.WITH_WARNINGS));
			}
			// Provide message to logger.
			parserLogger.log(new StringsLogRecord(Level.INFO, message
					.toString()));

			return test;
		}
	}

	/**
	 * Creates a MAS program from a MAS (.mas2g) file. Loads the file, parses
	 * its content, and validates the result.
	 *
	 * @param masFile
	 *            The the MAS (.mas2g) file.
	 * @throws ParserException
	 *             If ANTLR stream cannot be opened. If parsing MAS file
	 *             generates errors.
	 * @throws IOException
	 *             If file could not be closed properly.
	 * @return The parsed and validated MAS program.
	 */
	public MASProgram parseMASFile(File masFile) throws ParserException {
		// Logger to report issues found during parsing and validation.
		GOALLogger parserLogger = Loggers.getParserLogger();
		parserLogger.log(new StringsLogRecord(Level.INFO, "Parsing mas file " //$NON-NLS-1$
				+ masFile.getPath()));

		MASValidator validator = new MASValidator(masFile.getPath());
		validator.validate();
		MASProgram masProgram = validator.getProgram();
		if (masProgram == null) {
			throw new ParserException("Invalid MAS file"); //$NON-NLS-1$
		}
		addParsedProgram(masFile, masProgram);

		// Log messages.
		boolean hasErrors = false, hasWarnings = false;
		for (Message error : validator.getSyntaxErrors()) {
			hasErrors = true;
			parserLogger.log(new StringsLogRecord(Level.SEVERE, error
					.toString()));
		}
		for (Message error : validator.getErrors()) {
			hasErrors = true;
			parserLogger.log(new StringsLogRecord(Level.SEVERE, error
					.toString()));
		}
		for (Message warning : validator.getWarnings()) {
			hasWarnings = true;
			parserLogger.log(new StringsLogRecord(Level.WARNING, warning
					.toString()));
		}

		if (hasErrors) {
			// Output errors to parser tab (via parser logger).
			parserLogger.log(new StringsLogRecord(Level.INFO,
					"Parsing completed with errors.")); //$NON-NLS-1$
			// Also output warning to IDE console.
			Loggers.getWarningLogger().logln(
					String.format(Resources.get(WarningStrings.PARSING_ERRORS),
							masFile.getPath()));

			// parse any found agent files anyway
			parseGOALFiles(masProgram);

			// no need to further process the program if errors exist.
			return masProgram;
		} else {
			// Build message for final log report.
			StringBuilder message = new StringBuilder(35);
			message.append(String.format(
					Resources.get(WarningStrings.PARSING_COMPLETED),
					masFile.getPath()));
			if (hasWarnings) {
				message.append(Resources.get(WarningStrings.WITH_WARNINGS));
			}
			// Provide message to logger.
			parserLogger.log(new StringsLogRecord(Level.INFO, message
					.toString()));

			// Parse the agent files that are part of the MAS file.
			parseGOALFiles(masProgram);

			return masProgram;
		}
	}

	/**
	 * Parse the given agent files.
	 *
	 * @param agentFiles
	 *            The agent files.
	 */
	public void parseGOALFiles(MASProgram mas) {
		for (File agentFile : mas.getAgentFiles()) {
			try {
				parseGOALFile(agentFile, mas.getKRInterface(agentFile));
			} catch (Exception e) {
				throw new GOALBug("Unexpected failure in parser", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * TODO: duplicates most of the code of {@link parseMASFile}...
	 *
	 * Returns a GOAL program that is the result of parsing the agent file.
	 *
	 * @param goalFile
	 *            The agent file to load.
	 * @param language
	 *            The KR language that is used in the agent file.
	 * @return GOAL program resulting from parsing the agent file.
	 * @throws ParserException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be opened for
	 *             reading. Or if an ANTLR stream could not be opened. if the
	 *             parser or lexer encountered errors while parsing the given
	 *             .goal file. Indicates that the returned value is null or
	 *             otherwise invalid.
	 */
	public AgentProgram parseGOALFile(File goalFile, KRInterface language)
			throws ParserException {
		// Logger to report issues found during parsing and validation.
		GOALLogger parserLogger = Loggers.getParserLogger();
		parserLogger.log(new StringsLogRecord(Level.INFO, "Parsing agent file " //$NON-NLS-1$
				+ goalFile.getPath()));

		AgentValidator validator = new AgentValidator(goalFile.getPath());
		validator.setKRInterface(language);
		validator.validate();
		AgentProgram program = validator.getProgram();
		if (program == null) {
			throw new ParserException("Invalid GOAL file"); //$NON-NLS-1$
		}
		addParsedProgram(goalFile, program);

		// Report any errors encountered during parsing.
		boolean hasErrors = false, hasWarnings = false;
		for (Message error : validator.getSyntaxErrors()) {
			hasErrors = true;
			parserLogger.log(new StringsLogRecord(Level.SEVERE, error
					.toString()));
		}
		for (Message error : validator.getErrors()) {
			hasErrors = true;
			parserLogger.log(new StringsLogRecord(Level.SEVERE, error
					.toString()));
		}
		for (Message warning : validator.getWarnings()) {
			hasWarnings = true;
			parserLogger.log(new StringsLogRecord(Level.WARNING, warning
					.toString()));
		}

		if (hasErrors) {
			parserLogger.log(new StringsLogRecord(Level.INFO,
					"Parsing completed with errors.")); //$NON-NLS-1$
			// Also output warning to IDE console.
			Loggers.getWarningLogger().logln(
					String.format(Resources.get(WarningStrings.PARSING_ERRORS),
							goalFile.getPath()));
			return program;
		} else {
			// Build message for final log report.
			StringBuilder message = new StringBuilder(35);
			message.append(String.format(
					Resources.get(WarningStrings.PARSING_COMPLETED),
					goalFile.getPath()));
			if (hasWarnings) {
				message.append(Resources.get(WarningStrings.WITH_WARNINGS));
			}
			// Provide message to logger.
			parserLogger.log(new StringsLogRecord(Level.INFO, message
					.toString()));

			return program;
		}
	}

	/**
	 * Gets the files representing the child files of the given agent file. A
	 * file is a child file if the agent file has an #import &lt; child file
	 * &gt; command. This returns only DIRECT children of the file, not children
	 * of children.
	 *
	 * @param agentFile
	 *            The agent file to get the child files of.
	 * @return The list of files that are imported by the given agent file. A
	 *         new list is created every time this method is called.
	 */
	public List<File> getImportedFiles(File agentFile) {
		final AgentProgram file = getAgentProgram(agentFile);
		if (file != null) {
			return file.getImportedModules();
		} else {
			return new ArrayList<>(0);
		}
	}

	/**
	 * Creates a new file and inserts a corresponding template into the file for
	 * a given extension. Overwrites already existing file.
	 *
	 * @param newFile
	 *            The file to be created.
	 * @param extension
	 *            The {@link Extension} the file to be created should have. The
	 *            extension is also used to select and apply the corresponding
	 *            template to insert initial content in the file.
	 *
	 * @return The newly created or emptied file (CHECK which is just the same
	 *         file as given as first param?).
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public static File createfile(File newFile, Extension extension)
			throws IOException {
		/**
		 * do not use f.createNewFile() because that does not overwrite existing
		 * files.
		 */

		// make sure the folder to write the file to exists.
		newFile.getParentFile().mkdirs();

		// Copy template if we have an extension.
		if (extension != null) {
			try (FileOutputStream outFile = new FileOutputStream(newFile)) {
				String templatename = "template" //$NON-NLS-1$
						+ extension.toString().toLowerCase();
				try (InputStream template = ClassLoader.getSystemClassLoader()
						.getResourceAsStream(
								"goal/tools/SimpleIDE/files/" + templatename)) { //$NON-NLS-1$
					if (template != null) {
						IOUtils.copy(template, outFile);
					}
				}
			}
		}

		return newFile;
	}

}