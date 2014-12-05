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

import eis.iilang.Percept;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import krTools.KRInterface;
import krTools.database.Database;
import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import krTools.language.DatabaseFormula;
import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.msg.Message;
import mentalState.BASETYPE;

/**
 * <p>
 * A belief base is a {@link Database} with generic handling and querying
 * capabilities.
 * </p>
 * <p>
 * The theory represents the "visible" contents of the belief base. It should
 * correspond with the database, except possibly for background knowledge
 * present in the database which is not reflected in the theory. In order to
 * ensure the theory corresponds correctly in this sense with the database, any
 * changes to the belief base need to be made by using the methods provided by
 * this class. By directly modifying the underlying database, the correspondence
 * may be lost. Background knowledge added to the database is assumed to be
 * static. Additionally, the predicates declared/defined in the belief base's
 * theory and in background theories should not overlap (this will raise
 * exceptions when inserting the background knowledge into a Prolog database).
 * </p>
 * <p>
 * A BeliefBase is observable; observers are notified whenever the contents of
 * the {@link BeliefBase} have changed. (For example after a successful 'insert'
 * call)
 *
 * @author K.Hindriks
 * @modified N.Kraayenbrink extending {@link Observable}
 */
public class BeliefBase {
	/**
	 * The name of the agent whose goals are modeled by this goal base.
	 */
	private final AgentId agentName;
	/**
	 * The {@link BASETYPE} of this {@link BeliefBase}. Indicates whether base
	 * is used for percepts, mails, knowledge, or beliefs. Goals are handled by
	 * {@link GoalBase}s.
	 */
	private final BASETYPE type;
	/**
	 * Representation of the contents of this {@link BeliefBase} as a
	 * {@link Theory} for internal GOAL purposes.
	 */
	private Theory theory = null;
	/**
	 * The KR database used for storing the contents of this base.
	 */
	private Database database = null;
	/**
	 * DOC
	 */
	private mentalState.MentalState state;
	/**
	 * Keeps track of number of queries and updates performed.
	 */
	private final long count = 0;

	/**
	 * <p>
	 * Constructs a belief base of a certain {@link BASETYPE}, creates the
	 * corresponding database maintained by the KR layer, and initializes it
	 * with given content.
	 * </p>
	 * <p>
	 * The knowledge, beliefs, mails, and percepts of an agent are stored in
	 * {@link BeliefBase}s. Goals are handled by {@link GoalBase}s.
	 * </p>
	 *
	 * @param baseType
	 *            The type of this {@link BeliefBase}. Should be either
	 *            {@link BASETYPE#KNOWLEDGEBASE}, {@link BASETYPE#BELIEFBASE},
	 *            {@link BASETYPE#MAILBOX}, or {@link BASETYPE#PERCEPTBASE}.
	 * @param language
	 *            The KR language used for representing the contents of this
	 *            belief base and creating the corresponding KR database.
	 * @param content
	 *            The content to be inserted into the database that is created.
	 * @param owner
	 *            The name of the agent that owns this {@link BeliefBase}.
	 * @param agentName
	 *            The name of the agent whose beliefs, etc are represented using
	 *            this belief base.
	 * @throws KRInitFailedException
	 *             If the KR technology failed to create and/or initialize a new
	 *             database.
	 * @throws KRQueryFailedException 
	 * @throws KRDatabaseException 
	 */
	public BeliefBase(BASETYPE baseType, mentalState.MentalState state,
			List<DatabaseFormula> content, AgentProgram owner, AgentId agentName)
					throws KRInitFailedException, KRDatabaseException, KRQueryFailedException {
		this.agentName = agentName;
		this.type = baseType;
		this.state = state;		
		this.theory = new Theory(content);
		this.database = state.makeDatabase(this.type, content, owner);
	}

	/**
	 * Returns the {@link BASETYPE} of this {@link BeliefBase}.
	 *
	 * @return The type of this belief base.
	 */
	public BASETYPE getType() {
		return this.type;
	}

	/**
	 * Returns the {@link Theory} used by GOAL to represent this
	 * {@link BeliefBase}.
	 *
	 * @return The theory used by GOAL to represent this belief base.
	 */
	public Theory getTheory() {
		return this.theory;
	}

	/**
	 * Returns the {@link Database} used by the {@link KRInterface} for
	 * representing this {@link BeliefBase}.
	 *
	 * @return The database used by the KR language for representing this belief
	 *         base.
	 */
	protected Database getDatabase() {
		return this.database;
	}

	/*********** query methods ****************/

