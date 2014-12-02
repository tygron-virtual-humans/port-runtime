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
import goal.core.kr.language.Query;
import goal.core.program.actions.Action;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.AdoptOneAction;
import goal.core.program.actions.DeleteAction;
import goal.core.program.actions.DropAction;
import goal.core.program.actions.InsertAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.literals.AGoalLiteral;
import goal.core.program.literals.GoalLiteral;
import goal.core.program.literals.MentalLiteral;
import goal.core.program.rules.Rule;
import goal.tools.errorhandling.exceptions.GOALUserError;

/**
 * {@link DependencyGraph} generator for {@link Rule}s. Adds the declarative
 * content of the {@link Rule} to a dependency graph.
 *
 * @author K.Hindriks
 *
 */
public class RuleGraphGenerator extends DependencyGraphGenerator<Rule> {

	@Override
	protected void doCreateGraph(Rule subject) {
		// Add the rule's condition.
		boolean hasFilterAction = subject.getAction().hasFilterFocusAction();
		for (MentalLiteral lit : subject.getCondition().getLiterals()) {
			// Literals in the condition are queries.
			// CHECK: also negative literals? perhaps delegate to MentalFormula
			// to make more generic?
			try {
				super.getGraph().add(lit.getFormula());
			} catch (GOALUserError error) {
				this.report(error);
			}
			// positive (a-)goal literals are also definitions when a
			// filter-focus action is present
			if (hasFilterAction && lit.isPositive()) {
				if (lit instanceof AGoalLiteral || lit instanceof GoalLiteral) {
					for (DatabaseFormula formula : lit.getFormula().toUpdate()
							.getAddList()) {
						try {
							super.getGraph().add(formula, true, false);
						} catch (GOALUserError error) {
							this.report(error);
						}
					}
				}
			}
		}

		// Add declarative content of the rule's action to the graph.
		try {
			for (Action action : subject.getAction()) {
				// Handle cases for all types of {@link MentalAction}s.
				if (action instanceof AdoptAction) {
					AdoptAction adopt = (AdoptAction) action;
					// The add-list consists of both queries and definitions.
					// There should be no delete list.
					for (DatabaseFormula formula : adopt.getGoal().getAddList()) {
						super.getGraph().add(formula, true, true);
					}
				}
				if (action instanceof AdoptOneAction) {
					AdoptOneAction adoptone = (AdoptOneAction) action;
					// The add-list consists of both queries and definitions.
					// There should be no delete list.
					for (DatabaseFormula formula : adoptone.getGoal()
							.getAddList()) {
						super.getGraph().add(formula, true, true);
					}
				}
				if (action instanceof DropAction) {
					DropAction drop = (DropAction) action;
					// The add-list consists of queries.
					// There should be no delete list.
					for (DatabaseFormula formula : drop.getGoal().getAddList()) {
						super.getGraph().add(formula, false, true);
					}
				}
				if (action instanceof DeleteAction) {
					DeleteAction delete = (DeleteAction) action;
					// Negative occurrences in a DeleteAction are definitions.
					for (DatabaseFormula formula : delete.getBelief()
							.getDeleteList()) {
						super.getGraph().add(formula, true, false);
					}
				}
				if (action instanceof InsertAction) {
					InsertAction insert = (InsertAction) action;
					// positive occurrences in an InsertAction are definitions
					for (DatabaseFormula formula : insert.getBelief()
							.getAddList()) {
						super.getGraph().add(formula, true, false);
					}
				}
				// ASSUMES that Graph Generators are run after Validators.
				if (action instanceof UserSpecAction) {
					UserSpecAction userspec = (UserSpecAction) action;
					for (Query formula : userspec.getPreconditions()) {
						super.getGraph().add(formula);
					}
				}
			}
		} catch (GOALUserError error) {
			this.report(error);
		}
	}

}
