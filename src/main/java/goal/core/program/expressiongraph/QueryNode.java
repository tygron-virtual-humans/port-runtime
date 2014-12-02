package goal.core.program.expressiongraph;

import goal.core.kr.language.Expression;

/**
 * A node in the {@link ExpressionGraph} representing the query of an
 * expression.
 *
 * @param <T>
 *            The node's type
 */
@Deprecated
public class QueryNode<T extends Expression> {

	/**
	 * The definition this query refers to.
	 */
	DefinitionNode<T> definition;
	/**
	 * The actual query expression.
	 */
	private final T value;

	/**
	 * Creates a new query node with the given query expression. Will not
	 * initially have any referenced definition.
	 *
	 * @param value
	 *            The expression
	 */
	public QueryNode(T value) {
		this.value = value;
		this.definition = null;
	}

	/** @return The expression that this node represents. */
	public T getValue() {
		return this.value;
	}

	/**
	 * @return {@code true} iff this query has no associated definition.
	 */
	public boolean isUndefined() {
		return this.definition == null;
	}

	/**
	 * @return The {@link DefinitionNode} representing this query's definition.
	 */
	public DefinitionNode<T> getDefinition() {
		return this.definition;
	}

	/**
	 * @return A string representation of this query node.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(definition.toString());
		builder.append("\n");
		builder.append(value.toString());

		return builder.toString();
	}
}