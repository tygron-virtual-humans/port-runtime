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

package goal.core.program.dependencygraph;

import goal.core.kr.language.DatabaseFormula;
import goal.core.program.ActionSpecification;
import goal.core.program.expressiongraph.ExpressionGraph;
import goal.core.program.expressiongraph.ExpressionGraphGenerator;
import goal.tools.errorhandling.exceptions.GOALUserError;

/**
 * {@link ExpressionGraphGenerator} for {@link ActionSpecification}s. Adds the
 * declarative content of the {@link ActionSpecification} to an
 * {@link ExpressionGraph}.
 *
 * @author K.Hindriks
 *
 */
public class ActionSpecificationGraphGenerator extends
		DependencyGraphGenerator<ActionSpecification> {

	/**
	 * Fills the given expression graph with expressions found in this
	 * {@link ActionSpecification}.
	 *
	 * @param graph
	 *            The {@link ExpressionGraph} to fill.
	 */
	@Override
	protected void doCreateGraph(ActionSpecification subject) {
		// The precondition is queried.
		try {
			super.getGraph().add(subject.getPreCondition());
		} catch (GOALUserError error) {
			this.report(error);
		}
		// The add list of the postcondition consists of definitions.
		for (DatabaseFormula formula : subject.getPostCondition().getAddList()) {
			try {
				super.getGraph().add(formula, true, false);
			} catch (GOALUserError error) {
				this.report(error);
			}
		}
		// The delete list consists of queries.
		for (DatabaseFormula formula : subject.getPostCondition()
				.getDeleteList()) {
			try {
				super.getGraph().add(formula, false, true);
			} catch (GOALUserError error) {
				this.report(error);
			}
		}
	}

}
