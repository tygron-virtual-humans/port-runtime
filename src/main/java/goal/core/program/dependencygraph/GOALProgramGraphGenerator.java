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

package goal.core.program.dependencygraph;

import goal.core.kr.language.Expression;
import goal.core.program.GOALProgram;
import goal.core.program.Module;

/**
 * {@link DependencyGraph} generator for {@link GOALProgram}s.
 *
 * @author K.Hindriks
 *
 */
public class GOALProgramGraphGenerator extends
		DependencyGraphGenerator<GOALProgram> {

	/**
	 * Creates a graph with all {@link Expression}s in this program. Goes
	 * through all modules to create a graph.
	 *
	 * @throws UnsupportedOperationException
	 *             When this agent's KRLanguage does not support creating a
	 *             dependency graph.
	 * @throws UnsatisfiedLinkError
	 *             When the agent's KRLanguage could not be initialized.
	 */
	@Override
	protected void doCreateGraph(GOALProgram subject) {
		// the macro definitions and import commands do not contribute
		// to the dependency graph, only modules.
		for (String key : subject.getModules().keySet()) {
			for (Module module : subject.getModules().get(key)) {
				ModuleGraphGenerator moduleGraphGenerator = new ModuleGraphGenerator();
				moduleGraphGenerator.createGraph(module, this);
			}
		}
	}

}