	/**
	 * Performs a query on the belief base and returns a non-empty set of
	 * substitutions if the query succeeds.
	 *
	 * @param formula
	 *            The query.
	 * @param debugger
	 *            The current debugger.
	 *
	 * @return a set of substitutions each of which make the query true, or an
	 *         empty set otherwise.
	 */
	public final Set<Substitution> query(Query formula, Debugger debugger) {
		try {
			return database.query(formula);
		} catch (Exception e) {
			new Warning(debugger, String.format(
					Resources.get(WarningStrings.FAILED_DB_QUERY),
					formula.toString(), this.database.getName()), e);
			return new LinkedHashSet<>(0);
		}
	}

	/*********** updating (insertion, deletion) methods ****************/

	/**
	 * Adds a {@link DatbaseFormula} to the database, if the database does not
	 * contain the formula yet.
	 *
	 * @param formula
	 *            The formula to be inserted (added).
	 * @param debugger
	 *            The debugger monitoring the insertion.
	 * @return <code>true</code> if anything changed; <code>false</code> if
	 *         nothing changed or a KR technology exception occurred.
	 */
	public boolean insert(DatabaseFormula formula, Debugger debugger) {
		boolean change = this.theory.add(formula);
		if (change) {
			try {
				database.insert(formula);

				debugger.breakpoint(getChannel(), formula,
						"%s has been inserted into the belief base of %s.",
						formula, agentName);
			} catch (Exception e) {
				new Warning(debugger, String.format(
						Resources.get(WarningStrings.FAILED_ADD_DBFORMULA),
						formula.toString(), this.database.getName()), e);
				// KR did not succeed, remove formula again from theory to keep
				// KR database and theory synchronized.
				change = !this.theory.remove(formula);
			}
		}
		return change;
	}

	/**
	 * See {@link #update(List, List, SteppingDebugger)}.
	 *
	 * @param update
	 *            The update to be processed.
	 * @param debugger
	 *            The debugger monitoring the updating.
	 * @return <code>true</code> if anything changed; <code>false</code>
	 *         otherwise.
	 */
	public boolean insert(Update update, Debugger debugger) {
		return this.update(update.getAddList(), update.getDeleteList(),
				debugger);
	}

	/**
	 * DOC
	 *
	 * @param message
	 * @param received
	 * @param debugger
	 */
	public void insert(Message message, boolean received, Debugger debugger) {
		try {
			Set<DatabaseFormula> updates = state.insert(database, message, received);
			for (DatabaseFormula formula : updates) {
				boolean change = theory.add(formula);
				if (change) {
					debugger.breakpoint(getChannel(), formula,
							"%s has been inserted into the mailbox of %s.",
							formula, agentName);
				}
			}
		} catch (KRDatabaseException e) {
			new Warning(debugger, String.format(
					"Failed to add message %s to %s (received: %s)",
					message.toString(), this.database.getName(), received), e);
		}
	}

	/**
	 * Removes a {@link DatbaseFormula} from the database, if the database
	 * contains the formula.
	 *
	 * @param formula
	 *            The formula to be deleted (removed).
	 * @param debugger
	 *            The debugger monitoring the removal.
	 * @return <code>true</code> if anything changed; <code>false</code> if
	 *         nothing changed or a KR technology exception occurred.
	 */
	public boolean delete(DatabaseFormula formula, Debugger debugger) {
		boolean changed = this.theory.remove(formula);
		if (changed) {
			try {
				database.delete(formula);

				debugger.breakpoint(getChannel(), formula,
						"%s has been deleted from the belief base of %s.",
						formula, agentName);
			} catch (KRDatabaseException e) {
				new Warning(debugger, String.format(
						Resources.get(WarningStrings.FAILED_DEL_DBFORMULA),
						formula.toString(), this.database.getName()), e);
				// KR did not succeed, reinsert formula into theory again to
				// keep KR database and theory synchronized.
				changed = !this.theory.add(formula);
			}
		}
		return changed;
	}

	/**
	 * See {@link #update(List, List, SteppingDebugger)}. Deleting an
	 * {@link Update} is calling update with the add and delete lists reversed.
	 *
	 * @param update
	 *            The update to be processed.
	 * @param debugger
	 *            The debugger monitoring the updating.
	 * @return <code>true</code> if anything changed; <code>false</code>
	 *         otherwise.
	 */
	public boolean delete(Update update, Debugger debugger) {
		return this.update(update.getDeleteList(), update.getAddList(),
				debugger);
	}

