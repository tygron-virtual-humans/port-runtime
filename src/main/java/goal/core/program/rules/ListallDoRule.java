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

import goal.core.kr.KRlanguage;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Var;
import goal.core.mentalstate.SingleGoal;
import goal.core.program.actions.ActionCombo;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * <p>
 * A rule of the form:<br>
 * <code>&nbsp;&nbsp;&nbsp;&nbsp;listall CONDITION -&gt; VAR do RESULT.</code> <br>
 * or:<br>
 * <code>&nbsp;&nbsp;&nbsp;&nbsp;listall VAR &lt;- CONDITION do RESULT.</code>
 * </p>
 * <p>
 * When evaluated, all possible substitutions that make the condition true are
 * aggregated into a single {@link Term}, with which the given variable is
 * instantiated. Only that variable will be bound in the {@link ActionCombo}
 * that is the result of the rule (aside from possibly parameters of the parent
 * module).<br>
 * The {@link Term} is created as a list of lists, where each 'sub-list'
 * represents a single Substitution. Each value in the 'sub-list' is the value a
 * variable in the condition would have if the corresponding
 * {@link Substitution} was applied. The order of the variables is constant, and
 * is the order of occurrence in the condition (in <i>positive</i> literals).
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class ListallDoRule extends Rule {

	/** Auto-generated serial version ID */
	private static final long serialVersionUID = 8915342242706976088L;

	/**
	 * The variable that, when this rule is applied, is instantiated with the
	 * set of substitutions of this rule's condition.
	 */
	private final Var variable;
	/**
	 * A flag indicating if the rule was defined left-to-right. That is, if this
	 * is true, the rule was defined as COND -> VAR, and as VAR <- COND if this
	 * is false. Only affects toString methods.
	 */
	private final boolean ltr;

	/**
	 * Creates a new {@link ListallDoRule}.
	 *
	 * @param condition
	 *            The condition of the rule.
	 * @param variable
	 *            The variable to which the instances of the condition will be
	 *            bound.
	 * @param action
	 *            The result of executing the new rule.
	 * @param source
	 *            From where in the stream the new {@link ListallDoRule} was
	 *            read. May be null if not created by a parser.
	 * @param ltr
	 *            If the rule was defined with a -&gt;. If false, the rule is
	 *            assumed to be defined with a &lt;-. Only affects the way the
	 *            rule is printed in the {@link #toString()}-methods.
	 */
	public ListallDoRule(MentalStateCond condition, Var variable,
			ActionCombo action, InputStreamPosition source, boolean ltr) {
		super(condition, action, source);

		this.variable = variable;
		this.ltr = ltr;
	}

	/**
	 * @return The variable in this rule to which all substitutions of the
	 *         condition are mapped.
	 */
	public Var getVariable() {
		return this.variable;
	}

	@Override
	public Set<Var> getBoundVar() {
		// only the provided variable is bound.
		Set<Var> boundVars = new LinkedHashSet<>(1);
		boundVars.add(this.variable);
		return boundVars;
	}

	@Override
	public ListallDoRule applySubst(Substitution substitution) {
		// Make sure to not instantiate the variable assigned to by this rule.
		// TODO: can we delegate this to check during compile time?
		Substitution safesubstitution = substitution.clone();
		safesubstitution.remove(this.variable);
		return new ListallDoRule(this.getCondition().applySubst(substitution),
				this.variable, this.getAction().applySubst(safesubstitution),
				this.getSource(), this.ltr);
	}

	/**
	 * Collect all substitutions that hold with the given goal.
	 *
	 * @param substGoalLinks
	 *            the
	 * @param goal
	 * @return all substitutions in substGoalLinks that have given goal in their
	 *         SingleGoal list.
	 */
	private static Set<Substitution> collectSubsts(
			HashMap<Substitution, List<SingleGoal>> substGoalLinks,
			SingleGoal goal) {
		Set<Substitution> substs = new LinkedHashSet<>();
		for (Substitution sub : substGoalLinks.keySet()) {
			if (substGoalLinks.get(sub).contains(goal)) {
				substs.add(sub);
			}
		}
		return substs;
	}

	/**
	 * Combines all given substitutions into a single {@link Term}.
	 *
	 * @param substitutions
	 *            A set of substitutions to be mapped onto a single term.
	 * @param language
	 *            The KR language.
	 * @return A new term for the {@link #variable}. The substitution will be a
	 *         list of all values for that var in the given set of
	 *         {@link Substitution}s.
	 */
	private Term substitutionsToTerm(Set<Substitution> substitutions,
			KRlanguage language) {
		// First make single terms from each substitution.
		List<Term> substsAsTerms = new ArrayList<>(substitutions.size());
		// Get the variables from the condition of the rule; bindings for those
		// variables will be turned into a list.
		Set<Var> boundVar = this.getCondition().getFreeVar();
		List<Term> subTerms;
		for (Substitution substitution : substitutions) {
			subTerms = new LinkedList<>();
			for (Var v : boundVar) {
				if (!v.isAnonymous()) {
					subTerms.add(substitution.get(v));
				}
			}
			// if there is only one bound var, we shouldn't make lists of them.
			// the end result should simply be a list of values instead of a
			// list of singleton lists.
			if (subTerms.size() == 1) {
				substsAsTerms.add(subTerms.get(0));
			} else if (subTerms.size() > 1) {
				substsAsTerms.add(language.makeList(subTerms));
			}
			// if empty, do not add anything.
			// it means there is no substitution, so we want the end result to
			// be '[]' (and not '[[]]')
		}

		// Second combine the substitutions turned into terms into a single list
		// term.
		Term listTerm = language.makeList(substsAsTerms);

		return listTerm;
	}

	/**
	 * Create a set with a single substitution that assigns the (parameter)
	 * solutions to the variable of this rule.
	 *
	 * @param solutions
	 *            The set of solutions to process.
	 * @param language
	 *            The KR language.
	 *
	 * @return A singleton set with a substitution that binds the variable of
	 *         this rule with all solution substitutions provided as parameter.
	 */
	protected Set<Substitution> getVarSubstitution(Set<Substitution> solutions,
			KRlanguage language) {
		// If the solution set is empty, then the variable of this rule should
		// not be instantiated and we simply return the empty set.
		if (solutions.isEmpty()) {
			return solutions;
		}

		// Create the substitution for the variable of this listall rule.
		Substitution varSubst = this.variable.assign(substitutionsToTerm(
				solutions, language));
		// Add that substitution to a set and return it.
		Set<Substitution> result = new LinkedHashSet<>(1);
		result.add(varSubst);
		return result;
	}

	@Override
	public Result apply(RunState<?> runState, Set<Substitution> substset,
			HashMap<Substitution, List<SingleGoal>> substGoalLinks,
			Substitution globalsubst) {

		// #2578. We pick a random validating goal. Under discussion.
		SingleGoal goal = null;
		Set<Substitution> applicableSubst = substset;

		if (substGoalLinks != null) {
			goal = getRandomSingleGoal(substGoalLinks);
			runState.setFocusGoal(goal);
			applicableSubst = collectSubsts(substGoalLinks, goal);
		}

		// Create new substitution, replacing our #variable.
		Term newTerm = substitutionsToTerm(applicableSubst, runState
				.getMentalState().getKRLanguage());
		Substitution fullSubst = globalsubst.clone();
		fullSubst.addBinding(variable, newTerm);

		return getAction().run(runState, fullSubst, true);
	}

	/**
	 * Get a random singlegoal from the available goals
	 *
	 * @param substGoalLinks
	 *            A set of links between Substitutions and lists of SingleGoals
	 *            that validate the context of the target module with the given
	 *            Substitution. Is something (not null) only if
	 *            isRuleSinglegoal().
	 * @return
	 */
	private static SingleGoal getRandomSingleGoal(
			HashMap<Substitution, List<SingleGoal>> substGoalLinks) {
		Set<SingleGoal> allgoals = new LinkedHashSet<>();
		for (List<SingleGoal> goals : substGoalLinks.values()) {
			allgoals.addAll(goals);
		}
		List<SingleGoal> goalslist = new ArrayList<>(allgoals);
		return goalslist.get(new Random().nextInt(goalslist.size()));

	}

	@Override
	public String toRuleString() {
		if (this.ltr) {
			return "listall " + this.getCondition() + " -> " + this.variable
					+ " do " + this.getAction() + ".";
		} else {
			return "listall " + this.variable + " <- " + this.getCondition()
					+ " do " + this.getAction();
		}
	}

	@Override
	public String toString(String linePrefix) {
		return linePrefix + this.toRuleString() + ".";
	}

}
