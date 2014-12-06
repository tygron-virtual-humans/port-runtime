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

import java.io.File;
import java.util.Hashtable;

import krTools.language.Expression;
import languageTools.program.agent.AgentProgram;

/**
 * Performs a code analysis of the predicates in a GOAL agent program.
 *
 * @author W.Pasman 30.3.2011
 * @modified K.Hindriks dd110404
 */
public class PredicateAnalysis {

	private final File file;
	private final AgentProgram program;
	private int numDefinitions; // number of definitions in expressionGraph

	private final CodeAnalysisOverview overview = new CodeAnalysisOverview();

	/**
	 * String is name of predicate, e.g. 'clear' Integer is the number of
	 * occurrences of that predicate.
	 */
	private final Hashtable<String, Integer> expressioncounts = new Hashtable<>();

	/**
	 * Analyzes predicates in the program and creates a report.
	 *
	 * @param file
	 *            The file associated to the program.
	 * @param program
	 *            is program to be analysed.
	 */
	public PredicateAnalysis(File file, AgentProgram program) {
		this.file = file;
		this.program = program;

		performAnalysis();
		createPredicateUseReport();
	}

	/**
	 * Counts occurrences of predicates by creating an expression graph and
	 * expression hash table.
	 * TODO: disabled (AgentProgramGraphGenerator and ExpressionGraph are removed)
	 */
	private void performAnalysis() {
		/*AgentProgramGraphGenerator programGraphGenerator = new AgentProgramGraphGenerator();
		programGraphGenerator.setKRInterface(program.getKRInterface());
		programGraphGenerator.createGraph(program, null);
		ExpressionGraph expressionGraph = programGraphGenerator.getGraph();

		numDefinitions = 0;
		for (Expression exp : expressionGraph.getAllDefinitions()) {
			numDefinitions++;
			String op = exp.getSignature();
			if (expressioncounts.containsKey(op)) {
				expressioncounts.put(op, expressioncounts.get(op) + 1);
			} else {
				expressioncounts.put(op, 1);
			}
		}*/
	}

	/**
	 * Creates a report for the program.
	 */
	public void createPredicateUseReport() {
		overview.add("Overview of use of predicates in the agent program",
				file.toString());

		overview.add("Total number of predicates used", numDefinitions);

		for (String predicate : expressioncounts.keySet()) {
			overview.add("#Occurences of " + predicate,
					expressioncounts.get(predicate));
		}
	}

	public CodeAnalysisOverview getPredicateUseReport() {
		return overview;
	}
}