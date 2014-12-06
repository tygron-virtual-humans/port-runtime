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

import goal.util.Statistics;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import languageTools.program.agent.rules.Rule;

/**
 * Analyzes a set of rules.
 */
public class RuleSetAnalysis {
	private final List<Rule> ruleSet;
	/**
	 * Total number of action rules that appear in the rule set and,
	 * recursively, in (anonymous) modules.
	 */
	private int actionRuleCount;
	/**
	 * Consists of statistical measures over the actions. All actions are
	 * grouped according to the action name, and the number of times that that
	 * action occurs is counted.
	 */
	private final Hashtable<String, Statistics> actionStatistics = new Hashtable<>();
	// conditions
	private final Statistics belAtomStatistics = new Statistics(); // #belief
	// conditions
	private final Statistics agoalAtomStatistics = new Statistics(); // #a-goal
	// conditions
	private final Statistics goalaAtomStatistics = new Statistics(); // #goal-a
	// conditions
	private final Statistics goalAtomStatistics = new Statistics(); // #goal

	/**
	 * Analyzes the rule set.
	 *
	 * @param ruleSet
	 *            The rule set to be analyzed. The rule set may be empty but the
	 *            ruleSet must be non-null.
	 */
	public RuleSetAnalysis(List<Rule> ruleSet) {
		this.ruleSet = ruleSet;
		analyse();
	}

	/**
	 * Analyzes the rule set.
	 */
	private void analyse() {
		this.actionRuleCount = 0;

		for (Rule actionRule : this.ruleSet) {
			RuleAnalysis ruleAnalysis = new RuleAnalysis(actionRule);

			// action rule may invoke nested set of action rules which also need
			// to be counted
			this.actionRuleCount += ruleAnalysis.getNrActionRules();

			// analyze mental state conditions
			this.belAtomStatistics
					.add(new Double(ruleAnalysis.getNrBelAtoms()));
			this.agoalAtomStatistics.add(new Double(ruleAnalysis
					.getNrAGoalAtoms()));
			this.goalAtomStatistics.add(new Double(ruleAnalysis
					.getNrGoalAtoms()));
			this.goalaAtomStatistics.add(new Double(ruleAnalysis
					.getNrGoalaAtoms()));

			// analyze action part
			updateStats(ruleAnalysis.getActions());

			// blunt merge as discussed
			if (ruleAnalysis.getAnonymousModuleStatistics() != null) {
				Merge(ruleAnalysis.getAnonymousModuleStatistics());
			}
		}
	}

	/**
	 * @return The code analysis overview.
	 */
	public CodeAnalysisOverview getRuleSetCodeAnalysis() {
		CodeAnalysisOverview overview = new CodeAnalysisOverview();

		overview.add("#Action rules", this.actionRuleCount);

		for (String name : this.actionStatistics.keySet()) {
			overview.add("#Occurrences of action " + name,
					this.actionStatistics.get(name).getSum());
			// no need to check for emptiness, action must be present
			overview.add("Avg #occurrences in a rule", this.actionStatistics
					.get(name).getMean());
			overview.add("Min #occurrences in a single rule",
					this.actionStatistics.get(name).getMinimum());
			overview.add("Max #occurrences in a single rule",
					this.actionStatistics.get(name).getMaximum());
		}

		overview.add("Mental state conditions statistics", "",
				ItemType.STATISTICS);
		overview.add("#bel atoms", this.belAtomStatistics.getSum());
		if (!this.belAtomStatistics.isEmpty()) {
			overview.add("Avg #bel atoms in a rule",
					this.belAtomStatistics.getMean());
			overview.add("Min #bel atoms in single rule",
					this.belAtomStatistics.getMinimum());
			overview.add("Max #bel atoms in single rule",
					this.belAtomStatistics.getMaximum());
		}
		overview.add("#a-goal atoms", this.agoalAtomStatistics.getSum());
		if (!this.agoalAtomStatistics.isEmpty()) {
			overview.add("Avg #a-goal atoms in a rule",
					this.agoalAtomStatistics.getMean());
			overview.add("Min #a-goal atoms in single rule",
					this.agoalAtomStatistics.getMinimum());
			overview.add("Max #a-goal atoms in single rule",
					this.agoalAtomStatistics.getMaximum());
		}
		overview.add("#goal atoms", this.goalAtomStatistics.getSum());
		if (!this.goalAtomStatistics.isEmpty()) {
			overview.add("Avg #goal atoms in a rule",
					this.goalAtomStatistics.getMean());
			overview.add("Min #goal atoms in single rule",
					this.goalAtomStatistics.getMinimum());
			overview.add("Max #goal atoms in single rule",
					this.goalAtomStatistics.getMaximum());
		}
		overview.add("#goal-a atoms", this.goalaAtomStatistics.getSum());
		if (!this.goalaAtomStatistics.isEmpty()) {
			overview.add("Avg #goal-a atoms in a rule",
					this.goalaAtomStatistics.getMean());
			overview.add("Min #goal-a atoms in single rule",
					this.goalaAtomStatistics.getMinimum());
			overview.add("Max #goal-a atoms in single rule",
					this.goalaAtomStatistics.getMaximum());
		}

		return overview;
	}

	/**
	 * Returns the total number of action rules in the set and in associated
	 * anonymous modules.
	 *
	 * @return The total number of action rules.
	 */
	public Integer getNrActionRules() {
		return this.actionRuleCount;
	}

	/**
	 * Returns statistical information about actions that appear in the rules.
	 *
	 * @return Hashtable<String, Statistics> where String is the name of an
	 *         action and Statistics provides the statistics for that action.
	 */
	private Hashtable<String, Statistics> getActionStatistics() {
		return this.actionStatistics;
	}

