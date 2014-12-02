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

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Expression;
import goal.core.kr.language.Update;
import goal.core.program.ActionSpecification;
import goal.core.program.Module;
import goal.core.program.rules.Rule;
import goal.tools.errorhandling.exceptions.GOALUserError;

/**
 * {@link DependencyGraph} generator for {@link Module}s.
 *
 * @author K.Hindriks
 *
 */
public class ModuleGraphGenerator extends DependencyGraphGenerator<Module> {

	/**
	 * Creates a graph with all {@link Expression}s in the {@link Module}. Goes
	 * through all subcomponents of the module to create a graph.
	 *
	 */
	@Override
	protected void doCreateGraph(Module subject) {
		// Add knowledge specified in the module as definitions.
		for (DatabaseFormula formula : subject.getKnowledge()) {
			try {
				super.getGraph().add(formula, true, false);
			} catch (GOALUserError error) {
				this.report(error);
			}
		}
		// Add any beliefs specified in the module as definitions.
		for (DatabaseFormula formula : subject.getBeliefs()) {
			try {
				super.getGraph().add(formula, true, false);
			} catch (GOALUserError error) {
				this.report(error);
			}
		}
		// Goals in the goal base act both as definitions and queries.
		// ADHOC? The delete list of the goals should be empty, and should not
		// be added to the graph.
		for (Update goal : subject.getGoals()) {
			for (DatabaseFormula formula : goal.getAddList()) {
				try {
					super.getGraph().add(formula, true, true);
				} catch (GOALUserError error) {
					this.report(error);
				}
			}
		}
		// Add the declarative content of rules in the program section of the
		// module.
		for (Rule rule : subject.getRuleSet()) {
			RuleGraphGenerator ruleGraphGenerator = new RuleGraphGenerator();
			ruleGraphGenerator.createGraph(rule, this);
		}
		// Add the declarative content of action specifications to the graph.
		for (ActionSpecification actionSpec : subject.getActionSpecifications()) {
			ActionSpecificationGraphGenerator actionSpecificationGraphGenerator = new ActionSpecificationGraphGenerator();
			actionSpecificationGraphGenerator.createGraph(actionSpec, this);
		}
		// Recursively, add content of child modules to the graph.
		for (String key : subject.getModules().keySet()) {
			for (Module module : subject.getModules().get(key)) {
				ModuleGraphGenerator moduleGraphGenerator = new ModuleGraphGenerator();
				moduleGraphGenerator.createGraph(module, this);
			}
		}
	}

}
