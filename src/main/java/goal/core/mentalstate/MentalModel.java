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

package goal.core.mentalstate;

import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import krTools.KRInterface;
import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import krTools.language.DatabaseFormula;
import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.msc.AGoalLiteral;
import languageTools.program.agent.msc.BelLiteral;
import languageTools.program.agent.msc.GoalALiteral;
import languageTools.program.agent.msc.GoalLiteral;
import languageTools.program.agent.msc.MentalLiteral;
import mentalState.BASETYPE;

/**
 * A {@link MentalModel} is a belief base and a stack of goal bases. The top of
 * the goal-base-stack represents the current focus. It is part of the
 * MentalState (which also contains mailbox and perceptbase).
 * <p>
 * An agent can maintain mental models not only for itself but also for other
 * agents. This is why we need to be able to create multiple instances of
 * {@link MentalModel}s which are stored in the {@link MentalState}.
 * </p>
 *
 * @author K.Hindriks
 * @modified N.Kraayenbrink 31jan10-6feb10 An agent's (own) mental model can
 *           contain a stack of goal bases, depending whether or not it has its
 *           focus on a module.
 *
 */
public class MentalModel {
	/**
	 * A map containing the various {@link BeliefBase}s of type {@link BASETYPE}
	 * maintained by this {@link MentalModel}.
	 * <p>
	 * The knowledge of the agent is stored in its knowledge base. This base is
	 * static and does not change during runtime.
	 * </p>
	 * <p>
	 * A knowledge base is only used for the agent itself (the owner of this
	 * mental model) and not for other agents whose mental state is may be
	 * modeling. The idea here is that an agent does not have any direct or
	 * indirect access to the knowledge of another agent.
	 * </p>
	 * <p>
	 * The percepts the agent receives from its environment are stored in its
	 * percept base. This base is cleaned every reasoning cycle and the new
	 * percepts received are inserted.
	 * </p>
	 * <p>
	 * A percept base is only used for the agent itself (the owner of this
	 * mental model) and not for other agents because the agent does not have
	 * access to what another agent observes (and should store what it believes
	 * another agent has observed in that agent's mental model's belief base).
	 * </p>
	 * <p>
	 * The mail messages the agent receives from and sents to other agents are
	 * stored in its mailbox. This base is not cleaned but new messages are
	 * added. It is up to the agent itself to remove old messages.
	 * </p>
	 * <p>
	 * For reasons similar to those given for percepts above, a mailbox is only
	 * used for the agent itself (the owner of this mental model) and not for
	 * other agents.
	 * </p>
	 * <p>
	 * The beliefs of the agent that represent its environment are stored in its
	 * belief base. This is a base that is updated at runtime and used for
	 * maintaining an accurate picture of the actual state of affairs.
	 * </p>
	 * <p>
	 * A belief base is also used for other modeling the beliefs of other agents
	 * than the agent itself. That is, for maintaining a mental model of another
	 * agent's beliefs. The agent, of course, does not have direct access to
	 * another agent's beliefs and has to base such a mental model on what it
	 * observes the other agent is doing (from messages received, actions it
	 * performs).
	 * </p>
	 */
	private final Map<BASETYPE, BeliefBase> beliefBases = new LinkedHashMap<>();
	/**
	 * The goals of an agent are stored in a goal base. Because an agent may
	 * have different so-called "attention sets" (goal bases) at different
	 * moments due to focusing on particular goals in a specific context (a
	 * module call), an agent maintains a stack of such sets.
	 * <p>
	 * A goal base is also used for modeling the goals another agent is supposed
	 * to have but in that case no stack is maintained but a single goal base is
	 * used. (The stack only maintains a top element and only that element is
	 * used.)
	 * </p>
	 */
	private final Stack<GoalBase> goalBases = new Stack<>();
	/**
	 * Stack that keeps track of the 'views' on the {@link BeliefBase} of the
	 * {@link Agent} that owns this {@link MentalModel}.
	 */
	// private Stack<Theory> moduleViews;
	/**
	 * Keeps track of the time used by the KR layer.
	 */
	private long timeUsedByKR = 0;
	/**
	 * Keeps track of number of queries and updates performed.
	 */
	private long count = 0;

