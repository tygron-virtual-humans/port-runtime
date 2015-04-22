package goal.tools;

import goal.tools.debugger.BreakPoint;
import goal.tools.debugger.BreakPoint.Type;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import krTools.parser.SourceInfo;
import languageTools.parser.InputStreamPosition;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.Module;
import languageTools.program.agent.Module.TYPE;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.msc.MentalFormula;
import languageTools.program.agent.rules.Rule;

/**
 * Manages all breakpoints set by a user in a file.
 *
 * @author K.Hindriks
 */
public class BreakpointManager {
	/**
	 * Map that keeps track for each file (not necessarily an agent file!) for
	 * which {@link IParsedObject}s present in that file a breakpoint has been
	 * set.
	 */
	private final Map<File, Set<SourceInfo>> breakpoints;
	private final PlatformManager platform;

	public BreakpointManager(PlatformManager platform) {
		this.platform = platform;
		this.breakpoints = new HashMap<>();
	}

	/**
	 * Determines the set of objects in this program on which a breakpoint may
	 * be set.
	 *
	 * @param program
	 *            The program.
	 * @param boolean conditionalOnly Return only the code points that are
	 *        relevant to a conditional breakpoint (no mentalformulas)
	 *
	 * @return The set of objects in the program on which a breakpoint may be
	 *         set. They are sorted. The ID of the objects will be the index in
	 *         the returned list.
	 */
	private static List<InputStreamPosition> getBreakpointObjects(
			AgentProgram program, boolean conditionalOnly) {
		// Collect all objects for which a breakpoint can be set.
		List<InputStreamPosition> objects = new LinkedList<>();

		// All action specifications can be breakpoints.
		// #2117: All condition and action parts of rules can be breakpoints.
		for (Module module : program.getModules()) {
			for (Rule rule : module.getRules()) {
				// FIXME: used to be just 2 objects; now all literals/actions
				// are added
				if (!conditionalOnly) {
					for (MentalFormula literal : rule.getCondition()
							.getSubFormulas()) {
						objects.add((InputStreamPosition) literal
								.getSourceInfo());
					}
				}
				for (Action<?> action : rule.getAction()) {
					objects.add((InputStreamPosition) action.getSourceInfo());
				}
			}
		}

		// All non-anonymous modules may also be breakpoints.
		for (Module module : program.getModules()) {
			if (module.getType() != TYPE.ANONYMOUS) {
				objects.add((InputStreamPosition) module.getSourceInfo());
			}
		}

		Collections.sort(objects);
		return objects;
	}

	/**
	 * Attempts to add a breakpoint.
	 *
	 * @param sourceFile
	 *            The file to add a breakpoint to.
	 * @param bpt
	 *            The breakpoint suggestion that should be added.
	 *
	 * @return <ul>
	 *         <li>-2 if there is no reference to the given source file in this
	 *         registry.</li>
	 *         <li>-1 if there is a reference, but there is no breakpoint object
	 *         after or on the indicated line</li>
	 *         <li>A number &ge; lineNumber indicating the line on which a
	 *         breakpoint was set.</li>
	 *         </ul>
	 */
	private int addBreakpoint(File sourceFile, BreakPoint bpt) {
		// first check if the given file is used by any of the agents files
		List<File> referencingAgentFiles = getReferencingAgentFiles(sourceFile);
		if (referencingAgentFiles.isEmpty()) {
			return -2;
		}
		int line = -1;

		// since the breakpoints are not stored on an agent-basis but a
		// file-basis, we only need to check the breakpoints of one of the
		// agents that reference the given file.
		AgentProgram program = this.platform
				.getAgentProgram(referencingAgentFiles.get(0));
		for (InputStreamPosition bp : getBreakpointObjects(program,
				bpt.getType() == Type.CONDITIONAL)) {
			/*
			 * since the breakpoint-objects are ordered, the first match is the
			 * first object after the given line in the given file.
			 */
			if (bp.definedAfter(sourceFile, bpt.getLine())) {
				line = bp.getLineNumber();
				addBreakpoint(bp);
				break;
			}
		}
		return line;
	}

