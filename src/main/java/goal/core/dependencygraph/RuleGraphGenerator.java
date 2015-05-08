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
import languageTools.program.agent.Module.FocusMethod;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ActionCombo;
import languageTools.program.agent.actions.AdoptAction;
import languageTools.program.agent.actions.DeleteAction;
import languageTools.program.agent.actions.DropAction;
import languageTools.program.agent.actions.InsertAction;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.actions.UserSpecAction;
import languageTools.program.agent.msc.AGoalLiteral;
import languageTools.program.agent.msc.GoalLiteral;
import languageTools.program.agent.msc.MentalLiteral;
import languageTools.program.agent.rules.Rule;
import mentalState.DependencyGraph;

/**
 * {@link DependencyGraph} generator for {@link Rule}s. Adds the declarative
 * content of the {@link Rule} to a dependency graph.
 *
 * @author K.Hindriks
 *
 */
public class RuleGraphGenerator extends DependencyGraphGenerator<Rule> {
	/**
	 * Checks whether one of the action in the {@link ActionCombo} is a call to
	 * a module, i.e., a {@link ModuleCallAction} with associated
	 * {@link FocusMethod#FILTER}.
	 *
	 * @return {@code true} if at least one of the actions in the action combo
	 *         is a call to a module, i.e., a focus action, with filter as
	 *         associated focus method.
	 */
	public static boolean hasFilterFocusAction(ActionCombo combo) {
		for (Action<?> action : combo) {
			if (action instanceof ModuleCallAction) {
				if (((ModuleCallAction) action).getTarget().getFocusMethod() == FocusMethod.FILTER) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void doCreateGraph(Rule subject) {
		// Add the rule's condition.
		boolean hasFilterAction = hasFilterFocusAction(subject.getAction());
		for (MentalLiteral lit : subject.getCondition().getAllLiterals()) {
			// Literals in the condition are queries.
			// CHECK: also negative literals? perhaps delegate to MentalFormula
			// to make more generic?
			try {
				super.getGraph().add(lit.getFormula());
			} catch (KRException error) {
				report(error);
			}
			// positive (a-)goal literals are also definitions when a
			// filter-focus action is present
			if (hasFilterAction && lit.isPositive()) {
				if (lit instanceof AGoalLiteral || lit instanceof GoalLiteral) {
					for (DatabaseFormula formula : lit.getFormula().toUpdate()
							.getAddList()) {
						try {
							super.getGraph().add(formula, true, false);
						} catch (KRException error) {
							report(error);
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
					for (DatabaseFormula formula : adopt.getUpdate()
							.getAddList()) {
						super.getGraph().add(formula, true, true);
					}
				}
				/*
				 * else if (action instanceof AdoptOneAction) { AdoptOneAction
				 * adoptone = (AdoptOneAction) action; // The add-list consists
				 * of both queries and definitions. // There should be no delete
				 * list. for (DatabaseFormula formula : adoptone.getUpdate()
				 * .getAddList()) { super.getGraph().add(formula, true, true); }
				 * }
				 */
				else if (action instanceof DropAction) {
					DropAction drop = (DropAction) action;
					// The add-list consists of queries.
					// There should be no delete list.
					for (DatabaseFormula formula : drop.getUpdate()
							.getAddList()) {
						super.getGraph().add(formula, false, true);
					}
				} else if (action instanceof DeleteAction) {
					DeleteAction delete = (DeleteAction) action;
					// Negative occurrences in a DeleteAction are definitions.
					for (DatabaseFormula formula : delete.getUpdate()
							.getDeleteList()) {
						super.getGraph().add(formula, true, false);
					}
				} else if (action instanceof InsertAction) {
					InsertAction insert = (InsertAction) action;
					// positive occurrences in an InsertAction are definitions
					for (DatabaseFormula formula : insert.getUpdate()
							.getAddList()) {
						super.getGraph().add(formula, true, false);
					}
				}
				// ASSUMES that Graph Generators are run after Validators.
				else if (action instanceof UserSpecAction) {
					UserSpecAction userspec = (UserSpecAction) action;
					for (MentalLiteral formula : userspec.getPrecondition()
							.getAllLiterals()) {
						super.getGraph().add(formula.getFormula());
					}
				}
			}
		} catch (KRException error) {
			report(error);
		}
	}
}
