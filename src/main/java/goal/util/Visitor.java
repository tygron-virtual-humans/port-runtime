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

package goal.util;

import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.tools.debugger.Debugger;

/**
 * Base class of the Visitor pattern. Implementations of the Visitor class are
 * used to traverse the data structure of the GOAL agent and perform various
 * operations. Travelsal with this visitor class is the responsbility of the
 * object structure.
 *
 * Often, a KRT is required to subclass a provided Visitor to add the extra
 * functionality for the KRT parts of the GOAL agent.
 *
 * @author Tijmen Roberti
 */
public abstract class Visitor {
	public void visit(Agent<GOALInterpreter<Debugger>> agent) {
	}

	public void visit(goal.core.mentalstate.MentalState ms) {
	}

	public void visit(goal.core.mentalstate.BeliefBase bb) {
	}

	public void visit(goal.core.mentalstate.GoalBase gb) {
	}

	public void visit(goal.core.program.ActionSpecification actions) {
	}

	public void visit(goal.core.program.rules.IfThenRule actionrule) {
	}

	public void visit(goal.core.program.literals.MentalStateCond msc) {
	}

	public void visit(goal.core.program.literals.MentalLiteral mentalLiteral) {
	}

	// VERY UGLY catch all. This is to 'solve' the problem where in
	// KRT subclasses, the accept method needs to call a visit method
	// with the KRT specific type as argument. But because that type
	// is not known here at this moment, it raises a compile error.
	public <T> void visit(T t) {
	}
}
