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

import goal.core.agent.Agent;
import goal.core.kr.language.Substitution;
import goal.core.mentalstate.MentalState;
import goal.core.program.actions.ActionCombo;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.KRQueryFailedException;
import goal.tools.logging.Loggers;
import goal.util.BracketedOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * Container for the rules in the <code>program{}</code> section. Given a mental
 * state (and debugger), this will also determine what rule will be the next one
 * to fire according to the internal {@link RuleEvaluationOrder}.
 *
 * @author K.Hindriks
 * @author N.Kraayenbrink; extracted from {@link Agent}. Added option for random
 *         or linear rule evaluation.<br>
 *         Added macro support 4dec09<br>
 *         Rule evaluation has moved to goal.core.agent.instructions.* feb11
 * @modified K.Hindriks
 */
public class RuleSet extends ParsedObject implements Iterable<Rule> {

	/** Auto-generated serial version ID */
	private static final long serialVersionUID = 6416657021000176231L;
	/**
	 * The set of rules in this container.
	 */
	private final List<Rule> rules = new LinkedList<>();
	/**
	 * Determines how the next action to be executed is selected.
	 */
	private RuleEvaluationOrder ruleOrder;

	/**
	 * Creates an empty {@link RuleSet} of {@link Rule}s, with a given
	 * {@link RuleEvaluationOrder}, i.e. a rule evaluation order.
	 *
	 * @param order
	 *            The order in which the rules in the new rule set are evaluated
	 *            and executed. May be overridden with {@link #setOptions}.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public RuleSet(RuleEvaluationOrder order, InputStreamPosition source) {
		super(source);
		this.ruleOrder = order;
	}

	/**
	 * Get the (index)th rule.
	 *
	 * @param index
	 *            The index
	 *
	 * @return The (index)th rule in this {@link RuleSet}.
	 */
	public Rule getRule(int index) {
		return this.rules.get(index);
	}

	/**
	 * Adds a single rule to the set of rules.
	 *
	 * @param rule
	 *            The rule to add to the set of rules.
	 */
	public void addRule(Rule rule) {
		this.rules.add(rule);
	}

	/**
	 * Checks whether the set of rules is empty.
	 *
	 * @return {@code true} if there are no rules in this {@link RuleSet}.
	 */
	public boolean isEmpty() {
		return this.rules.isEmpty();
	}

	/**
	 * Returns the number of rules in this {@link RuleSet}.
	 *
	 * @return The number of rules in this rule set.
	 */
	public int getRuleCount() {
		return this.rules.size();
	}

	/**
	 * Returns the {@link Rule} evaluation order associated with this
	 * {@link RuleSet}.
	 *
	 * @return The order in which the rules in this rule set are evaluated.
	 */
	public RuleEvaluationOrder getRuleOrder() {
		return this.ruleOrder;
	}

	/**
	 * Set the order in which the rules in this {@link RuleSet} will be
	 * evaluated.
	 *
	 * @param ruleOrder
	 *            The rule evaluation order.
	 */
	public void setRuleOrder(RuleEvaluationOrder ruleOrder) {
		this.ruleOrder = ruleOrder;
	}

	/**
	 * Set the options for this set of rules.
	 * <p>
	 * A {@link RuleSet} only has one option that can be set, the rule
	 * evaluation order set with the key {@code order} of a program section in a
	 * module. The values for this key are fixed by the
	 * {@link RuleEvaluationOrder} class.
	 * <p>
	 * An invalid value is ignored, although a warning is generated.
	 * </p>
	 *
	 * @param options
	 *            A parsed list of options.
	 */
	public void setOptions(BracketedOptions options) {

		// options is null if no list is specified
		if (options == null) {
			return;
		}

		for (BracketedOptions.KEYS key : options.getKeys()) {
			String value = options.getStringValue(key);
			switch (key) {
			case ORDER:
				try {
					this.ruleOrder = RuleEvaluationOrder.valueOf(value
							.toUpperCase());
				} catch (IllegalArgumentException ex) {
					// IAE is thrown when value of the option is incorrect
					// throw a warning, but continue nonetheless
					Loggers.getParserLogger().logln(
							"Option value '" + value
							+ "' for 'order' is invalid.");
				}
				break;
			default:
				throw new GOALBug("Hit upon option " + key
						+ " which is unknown.");
			}
		}
	}

