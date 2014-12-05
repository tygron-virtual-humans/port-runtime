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
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import mentalstatefactory.MentalStateFactory;

/**
 * <p>
 * A goal base consists of a set of individual or single goals. Each individual
 * goal is represented as a single goal that consists of an update It should
 * correspond with the database, except possibly for background knowledge
 * present in the database which is not reflected in the theory. In order to
 * ensure the theory corresponds correctly in this sense with the database, any
 * changes to the belief base need to be made by using the methods provided by
 * this class. By directly modifying the underlying database, the correspondence
 * may be lost. Background knowledge added to the database is assumed to be
 * static. Additionally, the predicates declared/defined in the goal base's
 * theory and in background theories should not overlap (this will raise
 * exceptions when inserting the background knowledge into a Prolog database).
 *
 * </p>
 * <p>
 * Note that each goal of the agent is implemented as a separate database.
 *
 * </p>
 * <p>
 * The notification of changes works as follows. GoalBases representing modules
 * notify their parent module. This notification chain is created in
 * {@link MentalModel#focus}. Therefore, listeners for changes in goalbases can
 * observe the topmost goalbase and then also will be notified of submodule
 * changes.
 * </p>
 *
 * @author K.Hindriks
 * @modified N.Kraayenbrink small modifications to set-up of constructor and
 *           addition of isEmpty
 * @modified N.Kraayenbrink extends Observable, implements Observer. Observes
 *           the {@link GoalBase} one step deeper in the attention stack, so
 *           that the top-level goal base can notify observers of changes in any
 *           goal base.
 */

public final class GoalBase implements Iterable<SingleGoal> {
	/**
	 * The contents of this {@link GoalBase}. WARNING: only add and remove goals
	 * using {@link #addGoal(SingleGoal)} and {@link #remove(SingleGoal)} unless
	 * {@link SingleGoal#markOccurrence} or {@link SingleGoal#unmarkOccurrence}
	 * respectively has been called on the goal to add/remove.
	 */
	private final Set<SingleGoal> goals = new LinkedHashSet<>();
	/**
	 * The name of the {@link AgentProgram} that owns this {@link GoalBase}.
	 */
	private final AgentProgram owner;
	/**
	 * The name of the agent whose goals are modeled by this goal base.
	 */
	private AgentId agentName;
	/**
	 * The name of this goal base; used to link goal base to attention set
	 * associated with a particular module. The top level goal base is linked to
	 * the "main" module.
	 */
	private final String name;

	/**
	 * Keeps track of the time used by the KR layer.
	 */
	private long timeUsedByKR = 0;
	private long time;
	/**
	 * Keeps track of number of queries and updates performed.
	 */
	private long count = 0;

	/**
	 * Creates a new {@link GoalBase}. Use {@link #setGoals(List, String)} to
	 * add initial content to this goal base.
	 *
	 * @param language
	 *            The KR language used for representing goals.
	 * @param owner
	 * 			  The agent that owns this goal base.
	 * @param me
	 *            The name of the agent that owns this goal base.
	 * @param name
	 *            The name of the base itself.
	 * @param agentName
	 *            The name of the agent whose goals are modeled in this goal
	 *            base.
	 */
	public GoalBase(AgentId me, AgentProgram owner, String name,
			AgentId... agentName) {
		this.owner = owner;
		this.name = name;
		if (agentName.length == 0) {
			this.agentName = me;
		} else {
			this.agentName = agentName[0];
		}
	}

	/**
	 * Creates a goal base with a single, already existing goal inside.
	 *
	 * @param singleGoal
	 *            The goal to be inserted as only goal in the new goal base.
	 * @param owner
	 * 			  The agent that owns this goal base.
	 * @param me
	 *            The name of the agent that owns this goal base.
	 * @param name
	 *            The name of the base itself.
	 * @param debugger
	 *            The current debugger.
	 * @param agentName
	 *            The name of the agent whose goals are modeled in this goal
	 *            base.
	 */
	public GoalBase(SingleGoal singleGoal, AgentId me, AgentProgram owner,
			String name, Debugger debugger, AgentId... agentName) {
		this(me, owner, name, agentName);
		this.addGoal(singleGoal, debugger);
	}

	/**
	 * The name of this goal base, linking it to a module that has a particular
	 * attention set.
	 *
	 * @return The name of this goal base
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the set of goals present in this {@link GoalBase}, represented as
	 *         a set of {@link SingleGoal}s.
	 */
	public Set<SingleGoal> getGoals() {
		return goals;
	}

