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

import goal.core.agent.Agent;
import krTools.KRInterface;
import krTools.language.DatabaseFormula;
import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.Module;
import languageTools.program.agent.msc.MentalLiteral;
import languageTools.program.agent.msc.MentalStateCondition;
import languageTools.program.agent.msg.Message;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import goal.tools.logging.InfoLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import nl.tudelft.goal.messaging.messagebox.MessageBox;

/**
 * Represents a mental state of an agent and provides query and update
 * functionality. A mental state consists of one or more {@link MentalModel}s: A
 * model to represent the percepts, mail messages, knowledge, beliefs, and goals
 * of the agent owner of this state and models for representing other agent's
 * beliefs and goals.
 * <p>
 * Assumes that a mental state is represented using a single knowledge
 * representation language, i.e. {@link KRInterface}.
 * </p>
 * <p>
 * To get notified about changes you can subscribe as Observer to the individual
 * {@link BeliefBase}s. You can also subscribe to a top-level {@link GoalBase}
 * and receive all sub-module change info too.
 * </p>
 *
 * @author K.Hindriks
 *
 */
public class MentalState {
	/**
	 * The name of the agent that owns this {@link MentalState}.
	 */
	private final AgentId agentId;

	private final Set<AgentId> knownAgents = new LinkedHashSet<>();
	/**
	 * The {@link KRInterface} used for representing this {@link MentalState}.
	 */
	private final KRInterface language;
	/**
	 * Keys are string that represent the agent name as known and provided by
	 * EIS. Note that the Prolog representation may be different. TRAC #1128.
	 */
	private final Map<AgentId, MentalModel> models = new HashMap<>();
	/**
	 * Indicates whether the agent will use mental models for modeling other
	 * agent's mental states. This can be derived from the fact that an agent
	 * uses mental model queries and/or updates that use agent
	 * {@link SelectExpression}s of a type other than {@link SelectorType#SELF}
	 * or {@link SelectorType#THIS} (i.e., a variable, quantor, or constant).
	 */
	private boolean usesMentalModeling = false;

	/**
	 * Creates a mental state of an agent, including the initial belief and goal
	 * bases, a mailbox and a percept base. Knowledge is added to both the
	 * belief base of the agent and each of the individual goals included in the
	 * goal base.
	 *
	 * @param id
	 *            The agent that owns this {@link MentalState}.
	 * @param program
	 *            The parsed AgentProgram of the agent
	 * @param debugger
	 *            The current debugger
	 * @throws KRInitFailedException
	 *             when initialization of the belief base, goal base, mailbox or
	 *             percept base failed.
	 */
	public MentalState(AgentId id, AgentProgram program, Debugger debugger)
			throws KRInitFailedException {

		// Log creation of mental state event.
		new InfoLog("initializing mental state...");

		/**
		 * First store the agent's name and KR language; only thereafter call
		 * #addAgentModel(String, Debugger).
		 */
		this.language = program.getKRInterface();
		this.usesMentalModeling = program.usesMentalModels();
		this.agentId = id;
		this.addAgentModel(id, debugger, program);
	}

	/**
	 * Returns the {@link KRInterface} used for representing components in this
	 * {@link MentalState}.
	 *
	 * @return The KR language used for representing components in this mental
	 *         state.
	 */
	public KRInterface getKRInterface() {
		return this.language;
	}

	/**
	 * Returns the name of the {@link Agent} that owns this {@link MentalState}.
	 *
	 * @return The name of the agent that owns this mental state.
	 */
	public AgentId getAgentId() {
		return this.agentId;
	}

	/**
	 * Returns the agent's own {@link MentalModel}.
	 *
	 * @return The agent's own mental model.
	 */
	public MentalModel getOwnModel() {
		return models.get(agentId);
	}