	/**
	 * @return An {@link Iterator} over the set of action rules this
	 *         {@link RuleSet} contains.
	 */
	@Override
	public Iterator<Rule> iterator() {
		return this.rules.iterator();
	}

	/**
	 * Instantiates the rules in this {@link RuleSet} with the given
	 * substitution.
	 *
	 * @param substitution
	 *            The {@link Substitution} to apply to the rules.
	 * @return A new {@link RuleSet} with the same {@link RuleEvaluationOrder}
	 *         as this {@link RuleSet}, but whose {@link Rule}s have been
	 *         instantiated with the given {@link Substitution} (using
	 *         {@link Rule#applySubst(Substitution)}).
	 */
	public RuleSet applySubst(Substitution substitution) {
		RuleSet instance = new RuleSet(this.ruleOrder, this.getSource());

		for (Rule rule : this) {
			instance.addRule(rule.applySubst(substitution));
		}

		return instance;
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
	 */
	@SuppressWarnings("fallthrough")
	public final List<ActionCombo> getActionOptions(MentalState mentalState,
			Debugger debugger) {
		List<ActionCombo> actionOptions = new LinkedList<>();
		Set<Substitution> solutions;
		boolean finished = false;

		// Does not support linearall and randomall orders.
		switch (ruleOrder) {
		case LINEARALL:
		case RANDOMALL:
			throw new UnsupportedOperationException(
					"Linear and random all are not"
							+ "supported by RuleSet.getOptions(RunState).");
		default:
			// continue.
		}

		// In case of 'linear' style evaluation find the first applicable rule
		// and
		// return the options of that rule only; otherwise check all rules and
		// return the options for every rule that is applicable.
		for (Rule rule : this.rules) {
			// Evaluate the rule's condition.
			solutions = rule.getCondition().evaluate(mentalState, debugger);
			// Listall rules need to be processed further.
			if (rule instanceof ListallDoRule) {
				solutions = ((ListallDoRule) rule).getVarSubstitution(
						solutions, mentalState.getKRLanguage());
			}

			// If condition holds, then check for action options;
			// otherwise continue with next rule.
			if (!solutions.isEmpty()) {
				// Check options for each solution found for rule condition.
				for (Substitution substitution : solutions) {
					// First, instantiate the rule's action by applying the
					// substitution found.
					ActionCombo instantiatedAction = rule.getAction()
							.applySubst(substitution);
					// Second, check precondition and add all options for this
					// instantiation.
					actionOptions.addAll(instantiatedAction.getOptions(
							mentalState, debugger));
				}

				// In case rule evaluation order is 'linear' do not evaluate any
				// other rules
				// if we have already found some action options.
				if (actionOptions.size() > 0) {
					switch (ruleOrder) {
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
	 * Executes this {@link RuleSet}.
	 *
	 * @param runState
	 *            The run state in which the rule set is executed.
	 * @param substitution
	 *            The substitution provided by the module context that is passed
	 *            on to this rule set.
	 * @return The {@link Result} of executing this rule set.
	 * @throws KRQueryFailedException
	 *
	 *             FIXME: enable learner to deal with Rule#isSingleGoal
	 */
	@SuppressWarnings("fallthrough")
	public Result run(RunState<?> runState, Substitution substitution) {
		Result result = new Result();
		// Make a copy of the rules so we don't shuffle the original below.
		List<Rule> rules = new ArrayList<>(this.rules);

		switch (ruleOrder) {
		case ADAPTIVE:
		case LINEARADAPTIVE:
			/*
			 * For now there is no differentiation between adaptive and linear
			 * adaptive options. In both cases, a 'random' action option will be
			 * selected for execution by the learner.
			 */

			MentalState ms = runState.getMentalState();
			RuleSet ruleSet = this.applySubst(substitution);

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
			List<ActionCombo> options = ruleSet.getActionOptions(ms,
					runState.getDebugger());

			// There are no possible options for actions to execute.
			if (options.isEmpty()) {
				break;
			}

			// Select an action
			ActionCombo chosen = runState.getLearner().act(
					runState.getActiveModule().getName(), ms, options);

			/* Now execute the action option */
			result = chosen.run(runState, substitution, true);

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
				result = rule.run(runState, substitution);
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
				result.merge(rule.run(runState, substitution));
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

	@Override
	public String toString() {
		return this.toString("");
	}

	/**
	 * Generalization of {@link RuleSet#toString()}. Allows the same method to
	 * be used for various indentation levels.
	 *
	 * @param linePrefix
	 *            What to add in front of every line. Usually some amount of
	 *            tabs or spaces.
	 * @return A string-representation of this set of (action) rules.
	 */
	public String toString(String linePrefix) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(linePrefix + "program [order=");
		stringBuilder.append(this.ruleOrder.toString().toLowerCase());
		stringBuilder.append("] {\n");

		// for (Macro m : this.macros.getMacros()) {
		// stringBuilder.append(linePrefix + "\t");
		// stringBuilder.append(m.toString());
		// stringBuilder.append(".\n");
		// }
		stringBuilder.append(linePrefix + "\t\n");

		for (Rule rule : this.rules) {
			stringBuilder.append(rule.toString(linePrefix + "\t"));
			stringBuilder.append("\n");
		}
		stringBuilder.append(linePrefix + "}\n");
		return stringBuilder.toString();
	}

	/**
	 * The different orders in which the rules of an {@link RuleSet} can be
	 * evaluated.
	 */
	public enum RuleEvaluationOrder {
		/**
		 * Of the first rule with a viable instance, one random instance will be
		 * applied. A forall-do rule counts as one instance.
		 */
		LINEAR,
		/**
		 * Of all viable rule instances, one will be applied. A forall-do rule
		 * counts as one instance.
		 */
		RANDOM,
		/**
		 * All rules will be applied, from top to bottom. Instances from within
		 * a single rule are applied in random order. Only one instance from
		 * each if-then rule will be applied.
		 */
		LINEARALL,
		/**
		 * All viable rule instances will be applied, but in random order. Only
		 * one instance from each if-then rule will be applied.
		 */
		RANDOMALL,
		/**
		 * The first rule (in linear order) that is applicable will be applied
		 * and only the options of this rule are generated. The option that is
		 * selected depends on ongoing learning.
		 */
		LINEARADAPTIVE,
		/**
		 * All rules will be applied in linear order and all options generated.
		 * The option that is selected depends on ongoing learning.
		 */
		ADAPTIVE;

		/**
		 * @return {@code true} iff this {@link RuleEvaluationOrder} is
		 *         {@link #LINEAR} or {@link #LINEAR}.
		 */
		public boolean isLinear() {
			return this == LINEAR || this == LINEARALL || this == ADAPTIVE;
		}

		/**
		 * @return {@code true} iff this {@link RuleEvaluationOrder} is
		 *         {@link #LINEARALL} or {@link #RANDOMALL}.
		 */
		public boolean applyAll() {
			return this == LINEARALL || this == RANDOMALL || this == ADAPTIVE;
		}

		/**
		 * @return {@code true} iff this {@link RuleEvaluationOrder} is
		 *         {@link #ADAPTIVE}
		 */
		public boolean isAdaptive() {
			return this == ADAPTIVE;
		}
	}

}
