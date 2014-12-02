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
import goal.core.kr.language.CompiledQuery;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.core.program.Module;
import goal.core.program.Module.FocusMethod;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A rule consists of a condition (body) and an action (head). The condition of
 * a rule is a {@link MentalStateCond}. The action of a rule is an
 * {@link ActionCombo}. A rule is applicable if the condition of the rule AND
 * the precondition of the action hold. In that case, the action can be selected
 * for execution.
 *
 * @author N.Kraayenbrink
 * @author W.Pasman added support to store compiled rule.
 * @modified K.Hindriks
 */
public abstract class Rule extends ParsedObject {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 4375820963372087059L;
	/**
	 * The condition of the rule.
	 */
	private final MentalStateCond condition;
	/**
	 * The action of the rule.
	 */
	private ActionCombo action;
	/**
	 * Compiled version of the rule plus its first action precondition. If the
	 * compiledQuery is not null, the rule has been precompiled and that
	 * precompiled query must be used. The main thing that the compilation
	 * compiles is the
	 * {@link MentalStateCond#evaluate(MentalState, SteppingDebugger)
	 * } function.
	 */
	private CompiledQuery compiledQuery = null;

	/**
	 * Creates a new {@link Rule}
	 *
	 * @param condition
	 *            Determines when the rule is applicable.
	 * @param action
	 *            The action to perform if the rule is applicable.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 *
	 */
	protected Rule(MentalStateCond condition, ActionCombo action,
			InputStreamPosition source) {
		super(source);
		this.condition = condition;
		this.action = action;
	}

	/**
	 * Gets the condition (head) of this {@link Rule}.
	 *
	 * @return The condition of this {@link Rule} used for evaluating whether
	 *         the rule is applicable.
	 */
	public MentalStateCond getCondition() {
		return this.condition;
	}

	/**
	 * Returns the action of this rule.
	 *
	 * @return The {@link ActionCombo} that is performed if this {@link Rule} is
	 *         applied.
	 */
	public ActionCombo getAction() {
		return this.action;
	}

	/**
	 * Sets the {@link ActionCombo} for this {@link Rule}.
	 *
	 * @param action
	 *            The action to be associated with this rule.
	 */
	public void setAction(ActionCombo action) {
		this.action = action;
	}

