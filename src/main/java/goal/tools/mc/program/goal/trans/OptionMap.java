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

package goal.tools.mc.program.goal.trans;

import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.program.actions.ActionCombo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a mapping from transition classes (i.e. action rule) to the action
 * options that they generate in a specific mental state. Checking whether these
 * actually are options is not done in this class; it is assumed that they
 * simply are.
 *
 * @author sungshik
 *
 * @param <T>
 *            - The type of term associated with the KRT that is used with the
 *            transition classes and actions that constitute this map.
 * @param <A>
 *            - The type of atom associated with the KRT that is used with the
 *            transition classes and actions that constitute this map.
 * @param <U>
 *            - The type of update associated with the KRT that is used with the
 *            transition classes and actions that constitute this map.
 */
public class OptionMap<T extends Term, A extends Atom, U extends Update> {

	//
	// Private fields
	//

	/**
	 * The mapping from transition classes to action options. This is a linked
	 * hash map rather than an unordered hash map to ensure that the options are
	 * iterated in the same order always such that the same ample set is always
	 * chosen.
	 */
	private final Map<TauClass<T, A, U>, ArrayList<ActionCombo>> optionMap = new LinkedHashMap<TauClass<T, A, U>, ArrayList<ActionCombo>>();

	//
	// Public methods
	//

	/**
	 * Adds a transition class to this map.
	 *
	 * @param tauClass
	 *            - The transition class to be added.
	 */
	public void addClass(TauClass<T, A, U> tauClass) {
		optionMap.put(tauClass, new ArrayList<ActionCombo>());
	}

	/**
	 * Adds a list of options to this map given the specified transition class.
	 *
	 * @param tauClass
	 *            - The transition class that generates the options.
	 * @param options
	 *            - The options to be associated with the transition class.
	 */
	public void addOption(TauClass<T, A, U> tauClass, List<ActionCombo> options) {

		if (!optionMap.containsKey(tauClass)) {
			addClass(tauClass);
		}
		optionMap.get(tauClass).addAll(options);
	}

	public Map<TauClass<T, A, U>, ArrayList<ActionCombo>> getOptions() {
		return optionMap;
	}
}