	/**
	 * Returns a {@link BeliefBase} that is of a particular {@link BASETYPE}.
	 * <p>
	 * Can ask for {@link BASETYPE#KNOWLEDGEBASE}, {@link BASETYPE#BELIEFBASE},
	 * {@link BASETYPE#PERCEPTBASE}, {@link BASETYPE#MAILBOX}. Other types are
	 * not supported by this method.
	 *
	 * @param type
	 *            The type of belief base.
	 * @return A belief base of a particular type.
	 */
	public BeliefBase getOwnBase(BASETYPE type) {
		if (type.equals(BASETYPE.GOALBASE)) {
			throw new GOALBug(
					"A goal base should not be accessed via method getBase() from the class "
							+ getClass());
		}
		return getOwnModel().getBase(type);
	}

	/**
	 * Returns the current attention set of the agent. Always returns the
	 * attention set at the deepest focus level.
	 *
	 * @return The current attention set (set of goals) of the agent.
	 */
	public GoalBase getAttentionSet() {
		return getOwnModel().getAttentionSet(true);
	}

	/**
	 * Returns the stack of goal bases in which all goals the agent currently
	 * has (implicit if not in current attention set, explicit if present).
	 *
	 * @return The stack of goal bases (attention sets) of the agent.
	 */
	public Stack<GoalBase> getAttentionStack() {
		return getOwnModel().getAttentionStack();
	}

	/**
	 * Returns the names of the other agents for which this {@link MentalState}
	 * maintains {@link MentalModel}s.
	 *
	 * @return The names of other agents whose mental state are modeled in this
	 *         mental state.
	 */
	public Set<AgentId> getKnownAgents() {
		return this.knownAgents;
	}

	/**
	 * Adds a {@link MentalModel} for a (new) agent. Also used to create a
	 * mental model for the owner of this {@link MentalState}.
	 *
	 * CHECK that this method is thread safe. The agent may be running when this
	 * is called!
	 *
	 * @param id
	 *            The agent for which a mental model should be created.
	 * @param debugger
	 *            debugger to report on associated belief update. <b>Note:</b>
	 *            As during initialization the debugger needs to be different
	 *            (in order to avoid halting on this action) than during agent
	 *            execution (when the agent's debugger should be used), debugger
	 *            is a parameter of the method.
	 * @param goalProgram
	 *            The AgentProgram associated with the agent (if its me)
	 * @throws KRInitFailedException
	 *             If the KR technology failed to create the requested
	 *             databases.
	 */
	public synchronized void addAgentModel(AgentId id, Debugger debugger,
			AgentProgram... goalProgram) throws KRInitFailedException {
		// true if its me, the owner of this mental state.
		boolean me = id.equals(this.agentId);

		if (me || this.usesMentalModeling) {
			/**
			 * DO NOT CHANGE THE ORDER OF CREATION OF DATABASES BELOW! The
			 * {@link KnowledgeBase} of an agent must be created before any
			 * {@link GoalBase} or {@link BeliefBase} is created. This is
			 * because the latter import the knowledge base. Also, the order of
			 * introducing the mailbox, percept base, and belief base is
			 * important. The belief base may assume that the mailbox and
			 * percept base have already been created.
			 */

			// TODO: lazy creation of OTHER agents' models.

			// Add a mental model that can be used to model the (other) agent.
			// We know
			// that there is an(other) agent because we have a(n empty) mental
			// state.
			MentalModel model = new MentalModel();

			// Get content for the initial belief and goal base.
			if (me) {
				if (goalProgram.length != 1) {
					throw new GOALBug(
							"Could not find program to extract initial mental state content from.");
				}
				// Create the bases from the parsed GOAL agent program.
				model.addBase(agentId, agentId, language,
						goalProgram[0].getAllKnowledge(),
						BASETYPE.KNOWLEDGEBASE);
				model.addBase(agentId, agentId, language,
						new LinkedHashSet<DatabaseFormula>(), BASETYPE.MAILBOX);
				model.addBase(agentId, agentId, language,
						new LinkedHashSet<DatabaseFormula>(),
						BASETYPE.PERCEPTBASE);
			}
			// Create the belief base.
			Set<DatabaseFormula> initialBeliefs = new LinkedHashSet<>(0);
			model.addBase(this.agentId, id, this.language, initialBeliefs,
					BASETYPE.BELIEFBASE);
			// Create the goal base.
			List<Update> initialGoals = new ArrayList<>(0);
			model.addGoalBase(this.language, initialGoals, this.agentId,
					"main", id, debugger);

			// Add the mental model to the map of mental models maintained by
			// this
			// mental state.
			this.models.put(id, model);
		}

		// Insert the agent's existence as a fact 'agent(name)' in the belief
		// base.
		getOwnBase(BASETYPE.BELIEFBASE).updateAgentFact(true, id, me);
		knownAgents.add(id);
	}

