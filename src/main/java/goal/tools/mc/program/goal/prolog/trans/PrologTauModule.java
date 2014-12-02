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

package goal.tools.mc.program.goal.prolog.trans;

import goal.core.kr.language.DatabaseFormula;
import goal.core.program.Module;
import goal.core.program.rules.Rule;
import goal.tools.mc.program.goal.trans.Derives;
import goal.tools.mc.program.goal.trans.GoalRepository;
import goal.tools.mc.program.goal.trans.TauModule;

import java.util.Collection;

import swiprolog3.language.PrologTerm;
import swiprolog3.language.PrologUpdate;

/**
 * Represents a transition module when the Prolog KRT is used.
 *
 * @author sungshik
 *
 */
public class PrologTauModule extends
		TauModule<PrologTerm, PrologAtom, PrologUpdate> {

	//
	// Constructors
	//

	/**
	 * Constructs a transition module corresponding to the specified module.
	 *
	 * @param module
	 *            - The module to which the transition module to be constructed
	 *            should correspond.
	 */
	public PrologTauModule(Module module, Collection<DatabaseFormula> knowledge) {
		super(module, knowledge);
	}

	//
	// Protected methods
	//

	@Override
	protected PrologTauClass createTransitionClass(Rule rule) {
		return new PrologTauClass(this, rule);
	}

	@Override
	protected GoalRepository<PrologAtom, PrologUpdate> createGoalRepository() {
		return new PrologGoalRepository();
	}

	@Override
	protected Derives<PrologAtom> createDerives(
			Collection<DatabaseFormula> knowledge) {
		return new PrologDerives(knowledge);
	}
}