	/**
	 * Adds the content provided as a list of {@Update}s as goals to
	 * this {@link GoalBase}.
	 *
	 * @param content
	 *            The content to be added to this goal base.
	 * @param debugger
	 *            The current debugger.
	 */
	protected void setGoals(List<Update> content, Debugger debugger) {
		try
		{
			mentalState.MentalState state = MentalStateFactory.getInterface(
					owner.getKRInterface().getClass());
			for (Update goal : content) {
				count++;
				getTime();
				addGoal(new SingleGoal(goal, owner, state), debugger);
				updateTimeUsed();
			}
		}
		catch (Exception e) {
			new Warning(debugger, String.format(
					Resources.get(WarningStrings.FAILED_ADD_GOAL),
					content.toString(), this.owner.toString()), e);
		}
	}

	/**
	 * @return <code>true</code> iff no goals are present in this goal base.
	 */
	public boolean isEmpty() {
		return goals.isEmpty();
	}

	@Override
	public Iterator<SingleGoal> iterator() {
		return goals.iterator();
	}

	// *********** query methods ****************/

	/**
	 * Performs a query on the goal base by checking whether the query follows
	 * from a single goal stored in one of the databases associated with the
	 * goal base. The substitutions computed are such that when applied to the
	 * query the query follows from one of the goals. Note that knowledge is
	 * part of these databases, thus a query follows from a goal in combination
	 * with the knowledge the agent has.
	 *
	 * @param query
	 *            The query.
	 * @param debugger
	 *            The current debugger.
	 *
	 * @return a (possibly empty) set of substitutions each of which make the
	 *         query succeed.
	 */
	public final Set<Substitution> query(Query query, Debugger debugger) {
		Set<Substitution> substitutions = new LinkedHashSet<>();
		for (SingleGoal goal : this.goals) {
			try {
				// Get current time used in this thread.
				count++;
				getTime();
				substitutions.addAll(goal.getGoalDatabase().query(query));
				// Update time used.
				updateTimeUsed();
			} catch (Exception e) {
				new Warning(debugger, String.format(
						Resources.get(WarningStrings.FAILED_GOAL_QUERY),
						query.toString(), goal.getGoalDatabase().getName()), e);

				throw new GOALRuntimeErrorException(e);
			}
		}
		return substitutions;
	}

	// *********** insertion methods ****************/

	/**
	 * Inserts a new goal into the goal base. Checks whether the formula
	 * (update) already occurs in the goal base.
	 *
	 * @param goal
	 *            The goal to be inserted.
	 * @param debugger
	 *            The {@link SteppingDebugger} observing the call.
	 * @return true if anything changed.
	 */
	public boolean insert(Update goal, Debugger debugger) {
		try {
			mentalState.MentalState state = MentalStateFactory.getInterface(
					owner.getKRInterface().getClass());
			count++;
			getTime();
			addGoal(new SingleGoal(goal, this.owner, state), debugger);
			updateTimeUsed();
		} catch (Exception e) {
			new Warning(debugger, String.format(
					Resources.get(WarningStrings.FAILED_ADD_GOAL),
					goal.toString(), this.owner.toString()), e);
			return false;
		}
		return true;
	}

	/**
	 * Adds a single, existing goal to the goal base. Should always be used to
	 * add goals to this goal base, also internally, since it will mark the goal
	 * as used (by this goal base).
	 *
	 * @param goal
	 *            the goal to add.
	 * @param debugger
	 *            The current debugger.
	 */
	public void addGoal(SingleGoal goal, Debugger debugger) {
		addGoalPrivate(goal);
		debugger.breakpoint(
				Channel.GB_UPDATES,
				goal,
				"%s has been adopted into the "
						+ (this.owner.equals(this.agentName) ? ""
								: this.agentName + "'s ") + "goal base: %s.",
				goal, this.name);
	}

	/**
	 * Non-overwritable version of addGoal, as we need this during
	 * initialization.
	 */
	private void addGoalPrivate(SingleGoal goal) {
		goals.add(goal);
		goal.markOccurrence();
	}

	// *********** deletion methods ****************/