	/**
	 * Check if this rule must evaluate as a single-goal rule. A single-goal
	 * rule evaluates different from a normal rule: it can use only a single
	 * goal base for the entire condition. This is necessary for focus=SELECT
	 * rules.<br>
	 * This is implicit in the actions of the rule: If (at least one) of the
	 * actions in the rule is a module AND that module is focus=SELECT, this
	 * rule will be a single-goal rule.
	 *
	 * @return True if the rule is a single-goal rule.
	 */
	public boolean isRuleSinglegoal() {
		for (Action a : action) {
			if (a instanceof ModuleCallAction
					&& ((ModuleCallAction) a).getTarget().getFocusMethod() == FocusMethod.SELECT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Associated a pre-compiled query with this rule. This pre-compiled query
	 * is a compilation of the mental state condition of the rule and the
	 * precondition of the action of the rule into a single query in the KR
	 * language used for representing these.
	 *
	 * @param compiledQuery
	 *            The mental state and action pre-condition compiled to a query
	 *            in the {@link KRlanguage}.
	 */
	public void setPreCompiledQuery(CompiledQuery compiledQuery) {
		this.compiledQuery = compiledQuery;
	}

	/**
	 * Returns the non-anonymous variables that occur in the condition of the
	 * rule. These variables are said to bind variables that occur in the
	 * parameters of the action of the rule.
	 *
	 * @return The variables that bind variables that occur in the action of the
	 *         rule.
	 */
	public Set<Var> getBoundVar() {
		Set<Var> boundVar = new HashSet<>();
		// all non-anonymous free variables of the condition are bound
		for (Var v : this.condition.getFreeVar()) {
			if (!v.isAnonymous()) {
				boundVar.add(v);
			}
		}
		return boundVar;
	}

	/**
	 * Applies the given {@link Substitution} to this {@link Rule}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the rule.
	 * @return The instantiated rule where free variables bound by the
	 *         substitution have been replaced by ground terms.
	 */
	abstract Rule applySubst(Substitution subst);

	/**
	 * Evaluates and applies the {@link Rule}.
	 *
	 * First collect all substitutions that satisfy the condition of the
	 * {@link Rule}. Then apply the rule using the substitutions found.
	 * Application is specific for each type of rule. <br>
	 * Note that there is a fundamental difference between first collecting
	 * substitutions and then applying the rule versus applying the rule while
	 * one-by-one collecting substitutions. For the {@link IfThenRule} rule we
	 * cannot randomly shuffle the substitutions in the latter case (and thus
	 * cannot randomly pick an action to execute), and for the
	 * {@link ForallDoRule} the resulting set of substitutions that satisfy the
	 * rule's condition might be different as this set might change when
	 * executing actions while evaluating the rule's condition.
	 *
	 * @param runState
	 *            the current run-state of the agent.
	 * @param substitution
	 *            the global substitution to use for rule application. These are
	 *            substitutions that hold on the module level encapsulating this
	 *            rule.
	 * @return Result.
	 */
	public Result run(RunState<?> runState, Substitution substitution) {
		Set<Substitution> substset;
		HashMap<Substitution, List<SingleGoal>> substGoalLinks = null;
		MentalState mentalState = runState.getMentalState();
		Debugger debugger = runState.getDebugger();

		// FIXME using #toRuleString to prevent adding trailing dot...
		debugger.breakpoint(Channel.RULE_CONDITIONAL_VIEW, this.getCondition(),
				"Evaluating rule %s.", this.toRuleString());

		// Get substitutions that satisfy rule condition.
		// determine the rule mode
		if (isRuleSinglegoal()) {
			// rule is evaluated with single goals from attention set.
			substGoalLinks = new HashMap<>();
			substset = mentalState.contextQuery(condition, substGoalLinks,
					debugger);
		} else {
			// rule is evaluated using all goals in current attention set.
			substset = condition.evaluate(substitution, mentalState, debugger);
		}

		// If condition does not hold (no solutions), then report and return.
		if (substset.isEmpty()) {
			// FIXME using #toRuleString to prevent adding trailing dot...
			// #3079 this must NOT pass the action to the debugger.
			debugger.breakpoint(Channel.RULE_CONDITION_EVALUATION,
					this.getCondition(), "Condition of rule %s does not hold.",
					this.toRuleString());
			return new Result();
		}

		// FIXME using #toRuleString to prevent adding trailing dot...
		// #3079 this must pass the ACTION to the debugger
		debugger.breakpoint(Channel.HIDDEN_RULE_CONDITION_EVALUATION,
				this.getAction(), "Condition of rule %s holds.",
				this.toRuleString());
		debugger.breakpoint(Channel.RULE_CONDITION_EVALUATION,
				this.getCondition(), "Condition of rule %s holds for: %s.",
				this.toRuleString(), substset);

		// Apply rule.
		Result result = this.apply(runState, substset, substGoalLinks,
				substitution);

		// Start a new cycle if an action has been performed AND
		// we are in main module.
		if (result.hasPerformedAction() && runState.isMainModuleRunning()) {
			runState.startCycle(true);
		}

		return result;
	}

	/**
	 * Applies the rule using the set of substitutions that validate the
	 * condition of the rule.
	 *
	 * @param runState
	 *            the current run-state of the agent.
	 * @param substset
	 *            Set of substitutions that satisfy the condition of this rule.
	 * @param substGoalLinks
	 *            A set of links between {@link Substitution}s and lists of
	 *            {@link SingleGoal}s that validate the context of the target
	 *            module with the given {@link Substitution}. Is something (not
	 *            null) only if {@link #isRuleSinglegoal()}.
	 * @param globalsubst
	 *            the {@link Substitution} that holds at the rule's general
	 *            level - the module level. This is the substi before we applied
	 *            the condition. Needed in {@link ListallDoRule}.
	 *
	 * @return Result.
	 */
	public abstract Result apply(RunState<?> runState,
			Set<Substitution> substset,
			HashMap<Substitution, List<SingleGoal>> substGoalLinks,
			Substitution globalsubst);

	@Override
	public String toString() {
		return this.toString("");
	}

	// FIXME: Class should not be tasked with pretty printing??

	/**
	 * Generalization of {@link Rule#toString()}, allowing the returned string
	 * to be indented at various levels. Rules are printed including the
	 * trailing '.', and rules representing {@link Module}s are printed as a
	 * module.
	 *
	 * @param linePrefix
	 *            What to prefix every line with
	 * @return A string-representation of this action rule, or the
	 *         string-representation of the module this action rule represents.
	 */
	public abstract String toString(String linePrefix);

	/**
	 * Version of toString that will automatically pick the correct version of
	 * {@link #toIfThenString()} or {@link #toForallDoString()}. This will
	 * usually be the same as {@link #toString()}, except for a
	 * {@link FocusRule}. For a {@link FocusRule} this will return the same as
	 * {@link #toIfThenString()}, instead of a String-representation of the
	 * rule's target Module that is returned by {@link #toString()}.
	 *
	 * @return A one-line String representation of this {@link Rule}. Should be
	 *         the result of either {@link #toIfThenString()} or
	 *         {@link #toForallDoString()}.
	 */
	public abstract String toRuleString();

}
