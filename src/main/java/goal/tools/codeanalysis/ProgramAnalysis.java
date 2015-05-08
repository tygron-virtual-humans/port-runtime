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

package goal.tools.codeanalysis;

import goal.tools.PlatformManager;
import goal.tools.errorhandling.exceptions.GOALException;

import java.io.File;
import java.io.IOException;

import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.Module;
import languageTools.program.agent.Module.TYPE;

/**
 * Create an analysis of the contents of a GOAL program (#1186).
 *
 * @author W.Pasman 11aug10
 * @modified K.Hindrks dd110404
 *
 */
public class ProgramAnalysis {
	private final File file;
	private final AgentProgram program;
	private final CodeAnalysisOverview programOverview = new CodeAnalysisOverview();
	private CodeAnalysisOverview predicateOverview = new CodeAnalysisOverview();

	/**
	 * Combines the code analysis overview for the program and for the
	 * predicates used in the program.
	 *
	 * @param file
	 *            the program file to be analyzed.
	 * @throws IOException
	 * @throws GOALException
	 */
	public ProgramAnalysis(File file) throws IOException, GOALException {
		this.file = file;

		// Get the program to be analyzed by loading and parsing the program
		// file. Get the associated KR language used in the program file from
		// the MAS registry.

		this.program = PlatformManager.getCurrent().getAgentProgram(file);

		makeProgramCodeAnalysis();

		this.programOverview.addSeparator();

		makePredicateCodeAnalysis();
	}

	/**
	 * Creates the code analysis overview for the program.
	 */
	public void makeProgramCodeAnalysis() {
		this.programOverview.add(
				"Code analysis overview for the GOAL agent file ",
				getFileName());
		this.programOverview.add("#Knowledge clauses in all modules ",
				this.program.getAllKnowledge().size());
		this.programOverview.add("#Belief base clauses in the init module ",
				this.program.getAllBeliefs().size());
		this.programOverview.add("#Goals in agent program ", this.program
				.getAllGoals().size());
		this.programOverview.add("#Modules in agent program ", this.program
				.getModules().size());
		int ruleCount = 0;
		for (Module module : this.program.getModules()) {
			ruleCount += module.getRules().size();
		}
		this.programOverview.add("#Action rules in agent program ", ruleCount);
		this.programOverview.add("#Macros in agent program ", this.program
				.getAllMacros().size());
		this.programOverview.add("#Action specifications in agent program ",
				this.program.getAllActionSpecs().size());

		for (Module module : this.program.getModules()) {
			if (module.getType() != TYPE.ANONYMOUS) {
				// add a separator between foregoing and new module code
				// analysis.
				this.programOverview.addSeparator();
				this.programOverview.add(new ModuleAnalysis(module)
				.getModuleCodeAnalysisOverview());
			}
		}
	}

	/**
	 * DOC
	 */
	public void makePredicateCodeAnalysis() {
		this.predicateOverview = new PredicateAnalysis(this.file, this.program)
		.getPredicateUseReport();
	}

	/**
	 * Returns code analysis for the program.
	 *
	 * @return overview of the code analysis for the program.
	 */
	public CodeAnalysisOverview getProgramCodeAnalysis() {
		return this.programOverview;
	}

	/**
	 * Returns overview of the predicates used in the program.
	 *
	 * @return overview of predicate use.
	 */
	public CodeAnalysisOverview getPredicateCodeAnalysis() {
		return this.predicateOverview;
	}

	/**
	 * Returns the name of the file that is being analyzed.
	 *
	 * @return name of the GOAL agent file.
	 */
	public String getFileName() {
		return this.file.toString();
	}
}