	/**
	 * integrate other rule statistics into our own list. For instance we may
	 * have<br>
	 * act1 1,2,0,1,2<br>
	 * act2 0,2,1,0,0<br>
	 * <br>
	 * and other stat may have <br>
	 * act2 0,2<br>
	 * act3 1,1<br>
	 * <br>
	 * What is then supposed to happen is that after the merge we look like this <br>
	 * act1 1,2,0,1,2,0,0<br>
	 * act2 0,2,1,0,0,0,2<br>
	 * act3 0,0,0,0,0,1,1<br>
	 * <br>
	 */
	private void mergeStats(Hashtable<String, Statistics> otherstats) {
		if (otherstats.isEmpty()) {
			return;
		}

		// create blank statistics to fill up actions that did not appear in
		// otherstats.
		Statistics firstotherstat = otherstats.get(otherstats.keys()
				.nextElement());
		Statistics blankstat = new Statistics();
		for (int n = 0; n < firstotherstat.getSize(); n++) {
			blankstat.add(0.0);
		}

		// determine the NEW actions that we did not see yet.
		List<String> newactions = new ArrayList<>(otherstats.keySet());
		newactions.removeAll(this.actionStatistics.keySet());

		// insert the new actions in the statistics.
		for (String newaction : newactions) {
			addEmptyStatistic(newaction);
		}

		// finally, update all statistics.
		for (String action : this.actionStatistics.keySet()) {
			// update statistics for action
			Statistics s = this.actionStatistics.get(action);
			if (otherstats.containsKey(action)) {
				// for done actions, add the new number to the stats
				s.merge(otherstats.get(action));
			} else {
				s.merge(blankstat);
			}
		}
	}

	/**
	 * update the program stats with a given program rule statistics list.
	 *
	 * @param statistics
	 *            is a hashtable of statistics objects. Here, the key is the
	 *            action name and the value is the statistics for that action.
	 * @param newstats
	 *            is a program statistics list. It is a list of (action,number)
	 *            pairs where action is an action name and number is the number
	 *            of occurences of that action.
	 */
	private void updateStats(Hashtable<String, Integer> newstats) {
		// determine the NEW actions that we did not see yet.
		List<String> newactions = new ArrayList<>(newstats.keySet());
		newactions.removeAll(this.actionStatistics.keySet());

		// insert the new actions in the statistics.
		for (String newaction : newactions) {
			addEmptyStatistic(newaction);
		}

		// finally, update all statistics.
		for (String action : this.actionStatistics.keySet()) {
			// update statistics for action
			Statistics s = this.actionStatistics.get(action);
			if (newstats.containsKey(action)) {
				// for done actions, add the new number to the stats
				s.add(new Double(newstats.get(action)));
			} else {
				s.add(0.0);
			}
		}
	}

	/**
	 * New action enters the scene! We have to create a new statistic, with a
	 * leading number of zeros for all previous action rules. Make sure the name
	 * is not yet used, otherwise its statistics will be erased!
	 *
	 * @param statistics
	 *            The statistics hash table that needs the new statistic.
	 * @param newname
	 *            The new name that needs to be created.
	 */
	private void addEmptyStatistic(String newname) {
		Integer numberofcycles = 0;
		if (!this.actionStatistics.isEmpty()) {
			// try to get number of cycles. If fails, we have no cycles yet.
			Statistics firststat = this.actionStatistics.elements()
					.nextElement();
			numberofcycles = firststat.getSize();
		}
		// create array of zeros to instantiate new actions.
		List<Double> emptyActions = new ArrayList<>(numberofcycles);
		for (int n = 0; n < numberofcycles; n++) {
			emptyActions.add(0.);
		}
		Statistics emptystat = new Statistics(emptyActions);
		this.actionStatistics.put(newname, emptystat);
	}

	/**
	 * Returns statistics related to belief conditions.
	 *
	 * @return The statistics about belief mental atoms.
	 */
	public Statistics getBelAtomStatistics() {
		return this.belAtomStatistics;
	}

	/**
	 * Returns statistics related to a-goal conditions.
	 *
	 * @return The statistics about a-goal mental atoms.
	 */
	private Statistics getAGoalAtomStatistics() {
		return this.agoalAtomStatistics;
	}

	/**
	 * Returns statistics related to goal conditions.
	 *
	 * @return The statistics about goal mental atoms.
	 */
	private Statistics getGoalAtomStatistics() {
		return this.goalAtomStatistics;
	}

	/**
	 * Returns statistics related to goala conditions.
	 *
	 * @return The statistics about goala mental atoms.
	 */
	private Statistics getGoalaAtomStatistics() {
		return this.goalaAtomStatistics;
	}

	/**
	 * merge two RuleSetAnalysis results. This is used when two submodules
	 * appear in a single program section
	 *
	 * @param ruleSetAnalysis
	 *            is another RuleSetAnalysis
	 */
	public void Merge(RuleSetAnalysis ruleSetAnalysis) {
		this.belAtomStatistics.merge(ruleSetAnalysis.getBelAtomStatistics());
		this.agoalAtomStatistics
				.merge(ruleSetAnalysis.getAGoalAtomStatistics());
		this.goalaAtomStatistics
				.merge(ruleSetAnalysis.getGoalaAtomStatistics());
		this.goalAtomStatistics.merge(ruleSetAnalysis.getGoalAtomStatistics());
		mergeStats(ruleSetAnalysis.getActionStatistics());
	}
}