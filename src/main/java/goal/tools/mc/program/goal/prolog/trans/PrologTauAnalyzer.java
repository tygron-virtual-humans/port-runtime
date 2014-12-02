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
import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.actions.ActionCombo;
import goal.core.program.literals.MentalStateCond;
import goal.tools.mc.program.goal.GOALConversionUniverse;
import goal.tools.mc.program.goal.GOALState;
import goal.tools.mc.program.goal.trans.Derives;
import goal.tools.mc.program.goal.trans.MscSet;
import goal.tools.mc.program.goal.trans.TauAnalyzer;
import goal.tools.mc.program.goal.trans.TauClass;
import goal.tools.mc.program.goal.trans.TauModule;
import goal.tools.mc.program.goal.trans.TauStep;
import goal.tools.mc.program.goal.trans.TauUpdate;

import java.util.Collection;

import swiprolog3.language.PrologTerm;
import swiprolog3.language.PrologUpdate;

/**
 * Represents a transition analysis tool set when the Prolog KRT is used.
 *
 * @author sungshik
 *
 */
public class PrologTauAnalyzer extends
		TauAnalyzer<PrologTerm, PrologAtom, PrologUpdate> {

	/**
	 * Constructs a transition analyzer that analyzes the specified program,
	 * with respect to the specified vocabulary.
	 *
	 * @param program
	 *            - The program under analysis.
	 * @param voc
	 *            - The vocabulary to be used during the analysis.
	 */
	public PrologTauAnalyzer(GOALProgram program, MscSet voc) {
		super(program, voc);
	}

	//
	// Protected methods
	//

	@Override
	protected boolean areIndependent(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass1,
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass2) {

		/* Check enabledness condition in both directions */
		if (!getWriteSetNeg(tauClass2, tauClass1.getReadset()).isEmpty()) {
			return false;
		}
		if (!getWriteSetNeg(tauClass1, tauClass2.getReadset()).isEmpty()) {
			return false;
		}

		/* Check commutativity condition */
		TauUpdate<PrologAtom> updatetuple1 = tauClass1.getUpdates();
		TauUpdate<PrologAtom> updatetuple2 = tauClass2.getUpdates();
		return updatetuple1.areConflicting(updatetuple2);
	}

	@Override
	protected boolean canEnabledBy(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass1,
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass2) {

		return !getWriteSetPos(tauClass1, tauClass2.getReadset()).isEmpty();
	}

	@Override
	protected TauStep<PrologTerm, PrologAtom, PrologUpdate> createTauStep(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass,
			GOALConversionUniverse universe, GOALState source,
			GOALState destination, ActionCombo combo) {
		return new PrologTauStep(tauClass, universe, source, destination, combo);
	}

	@Override
	protected TauModule<PrologTerm, PrologAtom, PrologUpdate> createTauModule(
			Module module, Collection<DatabaseFormula> knowledge) {
		return new PrologTauModule(module, knowledge);
	}

	@Override
	protected boolean isVisible(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass, MscSet voc) {
		return !getWriteSet(tauClass, voc).isEmpty();
	}

	//
	// Private methods
	//

	/**
	 * Gets the total write set of the specified transition class with respect
	 * to the specified set of mental state conditions.
	 *
	 * @param tauClass
	 *            - The transition class to compute the write set for.
	 * @param capitalPsi
	 *            - The set of mental state conditions.
	 */
	public static MscSet getWriteSet(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass,
			MscSet capitalPsi) {

		try {
			MscSet writeSet = new MscSet();
			writeSet.addAll(getWriteSetPos(tauClass, capitalPsi));
			writeSet.addAll(getWriteSetNeg(tauClass, capitalPsi));
			return writeSet;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the negative write set of the specified transition class with
	 * respect to the specified set of mental state conditions.
	 *
	 * @param tauClass
	 *            - The transition class to compute the write set for.
	 * @param capitalPsi
	 *            - The set of mental state conditions.
	 */
	public static MscSet getWriteSetNeg(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass,
			MscSet capitalPsi) {

		try {

			/* Get the updates that the transition class brings about */
			TauUpdate<PrologAtom> updates = tauClass.getUpdates();

			/* Get the derives function to be used */
			Derives<PrologAtom> derives = tauClass.getModule().getDerives();

			/* Compute the negative write set */
			MscSet writeSet = new MscSet();
			for (MentalStateCond msc : capitalPsi) {

				/*
				 * If a mental state condition can become false due to the
				 * updates that the transition class brings about, add it
				 */
				if (derives.mscderneg(msc, updates)) {
					writeSet.add(msc);
				}
			}

			/* Return */
			return writeSet;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the positive write set of the specified transition class with
	 * respect to the specified set of mental state conditions.
	 *
	 * @param tauClass
	 *            - The transition class to compute the write set for.
	 * @param capitalPsi
	 *            - The set of mental state conditions.
	 */
	public static MscSet getWriteSetPos(
			TauClass<PrologTerm, PrologAtom, PrologUpdate> tauClass,
			MscSet capitalPsi) {

		try {

			/* Get the updates that the transition class brings about */
			TauUpdate<PrologAtom> updates = tauClass.getUpdates();

			/* Get the derives function to be used */
			Derives<PrologAtom> derives = tauClass.getModule().getDerives();

			/* Compute the positive write set */
			MscSet writeSet = new MscSet();
			for (MentalStateCond msc : capitalPsi) {

				/*
				 * If a mental state condition can become false due to the
				 * updates that the transition class brings about, add it
				 */
				if (derives.mscderpos(msc, updates)) {
					writeSet.add(msc);
				}
			}

			/* Return */
			return writeSet;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
