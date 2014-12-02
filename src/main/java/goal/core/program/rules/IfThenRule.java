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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * <p>
 * A rule of the form:<br>
 * <code>&nbsp;&nbsp;&nbsp;&nbsp;if CONDITION then RESULT</code><br>
 * <br>
 * Where <code>CONDITION</code> is a {@link MentalStateCond}, and
 * <code>RESULT</code> an {@link ActionCombo}.
 * </p>
 * <p>
 * When evaluated in a {@link RuleSet}, of all possible {@link Substitution}s
 * that make the rule valid, only one may be executed.<br>
 * This type of rule can be located in any <code>program{}</code>-section of any
 * module in a GOAL agent, as well as in the <code>perceptrules{}</code>
 * section.
 * </p>
 *
 * @author K.Hindriks
 * @modified N.Kraayenbrink - now has an {@link ActionCombo} as body.
 * @modified N.Kraayenbrink 9jan11 - renamed ActionRule to IfThenRule
 * @modified K.Hindriks
 */
public class IfThenRule extends Rule {

	/** Auto-generated serial version ID */
	private static final long serialVersionUID = 5072450381961516054L;

	/**
	 * Creates a new {@link IfThenRule} from a condition and a result.
	 *
	 * @param condition
	 *            The condition of the new rule. The result will never happen if
	 *            this condition is not satisfied.
	 * @param action
	 *            The result of the new rule. If there is a {@link Substitution}
	 *            that makes the condition {@code true} and this a valid
	 *            {@link ActionCombo}, it may be considered for execution.
	 * @param source
	 *            From where in the stream the new {@link IfThenRule} was read.
	 *            May be {@code null} if the rule is not created by a parser.
	 */
	public IfThenRule(MentalStateCond condition, ActionCombo action,
			InputStreamPosition source) {
		super(condition, action, source);
	}

	@Override
	public IfThenRule applySubst(Substitution substitution) {
		return new IfThenRule(this.getCondition().applySubst(substitution),
				this.getAction().applySubst(substitution), this.getSource());
	}

	@Override
	public Result apply(RunState<?> runState, Set<Substitution> substset,
			HashMap<Substitution, List<SingleGoal>> substGoalLinks,
			Substitution globaSubstitution) {
		Result result = new Result();
		// TODO: does not yet take collecting of goals for FILTER and SELECT
		// options of modules into account...

		// Shuffle list of substitutions.
		List<Substitution> substlist = new ArrayList<>(substset);
		Collections.shuffle(substlist);

		// Find action whose precondition also holds and perform it.
		// We later handle {@link ExitModuleAction}.
		final int max = substlist.size() - 1;
		for (int i = 0; i <= max; i++) {
			/**
			 * find the single goal that made this substitution true Stays null
			 * if this is not {@link Rule#isRuleSinglegoal()}.
			 */
			final Substitution subst = substlist.get(i);
			if (substGoalLinks != null) {
				List<SingleGoal> validatingGoals = substGoalLinks.get(subst);
				runState.setFocusGoal(validatingGoals.get(new Random()
						.nextInt(validatingGoals.size())));
			}

			result.merge(this.getAction().run(runState, subst, i == max));
			if (result.hasPerformedAction()) {
				break;
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

		builder.append("if ");
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

		builder.append(" then ").append(this.getAction());

		return builder.toString();
	}

}