	/**
	 * Removes a {@link MentalModel} of another agent from this
	 * {@link MentalState}. Also deletes any references to this agent in the
	 * belief base of the owner of this mental state (i.e. also removes the
	 * related 'agent(name)' fact).
	 *
	 * SHOULD ONLY BE USED TO REMOVE OTHER AGENTS' MODELS.
	 *
	 * @param id
	 *            The name of the agent whose model needs to be removed.
	 */
	public void removeAgentModel(AgentId id) {

		// Delete the fact that (other) agent exists from this agent's belief
		// base.
		getOwnBase(BASETYPE.BELIEFBASE).updateAgentFact(false, id, false);
		knownAgents.remove(id);

		// Agent with agentName no longer exists; remove corresponding mental
		// model.
		if (this.models.get(id) != null) {
			this.models.get(id).cleanUp();
			this.models.remove(id);
		}
	}

	/**
	 * Cleans up all databases maintained in this agent's {@link MentalModel}
	 * and removes the agent's mental model.
	 */
	public void cleanUp() {
		for (AgentId id : this.models.keySet()) {
			this.models.get(id).cleanUp();
		}
		this.models.clear();
	}

	/*********** query and update methods (interface to {@link MentalModel}s) ************/

	/**
	 * @param literal
	 * @param debugger
	 * @return
	 */
	public Set<Substitution> query(MentalLiteral literal, Debugger debugger) {
		// Process selector.
		Iterator<AgentId> agents;
		try {
			agents = literal.getSelector().resolve(this).iterator();
		} catch (KRInitFailedException e) {
			throw new GOALRuntimeErrorException(
					"Processing of selector failed: " + e.getMessage(), e);
		}
		boolean any = literal.getSelector().getAny();
		boolean focusl = literal.getSelector().getFocus();

		// Evaluate query and compute solutions.
		Set<Substitution> result = this.models.get(agents.next()).query(
				literal, focusl, debugger);

		if (any) {
			// We need to find only one agent whose mental model satisfies this
			// literal.
			while (agents.hasNext() && result.isEmpty()) {
				result = this.models.get(agents.next()).query(literal, focusl,
						debugger);
			}
		} else {
			// We need to verify that all models of agents in the set satisfy
			// this literal for
			// the same solution(s).
			while (agents.hasNext() && result.size() > 0) {
				Set<Substitution> currentResults = new LinkedHashSet<>();
				for (Substitution subst : result) {
					Set<Substitution> tempResult = this.models.get(
							agents.next()).query(literal.applySubst(subst),
									focusl, debugger);
					for (Substitution tempSubst : tempResult) {
						currentResults.add(subst.combine(tempSubst));
					}
				}
				result = currentResults;
			}
		}

		/*
		 * Negate the result if the literal is negated (by operator 'not') by
		 * returning an empty substitution set if there was a result, else
		 * returning a set containing one empty substitution to indicate a
		 * positive result.
		 */
		if (!literal.isPositive()) {
			if (result.isEmpty()) {
				result.add(this.getKRInterface().getEmptySubstitution());
			} else {
				return new LinkedHashSet<>(0);
			}
		}

		return result;
	}

	/**
	 * DOC
	 *
	 * @param query
	 * @param type
	 * @param debugger
	 * @return
	 */
	public Set<Substitution> query(Query query, BASETYPE type, Debugger debugger) {
		return this.getOwnBase(type).query(query, debugger);
	}

