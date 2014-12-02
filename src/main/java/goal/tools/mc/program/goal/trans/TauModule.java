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

package goal.tools.mc.program.goal.trans;

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.mentalstate.MentalState;
import goal.core.program.Module;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.AdoptOneAction;
import goal.core.program.rules.IfThenRule;
import goal.core.program.rules.Rule;
import goal.core.program.rules.RuleSet;
import goal.core.program.rules.RuleSet.RuleEvaluationOrder;
import goal.tools.debugger.Debugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a transition module.
 *
 * @author sungshik
 *
 * @param <T>
 *            The type of term associated with the KRT that the agent to which
 *            the transition module belongs uses.
 * @param <A>
 *            The type of atom associated with the KRT that the agent to which
 *            the transition module belongs uses.
 * @param <U>
 *            The type of update associated with the KRT that the agent to which
 *            the transition module belongs uses.
 */
public abstract class TauModule<T extends Term, A extends Atom, U extends Update> {

	//
	// Protected fields
	//

	/**
	 * Represents the derives methods that this module should use.
	 */
	protected Derives<A> derives;

	/**
	 * The module corresponding to this transition module.
	 */
	protected Module module;

	/**
	 * The goals that occur in this module.
	 */
	protected GoalRepository<A, U> repository;

	/**
	 * The transition classes belonging to this transition module.
	 */
	protected List<TauClass<T, A, U>> tauClasses = new ArrayList<TauClass<T, A, U>>();

	//
	// Constructors
	//

	/**
	 * Constructs a transition module corresponding to the specified module.
	 *
	 * @param module
	 *            - The module to which the transition module to be constructed
	 *            should correspond.
	 */
	public TauModule(Module module, Collection<DatabaseFormula> knowledge) {
		this.module = module;
		this.derives = createDerives(knowledge);
		this.repository = createGoalRepository();
		initRepository(knowledge);
		initClasses();
	}

	//
	// Abstract methods
	//

	/**
	 * Creates a transition class that fits this transition module, and that
	 * corresponds to the specified rule.
	 *
	 * @return The created transition class.
	 */
	protected abstract TauClass<T, A, U> createTransitionClass(Rule rule);

	/**
	 * Creates a goal repository that fits this transition module.
	 *
	 * @return The created goal repository.
	 */
	protected abstract GoalRepository<A, U> createGoalRepository();

	/**
	 * Creates a derives function that fits this transition module, and that
	 * uses the specified knowledge rules to make derivations.
	 *
	 * @param knowledge
	 *            - The knowledge rules to instantiate the derives function
	 *            with.
	 * @return The derives function.
	 */
	protected abstract Derives<A> createDerives(
			Collection<DatabaseFormula> knowledge);

	//
	// Public methods
	//

	/**
	 * Gets the transition classes belonging to this transition module.
	 */
	public List<TauClass<T, A, U>> getClasses() {
		ArrayList<TauClass<T, A, U>> classes = new ArrayList<TauClass<T, A, U>>();
		classes.addAll(tauClasses);
		return classes;
	}

