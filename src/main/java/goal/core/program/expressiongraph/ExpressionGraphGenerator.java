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

import goal.core.kr.KRlanguage;
import goal.core.program.GOALProgram;

/**
 *
 * Basic structure for creating an expression graph.
 *
 * The intended structure for subclasses is as follows:<br>
 * <ul>
 * <li>Create a subclass for each type of object for which a graph needs to be
 * made, with custom code for {@link #createGraph} in each of them.</li>
 * <li>If an object for which a graph needs to be made contains (links to) other
 * objects for which a graph needs to be made, call {@link #createGraph} on a
 * related graph object.</li>
 * </ul>
 *
 * @author K.Hindriks
 *
 * @param <T>
 *            The node's type
 */
@Deprecated
public abstract class ExpressionGraphGenerator<T> {

	/**
	 * The expression graph.
	 */
	private ExpressionGraph graph = null;
	/**
	 * Language that is used in the program. We need to know which language
	 * specific tools to call in order to be able to construct the graph.
	 */
	private KRlanguage language = null;

	/**
	 * Returns the {@link KRlanguage} used to construct the
	 * {@link ExpressionGraph}.
	 *
	 * @return The KR language used to construct the expression graph.
	 */
	public final KRlanguage getKRlanguage() {
		return language;
	}

	/**
	 * Sets the {@link KRlanguage} that is used to construct the expression
	 * graph. The KR language should be the same as the KR language of the agent
	 * program that is used to construct this {@link ExpressionGraph}.
	 *
	 * @param language
	 *            The KR language used to construct the expression graph.
	 */
	public final void setKRlanguage(KRlanguage language) {
		if (this.language == null) {
			this.language = language;
		} else {
			throw new UnsupportedOperationException(
					"KR language has already been set; cannot reset language.");
		}
	}

	/**
	 * Initializes this {@link ExpressionGraphGenerator} for a new
	 * {@link #createGraph} call. Intended use is for in {@link #createGraph}
	 * only.
	 *
	 * @param superior
	 *            The caller (second parameter) of the
	 *            {@link #createGraph(Object, Object)} method. If null, then
	 *            graph is created for first Object parameter of the method
	 *            only.
	 */
	private void initialize(ExpressionGraphGenerator<?> superior)
			throws UnsupportedOperationException {
		if (superior != null) {
			// Copy graph and language from superior.
			graph = superior.getGraph();
			language = superior.getKRlanguage();
		} else {
			if (language != null) {
				if (graph == null) {
					// Create the empty graph to start with.
					this.graph = language.makeExpressionGraph();
				}
			} else {
				throw new UnsupportedOperationException(
						"Cannot proceed because KR language is unknown; "
								+ "creation of expression graph is language-dependent.");
			}
		}
	}

	/**
	 * Creates a graph for a certain object.<br>
	 *
	 * @param subject
	 *            The object to for which a graph should be made.
	 * @param superior
	 *            The superior that is used to get the graph constructed so far
	 *            and the KR language.
	 */
	public final void createGraph(T subject,
			ExpressionGraphGenerator<?> superior) {
		this.initialize(superior);
		this.doCreateGraph(subject);
		// Link nodes in the graph.
		this.graph.connectNodes();
		// If available, provide caller (superior) with results.
		if (superior != null) {
			superior.graph = this.graph;
		}
	}

	/**
	 * Does the actual work of making a graph based on the object. Do not call
	 * this method but call {@link #createGraph} instead. {@link #createGraph}
	 * also handles initialization and the issue-free-check afterwards.
	 *
	 * @param subject
	 *            The object for which a graph should be made.
	 */
	protected abstract void doCreateGraph(T subject);

	/**
	 * Returns the graph that has been constructed so far.
	 *
	 * @return the expression graph for the {@link GOALProgram}.
	 */
	public final ExpressionGraph getGraph() {
		return graph;
	}

}