	/**
	 * Belief is inserted into the base associated with the type and the agent.
	 *
	 * @param update
	 *            The belief to be inserted.
	 * @param type
	 *            The type of the base to insert in.
	 * @param debugger
	 *            Debugger monitoring insertion.
	 * @param agent
	 *            An optional agent to do the insertion for; the current agent
	 *            is used otherwise
	 * @return success or failure.
	 */
	public boolean insert(Update update, BASETYPE type, Debugger debugger,
			AgentId... agent) {
		AgentId id = ((agent.length == 0) ? this.agentId : agent[0]);
		return this.models.get(id).getBase(type).insert(update, debugger);
	}

	/**
	 * Belief is inserted into the base associated with the type and the agent.
	 *
	 * @param formula
	 *            The belief to be inserted.
	 * @param type
	 *            The type of the base to insert in.
	 * @param debugger
	 *            Debugger monitoring insertion.
	 * @param agent
	 *            An optional agent to do the insertion for; the current agent
	 *            is used otherwise
	 *
	 * @return success or failure.
	 */
	public boolean insert(DatabaseFormula formula, BASETYPE type,
			Debugger debugger, AgentId... agent) {
		AgentId name = ((agent.length == 0) ? this.agentId : agent[0]);
		return this.models.get(name).getBase(type).insert(formula, debugger);
	}

	/**
	 * Belief is removed from the base associated with the type and the agent.
	 *
	 * @param update
	 *            The belief to be removed.
	 * @param type
	 *            The type of the base to remove from.
	 * @param debugger
	 *            Debugger monitoring deletion.
	 * @param agent
	 *            An optional agent to do the deletion for; the current agent is
	 *            used otherwise
	 *
	 * @return success or failure.
	 */
	public boolean delete(Update update, BASETYPE type, Debugger debugger,
			AgentId... agent) {
		AgentId name = ((agent.length == 0) ? this.getAgentId() : agent[0]);
		return this.models.get(name).getBase(type).delete(update, debugger);
	}

	/**
	 * Belief is removed from the base associated with the type and the agent.
	 *
	 * @param formula
	 *            The belief to be removed.
	 * @param type
	 *            The type of the base to remove from.
	 * @param debugger
	 *            Debugger monitoring deletion.
	 * @param agent
	 *            An optional agent to do the deletion for; the current agent is
	 *            used otherwise
	 *
	 * @return success or failure.
	 */
	public boolean delete(DatabaseFormula formula, BASETYPE type,
			Debugger debugger, AgentId... agent) {
		AgentId name = ((agent.length == 0) ? this.getAgentId() : agent[0]);
		return this.models.get(name).getBase(type).delete(formula, debugger);
	}

	/**
	 * Goal is inserted in goal base associated with the agent, but only if goal
	 * is not already present.
	 *
	 * @param update
	 *            The goal to be adopted.
	 * @param focus
	 *            Indicates whether focus attention set should be used or not.
	 * @param debugger
	 *            Debugger monitoring adoption.
	 * @param agent
	 *            An optional agent to do the adoption for; the current agent is
	 *            used otherwise
	 * @return success or failure.
	 */
	public boolean adopt(Update update, boolean focus, Debugger debugger,
			AgentId... agent) {
		AgentId name = ((agent.length == 0) ? this.getAgentId() : agent[0]);

		// Do not add goal if it already is implicated by an existing goal.
		// TODO: this is a precondition of adopt action; probably duplicating
		// code in actions.AdoptAction...
		if (!this.models.get(name).getAttentionSet(focus)
				.query(update.toQuery(), debugger).isEmpty()) {
			return false;
		}
		return this.models.get(name).getAttentionSet(focus)
				.insert(update, debugger);
	}

	/**
	 * Goals in goal base associated with the agent that entail the given goal
	 * are removed.
	 *
	 * @param update
	 *            The goal to be dropped.
	 * @param debugger
	 *            Debugger monitoring dropping of goal.
	 * @param agent
	 *            An optional agent to do the drop for; the current agent is
	 *            used otherwise
	 */
	public void drop(Update update, Debugger debugger, AgentId... agent) {
		AgentId name = ((agent.length == 0) ? this.getAgentId() : agent[0]);
		this.models.get(name).drop(update, debugger);
	}

