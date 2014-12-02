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

package goal.tools.mc.property;

import goal.tools.mc.core.Automaton;
import goal.tools.mc.property.ltl.Formula;

/**
 * Represents the property component of a model checker, which basically is a
 * representation of an automaton (which is why this interface extends
 * {@link Automaton}).
 * 
 * @author sungshik
 *
 */
public interface Property extends Automaton {

	/**
	 * Gets the property under investigation.
	 * 
	 * @return The property.
	 */
	Formula getProperty();
}
