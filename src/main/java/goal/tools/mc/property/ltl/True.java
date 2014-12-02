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

package goal.tools.mc.property.ltl;

import goal.tools.mc.core.lmhashset.LMHashSet;

/**
 * Represents LTL true.
 *
 * @author sungshik
 *
 */
public final class True extends Formula {

	@Override
	public boolean equals(Object o) {
		return o instanceof True;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public LMHashSet<Formula> getArgs() {
		return new LMHashSet<Formula>();
	}
}