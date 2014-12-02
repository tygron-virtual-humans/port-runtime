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

package goal.core.kr.language;

import java.util.List;

/**
 *
 * Represents an expression that can be used to update a database. Updates are
 * also used to represent goals, but goals are updates with empty delete and
 * mailbox update.
 *
 * It is assumed that an update is a conjunction of more basic updates. Basic
 * updates are either positive or negative literals (i.e. a formula), or
 * possibly another type of update such as a conditional effect in ADL. Updates
 * are related to DatabaseFormula as database formulas are assumed to be the
 * most basic updates possible (such as a positive literal). The UpdateEngine
 * should be able to handle an Update and should possess the know-how to update
 * a database with it. An update may have free variables as any other logical
 * expression.
 *
 * See Trac #775. This is now a collection of positive and negative updates.
 * There is no separation anymore between beliefbase and mailbox updates. All
 * semantic checking is now left to the parser as we do not know at this point
 * what the context is (insert, delete, mailbox actions allowed, etc). This
 * separation is made when necessary, in InsertAction and DeleteAction.
 * InsertAction will insert positives and delete negatives. DeleteAction will
 * delete positives and insert negatives. We always first apply the delete and
 * then the insert.
 *
 *
 * @author K.Hindriks
 * @author W.Pasman
 * @modified K.Hindriks
 */

public interface Update extends Expression {

	/**
	 * Returns the add list of this {@link Update} in the form of a list of
	 * {@link DatabaseFormula}s. The formulas returned are positive literals.
	 *
	 * @return The add list of this update, i.e. the positive literals that
	 *         occur in this update.
	 */
	List<DatabaseFormula> getAddList();

	/**
	 * Returns the delete list of this {@link Update} in the form of a list of
	 * {@link DatabaseFormula}s. The formulas returned are positive literals.
	 *
	 * @return The delete list of this update, i.e. the negative literals that
	 *         occur in this update (but the negations are removed in the list
	 *         that is returned).
	 */
	List<DatabaseFormula> getDeleteList();

	/**
	 * Instantiates (or renames) any free variables that are bound by
	 * substitution by applying substitution.
	 *
	 * @param substitution
	 *            The substitution.
	 *
	 * @return The new Update with the substitution applied
	 */
	Update applySubst(Substitution substitution);

	/**
	 * Get a subset update, with either only mail updates or with all other
	 * updates.
	 *
	 * @param selectMails
	 *            {@code true} if you want only mail updates; {@code false} if
	 *            you want all but mail updates. Mail updates are updates that
	 *            have a 'sent/2' or 'received/2' main operator.
	 * @return A {@link Update} with either only mail updates or all other
	 *         updates.
	 */
	Update filterMailUpdates(boolean selectMails);

	/**
	 * Converts update formula to class query.
	 *
	 * A goal in GOAL is inserted in a database, and is also queried on the
	 * belief base. As goals are represented as updates, we need a method to
	 * convert them to a query to be able to use a goal as a query.
	 *
	 * @return query formula that is result from converting update, using the
	 *         add-list associated with the update only. Used for goals. See
	 *         Goalbase.java.
	 */
	Query toQuery();

}
