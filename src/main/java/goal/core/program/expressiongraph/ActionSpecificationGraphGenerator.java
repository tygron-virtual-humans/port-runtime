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
import goal.core.program.ActionSpecification;

/**
 * {@link ExpressionGraphGenerator} for {@link ActionSpecification}s. Adds the
 * declarative content of the {@link ActionSpecification} to an
 * {@link ExpressionGraph}.
 *
 * @author K.Hindriks
 */
@Deprecated
public class ActionSpecificationGraphGenerator extends
ExpressionGraphGenerator<ActionSpecification> {

	/**
	 * Fills the given expression graph with expressions found in this
	 * {@link ActionSpecification}.
	 *
	 * @param graph
	 *            The {@link ExpressionGraph} to fill.
	 */
	@Override
	protected void doCreateGraph(ActionSpecification subject) {
		// the precondition is an obvious query
		super.getGraph().addQuery(subject.getPreCondition());
		// the add list of the postcondition is a definition
		// the delete list is neither
		for (DatabaseFormula addListElem : subject.getPostCondition()
				.getAddList()) {
			super.getGraph().addDefinition(addListElem);
		}
	}

}
