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

package goal.core.kr;

import goal.core.agent.AgentId;
import goal.core.kr.language.CompiledQuery;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Update;
import goal.core.mentalstate.BASETYPE;
import goal.core.program.Message;
import goal.tools.errorhandling.exceptions.KRInitFailedException;
import goal.tools.errorhandling.exceptions.KRQueryFailedException;

import java.util.Collection;
import java.util.Set;

/**
 * Database is an interface to a KR dependent implementation. Each database can
 * be viewed as a set of sentences, i.e. closed formula. An API implementation
 * of the Database interface needs to comply with this set-theoretic view of a
 * database and come with a set of associated set-theoretic methods such as
 * defined in the UpdateEngine interface.
 * <p>
 * For creating see the
 * {@link KRlanguage#makeDatabase(goal.core.mentalstate.BASETYPE, Theory, String)}
 * factory method.
 * <p>
 * Each agent is assigned with a SET of databases. The first database that has
 * to be made for an agent is the {@link BASETYPE#KNOWLEDGEBASE} database. After
 * that, a single {@link BASETYPE#BELIEFBASE}, {@link BASETYPE#MAILBOX},
 * {@link BASETYPE#PERCEPTBASE} can be made, and multiple
 * {@link BASETYPE#GOALBASE}s are allowed.
 * <p>
 * NOTE: that because the order of insertion may be important ArrayLists should
 * be used to implement this interface instead of Sets.
 *
 * @author K.Hindriks
 * @author W.Pasman 30jan2012 TRAC #1978 extended KR implementation to handle
 *         the specific relations that GOAL expects between the various
 *         databases of an agent (import of KNOWLEDGE into the other bases;
 *         import of mails and percepts in beliefbase).
 *         <p>
 *         removed initialize()
 * @modified K.Hindriks
 */
public interface Database {

	/**
	 * Returns the name of the database.
	 *
	 * TODO: how exactly is this name used? Each database should be provided
	 * with a label for internal reference by the GOAL interpreter.
	 *
	 * @return the name of this database.
	 */
	String getName();

	/**
	 * Returns the type of the database. Types represent different purposes that
	 * a database may be used for, i.e. representing percepts, mail messages,
	 * knowledge, beliefs, goals, and other agent's beliefs and goals.
	 *
	 * @return the {@link BASETYPE} of this database.
	 */
	BASETYPE getType();

	/**
	 * Defines the inference mechanism associated with a KRlanguage. The GOAL
	 * programming language requires that at least this method is specified in
	 * order to be able to effectively operate with the KRlanguage.
	 *
	 * @param query
	 *            The query.
	 *
	 * @return set of substitutions. This set is empty if there are no
	 *         solutions. If there is a one solution without substitutions,
	 *         returns set with one empty substitution
	 */
	Set<Substitution> query(Query query);

	/**
	 * As {@link #query} except that it takes a precompiled query.
	 *
	 * @param query
	 *            is the query to be done.
	 * @param debugger
	 *            is the debugger controlling the call
	 * @return set of all possible substitutions that validate the query, or an
	 *         empty set if the query can not be validated.
	 * @throws KRQueryFailedException
	 */
	// TODO: may have been broken as doc said this method crucially depended on
	// the database
	// that was provided as argument (but now has been removed).
	Set<Substitution> query(CompiledQuery query);

	/**
	 * Performs a query on the database returning all receivers of the given
	 * message according to the message base.
	 *
	 * @param message
	 *            The message.
	 * @return DOC
	 */
	Collection<String> getReceiversOfMessage(Message message);

	/**
	 * Inserts a formula into the database.
	 * <p>
	 * As a result, the database will entail the added formula. There are no
	 * guarantees regarding consistency. I.e., the database may become logically
	 * inconsistent.
	 * </p>
	 *
	 * @param formula
	 *            The sentence to be added.
	 * @param debugger
	 *            The debugger monitoring the call.
	 */
	void insert(DatabaseFormula formula);

	/**
	 * Inserts an update to a database. As a result, the database will entail
	 * the added formula, but also may become logically inconsistent.
	 *
	 * @param update
	 *            represents the update to be made.
	 * @param debugger
	 *            The debugger monitoring the update.
	 */
	void insert(Update update);

	/**
	 * Inserts a percept into the database.
	 *
	 * @param percept
	 *            The EIS percept to be inserted.
	 * @param debugger
	 *            The debugger monitoring the call.
	 * @return The formula that was added to the percept base.
	 */
	DatabaseFormula insert(eis.iilang.Percept percept);

	/**
	 * Inserts a sent or received fact for the given message into the database.
	 *
	 * @param message
	 *            The message that has been sent or received.
	 * @param received
	 *            {@code true} if the message has been received; {@code false}
	 *            if it has been sent.
	 * @param debugger
	 *            The debugger monitoring the call.
	 * @return The set of database formulas that have been inserted into the
	 *         database.
	 */
	Set<DatabaseFormula> insert(Message message, boolean received);

	/**
	 * Removes a formula from a database. After applying the method the sentence
	 * that is removed should no longer follow from the database.
	 *
	 * @param formula
	 *            The formula to be removed.
	 * @param debugger
	 *            The debugger monitoring the update.
	 */
	void delete(DatabaseFormula formula);

	/**
	 * Removes a sentence from a database. After applying the method the
	 * sentence that is removed should no longer follow from the database.
	 *
	 * @param update
	 *            The update which determines what has to be deleted.
	 * @param debugger
	 *            The debugger monitoring the update.
	 */
	void delete(Update update);

	/**
	 * Removes a percept from the database.
	 *
	 * @param percept
	 *            The EIS percept to be deleted.
	 * @param debugger
	 *            The debugger monitoring the call.
	 * @return The formula that was deleted from the percept base.
	 */
	DatabaseFormula delete(eis.iilang.Percept percept);

	/**
	 * Updates the 'agent(name)' fact for agent with id.
	 *
	 * @param insert
	 *            {@code true} if the fact needs to be inserted; {@code false}
	 *            if the fact needs to be removed;
	 * @param id
	 *            Id of the agent whose related agent fact needs to be updated.
	 * @param me
	 *            {@code true} if the related 'me(name)' fact also needs to be
	 *            updated.
	 * @param database
	 *            The database that needs to be updated.
	 * @return The facts that were inserted or removed.
	 * @throws KRInitFailedException
	 */
	Set<DatabaseFormula> updateAgentFact(boolean insert, AgentId id, boolean me)
			throws KRInitFailedException;

	/**
	 * Returns all formulas that are stored in the database.
	 *
	 * @return The method returns a list of all sentences that are elements in
	 *         the set-theoretical sense of the database.
	 * @throws KRInitFailedException
	 */
	DatabaseFormula[] getAllSentences() throws KRInitFailedException;

	/**
	 * Clean up database before deleting object. Call this method whenever
	 * database object is deleted.
	 *
	 * @throws KRInitFailedException
	 */
	void cleanUp() throws KRInitFailedException;

	/**
	 * Show statistics regarding number of queries performed on database.
	 */
	void showStatistics();

	@Override
	String toString();
}