	/**
	 * Drops all goals that entail the goal to be dropped.
	 *
	 * @param dropgoal
	 *            goal to be dropped.
	 * @param debugger
	 *            the current debugger
	 * @return A (possibly empty) list of goals that have been dropped.
	 */
	public List<SingleGoal> drop(Update dropgoal, Debugger debugger) {
		List<SingleGoal> goalsToBeDropped = new LinkedList<>();
		for (SingleGoal goal : goals) {
			try {
				// Get current time used in this thread.
				count++;
				getTime();
				if (!goal.getGoalDatabase().query(dropgoal.toQuery()).isEmpty()) {
					goalsToBeDropped.add(goal);
				}
				// Update time used.
				updateTimeUsed();
			} catch (Exception e) {
				new Warning(debugger, String.format(Resources
						.get(WarningStrings.FAILED_GB_QUERY), dropgoal
						.toQuery().toString(), this.owner.toString()), e);
			}
		}
		goals.removeAll(goalsToBeDropped);
		for (SingleGoal goal : goalsToBeDropped) {
			debugger.breakpoint(
					Channel.GB_UPDATES,
					goal,
					"Goal %s"
							+ " has been dropped from the "
							+ (this.owner.equals(this.agentName) ? ""
									: this.agentName + "'s ")
							+ "goal base: %s.", goal.toString(), this.name);

			count++;
			getTime();
			goal.unmarkOccurrence();
			updateTimeUsed();

		}
		return goalsToBeDropped;
	}

	/**
	 * DOC
	 *
	 * @param goal
	 *            The goal to remove
	 * @param debugger
	 *            The current debugger.
	 * @return True when the remove was successful.
	 * @throws KRInitFailedException
	 */
	public boolean remove(SingleGoal goal, Debugger debugger)
			throws KRInitFailedException {
		boolean result = this.goals.remove(goal);
		if (result) {
			debugger.breakpoint(Channel.GOAL_ACHIEVED, goal,
					"Goal %s has been achieved and removed from the "
							+ (this.owner.equals(this.agentName) ? ""
									: this.agentName + "'s ")
							+ "goal base: %s.", goal, getName());
			// #2968 goal is to be disposed. Don't use with delay..
			goal.unmarkOccurrence();
		}
		return result;
	}

	/**
	 * Used by Model Checker.
	 *
	 * @param goal
	 *            The goal to remove
	 * @param debugger
	 *            The current debugger.
	 * @return True when the remove was successful.
	 * @throws KRInitFailedException
	 */
	public boolean remove(Update goal, Debugger debugger)
			throws KRInitFailedException {
		for (SingleGoal g : this.goals) {
			if (g.getGoal().equals(goal)) {
				return this.remove(g, debugger);
			}
		}
		return false;
	}

	/**
	 * cleanup all databases used to store goals by calling the corresponding
	 * methods of the kr technology. Should be called when goal base is deleted. <br>
	 */
	public void cleanUp() {
		for (SingleGoal goal : goals) {
			goal.unmarkOccurrence();
		}
	}

	// *********** helper methods ****************/

	/**
	 * Converts goal base into a string.
	 */
	@Override
	public String toString() {
		StringBuffer text = new StringBuffer("GoalBase[");
		boolean first = true;
		for (SingleGoal goal : goals) {
			if (first) {
				text.append(goal.toString());
				first = false;
			} else {
				text.append(", ");
				text.append(goal.toString());
			}
		}
		text.append("]");
		return text.toString();
	}

	/**
	 * Gets the goals in this {@link GoalBase} as a string representation, one
	 * goal per line. Used by export functionality and Database viewer.
	 *
	 * FIXME change name. This does not show anything, it just returns string
	 * representation.
	 *
	 * @return The goals inside this goal base, one per newline-terminated line.
	 *
	 */
	public String showContents() {
		StringBuilder sbuild = new StringBuilder();
		for (SingleGoal goal : goals) {
			sbuild.append(goal.toString());
			sbuild.append(".\n");
		}
		return sbuild.toString();
	}

	/**
	 * Returns the time used to perform queries and updates on this
	 * {@link BeliefBase} in this thread by the KR language.
	 *
	 * @return The time used in this thread to perform queries on this belief
	 *         base by the KR language.
	 */
	public long getTimeUsedByKR() {
		return timeUsedByKR;
	}

	/**
	 * Assigns the current thread CPU time to {@link #time}.
	 */
	private void getTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		time = bean.getCurrentThreadCpuTime();
	}

	/**
	 * Updates the time used to perform queries and updates on this
	 * {@link BeliefBase} so far. {@link #timeUsedByKR} is used to keep track of
	 * the total time used.
	 */
	private void updateTimeUsed() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		timeUsedByKR += (bean.getCurrentThreadCpuTime() - time);
	}

	/**
	 * Returns the total number of queries and updates that have been performed
	 * on this {@link BeliefBase} while executing a GOAL agent.
	 *
	 * @return The total number of queries performed on this belief base.
	 */
	public long getCount() {
		return count;
	}

}
