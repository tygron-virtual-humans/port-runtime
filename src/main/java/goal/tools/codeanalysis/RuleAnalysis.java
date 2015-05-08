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

import java.util.Hashtable;

import languageTools.program.agent.Module;
import languageTools.program.agent.Module.TYPE;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.msc.AGoalLiteral;
import languageTools.program.agent.msc.BelLiteral;
import languageTools.program.agent.msc.GoalALiteral;
import languageTools.program.agent.msc.GoalLiteral;
import languageTools.program.agent.msc.MentalFormula;
import languageTools.program.agent.msc.MentalStateCondition;
import languageTools.program.agent.rules.Rule;

/**
 * Creates a <em>static</em> analysis of a program section.
 * <p>
 * The number of occurrences of mental operators (e.g. bel, a-goal, etc) are
 * counted. The number of actions are counted. For an action combo, the number
 * of actions in the combo are summed.
 *
 * @author W.Pasman 11.8.2010
 * @modified Koen dd110404
 */
public class RuleAnalysis {
	// counters for the various mental atoms in conditions of the rules.
	private int belAtomCount = 0;
	private int goalAtomCount = 0;
	private int agoalAtomCount = 0;
	private int goalaAtomCount = 0;
	// non-null when there are anonymous modules.
	private RuleSetAnalysis anonymousModuleAnalysis = null;
	// For the actions, a Hash table with action name and #occurrences is
	// created.
	private final Hashtable<String, Integer> actionsCount = new Hashtable<String, Integer>();

	/**
	 * Analyzes an individual action rule.
	 *
	 * @param actionRule
	 *            The rule to be analyzed.
	 */
	public RuleAnalysis(Rule actionRule) {
		analyzeCondition(actionRule.getCondition());
		for (Action action : actionRule.getAction()) {
			analyzeAction(action);
		}
	}

	/**
	 * Returns the number of action rules including those in anonymous
	 * submodules.
	 *
	 * @return The number of action rules.
	 */
	public Integer getNrActionRules() {
		if (this.anonymousModuleAnalysis != null) {
			return this.anonymousModuleAnalysis.getNrActionRules() + 1;
		}
		return 1;
	}

	/**
	 *
	 * @param action
	 */
	private void analyzeAction(Action action) {
		if (action instanceof ModuleCallAction) {
			Module module = ((ModuleCallAction) action).getTarget();
			if (module.getType() == TYPE.ANONYMOUS) {
				RuleSetAnalysis moduleAnalysis = new RuleSetAnalysis(
						module.getRules());
				if (this.anonymousModuleAnalysis == null) {
					this.anonymousModuleAnalysis = moduleAnalysis;
				} else {
					this.anonymousModuleAnalysis.Merge(moduleAnalysis);
				}
			}
		} else {
			String actionName = action.getName();
			if (this.actionsCount.containsKey(actionName)) {
				// increment
				this.actionsCount.put(actionName,
						this.actionsCount.get(actionName) + 1);
			} else {
				this.actionsCount.put(actionName, 1);
			}
		}
	}

	/**
	 * Analyzes the mental state condition of the rule.
	 *
	 * @param condition
	 *            The mental state condition of the rule.
	 */
	private void analyzeCondition(MentalStateCondition condition) {
		for (MentalFormula mentalLiteral : condition.getSubFormulas()) {
			if (mentalLiteral instanceof BelLiteral) {
				this.belAtomCount++;
			} else if (mentalLiteral instanceof GoalLiteral) {
				this.goalAtomCount++;
			} else if (mentalLiteral instanceof AGoalLiteral) {
				this.agoalAtomCount++;
			} else if (mentalLiteral instanceof GoalALiteral) {
				this.goalaAtomCount++;
			}
		}
	}

	/**
	 * DOC
	 *
	 * @return
	 */
	public Hashtable<String, Integer> getActions() {
		return this.actionsCount;
	}

	/**
	 * @return The number of belief atoms that occur in the rule.
	 */
	public Integer getNrBelAtoms() {
		return this.belAtomCount;
	}

	/**
	 * @return The number of agoal atoms that occur in the rule.
	 */
	public Integer getNrAGoalAtoms() {
		return this.agoalAtomCount;
	}

	/**
	 * @return The number of goala atoms that occur in the rule.
	 */
	public Integer getNrGoalaAtoms() {
		return this.goalaAtomCount;
	}

	/**
	 * @return The number of goal atoms that occur in the rule.
	 */
	public Integer getNrGoalAtoms() {
		return this.goalAtomCount;
	}

	/**
	 * get the integrated submodule statistics. See
	 * {@link RuleSetAnalysis#Merge(RuleSetAnalysis)}
	 *
	 * @return merged submodule statistics for all submodules. Or null if there
	 *         are no submodules.
	 */
	public RuleSetAnalysis getAnonymousModuleStatistics() {
		return this.anonymousModuleAnalysis;
	}
}