	/**
	 * Deletes the formulas in the delete list and adds the formulas in the add
	 * list to this {@link BeliefBase}. First removes and then adds, so any
	 * formulas that appear in both lists will result in adding the formula if
	 * it is not already present.
	 *
	 * @param addList
	 *            The 'add' list of formulas that are to be inserted.
	 * @param deleteList
	 *            The 'delete' list of formulas that are to be removed.
	 * @param debugger
	 *            The {@link SteppingDebugger} monitoring the updating.
	 *
	 * @return <code>true</code> if anything changed; <code>false</code>
	 *         otherwise.
	 */
	public boolean update(List<DatabaseFormula> addList,
			List<DatabaseFormula> deleteList, Debugger debugger) {
		boolean changed = false;

		// Delegate reporting to methods called.

		// First handle the delete list and remove formulas.
		for (DatabaseFormula formula : deleteList) {
			changed = this.delete(formula, debugger) || changed;
		}
		// And then handle the add list and add formulas.
		for (DatabaseFormula formula : addList) {
			changed = this.insert(formula, debugger) || changed;
		}

		return changed;
	}

	/**
	 * Inserts new content (the {@link addList} into the percept base and
	 * removes old content (the {@link deleteList}) from the percept base.
	 *
	 * @param addList
	 *            The set of facts to be added. (The parameter is called addList
	 *            for historical reasons.)
	 * @param deleteList
	 *            The set of facts to be deleted. (The parameter is called
	 *            deleteList for historical reasons.)
	 * @param debugger
	 *            The debugger monitoring the call.
	 */
	public void updatePercepts(Set<Percept> addList, Set<Percept> deleteList,
			Debugger debugger) {
		if (!addList.isEmpty() || !deleteList.isEmpty()) {
			debugger.breakpoint(Channel.PERCEPTS, null, "Processing percepts.");

			Database perceptbase = getDatabase();
			for (eis.iilang.Percept percept : deleteList) {
				try {
					DatabaseFormula formula = state.delete(perceptbase, percept);
					theory.remove(formula);
					debugger.breakpoint(getChannel(), formula,
							"%s has been deleted from the percept base of %s.",
							formula, agentName);
				} catch (KRDatabaseException e) {
					throw new GOALRuntimeErrorException("Could not delete percept"
							+ percept + " from " +  agentName + "'s" + "percept base", e);
				}
			}
			for (eis.iilang.Percept percept : addList) {
				try {
					DatabaseFormula formula = state.insert(perceptbase, percept);
					theory.add(formula);
					debugger.breakpoint(getChannel(), formula,
							"%s has been inserted into the percept base of %s.",
							formula, agentName);
				} catch (KRDatabaseException e) {
					throw new GOALRuntimeErrorException("Could not add percept"
							+ percept + " into " +  agentName + "'s" + "percept base", e);
				}
			}
			debugger.breakpoint(Channel.PERCEPTS, null, "Percepts processed.");
		}
	}

	/**
	 * DOC
	 *
	 * @param insert
	 * @param id
	 * @param me
	 *
	 * @throws KRInitFailedException
	 */
	public void updateAgentFact(boolean insert, AgentId id, boolean me) {
		try {
			Set<DatabaseFormula> updates = state.updateAgentFact(database, insert, id, me);
			for (DatabaseFormula formula : updates) {
				theory.add(formula);
			}
		} catch (KRDatabaseException e) {
			throw new GOALRuntimeErrorException("Could not "
					+ (insert ? "add " : "remove ") + "fact that " + id
					+ (me ? " (which is me) " : "") + "exists "
					+ (insert ? "to " : "from ")
					+ (me ? "my " : agentName + "'s") + "belief base", e);
		}

		// Report update of belief base.
		// FIXME: results in immediate deadlock because DataBasePanel who
		// subscribed wants access to BeliefBase
		// right away...
		// debugger.breakpoint(getChannel(), null,
		// "Agent fact for %s has been " + (insert ? "inserted" : "deleted") +
		// ".", id);
	}

	// *********** helper methods ****************/

	/**
	 * Returns a string representation of the contents of this
	 * {@link BeliefBase}.
	 */
	@Override
	public String toString() {
		return this.type + "[\n" + this.theory + "]";
	}

	/**
	 * Returns the channel on which debug messages should be reported for this
	 * particular database type.
	 *
	 * @return The channel on which to output debug messages.
	 */
	private Channel getChannel() {
		switch (this.type) {
		case BELIEFBASE:
			return Channel.BB_UPDATES;
		case MAILBOX:
			return Channel.MAILS_CONDITIONAL_VIEW;
		case PERCEPTBASE:
			return Channel.PERCEPTS_CONDITIONAL_VIEW;
		default:
			// knowledge base changes are not reported via debug channels and
			// goal base cannot be here.
			throw new GOALBug(
					"Attempt to report debug out message for database "
							+ this.type);
		}
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
