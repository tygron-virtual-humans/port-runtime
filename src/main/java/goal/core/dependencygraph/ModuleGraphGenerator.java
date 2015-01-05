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

package goal.core.dependencygraph;

import krTools.errors.exceptions.KRException;
import krTools.language.DatabaseFormula;
import krTools.language.Expression;
import krTools.language.Query;
import krTools.language.Update;
import languageTools.program.agent.ActionSpecification;
import languageTools.program.agent.Module;
import languageTools.program.agent.rules.Rule;
import mentalState.DependencyGraph;

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
			} catch (KRException error) {
				report(error);
			}
		}
		// Add any beliefs specified in the module as definitions.
		for (DatabaseFormula formula : subject.getBeliefs()) {
			try {
				super.getGraph().add(formula, true, false);
			} catch (KRException error) {
				report(error);
			}
		}
		// Goals in the goal base act both as definitions and queries.
		// ADHOC? The delete list of the goals should be empty, and should not
		// be added to the graph.
		for (Query query : subject.getGoals()) {
			if (query.isUpdate()) {
				Update goal = query.toUpdate();
				for (DatabaseFormula formula : goal.getAddList()) {
					try {
						super.getGraph().add(formula, true, true);
					} catch (KRException error) {
						report(error);
					}
				}
			}
		}
		// Add the declarative content of rules in the program section of the
		// module.
		for (Rule rule : subject.getRules()) {
			RuleGraphGenerator ruleGraphGenerator = new RuleGraphGenerator();
			ruleGraphGenerator.createGraph(rule, this);
		}
		// Add the declarative content of action specifications to the graph.
		for (ActionSpecification actionSpec : subject.getActionSpecifications()) {
			ActionSpecificationGraphGenerator actionSpecificationGraphGenerator = new ActionSpecificationGraphGenerator();
			actionSpecificationGraphGenerator.createGraph(actionSpec, this);
		}
	}

}
