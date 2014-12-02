package goal.core.program.expressiongraph;

import goal.core.kr.language.Expression;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A node in the {@link ExpressionGraph} representing the definition of an
 * {@link Expression}.<br>
 * If used as an Iterable, it's {@link #iterator()} will iterate over the
 * expressions that form the definition.
 *
 * @param <T>
 *            The node's type
 */
@Deprecated
public class DefinitionNode<T extends Expression> implements Iterable<T> {
	/**
	 * The query nodes that refer to this definition.
	 */
	private final List<QueryNode<T>> queries;
	/**
	 * The expressions that together form the complete definition.
	 */
	private final List<T> values;
	/**
	 * Indicates whether this is a basic definition, i.e. not defined in terms
	 * of other queries. {@code true} by default.
	 */
	boolean isBasic = true;

	/**
	 * Creates a new, empty definition node.
	 */
	public DefinitionNode() {
		this.values = new LinkedList<>();
		this.queries = new LinkedList<>();
	}

	/**
	 * Links a query to this definition. Also sets the definition in the given
	 * query node to this definition node.
	 *
	 * @param query
	 *            The query to add.
	 */
	public void addQuery(QueryNode<T> query) {
		this.queries.add(query);
		query.definition = this;
	}

	/**
	 * Adds a new occurrence of the definition.
	 *
	 * @param value
	 *            The occurrence.
	 */
	public void addValue(T value) {
		this.values.add(value);
	}

	/**
	 * @return <code>true</code> if there are no queries linked to this
	 *         definition.
	 */
	public boolean isUnused() {
		return this.queries.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return this.values.iterator();
	}

	/**
	 * @return An iteration over the queries for this definition.
	 */
	public Iterable<QueryNode<T>> getQueries() {
		return this.queries;
	}

	/**
	 * Returns {@code true} if the definition stored in this node is basic, i.e.
	 * all queries have the same signature as the definition.
	 *
	 * @return {@code true} if the signature of all queries referring to the
	 *         definition have the same signature.
	 */
	public boolean isBasicDefinition() {
		return isBasic;
	}

	/**
	 * Sets the {@link #isBasic} variable.
	 *
	 * @param isBasic
	 *            The boolean value to which {@link #isBasic} should be set.
	 */
	public void setBasic(boolean isBasic) {
		this.isBasic = isBasic;
	}

	/**
	 * Returns a string representation of this {@link DefinitionNode}, with on
	 * the first line a list of instances of this definition and on the second
	 * line the list of associated queries.
	 *
	 * @return A string representation of this definition node.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(this.values.toString());
		builder.append("\n");
		builder.append(this.queries.toString());

		return builder.toString();
	}
}