package goal.core.program.dependencygraph;

import goal.core.kr.KRlanguage;
import goal.core.kr.language.Expression;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A {@link Node} is used to store expressions from the {@link KRlanguage} that
 * the agent uses in a {@link DependencyGraph}.
 *
 * @author K.Hindriks
 *
 * @param <T>
 *            The node type
 */
public class Node<T extends Expression> {

	/**
	 * Signature of all the expressions stored in this node.
	 */
	private final String signature;
	/**
	 * The KR language definitions stored in this {@link Node}. All these
	 * expressions should have the same signature.
	 */
	private final List<T> definitions = new LinkedList<>();
	/**
	 * The KR language queries stored in this {@link Node}. All these
	 * expressions should have the same signature.
	 */
	private final List<T> queries = new LinkedList<>();

	/**
	 * Used to check for cycles when computing all basic dependencies, see
	 * {@link #getBasicDependencies()}.
	 */
	private boolean visited = false;
	/**
	 * The list of expression (nodes) that this expression('s evaluation)
	 * depends on.
	 */
	private final List<Node<T>> dependencies = new LinkedList<>();

	/**
	 * @param signature
	 *            The node signature.
	 */
	public Node(String signature) {
		this.signature = signature;
	}

	/**
	 * Adds an expression to the list of {@link #definitions} stored in this
	 * node. All occurrences of expressions with the same signature should be
	 * stored in one and the same node.
	 *
	 * @param expression
	 *            The expression that is added to the list of definitions.
	 */
	public void addDefinition(T expression) {
		this.definitions.add(expression);
	}

	/**
	 * Adds an expression to the list of {@link #queries} stored in this node.
	 * All occurrences of expressions with the same signature should be stored
	 * in one and the same node.
	 *
	 * @param expression
	 *            The expression that is added to the list of queries.
	 */
	public void addQuery(T expression) {
		this.queries.add(expression);
	}

	public String getSignature() {
		return signature;
	}

	public List<T> getDefinitions() {
		return definitions;
	}

	public List<T> getQueries() {
		return queries;
	}

	/**
	 * Adds a node to this {@link Node}'s dependency list. To avoid cyclic
	 * dependencies, this node itself cannot be added to it's own dependency
	 * list.
	 *
	 * @param node
	 *            The node that is added to this node's dependency list.
	 */
	public void addDependency(Node<T> node) {
		// Do not add the same node twice.
		for (Node<T> dependency : dependencies) {
			if (dependency.signature.equals(node.getSignature())) {
				return;
			}
		}
		dependencies.add(node);
	}

	public List<Node<T>> getDependencies() {
		return dependencies;
	}

	public boolean isDefined() {
		return !definitions.isEmpty();
	}

	public boolean isQueried() {
		return !queries.isEmpty();
	}

	public boolean isBasic() {
		return dependencies.isEmpty();
	}

	/**
	 * Returns the signatures for the basic dependencies of this node's
	 * expression. That is, those expressions on which the evaluation of the
	 * expression stored in this node depends that not depend themselves on
	 * other expressions, or the expression stored itself.
	 *
	 * @return The signatures.
	 */
	public Set<String> getBasicDependencies() {
		Set<String> signatures = new HashSet<>();
		// Check whether we have been here already; if so, immediately return,
		// in
		// order to avoid cycles.
		if (this.visited) {
			return signatures;
		} else {
			this.visited = true;
		}
		// Add the signature of this node if it is basic, i.e. the expression's
		// evaluation does not depend on other expressions.
		if (this.isBasic()) {
			signatures.add(this.getSignature());
		} else {
			for (Node<T> node : dependencies) {
				signatures.addAll(node.getBasicDependencies());
			}
		}
		this.visited = false;
		return signatures;
	}

	/**
	 * Returns a string representation of this {@link Node}, with on the first
	 * line the expressions stored in this node and on the second line the list
	 * of associated queries.
	 *
	 * @return A string representation of this node.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Definitions:");
		builder.append(definitions.toString());
		builder.append("\n Queries:");
		builder.append(queries.toString());
		builder.append("\n Dependencies:");
		builder.append(dependencies.toString());

		return builder.toString();
	}

}
