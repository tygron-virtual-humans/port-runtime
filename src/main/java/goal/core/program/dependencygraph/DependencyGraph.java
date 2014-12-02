package goal.core.program.dependencygraph;

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Expression;
import goal.core.kr.language.Query;
import goal.tools.errorhandling.exceptions.GOALUserError;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * A {@link DependencyGraph} consists of a set of {@link Node}s, where each node
 * represents an expression from the KR language used. Links between the nodes
 * represent dependencies. That is, whenever a link from one expression node to
 * another expression node exists, the evaluation of the first expression
 * depends on the evaluation of the second expression.
 * </p>
 * <p>
 * For example, if the KR language is Prolog, then a clause p(X) :- q(X) in an
 * agent program introduces a dependency between p(X) and q(X), where the
 * evaluation of p(X) depends on the evaluation of q(X).
 * </p>
 * <p>
 * A dependency graph is used to verify that predicates are both used as well as
 * defined in an agent program, and for computing the view associated with a
 * {@link Module}.
 * </p>
 * <p>
 * The expressions stored in the nodes are supposed to have a unique
 * <i>signature</i>, i.e. a pair name/arity, that is used to store nodes in a
 * hash map.
 * </p>
 *
 * @author K.Hindriks
 *
 * @param <T>
 *            The node's type
 */
public abstract class DependencyGraph<T extends Expression> {

	/**
	 * A hash map is used to store nodes in the dependency graph. The strings to
	 * index the nodes are the signatures associated with the expression stored
	 * in a node.
	 */
	protected Map<String, Node<T>> graph = new HashMap<>();

	/**
	 * Adds a definition to this {@link DependencyGraph}. The given formula may
	 * contain sub-expressions that define the main operator and that need to be
	 * stored in separate nodes as the defined operator may depend on these
	 * sub-expressions.
	 *
	 * @param formula
	 *            The formula that is added to the graph.
	 * @param defined
	 *            Indicates whether the formula represents a definition.
	 * @param queried
	 *            Indicates whether the formula represents a query.
	 * @throws GOALUserError
	 */
	public abstract void add(DatabaseFormula formula, boolean defined,
			boolean queried) throws GOALUserError;

	/**
	 * Adds a {@link Query} to this {@link DependencyGraph}. If a query is a
	 * composed formula, then the formula is broken down in its constituents and
	 * represented by several {@link Node}s.
	 *
	 * @param query
	 *            The query that is added.
	 * @throws GOALUserError
	 */
	public abstract void add(Query query) throws GOALUserError;

	/**
	 * Returns the definitions in the program that are never used.
	 *
	 * @return A list of all definitions that are unused.
	 */
	public List<? extends Expression> getUnusedDefinitions() {
		List<T> unusedDefinitions = new LinkedList<>();
		for (Node<T> node : graph.values()) {
			if (node.isDefined() && !node.isQueried()) {
				// Expression is introduced but never used.
				unusedDefinitions.addAll(node.getDefinitions());
			}
		}

		return unusedDefinitions;
	}

	/**
	 * Returns those queries that have never been properly introduced.
	 *
	 * @return A list queries that reference undefined expressions.
	 */
	public List<? extends Expression> getUndefinedQueries() {
		List<T> undefinedQueries = new LinkedList<>();
		for (Node<T> node : graph.values()) {
			if (!node.isDefined() && node.isQueried()) {
				// Expression is queried but never introduced.
				undefinedQueries.addAll(node.getQueries());
			}
		}

		return undefinedQueries;
	}

	/**
	 * Returns all queries in the graph.
	 *
	 * @return A list of all queries.
	 */
	public List<? extends Expression> getQueries() {
		List<T> queries = new LinkedList<>();
		for (Node<T> node : graph.values()) {
			if (node.isQueried()) {
				// Expression is queried.
				queries.addAll(node.getQueries());
			}
		}

		return queries;
	}

	/**
	 * String representation of this graph.
	 */
	@Override
	public String toString() {
		Node<T> node;
		int counter;

		StringBuffer buffer = new StringBuffer("Dependency Graph:\n");
		buffer.append("----Definitions----\n");
		// Initialize counter.
		counter = 0;
		for (String sig : graph.keySet()) {
			node = graph.get(sig);
			if (node.isDefined()) {
				counter++;
				buffer.append(sig);
				buffer.append(" @ ");
				for (Expression def : node.getDefinitions()) {
					buffer.append(def.getSource().toShortString());
					buffer.append(" ; ");
				}
				if (!node.isBasic()) {
					buffer.append("\n    = depends on =>  ");
					Iterator<Node<T>> dependencyIterator = node
							.getDependencies().iterator();
					buffer.append(dependencyIterator.next().getSignature());
					while (dependencyIterator.hasNext()) {
						buffer.append(" , "
								+ dependencyIterator.next().getSignature());
					}
				}
				buffer.append("\n");
			}
		}
		buffer.append(counter + " definitions.");
		buffer.append("\n----Queries----\n");
		// Initialize counter.
		counter = 0;
		for (String sig2 : graph.keySet()) {
			node = graph.get(sig2);
			if (node.isQueried()) {
				counter++;
				for (Expression query : node.getQueries()) {
					buffer.append(query);
					buffer.append(" @ ");
					buffer.append(query.getSource().toShortString());
					buffer.append("   ==>  ");
					if (node.isDefined()) {
						buffer.append(node.getSignature());
					}
					buffer.append("\n");
				}
			}
		}
		buffer.append(counter + " queries.");
		buffer.append("\n----Basic Definitions----\n");
		// Initialize counter.
		counter = 0;
		for (Node<T> node2 : graph.values()) {
			if (node2.isBasic()) {
				counter++;
				buffer.append(node2.getSignature());
				buffer.append("\n");
			}
		}
		buffer.append(counter + " basic definitions.\n");
		return buffer.toString();
	}

}
