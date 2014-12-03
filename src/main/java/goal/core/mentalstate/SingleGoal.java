package goal.core.mentalstate;

import krTools.language.Update;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;
import krTools.database.Database;
import krTools.errors.exceptions.KRInitFailedException;
import languageTools.program.agent.AgentId;

/*********** SingleGoal class ****************/

/**
 * Stores a {@link Database}, {@link Update} pair that represent a single goal.
 * The database is maintained by the KR technology while the update is used for
 * purposes internal to GOAL.
 *
 * <p>
 * If you make duplicate use of a {@link SingleGoal}. you should call
 * {@link #markOccurrence()}. If you don't, SingleGoals may be cleaned up
 * prematurely.
 *
 * <p>
 * {@link SingleGoal} should not be used outside this package. This is mainly a
 * support class for {@link GoalBase}.
 * <p>
 * #2884 this was an inner class of {@link GoalBase}. However,
 * {@link SingleGoal} is being reused by other {@link GoalBase}s and therefore
 * this was not good. Also the timing info (usage time counting) went wrong that
 * way as the creator (instead of the reuser) was accounted for used time in
 * SingleGoal.
 *
 * @author K.Hindriks
 *
 */
public class SingleGoal {
	/**
	 * The single goal represented as an {@link Update}.
	 */
	private Update goal;
	/**
	 * The KR database that stores the goal.
	 */
	private Database database = null;
	/**
	 *
	 */
	private int useCount = 0;

	/**
	 * DOC
	 *
	 * @param goal
	 *            goal to be added. ASSUMES goal consists of a list of database
	 *            formulas to be added only (i.e. delete and mailbox lists of
	 *            update are empty).
	 * @param agent
	 *            The agent this goal is for
	 * @param language
	 *            The KRlanguage to use
	 * @throws GOALRuntimeErrorException
	 *             see {@link KRlanguage#makeDatabase}.
	 */
	protected SingleGoal(Update goal, AgentId agent, KRlanguage language) {
		this.goal = goal;

		try {
			this.database = language.makeDatabase(BASETYPE.GOALBASE,
					this.goal.getAddList(), agent.getName());
		} catch (KRInitFailedException e) {
			throw new GOALRuntimeErrorException("Could not add new goal "
					+ goal, e);
		}
	}

	/**
	 * Returns the goal represented by this {@link SingleGoal}. FIXME Should be
	 * protected, but modelchecker uses this.
	 *
	 * @return The goal represented by this single goal container.
	 */
	public Update getGoal() {
		return this.goal;
	}

	/**
	 * Returns the KR database that the KR technology uses for representing the
	 * goal.
	 *
	 * @return The KR database that the KR technology uses for representing the
	 *         goal.
	 */
	protected Database getGoalDatabase() {
		return this.database;
	}

	/**
	 * @return string representation of goal.
	 */
	@Override
	public String toString() {
		return "" + this.goal;
	}

	/**
	 * Adds one to the counter counting the number of times this SingleGoal is
	 * used. Must be undone with {@link #unmarkOccurrence()}.
	 */
	protected void markOccurrence() {
		this.useCount++;
	}

	/**
	 * Removes one from the counter counting the number of times this SingleGoal
	 * is used. If the counter reaches zero, the database is cleaned up.
	 */
	protected void unmarkOccurrence() {
		this.useCount--;
		if (useCount == 0) {
			cleanUp();
		}
	}

	/**
	 * Forces clean up of the KR database. Should be used only as a forced final
	 * attempt to clean up, never during normal run. See
	 * {@link #unmarkOccurrence()}.
	 */
	protected void cleanUp() {
		try {
			this.database.cleanUp();
		} catch (KRInitFailedException e) {
			throw new GOALRuntimeErrorException("Could not remove goal "
					+ this.goal + " from database", e);
		}

		this.goal = null;
	}

	/**
	 * {@link SingleGoal}s are considered equal if the goals, i.e. the
	 * {@link Update}s they consist of, are equal.
	 *
	 * @return <code>true</code> if obj is equal to this {@link SingleGoal}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SingleGoal)) {
			return false;
		}
		return this.goal.equals(((SingleGoal) obj).getGoal());
	}

	@Override
	public int hashCode() {
		return this.goal.hashCode();
	}
}