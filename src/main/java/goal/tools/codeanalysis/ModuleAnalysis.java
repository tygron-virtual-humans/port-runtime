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

import languageTools.program.agent.Module;

/**
 * Creates a code analysis for a module.
 *
 * @author W.Pasman 30.3.2011
 * @modified Koen dd110404
 */
public class ModuleAnalysis {

	private final Module module; // the module that is being analyzed.
	private final CodeAnalysisOverview moduleOverview = new CodeAnalysisOverview();
	private RuleSetAnalysis actionRulesAnalysis;

	/**
	 * Creates a code analysis for the module.
	 *
	 * @param module
	 *            module to be analyzed.
	 */
	public ModuleAnalysis(Module module) {
		this.module = module;

		makeModuleCodeAnalysis();
	}

	/**
	 * Creates a code analysis for the module.
	 */
	public void makeModuleCodeAnalysis() {
		this.moduleOverview.add("Code analysis overview for the module",
				this.module.getName());
		this.moduleOverview.add("#Knowledge clauses", this.module
				.getKnowledge().size());
		this.moduleOverview.add("#Goals", this.module.getGoals().size());
		this.actionRulesAnalysis = new RuleSetAnalysis(this.module.getRules());
		this.moduleOverview.add(this.actionRulesAnalysis
				.getRuleSetCodeAnalysis());
		this.moduleOverview.add("#Action specifications", this.module
				.getActionSpecifications().size());
	}

	/**
	 * Returns the code analysis overview for the module analyzed.
	 *
	 * @return code analysis overview for module.
	 */
	public CodeAnalysisOverview getModuleCodeAnalysisOverview() {
		return this.moduleOverview;
	}
}