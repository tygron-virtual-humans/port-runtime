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
import goal.core.kr.language.Update;
import goal.core.program.actions.Action;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.AdoptOneAction;
import goal.core.program.actions.DeleteAction;
import goal.core.program.actions.DropAction;
import goal.core.program.actions.InsertAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.rules.Rule;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.goal.trans.RepositoryGoal;
import goal.tools.mc.program.goal.trans.TauClass;
import goal.tools.mc.program.goal.trans.TauModule;
import goal.tools.mc.program.goal.trans.TauUpdate;
import goal.tools.mc.program.goal.trans.TauUpdate.Op;

import java.util.ArrayList;
import java.util.List;

import jpl.Term;
import swiprolog3.language.PrologDBFormula;
import swiprolog3.language.PrologTerm;
import swiprolog3.language.PrologUpdate;

/**
 * Represents a transition class when the Prolog KRT is used.
 *
 * @author sungshik
 *
 */
public class PrologTauClass extends
		TauClass<PrologTerm, PrologAtom, PrologUpdate> {

	//
	// Private fields
	//

	/**
	 * Represents (an over-approximation of) the updates that transitions
	 * belonging to this transition class can bring about.
	 */
	private TauUpdate<PrologAtom> updates;

	//
	// Constructors
	//

	/**
	 * Constructs a transition class belonging to the specified transition
	 * module, and corresponding to the specified rule.
	 *
	 * @param tauModule
	 *            - The transition module to which the transition class to be
	 *            constructed belongs.
	 * @param rule
	 *            - The rule corresponding to the transition class to be
	 *            created.
	 */
	public PrologTauClass(
			TauModule<PrologTerm, PrologAtom, PrologUpdate> tauModule, Rule rule) {
		super(tauModule, rule);
	}

	//
	// Public methods
	//

	@Override
	public TauUpdate<PrologAtom> getUpdates() {

		try {

			/* If updates have not yet been defined, define them */
			if (updates == null) {
				updates = new PrologTauUpdate();

				/* Get a reference to the derives function */
				PrologDerives derives = (PrologDerives) tauModule.getDerives();

				/* Iterate the actions in the combo */
				for (Action act : combo) {

					/* User-defined action */
					if (act instanceof UserSpecAction
							|| act instanceof InsertAction
							|| act instanceof DeleteAction) {

						/* Get the PrologTerms that constitute the update */
						// FIXME: Initialized to null!?!?
						List<jpl.Term> terms = null;
						if (act instanceof UserSpecAction) {
							UserSpecAction uda = (UserSpecAction) act;
							terms = new ArrayList<jpl.Term>();
							for (Update post : uda.getPostconditions()) {
								terms.add(((PrologUpdate) post).getTerm());
							}
						} else if (act instanceof InsertAction) {
							InsertAction insert = (InsertAction) act;
							terms.add(((PrologUpdate) insert.getBelief())
									.getTerm());
						} else {
							DeleteAction delete = (DeleteAction) act;
							terms.add((((PrologUpdate) delete.getBelief())
									.getTerm()));
						}

						/* Add the updates */
						for (jpl.Term term : terms) {
							if (term.isAtom()) {
								updates.addBelief(Op.ADD,
										PrologAtom.factory(term));
							} else {
								updates.addBelief(Op.DEL, PrologAtom
										.factory(PrologAtom.affirm(term)));
							}
						}

						/* Determine achieved goals */
						PrologGoalRepository repository = (PrologGoalRepository) tauModule
								.getRepository();
						for (RepositoryGoal<PrologAtom, PrologUpdate> goal : repository
								.getGoals()) {
							// FIXME: if
							// (derives.plderpos(updates.getSigmaPlus(),
							// FIXME: updates.getSigmaMin(), goal.getGoal()
							// FIXME: .getTerm(), new PrologDerInfo())) {
							// FIXME: updates.addGoal(Op.DEL, goal.getAtoms());
							// FIXME: }
						}
					}

					/* Adopt action */
					if (act instanceof AdoptAction
							|| act instanceof AdoptOneAction) {

						/* Get goal as PrologUpdate */
						PrologUpdate u = ((PrologUpdate) (act instanceof AdoptAction ? ((AdoptAction) act)
								.getGoal() : ((AdoptOneAction) act).getGoal()));

						/* Unzip u, and add terms as atoms to set */
						LMHashSet<PrologAtom> set = new LMHashSet<PrologAtom>();
						for (DatabaseFormula f : u.getAddList()) {
							Term term = ((PrologDBFormula) f).getTerm();
							assert PrologAtom.isAtom(term);
							set.add(PrologAtom.factory(term));
						}

						/* Add the set as a goal */
						updates.addGoal(Op.ADD, set);
					}

					/* Drop action */
					if (act instanceof DropAction) {
						DropAction drop = (DropAction) act;
						Term dropTerm = ((PrologUpdate) drop.getGoal())
								.getTerm();
						PrologGoalRepository repository = (PrologGoalRepository) tauModule
								.getRepository();
						for (RepositoryGoal<PrologAtom, PrologUpdate> goal : repository
								.getGoals()) {
							// FIXME: if (derives.plderpos(
							// FIXME: ((PrologRepositoryGoal) goal).getAtoms(),
							// FIXME: derives.WILDCARD, dropTerm,
							// FIXME: new PrologDerInfo())) {
							// FIXME: updates.addGoal(Op.DEL, goal.getAtoms());
							// FIXME: }
						}
					}
				}
			}
			return updates;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