	/**
	 * Creates a new focus of attention on a particular set of goals.
	 *
	 * @param attentionSet
	 *            The set of goals the agent should focus on.
	 * @param debugger
	 *            Debugger monitoring focus.
	 */
	public void focus(GoalBase attentionSet, Debugger debugger) {
		this.models.get(this.getAgentId()).focus(attentionSet, debugger);
	}

	/**
	 * Checks whether any goals have been achieved in the mean time, and, if so,
	 * removes those from the goal base of the agent.
	 *
	 * @param debugger
	 *            The debugger monitoring the procedure.
	 * @param agent
	 *            An optional agent to do the update for; the current agent is
	 *            used otherwise
	 */
	public void updateGoalState(Debugger debugger, AgentId... agent) {
		AgentId name = ((agent.length == 0) ? this.getAgentId() : agent[0]);
		this.models.get(name).updateGoalState(debugger);
	}

	/**
	 * Get the reward from the environment. CHECK uses the agent's environment
	 * and {@link MessageBox}. Might be not ok.
	 *
	 * @param envReward
	 *            is the award that the env gives to the current state. May be
	 *            null if there is no env reward.
	 * @author D.Singh
	 * @modified K.Hindriks moved from {@link Agent} class to this class.
	 * @modified W.Pasman #2393
	 *
	 * @return The reward
	 */
	public double getReward(Double envReward) {
		if (envReward == null) {
			return this.getAttentionSet().getGoals().isEmpty() ? 1.0 : 0.0;
		}
		return envReward;
	}

	/**
	 * Generates {@link Substitution}s that validate the given
	 * {@link MentalStateCondition}, with the added restriction that only one goal in
	 * the goalbase may be used to validate it. This goal may be different for
	 * each of the returned substitutions.
	 * <p>
	 * * Does focus/defocus using self.goal etc work in context condition too??
	 * Nick: it should. for the filter focus method the context is just
	 * evaluated as a head of an action rule in the parent module. The select
	 * focus method should also have no problems, as it either queries the
	 * top-level set or the temporary set with just a single goal.
	 *
	 * <br>
	 * This function seems very similar to
	 * {@link MentalStateCondition#evaluate(MentalState, SteppingDebugger)}. We might
	 * want to document which one to use when.
	 *
	 * <h1>known issues</h1>
	 * #1966 If there are no goals in the goalbase, contextQuery always returns
	 * an empty set. But this is wrong, for example querying
	 * <em>not(goal(aap))</em> should return the empty substitution as solution,
	 * right? {@link MentalStateCondition#evaluate(MentalState, SteppingDebugger)}
	 * seems to not have this problem.
	 *
	 * @param context
	 *            The {@link MentalStateCondition} to validate, usually a context
	 *            from a {@link Module}.
	 *
	 * @param validatingGoals
	 *            An existing {@link HashMap} (usually empty), where links
	 *            between {@link Substitution}s and {@link SingleGoal}s are
	 *            stored. Multiple goals may validate the same substitution.
	 * @param debugger
	 *            The {@link SteppingDebugger} in charge of the call.
	 *
	 * @return The set of {@link Substitution}s that validate the given
	 *         {@link MentalStateCondition} using only one goal from the goal base.
	 *
	 * @throws KRQueryFailedException
	 *             If something went wrong when querying
	 *
	 *             TODO: move to MentalStateCondition.
	 */
	public Set<Substitution> contextQuery(MentalStateCondition context,
			Map<Substitution, List<SingleGoal>> validatingGoals,
			Debugger debugger) {

		Set<Substitution> substitutions = new LinkedHashSet<>();
		Set<Substitution> partSubsts;

		// iterate over the goals in the current goal base; we need to get
		// substitutions that validate the context using only one goal.
		for (SingleGoal goal : this.getAttentionSet()) {
			// temporarily only have one of the goals
			this.models
			.get(this.agentId)
			.getAttentionStack()
			.push(new GoalBase(this.language, goal, this.agentId, this
					.getAgentId().getName(), debugger, this.agentId));

			// get the substitutions that make the given context true, given
			// the current single goal. Add these to the total set of
			// substitutions.
			try {
				partSubsts = context.evaluate(this, debugger);
				if (!partSubsts.isEmpty()) {
					substitutions.addAll(partSubsts);
					// make sure we do not have to re-query everything in order
					// to find the goal that validated the context, given one
					// of the substitutions just found
					for (Substitution subst : partSubsts) {
						if (!validatingGoals.containsKey(subst)) {
							validatingGoals.put(subst,
									new LinkedList<SingleGoal>());
						}
						validatingGoals.get(subst).add(goal);
					}
				}
			} finally {
				// remove the temporary goal base before exiting the method,
				// whether or not an exception was thrown
				getOwnModel().getAttentionStack().pop().cleanUp();
			}
		}

		return substitutions;
	}

