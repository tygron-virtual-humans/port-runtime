package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Term;
import languageTools.program.agent.rules.ForallDoRule;
import languageTools.program.agent.rules.IfThenRule;
import languageTools.program.agent.rules.ListallDoRule;
import languageTools.program.agent.rules.Rule;

public class RuleExecutor {
	private final Rule rule;

	public RuleExecutor(Rule rule) {
		this.rule = rule;
	}

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
		debugger.breakpoint(Channel.RULE_CONDITIONAL_VIEW,
				this.rule.getCondition(), "Evaluating rule %s.",
				this.rule.toString());

		// Get substitutions that satisfy rule condition.
		// determine the rule mode
		if (this.rule.isRuleSinglegoal()) {
			// rule is evaluated with single goals from attention set.
			substGoalLinks = new HashMap<>();
			substset = mentalState.contextQuery(this.rule.getCondition(),
					substGoalLinks, debugger);
		} else {
			// rule is evaluated using all goals in current attention set.
			substset = new MentalStateConditionExecutor(
					this.rule.getCondition()).evaluate(substitution,
					mentalState, debugger);
		}

		// If condition does not hold (no solutions), then report and return.
		if (substset.isEmpty()) {
			// FIXME using #toRuleString to prevent adding trailing dot...
			// #3079 this must NOT pass the action to the debugger.
			debugger.breakpoint(Channel.RULE_CONDITION_EVALUATION,
					this.rule.getCondition(),
					"Condition of rule %s does not hold.", this.rule.toString());
			return new Result();
		}

		// FIXME using #toRuleString to prevent adding trailing dot...
		// #3079 this must pass the ACTION to the debugger
		debugger.breakpoint(Channel.HIDDEN_RULE_CONDITION_EVALUATION,
				this.rule.getAction(), "Condition of rule %s holds.",
				this.rule.toString());
		debugger.breakpoint(Channel.RULE_CONDITION_EVALUATION,
				this.rule.getCondition(),
				"Condition of rule %s holds for: %s.", this.rule.toString(),
				substset);

		// Apply rule.
		Result result = apply(runState, substset, substGoalLinks, substitution);

		// Start a new cycle if an action has been performed AND
		// we are in main module.
		if (result.hasPerformedAction() && runState.isMainModuleRunning()) {
			runState.startCycle(true);
		}

		return result;
	}

	private Result apply(RunState<?> runState, Set<Substitution> substset,
			HashMap<Substitution, List<SingleGoal>> substGoalLinks,
			Substitution globalsubst) {
		final ActionComboExecutor executor = new ActionComboExecutor(
				this.rule.getAction());
		Result result = new Result();
		// TODO: does not yet take collecting of goals for FILTER and SELECT
		// options of modules into account...

		if (this.rule instanceof IfThenRule) {
			// Shuffle list of substitutions.
			List<Substitution> substlist = new ArrayList<>(substset);
			Collections.shuffle(substlist);

			// Find action whose precondition also holds and perform it.
			// We later handle {@link ExitModuleAction}.
			final int max = substlist.size() - 1;
			for (int i = 0; i <= max; i++) {
				/**
				 * find the single goal that made this substitution true Stays
				 * null if this is not {@link Rule#isRuleSinglegoal()}.
				 */
				final Substitution subst = substlist.get(i);
				if (substGoalLinks != null) {
					List<SingleGoal> validatingGoals = substGoalLinks
							.get(subst);
					runState.setFocusGoal(validatingGoals.get(new Random()
							.nextInt(validatingGoals.size())));
				}

				result.merge(executor.run(runState, subst, i == max));
				if (result.hasPerformedAction()) {
					break;
				}
			}
		} else if (this.rule instanceof ForallDoRule) {
			// Apply rule as long as there are still substitutions that satisfy
			// its
			// condition, and no {@link ExitModuleAction} has been performed.
			for (Substitution substitution : substset) {
				if (substGoalLinks == null) {
					result.merge(executor.run(runState, substitution, true));
					if (result.isModuleTerminated()) {
						break;
					}
				} else {
					for (SingleGoal goal : substGoalLinks.get(substitution)) {
						runState.setFocusGoal(goal);
						result.merge(executor.run(runState, substitution, true));
						if (result.isModuleTerminated()) {
							break;
						}
					}
				}
			}
		} else if (this.rule instanceof ListallDoRule) {
			// #2578. We pick a random validating goal. Under discussion.
			SingleGoal goal = null;
			Set<Substitution> applicableSubst = substset;

			if (substGoalLinks != null) {
				Set<SingleGoal> allgoals = new LinkedHashSet<>();
				for (List<SingleGoal> goals : substGoalLinks.values()) {
					allgoals.addAll(goals);
				}
				List<SingleGoal> goalslist = new ArrayList<>(allgoals);
				goal = goalslist.get(new Random().nextInt(goalslist.size()));
				runState.setFocusGoal(goal);
				Set<Substitution> substs = new LinkedHashSet<>();
				for (Substitution sub : substGoalLinks.keySet()) {
					if (substGoalLinks.get(sub).contains(goal)) {
						substs.add(sub);
					}
				}
				applicableSubst = substs;
			}

			// Create new substitution, replacing our #variable.
			Substitution fullSubst = globalsubst.clone();
			Term newTerm = ExecuteTools.substitutionsToTerm(applicableSubst,
					runState.getActiveModule().getKRInterface(), rule);
			fullSubst.addBinding(((ListallDoRule) this.rule).getVariable(),
					newTerm);

			result = executor.run(runState, fullSubst, true);
		}

		return result;
	}

}
