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
import goal.core.program.actions.ActionCombo;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.goal.GOALCE_Belief;
import goal.tools.mc.program.goal.GOALCE_GoalAtDepth;
import goal.tools.mc.program.goal.GOALConversionElement;
import goal.tools.mc.program.goal.GOALConversionUniverse;
import goal.tools.mc.program.goal.GOALState;
import goal.tools.mc.program.goal.trans.TauClass;
import goal.tools.mc.program.goal.trans.TauStep;
import goal.tools.mc.program.goal.trans.TauUpdate;
import goal.tools.mc.program.goal.trans.TauUpdate.Op;
import swiprolog3.language.PrologDBFormula;
import swiprolog3.language.PrologTerm;
import swiprolog3.language.PrologUpdate;

/**
 * Represents a step when the Prolog KRT is used.
 *
 * @author sungshik
 *
 */
public class PrologTauStep extends
TauStep<PrologTerm, PrologAtom, PrologUpdate> {

	/**
	 * Constructs a step according to the specified parameters.
	 *
	 * @param tauClass
	 *            - See {@link #tauClass}.
	 * @param universe
	 *            - See {@link #universe}.
	 * @param source
	 *            - See {@link #source}.
	 * @param destination
	 *            - See {@link #destination}.
	 * @param act
	 *            - See {@link #act}.
	 */
	public PrologTauStep(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauclass,
			GOALConversionUniverse universe, GOALState source,
			GOALState destination, ActionCombo combo) {

		super(tauclass, universe, source, destination, combo);
	}

	@Override
	public TauUpdate<PrologAtom> getUpdates() {

		try {

			/* Declare data structures */
			TauUpdate<PrologAtom> updates = new PrologTauUpdate();
			GOALConversionElement elem;

			/*
			 * Get the beliefs and goals that are added due to this step's
			 * execution, and iterate
			 */
			GOALState adds = destination.minus(source);
			for (int i = adds.nextSetBit(0); i >= 0; i = adds.nextSetBit(i + 1)) {
				elem = universe.getAtIndex(i);

				/*
				 * If a belief is added, add this belief to the updates as an
				 * addition
				 */
				if (elem instanceof GOALCE_Belief) {

					/* Unpack the actual belief, i.e. Prolog term */
					GOALCE_Belief belief = (GOALCE_Belief) elem;
					PrologDBFormula pdbf = (PrologDBFormula) belief.belief;
					PrologTerm term = new PrologTerm(pdbf.getTerm(), null);

					/* Add the term as a belief addition */
					updates.addBelief(Op.ADD,
							PrologAtom.factory(term.getTerm()));
				}

				/*
				 * If a goal is added, add this goal to the updates as a
				 * deletion
				 */
				else if (elem instanceof GOALCE_GoalAtDepth) {

					/* Unpack the actual goal, i.e. Prolog update */
					GOALCE_GoalAtDepth goal = (GOALCE_GoalAtDepth) elem;
					PrologUpdate update = (PrologUpdate) goal.goal;

					/* Translate the update to a set of atoms */
					LMHashSet<PrologAtom> gamma = new LMHashSet<PrologAtom>();
					for (DatabaseFormula f : update.getAddList()) {
						PrologTerm term = new PrologTerm(
								((PrologDBFormula) f).getTerm(), null);
						gamma.add(PrologAtom.factory(term.getTerm()));
					}

					/* Add the set of atoms as a goal addition */
					updates.addGoal(Op.ADD, gamma);
				}
			}

			/*
			 * Get the beliefs and goals that are deleted due to this step's
			 * execution, and iterate
			 */
			GOALState dels = source.minus(destination);
			for (int i = dels.nextSetBit(0); i >= 0; i = dels.nextSetBit(i + 1)) {
				elem = universe.getAtIndex(i);

				/*
				 * If a belief is deleted, add this belief to the updates as a
				 * deletion
				 */
				if (elem instanceof GOALCE_Belief) {

					/* Unpack the actual belief, i.e. Prolog term */
					GOALCE_Belief belief = (GOALCE_Belief) elem;
					PrologDBFormula pdbf = (PrologDBFormula) belief.belief;
					PrologTerm term = new PrologTerm(pdbf.getTerm(), null);

					/* Add the term as a belief deletion */
					updates.addBelief(Op.DEL,
							PrologAtom.factory(term.getTerm()));
				}

				/*
				 * If a goal is deleted, add this goal to the updates as a
				 * deletion
				 */
				else if (elem instanceof GOALCE_GoalAtDepth) {

					/* Unpack the actual goal, i.e. Prolog update */
					GOALCE_GoalAtDepth goal = (GOALCE_GoalAtDepth) elem;
					PrologUpdate update = (PrologUpdate) goal.goal;

					/* Translate the update to a set of atoms */
					LMHashSet<PrologAtom> gamma = new LMHashSet<PrologAtom>();
					for (DatabaseFormula f : update.getAddList()) {
						PrologTerm term = new PrologTerm(
								((PrologDBFormula) f).getTerm(), null);
						gamma.add(PrologAtom.factory(term.getTerm()));
					}

					/* Add the set of atoms as a goal deletion */
					updates.addGoal(Op.DEL, gamma);
				}
			}

			/* Return */
			return updates;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return act.toString();
	}
}