	/**
	 * Checks in the message base which agents this message has already been
	 * sent to, and returns their names as a Set of Strings.
	 *
	 * @param msg
	 *            The {@link Message} to be search for in the message base
	 * @return a Set of agent names.
	 */
	public Collection<String> getReceiversOfMessage(Message msg) {
		return getOwnModel().getBase(BASETYPE.MAILBOX).getDatabase()
				.getReceiversOfMessage(msg);
	}

	/*********** helper methods ****************/

	/**
	 * Returns the content of this mental state as a string.
	 *
	 * @return A string representation of this mental state's content.
	 */
	@Override
	public String toString() {
		return "MentalState[" + this.models + "]";
	}

	/**
	 * Converts belief base and/or goal base to text string.
	 *
	 * @param addknowledge
	 *            set to true if knowledge should be included
	 * @param addbeliefs
	 *            set to true if beliefs should be included
	 * @param addpercepts
	 *            set to true if percepts should be included
	 * @param addmailbox
	 *            set to true if mailbox should be included
	 * @param addgoals
	 *            set to true if goals should be included
	 * @param focus
	 *            is true if we need to use current focus, or false if we need
	 *            to reset to global focus.
	 *
	 * @return The text string
	 */
	public String toString(boolean addknowledge, boolean addbeliefs,
			boolean addpercepts, boolean addmailbox, boolean addgoals,
			boolean focus) {
		String text = "";

		if (addknowledge) {
			// first convert the KB to string.
			text += "% ----- Knowledge -----\n";
			text += this.getOwnBase(BASETYPE.KNOWLEDGEBASE).getTheory();
		}

		if (addbeliefs) {
			text += "% ----- beliefs -----\n";
			text += this.getOwnBase(BASETYPE.BELIEFBASE).getTheory();
		}

		if (addpercepts) {
			text += "% ----- percepts -----\n";
			text += this.getOwnBase(BASETYPE.PERCEPTBASE).getTheory();
		}

		if (addmailbox) {
			text += "% ----- mails -----\n";
			text += this.getOwnBase(BASETYPE.MAILBOX).getTheory();
		}

		if (addgoals) {
			text += "% ----- goals -----\n";
			text = text + getOwnModel().getAttentionSet(focus).showContents();
		}

		return text;
	}

	/**
	 * @return See {@link MentalModel#printAttentionStack()}.
	 */
	public String printAttentionStack() {
		return getOwnModel().printAttentionStack();
	}

	/**
	 * Returns the total number of queries and updates that have been performed
	 * on the {@link BeliefBase}s and {@link GoalBase}s of the {@link Agent}
	 * that owns this {@link MentalState}.
	 *
	 * @return The total number of queries and updates that have been performed.
	 */
	public long getCount() {
		long countSum = 0;

		for (MentalModel model : models.values()) {
			// Add count for mailbox, percept and belief bases.
			for (BASETYPE type : BASETYPE.values()) {
				if (model.getBase(type) != null) {
					countSum += model.getBase(type).getCount();
				}
			}
			// Add count for goal bases.
			for (GoalBase base : model.getAttentionStack()) {
				countSum += base.getCount();
			}
			// Add count for queries and updates that have been performed on
			// goal bases that have been removed, which is stored in the mental
			// model.
			countSum += model.getCount();
		}
		return countSum;
	}

}