	/**
	 * Gets the options in the given mental state. This method mimics
	 * {@link RuleSet#getActionOptions}, with the addition that options are
	 * stored and returned by transition class.
	 *
	 * @param ms
	 *            - The mental state to get options for.
	 * @return A mapping from transition classes to action options in the
	 *         specified mental state.
	 */
	public OptionMap<T, A, U> getActionOptions(MentalState mentalState,
			Debugger debugger) {

		try {
			/* Declare some variables */
			OptionMap<T, A, U> optionMap = new OptionMap<T, A, U>();
			Set<Substitution> substitutions;

			/* Iterate rules */
			RuleSet rules = module.getRuleSet();
			if (rules != null) {
				RuleEvaluationOrder ordr = rules.getRuleOrder();
				for (Rule rule : rules) {
					substitutions = rule.getCondition().evaluate(mentalState,
							debugger);

					/* If the rule's antecedent is satisfied, proceed */
					if (!substitutions.isEmpty()) {
						ArrayList<ActionCombo> options = new ArrayList<ActionCombo>();

						/*
						 * Instantiate actions according to substitutions, and
						 * get the action options for these instantiations
						 */
						for (Substitution subst : substitutions) {
							ArrayList<Action> instantiatedActions = new ArrayList<Action>();
							for (Action action : rule.getAction()) {
								instantiatedActions.add(action
										.applySubst(subst));
							}
							ActionCombo instantiAct = new ActionCombo(
									instantiatedActions, rule.getAction()
									.getSource());
							options.addAll(instantiAct.getOptions(mentalState,
									debugger));
						}

						/*
						 * If there are options and rule is an action rule,
						 * insert a new entry in the option map
						 */
						if (!options.isEmpty() && rule instanceof IfThenRule) {
							IfThenRule actrule = (IfThenRule) rule;
							optionMap.addOption(searchTauClass(actrule),
									options);
						}

						/* Break if the application order is linear */
						if (!options.isEmpty()
								&& ordr == RuleEvaluationOrder.LINEAR) {
							break;
						}
					}
				}
			}
			return optionMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the derives function.
	 *
	 * @return The derives function associated with this transition module.
	 */
	public Derives<A> getDerives() {
		return derives;
	}

	/**
	 * Gets the goal repository.
	 *
	 * @return The goal respository associated with this transition module.
	 */
	public GoalRepository<A, U> getRepository() {
		return repository;
	}

	/**
	 * Hides a rule from the program section of the module corresponding to this
	 * transition module such that it is no longer checked for applicability.
	 *
	 * @param rule
	 *            - The rule to be hided.
	 */
	public void hideRule(Rule rule) {

		try {

			/* Fetch the current program section */
			RuleSet currentProgram = module.getRuleSet();

			/*
			 * Create a new rule set that will serve as the new program section
			 */
			RuleSet newProgram = new RuleSet(RuleEvaluationOrder.LINEAR,
					currentProgram.getSource());

			/* Set the rule order of the new program section */
			newProgram.setRuleOrder(currentProgram.getRuleOrder());

			/*
			 * Add all rules except the rule to be hidden to the new program
			 * section
			 */
			for (Rule r : module.getRuleSet()) {
				if (!r.equals(rule)) {
					newProgram.addRule(r);
				}
			}

			/* Replace the old program section with the new */
			module.setRuleSet(newProgram);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Search the transition class corresponding to the specified rule.
	 *
	 * @param rule
	 *            - The rule whose corresponding transition class is to be
	 *            sought.
	 * @return The transition class to which the rule corresponds if one exists
	 *         in this transition module; <code>null</code> otherwise.
	 */
	public TauClass<T, A, U> searchTauClass(Rule rule) {
		for (TauClass<T, A, U> tauClass : tauClasses) {
			if (tauClass.getRule().equals(rule)) {
				return tauClass;
			}
		}
		return null;
	}

	//
	// Private methods
	//

	/**
	 * Initializes the goal repository.
	 */
	@SuppressWarnings("unchecked")
	private void initRepository(Collection<DatabaseFormula> knowledge) {

		try {

			/* Add the goals in the initial goal base to the repository */
			for (Update u : module.getGoals()) {
				U uu = (U) u;
				repository.add(repository.createRepositoryGoal(uu, knowledge));
			}

			/* Add the goals that occur in adopt actions to the repository */
			if (module.getRuleSet() != null) {
				for (Rule rule : module.getRuleSet()) {
					if (rule instanceof IfThenRule) {
						for (Action act : rule.getAction()) {
							if (act instanceof AdoptAction
									|| act instanceof AdoptOneAction) {
								Update u;
								if (act instanceof AdoptAction) {
									u = ((AdoptAction) act).getGoal();
								} else {
									u = ((AdoptOneAction) act).getGoal();
								}
								U uu = (U) u;
								RepositoryGoal<A, U> goal = repository
										.createRepositoryGoal(uu, knowledge);
								repository.add(goal);
							}
						}
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the transition classes belonging to this transition module.
	 */
	private void initClasses() {
		if (module.getRuleSet() != null) {
			for (Rule rule : module.getRuleSet()) {
				if (rule instanceof IfThenRule) {
					IfThenRule actionrule = (IfThenRule) rule;
					tauClasses.add(createTransitionClass(actionrule));
				}
			}
		}
	}
}
