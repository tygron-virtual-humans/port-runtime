package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;

import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.KRInterface;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import languageTools.program.agent.Module.RuleEvaluationOrder;
import languageTools.program.agent.actions.ActionCombo;
import languageTools.program.agent.rules.ListallDoRule;
import languageTools.program.agent.rules.Rule;

public class RulesExecutor {
	/**
	 * The set of rules in this container.
	 */
	private List<Rule> rules = new LinkedList<>();
	/**
	 * Determines how the next action to be executed is selected.
	 */
	private final RuleEvaluationOrder ruleOrder;

	public RulesExecutor(List<Rule> rules, RuleEvaluationOrder ruleOrder) {
		this.rules = rules;
		this.ruleOrder = ruleOrder;
	}

	/**
	 * Executes this {@link RuleSet}.
	 *
	 * @param runState
	 *            The run state in which the rule set is executed.
	 * @param substitution
	 *            The substitution provided by the module context that is passed
	 *            on to this rule set.
	 * @return The {@link Result} of executing this rule set.
	 * @throws KRInitFailedException
	 * @throws UnknownObjectException
	 * @throws KRQueryFailedException
	 *
	 *             FIXME: enable learner to deal with Rule#isSingleGoal
	 */
	@SuppressWarnings("fallthrough")
	public Result run(RunState<?> runState, Substitution substitution) {
		KRInterface krInterface = runState.getActiveModule().getKRInterface();
		Result result = new Result();
		// Make a copy of the rules so we don't shuffle the original below.
		List<Rule> rules = new ArrayList<>(this.rules);

		switch (this.ruleOrder) {
		case ADAPTIVE:
		case LINEARADAPTIVE:
			/*
			 * For now there is no differentiation between adaptive and linear
			 * adaptive options. In both cases, a 'random' action option will be
			 * selected for execution by the learner.
			 */
			MentalState ms = runState.getMentalState();

			runState.incrementRoundCounter();
			runState.getDebugger().breakpoint(
					Channel.REASONING_CYCLE_SEPARATOR,
					null,
					"+++++++ Adaptive Cycle " + runState.getRoundCounter()
							+ " +++++++ ");

			/*
			 * Get the learner to choose one action option, from the input list
			 * of action options.
			 */
			List<ActionCombo> options = getActionOptions(ms,
					runState.getDebugger(), krInterface);

			// There are no possible options for actions to execute.
			if (options.isEmpty()) {
				break;
			}

			// Select an action
			ActionCombo chosen = runState.getLearner().act(
					runState.getActiveModule().getName(), ms, options);

			/*
			 * Now execute the action option TODO: context?!
			 */
			result = new ActionComboExecutor(chosen).run(runState,
					substitution, true);

			/*
			 * Obtain the reward from the environment. Or, if the environment
			 * does not support rewards, then create an internal reward based on
			 * whether we have achieved all our goals or not.
			 */
			boolean goalsEmpty = ms.getAttentionSet().getGoals().isEmpty();
			// runState should now have reward set.
			Double envReward = runState.getReward();
			double reward = (envReward != null) ? envReward : goalsEmpty ? 1.0
					: 0.0;

			if (!goalsEmpty) {
				/* Update the learner with the reward from the last action */
				runState.getLearner().update(
						runState.getActiveModule().getName(), ms, reward);
			} else {
				/*
				 * If goals were achieved, then the final reward is calculated,
				 * and the learning episode finished, in RunState.kill() when
				 * the agent is killed.
				 */
			}
			break;
		case RANDOM:
			Collections.shuffle(rules);
		case LINEAR:
			for (Rule rule : rules) {
				result = new RuleExecutor(rule).run(runState, substitution);
				if (result.isFinished()) {
					break;
				}
			}
			break;
		case RANDOMALL:
			Collections.shuffle(rules);
		case LINEARALL:
			// Continue evaluating and applying rule as long as there are more,
			// and no {@link ExitModuleAction} has been performed.
			for (Rule rule : rules) {
				result.merge(new RuleExecutor(rule).run(runState, substitution));
				if (result.isModuleTerminated()) {
					break;
				}
			}
			break;
		default:
			break;
		}

		return result;
	}