	/**
	 * @param type
	 *            The type to fetch.
	 * @return The base for the given type.
	 */
	public BeliefBase getBase(BASETYPE type) {
		return this.beliefBases.get(type);
	}

	/**
	 * Adds a {@link BeliefBase} of type {@link BASETYPE} to this
	 * {@link MentalModel}. Use
	 * {@link #addGoalBase(String, KRInterface, Set, BASETYPE)} to add a goal
	 * base!
	 *
	 * @param owner
	 *            The name of the agent that owns this {@link MentalModel}.
	 * @param agentName
	 *            The name of the agent whose beliefs, etc. are represented in
	 *            this mental model.
	 * @param language
	 *            The {@link KRInterface} used for representing formulas in the
	 *            belief base.
	 * @param content
	 *            The content, i.e. a set of {@link DatabaseFormula}s, to be
	 *            added to the base.
	 * @param type
	 *            The {@link BASETYPE} of the base.
	 * @throws KRInitFailedException
	 *             If the KR technology was unable to create the requested
	 *             database for storing formulas of the particular type.
	 * @throws KRQueryFailedException
	 * @throws KRDatabaseException
	 */
	public void addBase(AgentProgram owner, AgentId agentName,
			mentalState.MentalState state, List<DatabaseFormula> content,
			BASETYPE type) throws KRInitFailedException, KRDatabaseException,
			KRQueryFailedException {
		if (type.equals(BASETYPE.GOALBASE)) {
			throw new GOALBug(
					"The method addBase was used to set a base of type "
							+ BASETYPE.GOALBASE
							+ "but should only be used to add other bases. Use"
							+ "the method addGoalBase to add a goal base.");
		}
		BeliefBase base = new BeliefBase(type, state, content, owner, agentName);
		this.beliefBases.put(type, base);
	}

	/**
	 * The goal base is still a quite different beast and we need to treat it
	 * differently from a belief base.
	 *
	 * @param language
	 * @param content
	 * @param owner
	 * @param name
	 * @param agentName
	 * @param debugger
	 */
	public void addGoalBase(List<Update> content,
			mentalState.MentalState state, AgentProgram agent, AgentId owner,
			String name, AgentId agentName, Debugger debugger) {
		// Create new goal base and add content.
		GoalBase goalBase = new GoalBase(state, owner, agent, name, agentName);
		goalBase.setGoals(content, debugger);
		// Push the goal base on the stack of goal bases.
		this.goalBases.push(goalBase);
	}

	/**
	 * Cleans up all databases created by KR technology for the belief and goal
	 * base stack in this {@link MentalModel}.
	 *
	 * @throws GOALRuntimeErrorException
	 *             DOC
	 */
	public void cleanUp() {
		// Clean up knowledge, belief, percept bases, and mailbox.
		for (BASETYPE type : this.beliefBases.keySet()) {
			try {
				this.beliefBases.get(type).getDatabase().destroy();
			} catch (KRDatabaseException e) {
				throw new GOALRuntimeErrorException("Could not remove " + type,
						e);
			}
		}
		this.beliefBases.clear();

		// Clean up attention stack.
		while (!this.goalBases.isEmpty()) {
			// We're cleaning up and removing goal bases.
			// Collect and sum time used by KR and query/update count.
			this.timeUsedByKR += this.goalBases.peek().getTimeUsedByKR();
			this.count += this.goalBases.peek().getCount();
			this.goalBases.pop().cleanUp();
		}
	}