	/**
	 * Adds a breakpoint to the set of breakpoints of a certain file.
	 *
	 * @param breakpos
	 *            The location to put a breakpoint. Contains a reference to the
	 *            file it is located in.
	 */
	private void addBreakpoint(SourceInfo breakpos) {
		assert breakpos != null;
		if (!this.breakpoints.containsKey(breakpos.getSource())) {
			this.breakpoints.put(breakpos.getSource(),
					new HashSet<SourceInfo>());
		}
		Set<SourceInfo> bps = this.breakpoints.get(breakpos.getSource());
		bps.add(breakpos);
	}

	/**
	 * Sets the breakpoints for the specified file to the specified set of
	 * breakpoints. The given line numbers may not be the actual location of the
	 * breakpoints after this call; they are only indications.
	 *
	 * @param file
	 *            The file to set the breakpoints for.
	 * @param bpts
	 *            The breakpoints to set.
	 */
	public void setBreakpoints(File file, Set<BreakPoint> bpts) {
		List<File> referencingAgentFiles = getReferencingAgentFiles(file);
		// don't bother settings breakpoints for files that are not referenced.
		if (referencingAgentFiles.isEmpty()) {
			return;
		}
		// remove all old breakpoints for the file
		this.breakpoints.remove(file);
		// add all the given lines as new breakpoints
		for (BreakPoint bpt : bpts) {
			addBreakpoint(file, bpt);
		}
	}

	/**
	 * The set of breakpoints for the given file. May return null when that file
	 * is not referenced by any of the parsed agent files in this registry, or
	 * if the agent file is not valid at the moment.
	 *
	 * @param file
	 *            The file to get the breakpoints for
	 * @return A set of IParsedObjects corresponding to breakpoints.
	 */
	public Set<SourceInfo> getBreakpoints(File file) {
		// return null when the agent file is not valid. Indicates that the
		// breakpoints should not be updated.
		List<File> referencingAgentFiles = getReferencingAgentFiles(file);
		if (referencingAgentFiles.isEmpty()) {
			return null;
		}
		// return the breakpoints iff one of the referencing agent files is
		// correctly parsed.
		for (File agentFile : referencingAgentFiles) {
			if (this.platform.getAgentProgram(agentFile) != null) {
				return this.breakpoints.get(file);
			}
		}
		return null;
	}

	/**
	 * Get a list of agent goal files that reference the given file. The goal
	 * files that we check are the {@link PlatformManager#getAllGOALFiles()}.
	 * The given file may be an agent file, in which case that file is included
	 * as well (assuming it is part of this registry).
	 *
	 * @param file
	 *            a {@link File} that agents might refer to.
	 * @return list of all goal files that use given file.
	 */
	private List<File> getReferencingAgentFiles(File file) {
		List<File> referencingAgentFiles = new LinkedList<>();
		for (File agentFile : this.platform.getAllGOALFiles()) {
			/*
			 * for (File importedFile : platform.getAgentProgram(agentFile)
			 * .getImports()) { if (importedFile.equals(file)) {
			 * referencingAgentFiles.add(agentFile); break; } }
			 */
			if (agentFile.getName().equals(file.getName())) {
				referencingAgentFiles.add(agentFile);
			}
		}
		return referencingAgentFiles;
	}

	/**
	 * Get ALL the breakpoints, from the goal file and from the imports of the
	 * goal file.
	 *
	 * @param goalProgramFile
	 *            a file containing a {@link AgentProgram}.
	 * @return list of all breakpoints in the program, including imported files.
	 *         List can be empty if no breakpoints have been set for this goal
	 *         program.
	 */
	public Set<SourceInfo> getAllBreakpoints(File goalProgramFile) {
		HashSet1<SourceInfo> breakpts = new HashSet1<>();
		breakpts.addAll(getBreakpoints(goalProgramFile));
		/*
		 * for (File importedFile : platform.getAgentProgram(goalProgramFile)
		 * .getImports()) { breakpts.addAll(getBreakpoints(importedFile)); }
		 */
		return breakpts;
	}

	/**
	 * set that is robust to adding null.
	 *
	 * @author W.Pasman 16sep14
	 *
	 * @param <T>
	 */
	class HashSet1<T> extends HashSet<T> {
		/**
		 *
		 */
		private static final long serialVersionUID = -8565625930726693792L;

		public void addAll(Set<T> elements) {
			if (elements != null) {
				super.addAll(elements);
			}
		}
	}
}