	/**
	 * Returns the actions that can be performed in the agent's current mental
	 * state (extracted from the run state).
	 * <p>
	 * Only supports linear and random rule evaluation orders, but not the 'all'
	 * orders; i.e., linearall or randomall are not supported.
	 * </p>
	 * <p>
	 * In case of the 'linear' modes only returns the options generated by the
	 * first rule that is applicable. In case of the 'random' modes all options
	 * for every rule that is applicable are returned.
	 * </p>
	 *
	 * @param mentalState
	 *            The mental state used for evaluating the action options.
	 * @param debugger
	 *            The current debugger.
	 * @return A list of actions that may be performed in the given mental
	 *         state, possibly empty.
	 * @throws KRInitFailedException
	 * @throws UnknownObjectException
	 */
	@SuppressWarnings("fallthrough")
	private final List<ActionCombo> getActionOptions(MentalState mentalState,
			Debugger debugger, KRInterface krInterface) {
		List<ActionCombo> actionOptions = new LinkedList<>();
		Set<Substitution> solutions;
		boolean finished = false;

		// Does not support linearall and randomall orders.
		switch (this.ruleOrder) {
		case LINEARALL:
		case RANDOMALL:
			throw new UnsupportedOperationException(
					"Linear and random all are not"
							+ "supported by RuleSet.getOptions(RunState).");
		default:
			// continue.
		}

		/*
		 * In case of 'linear' style evaluation find the first applicable rule
		 * and return the options of that rule only; otherwise check all rules
		 * and return the options for every rule that is applicable.
		 */
		for (Rule rule : this.rules) {
			// Evaluate the rule's condition.
			solutions = new MentalStateConditionExecutor(rule.getCondition())
					.evaluate(mentalState, debugger);
			// Listall rules need to be processed further.
			if (rule instanceof ListallDoRule) {
				solutions = getVarSubstitution((ListallDoRule) rule, solutions,
						krInterface);
			}

			// If condition holds, then check for action options;
			// otherwise continue with next rule.
			if (!solutions.isEmpty()) {
				// Check options for each solution found for rule condition.
				for (Substitution substitution : solutions) {
					// First, instantiate the rule's action by applying the
					// substitution found.
					ActionComboExecutor instantiatedAction = new ActionComboExecutor(
							rule.getAction().applySubst(substitution));
					instantiatedAction.setContext(rule.getCondition()
							.applySubst(substitution));
					// Second, check precondition and add all options for this
					// instantiation.
					actionOptions.addAll(instantiatedAction.getOptions(
							mentalState, substitution, debugger));
				}

				// In case rule evaluation order is 'linear' do not evaluate any
				// other rules
				// if we have already found some action options.
				if (actionOptions.size() > 0) {
					switch (this.ruleOrder) {
					case LINEAR:
					case LINEARADAPTIVE:
						finished = true;
					default:
						// continue.
					}
				}

				// If we're finished break out of the loop.
				if (finished) {
					break;
				}
			}
		}

		return actionOptions;
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
	 * @throws UnknownObjectException
	 * @throws KRInitFailedException
	 */
	protected Set<Substitution> getVarSubstitution(ListallDoRule rule,
			Set<Substitution> solutions, KRInterface krInterface) {
		// If the solution set is empty, then the variable of this rule should
		// not be instantiated and we simply return the empty set.
		if (solutions.isEmpty()) {
			return solutions;
		}
		// Create the substitution for the variable of this listall rule.
		Term term = ExecuteTools.substitutionsToTerm(solutions, krInterface,
				rule);
		Substitution varSubst = krInterface.getSubstitution(null);
		varSubst.addBinding(rule.getVariable(), term);
		// Add that substitution to a set and return it.
		Set<Substitution> result = new LinkedHashSet<>(1);
		result.add(varSubst);
		return result;
	}
}