	/**
	 * Returns either the current attention set that the agent focuses on or
	 * else the top level goal base of the agent.
	 *
	 * @param use
	 *            {@code true} if we want to use the current focus;
	 *            {@code false} if we want to use the top level goal base.
	 * @return The current attention set, or the top level goal base.
	 */
	protected GoalBase getAttentionSet(boolean use) {
		if (use) {
			return this.goalBases.peek();
		} else {
			return this.goalBases.firstElement();
		}
	}

	/**
	 * Returns the stack of goal bases, called the attention stack.
	 *
	 * @return The attention stack.
	 */
	protected Stack<GoalBase> getAttentionStack() {
		return this.goalBases;
	}

	/************* query functionality ********************/

	/**
	 * Checks if a mental atom follows from the belief and/or goal base. A
	 * mental atom is a literal such as "bel(...)" or "goal(...)".
	 *
	 * @param literal
	 *            the mental atom to be checked.
	 * @param debugger
	 *            The debugger controlling the call
	 * @param focus
	 *            is true if we need to use current focus, or false if we need
	 *            to reset to global focus.
	 * @return a list of substitutions that when applied to the mental atom
	 *         result in instances of it that follow from the belief and/or goal
	 *         base. If the mental atom is closed, and follows from the mental
	 *         state, then pSubst is an empty list.
	 */
	public final Set<Substitution> query(MentalLiteral literal, boolean focus,
			Debugger debugger) {
		Query formula = literal.getFormula();
		Set<Substitution> substitutions = new HashSet<>();
		if (literal instanceof BelLiteral) {
			substitutions = beliefQuery(formula, debugger);
		} else if (literal instanceof GoalLiteral) {
			substitutions = goalQuery(formula, focus, debugger);
		} else if (literal instanceof AGoalLiteral) {
			substitutions = agoalQuery(formula, focus, debugger);
		} else if (literal instanceof GoalALiteral) {
			substitutions = goalaQuery(formula, focus, debugger);
		}

		if (substitutions.isEmpty()) {
			debugger.breakpoint(Channel.ATOM_QUERIES, literal,
					literal.getSourceInfo(), "Condition %s has no solutions.",
					literal);
		} else {
			debugger.breakpoint(Channel.ATOM_QUERIES, literal,
					literal.getSourceInfo(),
					"Condition %s holds for %s substitution(s): %s.", literal,
					substitutions.size(), substitutions);
		}

		return substitutions;
	}

	/**
	 * Evaluates a query on the belief base.
	 *
	 * @param query
	 *            formula to be queried.
	 * @param debugger
	 *            The debugger controlling the call
	 * @return (possibly empty) set of substitutions that when applied to the
	 *         formula ensure it follows from the belief base.
	 */
	public final Set<Substitution> beliefQuery(Query query, Debugger debugger) {
		return this.beliefBases.get(BASETYPE.BELIEFBASE).query(query, debugger);
	}

	/**
	 * Evaluates a query on the goal base.
	 *
	 * @param query
	 *            formula to be queried.
	 * @param focus
	 *            is true if we need to use current focus, or false if we need
	 *            to reset to global focus.
	 * @param debugger
	 *            The debugger controlling the call
	 * @return (possibly empty) set of substitutions that when applied to the
	 *         formula ensure it follows from the goal base.
	 */
	public final Set<Substitution> goalQuery(Query query, boolean focus,
			Debugger debugger) {
		return getAttentionSet(focus).query(query, debugger);
	}

