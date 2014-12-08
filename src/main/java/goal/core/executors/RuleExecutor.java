package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import krTools.KRInterface;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.rules.ForallDoRule;
import languageTools.program.agent.rules.IfThenRule;
import languageTools.program.agent.rules.ListallDoRule;
import languageTools.program.agent.rules.Rule;
import mentalstatefactory.MentalStateFactory;

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
			try {
				Term newTerm = substitutionsToTerm(applicableSubst, runState
						.getActiveModule().getKRInterface());
				fullSubst.addBinding(((ListallDoRule) this.rule).getVariable(),
						newTerm);
			} catch (KRInitFailedException | UnknownObjectException e) {
				throw new GOALRuntimeErrorException(
						"Converting substitutions to a term failed: "
								+ e.getMessage(), e);
			}

			result = executor.run(runState, fullSubst, true);
		}

		return result;
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
	 * @throws KRInitFailedException
	 * @throws UnknownObjectException
	 */
	private Term substitutionsToTerm(Set<Substitution> substitutions,
			KRInterface language) throws KRInitFailedException,
			UnknownObjectException {
		mentalState.MentalState state = MentalStateFactory
				.getInterface(language.getClass());
		// First make single terms from each substitution.
		List<Term> substsAsTerms = new ArrayList<>(substitutions.size());
		// Get the variables from the condition of the rule; bindings for those
		// variables will be turned into a list.
		Set<Var> boundVar = this.rule.getCondition().getFreeVar();
		List<Term> subTerms;
		for (Substitution substitution : substitutions) {
			subTerms = new LinkedList<>();
			for (Var v : boundVar) {
				// if (!v.isAnonymous()) { FIXME
				subTerms.add(substitution.get(v));
				// }
			}
			// if there is only one bound var, we shouldn't make lists of them.
			// the end result should simply be a list of values instead of a
			// list of singleton lists.
			if (subTerms.size() == 1) {
				substsAsTerms.add(subTerms.get(0));
			} else if (subTerms.size() > 1) {
				substsAsTerms.add(state.makeList(subTerms));
			}
			// if empty, do not add anything.
			// it means there is no substitution, so we want the end result to
			// be '[]' (and not '[[]]')
		}

		// Second combine the substitutions turned into terms into a single list
		// term.
		return state.makeList(substsAsTerms);
	}
}
