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

package goal.core.program.expressiongraph;

import goal.core.kr.language.DatabaseFormula;
import goal.core.program.actions.Action;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.AdoptOneAction;
import goal.core.program.actions.DeleteAction;
import goal.core.program.actions.DropAction;
import goal.core.program.actions.InsertAction;
import goal.core.program.literals.AGoalLiteral;
import goal.core.program.literals.GoalLiteral;
import goal.core.program.literals.MentalLiteral;
import goal.core.program.rules.Rule;

/**
 * {@link ExpressionGraphGenerator} for {@link Rule}s. Adds the declarative
 * content of the {@link Rule} to an expression graph.
 *
 * @author K.Hindriks
 */
@Deprecated
public class RuleGraphGenerator extends ExpressionGraphGenerator<Rule> {

	@Override
	protected void doCreateGraph(Rule subject) {
		// Add the rule's condition.
		boolean hasFilterAction = subject.getAction().hasFilterFocusAction();
		for (MentalLiteral lit : subject.getCondition().getLiterals()) {
			// Literals in the condition are queries.
			// CHECK: also negative literals? perhaps delegate to MentalFormula
			// to make more generic?
			super.getGraph().addQuery(lit.getFormula());
			// positive (a-)goal literals are also definitions when a
			// filter-focus action is present
			if (hasFilterAction && lit.isPositive()) {
				if (lit instanceof AGoalLiteral || lit instanceof GoalLiteral) {
					for (DatabaseFormula formula : lit.getFormula().toUpdate()
							.getAddList()) {
						super.getGraph().addDefinition(formula);
					}
				}
			}
		}

		// Add declarative content of the rule's action to the graph.
		for (Action action : subject.getAction()) {
			if (action instanceof AdoptAction) {
				AdoptAction adopt = (AdoptAction) action;
				// The add-list are both queries and definitions.
				// there should be no delete list, and if there was
				// it should not be added.
				for (DatabaseFormula formula : adopt.getGoal().getAddList()) {
					super.getGraph().addDefinition(formula);
					super.getGraph().addQuery(formula);
				}
			}
			if (action instanceof AdoptOneAction) {
				AdoptOneAction adoptone = (AdoptOneAction) action;
				// The add-list are both queries and definitions.
				// there should be no delete list, and if there was
				// it should not be added.
				for (DatabaseFormula formula : adoptone.getGoal().getAddList()) {
					super.getGraph().addDefinition(formula);
					super.getGraph().addQuery(formula);
				}
			}
			if (action instanceof DropAction) {
				DropAction drop = (DropAction) action;
				// The add-list are queries.
				// there should be no delete list.
				for (DatabaseFormula formula : drop.getGoal().getAddList()) {
					super.getGraph().addQuery(formula);
				}
			}
			if (action instanceof DeleteAction) {
				DeleteAction delete = (DeleteAction) action;
				// negative occurrences in a DeleteAction are definitions
				for (DatabaseFormula formula : delete.getBelief()
						.getDeleteList()) {
					super.getGraph().addDefinition(formula);
				}
			}
			if (action instanceof InsertAction) {
				InsertAction insert = (InsertAction) action;
				// positive occurrences in an InsertAction are definitions
				for (DatabaseFormula formula : insert.getBelief().getAddList()) {
					super.getGraph().addDefinition(formula);
				}
			}
		}
	}
}
