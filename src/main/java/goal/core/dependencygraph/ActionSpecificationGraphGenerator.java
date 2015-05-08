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

package goal.core.dependencygraph;

import krTools.errors.exceptions.KRException;
import krTools.language.DatabaseFormula;
import languageTools.program.agent.ActionSpecification;
import languageTools.program.agent.msc.MentalLiteral;
import mentalState.DependencyGraph;

/**
 * {@link DependencyGraphGenerator} for {@link ActionSpecification}s. Adds the
 * declarative content of the {@link ActionSpecification} to an
 * {@link DependencyGraph}.
 *
 * @author K.Hindriks
 *
 */
public class ActionSpecificationGraphGenerator extends
DependencyGraphGenerator<ActionSpecification> {

	/**
	 * Fills the given expression graph with expressions found in this
	 * {@link ActionSpecification}.
	 */
	@Override
	protected void doCreateGraph(ActionSpecification subject) {
		// The precondition is queried.
		try {
			for (MentalLiteral formula : subject.getPreCondition()
					.getAllLiterals()) {
				super.getGraph().add(formula.getFormula());
			}
		} catch (KRException error) {
			report(error);
		}
		// The add list of the postcondition consists of definitions.
		for (DatabaseFormula formula : subject.getPostCondition().getAddList()) {
			try {
				super.getGraph().add(formula, true, false);
			} catch (KRException error) {
				report(error);
			}
		}
		// The delete list consists of queries.
		for (DatabaseFormula formula : subject.getPostCondition()
				.getDeleteList()) {
			try {
				super.getGraph().add(formula, false, true);
			} catch (KRException error) {
				report(error);
			}
		}
	}
}
