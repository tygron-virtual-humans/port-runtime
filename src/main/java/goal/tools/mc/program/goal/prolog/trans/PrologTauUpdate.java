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

import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.goal.trans.TauUpdate;

/**
 * Represents updates that are brought about by execution of a step, transition,
 * or transition class when the Prolog KRT is used.
 *
 * @author sungshik
 *
 */
public class PrologTauUpdate extends TauUpdate<PrologAtom> {

	@Override
	public boolean areConflicting(TauUpdate<PrologAtom> update) {

		try {

			/* Compare belief base updates */
			// FIXME: LMHashSet<jpl.Atom> sigmaPlusX = getSigmaPlus();
			// FIXME: LMHashSet<jpl.Atom> sigmaMinX = getSigmaMin();
			// FIXME: LMHashSet<jpl.Atom> sigmaPlusY = update.getSigmaPlus();
			// FIXME: LMHashSet<jpl.Atom> sigmaMinY = update.getSigmaMin();
			LMHashSet<jpl.Atom> sigmaPlusX = null;
			LMHashSet<jpl.Atom> sigmaMinX = null;
			LMHashSet<jpl.Atom> sigmaPlusY = null;
			LMHashSet<jpl.Atom> sigmaMinY = null;
			if (!PrologEquals.areDisjoint(sigmaPlusX, sigmaMinY).isEmpty()) {
				return false;
			}
			if (!PrologEquals.areDisjoint(sigmaMinX, sigmaPlusY).isEmpty()) {
				return false;
			}

			/* Compare goal base updates */
			// FIXME: for (LMHashSet<jpl.Atom> gammaPlusX : getGammaPlus()) {
			// FIXME: for (LMHashSet<jpl.Atom> gammaMinY : update.getGammaMin())
			// {
			// FIXME: if (!PrologEquals.areDisjoint(gammaPlusX, gammaMinY)
			// FIXME: .isEmpty()) {
			// FIXME: return false;
			// FIXME: }
			// FIXME: }
			// FIXME: }
			// FIXME: for (LMHashSet<PrologAtom> gammaMinX : getGammaMin()) {
			// FIXME: for (LMHashSet<PrologAtom> gammaPlusY :
			// update.getGammaPlus()) {
			// FIXME: if (!PrologEquals.areDisjoint(gammaMinX, gammaPlusY)
			// FIXME: .isEmpty()) {
			// FIXME: return false;
			// FIXME: }
			// FIXME: }
			// FIXME: }

			/* Return true if this point is ever reached */
			return true;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