	/**
	 * Evaluates a query on the goal and belief base.
	 *
	 * @param query
	 *            formula to be queried.
	 * @param focus
	 *            is true if we need to use current focus, or false if we need
	 *            to reset to global focus.
	 * @param debugger
	 *            The debugger controlling the call
	 * @return (possibly empty) set of substitutions that when applied to
	 *         formula ensure it follows from the goal base but NOT from the
	 *         belief base.
	 */
	public final Set<Substitution> agoalQuery(Query query, boolean focus,
			Debugger debugger) {
		Set<Substitution> substitutions = new LinkedHashSet<>();

		// First, check whether query follows from goal base.
		substitutions.addAll(goalQuery(query, focus, debugger));

		// Second, remove all substitutions for which query after applying
		// that substitution to it also follows from the belief base.
		Set<Substitution> removeSet = new LinkedHashSet<>();
		Query instantiatedQuery;
		for (Substitution substitution : substitutions) {
			instantiatedQuery = query.applySubst(substitution);
			if (!instantiatedQuery.isClosed()) { // should be closed; see TRAC
				// #174.
				throw new GOALRuntimeErrorException(
						"(MentalModel): goal query "
								+ query.toString()
								+ " did not result in closed formula but returned "
								+ instantiatedQuery.toString() + " instead.");
			}
			if (!beliefQuery(instantiatedQuery, debugger).isEmpty()) {
				removeSet.add(substitution);
			}
		}

		substitutions.removeAll(removeSet);

		return substitutions;
	}

	/**
	 * Evaluates a query on the goal and belief base.
	 *
	 * @param query
	 *            formula to be queried.
	 * @param focus
	 *            is true if we need to use current focus, or false if we need
	 *            to reset to global focus.
	 * @param debugger
	 *            The debugger controlling the call
	 * @return (possibly empty) set of substitutions that when applied to
	 *         formula ensure it follows from the goal base AND from the belief
	 *         base.
	 */
	public final Set<Substitution> goalaQuery(Query query, boolean focus,
			Debugger debugger) {
		Set<Substitution> lSubstSet = new LinkedHashSet<>();
		Set<Substitution> retainSubstSet = new LinkedHashSet<>();
		Query instantiatedQuery;

		// First, check whether pForm follows from goal base.
		lSubstSet.addAll(goalQuery(query, focus, debugger));

		// Second, check which substitutions such that when applied to pForm
		// pForm also follows from the belief base.
		for (Substitution lSubst : lSubstSet) {
			instantiatedQuery = query.applySubst(lSubst);
			if (!instantiatedQuery.isClosed()) {
				throw new GOALRuntimeErrorException(
						"(MentalModel): goal query with "
								+ query.toString()
								+ " did not result in closed formula but returned "
								+ instantiatedQuery.toString() + " instead.");
			}
			if (!beliefQuery(instantiatedQuery, debugger).isEmpty()) {
				retainSubstSet.add(lSubst);
			}
		}

		lSubstSet.retainAll(retainSubstSet);
		return lSubstSet;
	}

	/**
	 * Removes all goals from each {@link GoalBase} in the attention set
	 * {@link #goalBases} from which the goal to be dropped can be derived.
	 *
	 * @param goal
	 *            The goal to be dropped.
	 * @param debugger
	 *            Debugger observing the procedure.
	 */
	public void drop(Update goal, Debugger debugger) {
		for (int i = 0; i < this.goalBases.size(); i++) {
			this.goalBases.elementAt(i).drop(goal, debugger);
		}
	}

	/**
	 * Implements the blind commitment strategy of a GOAL agent. It removes
	 * goals when they are believed to be achieved completely. For efficiency
	 * reasons, this method should be called only twice: (i) after updating a
	 * mental state with percepts, and (ii) after performing an action.<br>
	 * If no goals are present in the goal base after removing achieved goals,
	 * it is attempted to de-focus the agent from the module currently focused
	 * on, up to a module where goals are present again (or the top-level
	 * module).
	 *
	 * @param debugger
	 *            The debugger controlling the call
	 */
	public void updateGoalState(Debugger debugger) {
		if (this.goalBases.isEmpty()) {
			// nothing to do here (model is not used).
			return;
		}

		Set<SingleGoal> goals = getAttentionSet(true).getGoals();
		List<SingleGoal> goalsToBeRemoved = new LinkedList<>();
		for (SingleGoal goal : goals) {
			if (!this.beliefBases.get(BASETYPE.BELIEFBASE)
					.query(goal.getGoal().toQuery(), debugger).isEmpty()) {
				goalsToBeRemoved.add(goal);
			}
		}

		for (SingleGoal goal : goalsToBeRemoved) {
			try {
				getAttentionSet(true).remove(goal, debugger);
			} catch (KRInitFailedException e) {
				throw new IllegalStateException(String.format(Resources
						.get(WarningStrings.FAILED_REMOVING_GOAL_FROM_GB), goal
						.toString()), e);
			}
		}
	}

