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

package goal.core.program.expressiongraph;

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Expression;
import goal.core.kr.language.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the definitions and queries of terms / expressions used in
 * the KRLanguage-part of a GOAL Agent.<br>
 * To use this class, first fill it with definitions ({@link #addDefinition})
 * and queries ({@link #addQuery(DatabaseFormula)} and {@link #addQuery(Query)}
 * ), then call {@link #connectNodes()} to connect the queries with their
 * definitions. Then use any of the four getters to access the stored
 * expressions.
 *
 * @author N.Kraayenbrink
 */
@Deprecated
public abstract class ExpressionGraph {

	/**
	 * Adds a definition to this graph. The given formula may contain
	 * sub-expressions that are queries to other expressions; {@link #addQuery}
	 * will be called for those by this method.
	 *
	 * @param formula
	 *            The formula to add to the graph as definition.
	 */
	public abstract void addDefinition(DatabaseFormula formula);

	/**
	 * Adds a query to this graph. Queries will never contain any expression
	 * definition.
	 *
	 * @param query
	 *            The query to add.
	 */
	public abstract void addQuery(Query query);

	/**
	 * Adds a database formula to this graph as a query. It is assumed that this
	 * formula comes from the delete list of an action-specification
	 * post-condition, and thus only contains a single term.
	 *
	 * @param formula
	 *            The formula to add as query.
	 */
	public abstract void addQuery(DatabaseFormula formula);

	/**
	 * Parses the nodes in the graph, connecting usage nodes with their
	 * corresponding definition nodes.
	 */
	public abstract void connectNodes();

	/**
	 * @return An iteration over all definitions that are unused.
	 */
	public abstract Iterable<? extends Expression> getUnusedDefinitions();

	/**
	 * @return An iteration over all queries referencing undefined expressions.
	 */
	public abstract Iterable<? extends Expression> getUndefinedQueries();

	/**
	 * @return An iteration over all defined expressions.
	 */
	public abstract Iterable<? extends Expression> getAllDefinitions();

	/**
	 * @return An iteration over all queried expressions.
	 */
	public abstract Iterable<? extends Expression> getAllQueries();

	/**
	 * Implementation of {@link ExpressionGraph} that does nothing. Any call to
	 * an add-method will be ignored, and all get-methods return an empty list.
	 *
	 * @author N.Kraayenbrink
	 */
	public static class EmptyGraph extends ExpressionGraph {

		private final List<Expression> emptyList;

		public EmptyGraph() {
			this.emptyList = new ArrayList<>(0);
		}

		@Override
		public void addDefinition(DatabaseFormula formula) {
		}

		@Override
		public void addQuery(Query query) {
		}

		@Override
		public void addQuery(DatabaseFormula formula) {
		}

		@Override
		public void connectNodes() {
		}

		@Override
		public Iterable<? extends Expression> getAllDefinitions() {
			return this.emptyList;
		}

		@Override
		public Iterable<? extends Expression> getAllQueries() {
			return this.emptyList;
		}

		@Override
		public Iterable<? extends Expression> getUndefinedQueries() {
			return this.emptyList;
		}

		@Override
		public Iterable<? extends Expression> getUnusedDefinitions() {
			return this.emptyList;
		}

	}

}
