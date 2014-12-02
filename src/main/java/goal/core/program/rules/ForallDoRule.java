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

package goal.core.program.rules;

import goal.core.kr.language.Substitution;
import goal.core.mentalstate.SingleGoal;
import goal.core.program.actions.ActionCombo;
import goal.core.program.literals.Macro;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * A rule of the form:<br>
 * <code>&nbsp;&nbsp;&nbsp;&nbsp;forall CONDITION do RESULT.</code><br>
 * <br>
 * Where <code>CONDITION</code> is a {@link MentalStateCond}, and
 * <code>RESULT</code> an {@link ActionCombo}.
 * </p>
 * <p>
 * When evaluated in a {@link RuleSet}, all possible substitutions that make the
 * rule valid may be executed. If the rule is selected for execution, <i>all</i>
 * valid substitutions must be executed.<br>
 * This type of rule can be located in any <code>program{}</code>-section of any
 * module in a GOAL agent.
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified N.Kraayenbrink 9jan11 - Renamed PerceptRule to ForallDoRule
 * @modified K.Hindriks
 */
public class ForallDoRule extends Rule {

	/** Auto-generated serial version ID */
	private static final long serialVersionUID = 1346441169688187288L;

	/**
	 * Creates a new {@link ForallDoRule}.
	 *
	 * @param condition
	 *            What should hold before the rule should be executed.
	 * @param action
	 *            The result of executing the rule.
	 * @param source
	 *            The source code location of this rule, if available;
	 *            {@code null} otherwise.
	 */
	public ForallDoRule(MentalStateCond condition, ActionCombo action,
			InputStreamPosition source) {
		super(condition, action, source);
	}

	@Override
	public ForallDoRule applySubst(Substitution substitution) {
		return new ForallDoRule(this.getCondition().applySubst(substitution),
				this.getAction().applySubst(substitution), this.getSource());
	}

	/**
	 * DOC
	 */
	@Override
	public Result apply(RunState<?> runState, Set<Substitution> substset,
			HashMap<Substitution, List<SingleGoal>> substGoalLinks,
			Substitution gloSubstitution) {
		Result result = new Result();

		// Apply rule as long as there are still substitutions that satisfy its
		// condition, and no {@link ExitModuleAction} has been performed.
		for (Substitution substitution : substset) {
			if (substGoalLinks == null) {
				result.merge(getAction().run(runState, substitution, true));
				if (result.isModuleTerminated()) {
					return result;
				}
			} else {
				for (SingleGoal goal : substGoalLinks.get(substitution)) {
					runState.setFocusGoal(goal);
					result.merge(getAction().run(runState, substitution, true));
					if (result.isModuleTerminated()) {
						return result;
					}
				}
			}
		}
		return result;
	}

	@Override
	public String toString(String linePrefix) {
		return linePrefix + this.toRuleString() + ".";
	}

	@Override
	public String toRuleString() {
		StringBuilder builder = new StringBuilder();

		builder.append("forall ");
		boolean addComma = false;
		for (MentalFormula formula : this.getCondition().getSubFormulas()) {
			if (addComma) {
				builder.append(", ");
			}
			if (formula instanceof Macro) {
				builder.append(((Macro) formula).toShortString());
			} else {
				builder.append(formula.toString());
			}
			addComma = true;
		}

		builder.append(" do ").append(this.getAction());

		return builder.toString();
	}

}