	/**
	 * @return A string representation of the stack of attention sets stored in
	 *         this {@link MentalModel}. Contains the results of
	 *         {@link GoalBase#showContents()} for all sets.
	 */
	public String printAttentionStack() {
		StringBuilder text = new StringBuilder();
		int size = this.goalBases.size();

		text.append(this.goalBases.elementAt(0).getName() + ":\n");
		text.append(this.goalBases.elementAt(0).showContents());
		for (int i = 1; i < size; i++) {
			text.append("\n");
			text.append(this.goalBases.elementAt(i).getName());
			text.append(":\n");
			text.append(this.goalBases.elementAt(i).showContents());
		}

		return text.toString();
	}

	/**
	 * Makes the agent to which this model belongs focus its attention on a
	 * certain set of goals. The current set of goals are put on hold until
	 * {@link #defocus(Debugger)} is called.
	 * <p>
	 * It is assumed {@link #preFocus(String)} is already called, as that was
	 * necessary to construct the goal base more easily.
	 * </p>
	 *
	 * @param attentionSet
	 *            The set of goals the agent will focus on.
	 * @param debugger
	 *            The debugger monitoring the focusing.
	 */
	protected void focus(GoalBase attentionSet, Debugger debugger) {
		this.goalBases.push(attentionSet);
		debugger.breakpoint(Channel.GB_CHANGES, attentionSet, null,
				"focused to %s", attentionSet.getName());
	}

	/**
	 * De-focuses the attention of the agent to which this model belongs to.
	 * This also removes any achieved goals from the goal base the agent
	 * de-focuses to (that is, any achieved goals the agent temporarily ignored
	 * because it was focused on something else).<br>
	 * This method does nothing if the agent is not focused on anything.
	 *
	 * @param debugger
	 *            The current debugger.
	 */
	public void defocus(Debugger debugger) {
		GoalBase oldAttentionSet = this.goalBases.pop();
		// mark the attention set as unused (or at least remove one usage mark)

		// delete all observes from the now-obsolete goal base
		// but only if the attention set is not in use somewhere else
		oldAttentionSet.cleanUp();
		// remove any goals from new current attention set that have been
		// achieved.
		updateGoalState(debugger);
		debugger.breakpoint(Channel.GB_CHANGES, oldAttentionSet, null,
				"dropped goalbase  %s", oldAttentionSet.getName());

	}

	/**
	 * Returns a string representation of this {@link MentalModel}.
	 *
	 * @return A string representation of the mental model.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("MentalModel[");
		for (BASETYPE type : this.beliefBases.keySet()) {
			builder.append(this.beliefBases.get(type)).toString();
			builder.append(",\n");
		}
		builder.append(getAttentionSet(true).toString());
		builder.append("\n]");
		return builder.toString();
	}

	/**
	 * Returns the time used to perform queries and updates on this
	 * {@link BeliefBase} in this thread by the KR language.
	 *
	 * @return The time used in this thread to perform queries on this belief
	 *         base by the KR language.
	 */
	public long getTimeUsedByKR() {
		return this.timeUsedByKR;
	}

	/**
	 * Returns the total number of queries and updates that have been performed
	 * on this {@link BeliefBase} while executing a GOAL agent.
	 *
	 * @return The total number of queries performed on this belief base.
	 */
	public long getCount() {
		return this.count;
	}

}