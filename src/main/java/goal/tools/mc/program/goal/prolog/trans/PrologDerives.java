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
import goal.core.kr.language.Substitution;
import goal.core.program.literals.AGoalLiteral;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.GoalLiteral;
import goal.core.program.literals.MentalLiteral;
import goal.core.program.literals.MentalStateCond;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.goal.trans.Derives;
import goal.tools.mc.program.goal.trans.TauUpdate;

import java.util.ArrayList;
import java.util.List;

import swiprolog3.language.JPLUtils;
import swiprolog3.language.PrologDBFormula;
import swiprolog3.language.PrologQuery;
import swiprolog3.language.PrologSubstitution;
import swiprolog3.parser.PrologOperators;

/**
 * Implements the methods for determining whether a mental state condition can
 * become (un)derivable after certain updates to the mental state when the
 * Prolog KRT is used. Works only for a restricted number of Prolog operators
 * and predicates: conjunction, negation, and arithmetic.
 *
 * @author sungshik
 *
 */
public class PrologDerives implements Derives<PrologAtom> {

	//
	// Public fields
	//

	/**
	 * The wild card, which contains {@link #TRUE} and {@link #FALSE} after
	 * initialization.
	 */
	public final LMHashSet<jpl.Atom> WILDCARD = new LMHashSet<jpl.Atom>();

	//
	// Private fields
	//

	/**
	 * The false wild card.
	 */
	private final jpl.Atom FALSE = new jpl.Atom("false-wildcard");

	/**
	 * The knowledge rules that are used to make derivations.
	 */
	private final List<PrologKnowledgeRule> rules = new ArrayList<PrologKnowledgeRule>();

	/**
	 * The true wild card.
	 */
	private final jpl.Atom TRUE = new jpl.Atom("true-wildcard");

	//
	// Constructors
	//

	/**
	 * Constructs a derivation facility given the specified set of knowledge.
	 *
	 * @param knowledge
	 *            - The knowledge that should be taken into consideration when
	 *            making derivations.
	 */
	public PrologDerives(Iterable<DatabaseFormula> knowledge) {
		this.WILDCARD.add(TRUE);

		/* Process knowledge to a more convenient representation */
		for (DatabaseFormula f : knowledge) {
			PrologDBFormula pdbf = (PrologDBFormula) f;
			PrologKnowledgeRule pkr = new PrologKnowledgeRule(pdbf);
			if (!pkr.isFact()) {
				rules.add(pkr);
			}
		}
	}

	//
	// Public methods
	//

	@Override
	public boolean mscderneg(MentalStateCond msc, TauUpdate<PrologAtom> update) {

		try {

			/* Initialize shortcut references */
			// FIXME: LMHashSet<jpl.Atom> sigmaPlus = update.getSigmaPlus();
			// FIXME: LMHashSet<jpl.Atom> sigmaMin = update.getSigmaMin();
			// FIXME: LMHashSet<LMHashSet<jpl.Atom>> gammaPlus =
			// update.getGammaPlus();
			// FIXME: LMHashSet<LMHashSet<jpl.Atom>> gammaMin =
			// update.getGammaMin();
			// We need these to be of type:
			LMHashSet<jpl.Atom> sigmaPlus = null;
			LMHashSet<jpl.Atom> sigmaMin = null;
			LMHashSet<LMHashSet<jpl.Atom>> gammaPlus = null;
			LMHashSet<LMHashSet<jpl.Atom>> gammaMin = null;

			/* Construct an information object */
			PrologDerInfo info = new PrologDerInfo();

			/*
			 * Iterate the mental literals in msc: if at least one literal can
			 * become underivable, return true
			 */
			for (MentalLiteral l : msc.getLiterals()) {
				jpl.Term term = ((PrologQuery) l.getFormula()).getTerm();

				/*
				 * If l is a positive belief literal and term becomes
				 * underivable from the belief base, return true
				 */
				if (l instanceof BelLiteral
						&& l.isPositive()
						&& plderneg(sigmaPlus, sigmaMin, term,
								info.clearApplied())) {
					return true;
				}

				/*
				 * If l is a negative belief literal and term becomes derivable
				 * from the belief base, return true
				 */
				if (l instanceof BelLiteral
						&& !l.isPositive()
						&& plderpos(sigmaPlus, sigmaMin, term,
								info.clearApplied())) {
					return true;
				}

				/*
				 * If l is a positive goal literal and there exists a deleted
				 * goal from which term is underivable, return true
				 */
				if (l instanceof GoalLiteral && l.isPositive()) {
					for (LMHashSet<jpl.Atom> goal : gammaMin) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}
				}

				/*
				 * If l is a negative goal literal and there exists an added
				 * goal from which term is derivable, return true
				 */
				if (l instanceof GoalLiteral && !l.isPositive()) {
					for (LMHashSet<jpl.Atom> goal : gammaPlus) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}
				}

				/*
				 * If l is a positive a-goal literal and term becomes derivable
				 * from the belief base or there exists a deleted goal from
				 * which term is derivable, return true
				 */
				if (l instanceof AGoalLiteral && l.isPositive()) {
					if (plderpos(sigmaPlus, sigmaMin, term, info.clearApplied())) {
						return true;
					}
					for (LMHashSet<jpl.Atom> goal : gammaMin) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}
				}

				/*
				 * If l is a negative a-goal literal and term becomes
				 * underivable from the belief base or there exists an added
				 * goal from which term is derivable, return true
				 */
				if (l instanceof AGoalLiteral && !l.isPositive()) {
					if (plderneg(sigmaPlus, sigmaMin, term, info.clearApplied())) {
						return true;
					}
					for (LMHashSet<jpl.Atom> goal : gammaPlus) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}
				}
			}

			/*
			 * If this point is ever reached, all efforts to find some evidence
			 * that msc may become underivable have failed, hence it does not
			 * become underivable
			 */
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean mscderpos(MentalStateCond msc, TauUpdate<PrologAtom> update) {

		try {

			/* Initialize shortcut references */
			// FIXME: LMHashSet<jpl.Atom> sigmaPlus = update.getSigmaPlus();
			// FIXME: LMHashSet<jpl.Atom> sigmaMin = update.getSigmaMin();
			// FIXME: LMHashSet<LMHashSet<jpl.Atom>> gammaPlus =
			// update.getGammaPlus();
			// FIXME: LMHashSet<LMHashSet<jpl.Atom>> gammaMin =
			// update.getGammaMin();
			// We need these to be of type:
			LMHashSet<jpl.Atom> sigmaPlus = null;
			LMHashSet<jpl.Atom> sigmaMin = null;
			LMHashSet<LMHashSet<jpl.Atom>> gammaPlus = null;
			LMHashSet<LMHashSet<jpl.Atom>> gammaMin = null;

			/* Construct an information object */
			PrologDerInfo info = new PrologDerInfo();

			/*
			 * Iterate the mental literals in msc: if at least one literal can
			 * become derivable, assume that this was the last literal that
			 * previously was false such that the entire msc becomes true, and
			 * return true
			 */
			for (MentalLiteral l : msc.getLiterals()) {
				jpl.Term term = ((PrologQuery) l.getFormula()).getTerm();

				/*
				 * If l is a positive belief literal and term becomes derivable
				 * from the belief base, return true
				 */
				if (l instanceof BelLiteral
						&& l.isPositive()
						&& plderpos(sigmaPlus, sigmaMin, term,
								info.clearApplied())) {
					return true;
				}

				/*
				 * If l is a negative belief literal and term becomes
				 * underivable from the belief base, return true
				 */
				if (l instanceof BelLiteral
						&& !l.isPositive()
						&& plderneg(sigmaPlus, sigmaMin, term,
								info.clearApplied())) {
					return true;
				}

				/*
				 * If l is a positive goal literal and there exists an added
				 * goal from which term is derivable, return true
				 */
				if (l instanceof GoalLiteral && l.isPositive()) {
					for (LMHashSet<jpl.Atom> goal : gammaPlus) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}
				}

				/*
				 * If l is a negative goal literal and there exists a deleted
				 * goal from which term is derivable, return true
				 */
				if (l instanceof GoalLiteral && !l.isPositive()) {
					for (LMHashSet<jpl.Atom> goal : gammaMin) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;

						}
					}
				}

				/*
				 * If l is a positive a-goal literal and term becomes
				 * underivable from the belief base or there exists an added
				 * goal from which term is derivable, return true
				 */
				if (l instanceof AGoalLiteral && l.isPositive()) {
					if (plderneg(sigmaPlus, sigmaMin, term, info.clearApplied())) {
						return true;
					}
					for (LMHashSet<jpl.Atom> goal : gammaPlus) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}
				}

				/*
				 * If l is a negative a-goal literal and term becomes derivable
				 * from the belief base or there exists a deleted goal from
				 * which term is derivable, return true
				 */
				if (l instanceof AGoalLiteral && l.isPositive()) {
					if (plderpos(sigmaPlus, sigmaMin, term, info.clearApplied())) {
						return true;
					}
					for (LMHashSet<jpl.Atom> goal : gammaMin) {
						if (plderpos(goal, WILDCARD, term, info.clearApplied())) {
							return true;
						}
					}

				}
			}

			/*
			 * If this point is ever reached, all efforts to find some evidence
			 * that msc may become derivable have failed, hence it does not
			 * become derivable
			 */
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Determines whether the specified term can become underivable from a set
	 * of Prolog clauses at least containing the knowledge rules {@link #rules},
	 * if the atoms in <code>plus</code> are added and the atoms in
	 * <code>min</code> deleted. This method is over-approximative in the sense
	 * that if the specified term can become underivable, <code>true</code> is
	 * returned. However, it may happen that if the specified does not become
	 * underivable, <code>true</code> is returned nevertheless.
	 *
	 * @param plus
	 *            - The set of terms that are added.
	 * @param min
	 *            - The set of terms that are deleted.
	 * @param term
	 *            - The term to check for whether it can become underivable.
	 * @param info
	 *            - Information about the search so far; for instance, we need
	 *            to check that the same knowledge rule is not applied twice,
	 *            because this may result in non-terminating behavior.
	 * @return - <code>true<code> if the term can become underivable in an over-
	 * approximative fashion; <code>false</code> otherwise.
	 */
	public boolean plderneg(LMHashSet<jpl.Atom> plus, LMHashSet<jpl.Atom> min,
			jpl.Term term, PrologDerInfo info) {

		try {
			String op = JPLUtils.getSignature(term);

			/*
			 * If term is a not term, apply plderpos on its argument and return
			 * that call's result
			 */
			if (op.equals("not/1")) {

				/* If plus contains the wild card, return immediately */
				if (plus.contains(FALSE)) {
					return true;
				}

				/* Call plderpos on term's argument */
				else {
					jpl.Term arg = term.args()[0];
					return plderpos(plus, min, arg, info);
				}
			}

			/*
			 * If term is a conjunction, apply plderneg on the conjuncts, and
			 * return true if at least one of these calls returns true
			 */
			else if (op.equals(",/2")) {
				for (jpl.Term arg : term.args()) {
					if (plderneg(plus, min, arg, info)) {
						return true;
					}
				}
			}

			/* If term is not a prolog built-in, assume it is an atom. */
			else if (!PrologOperators
					.prologBuiltin(JPLUtils.getSignature(term))) {
				jpl.Atom atom = (jpl.Atom) term;

				/*
				 * If there exists an atom in min that is "equal" atom, then
				 * atom may become underivable, hence term, hence return true
				 */
				for (jpl.Atom at : min) {
					if (JPLUtils.equals(atom, at)) {
						return true;
					}
				}

				/*
				 * If there does not exist an atom in min that is "equal" to
				 * atom, then atom might be the head of a knowledge rule whose
				 * body has become underivable
				 */
				for (PrologKnowledgeRule rule : rules) {
					Substitution theta = PrologSubstitution
							.getSubstitutionOrNull(JPLUtils
									.mgu(atom, rule.head));

					/*
					 * If such an rule head indeed exists, check if we have not
					 * applied this rule; if not, determine whether the body of
					 * this rule becomes underivable
					 */
					if (theta != null && !info.appliedNeg.contains(rule)) {
						info.appliedNeg.add(rule);
						if (plderneg(plus, min, JPLUtils.applySubst(
								((PrologSubstitution) theta).getJPLSolution(),
								rule.body), info)) {
							return true;
						}
					}
				}
			}

			/*
			 * If this point is ever reached, all efforts to find some evidence
			 * that term may become underivable have failed, hence it does not
			 * become underivable
			 */
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Determines whether the specified term can become derivable from a set of
	 * Prolog clauses at least containing the knowledge rules {@link #rules}, if
	 * the atoms in <code>plus</code> are added and the atoms in
	 * <code>min</code> deleted. This method is over-approximative in the sense
	 * that if the specified term can become derivable, <code>true</code> is
	 * returned. However, it may happen that if the specified does not become
	 * derivable, <code>true</code> is returned nevertheless.
	 *
	 * @param plus
	 *            - The set of terms that are added.
	 * @param min
	 *            - The set of terms that are deleted.
	 * @param term
	 *            - The term to check for whether it can become derivable.
	 * @param info
	 *            - Information about the search so far; for instance, we need
	 *            to check that the same knowledge rule is not applied twice,
	 *            because this may result in non-terminating behavior.
	 * @return - <code>true<code> if the term can become derivable in an over-
	 * approximative fashion; <code>false</code> otherwise.
	 */
	public boolean plderpos(LMHashSet<jpl.Atom> plus, LMHashSet<jpl.Atom> min,
			jpl.Term term, PrologDerInfo info) {

		try {
			String op = JPLUtils.getSignature(term);

			/*
			 * If term is a not term, apply plderneg on its argument and return
			 * that call's result
			 */
			if (op.equals("not/1")) {

				/* If min contains the wild card, return immediately */
				if (min.contains(TRUE)) {
					return true;
				}

				/* Call plderneg on term's argument */
				else {
					jpl.Term arg = term.args()[0];
					return plderneg(plus, min, arg, info);
				}
			}

			/*
			 * If term is a conjunction, apply plderpos on the conjuncts, and
			 * return true if at least one of these calls returns true
			 */
			else if (op.equals(",/2")) {
				for (jpl.Term arg : term.args()) {
					if (plderpos(plus, min, arg, info)) {
						return true;
					}
				}
			}

			/* If term is not a prolog built-in, assume it is an atom. */
			else if (!PrologOperators
					.prologBuiltin(JPLUtils.getSignature(term))) {
				jpl.Atom atom = (jpl.Atom) term;

				/*
				 * If there exists an atom in plus that is "equal" atom, then
				 * atom may become derivable, hence term, hence return true
				 */
				for (jpl.Atom at : plus) {
					if (PrologEquals.areEqual(atom, at) != null) {
						return true;
					}
				}

				/*
				 * If there does not exist an atom in plus that is "equal" to
				 * atom, then atom might be the head of a knowledge rule whose
				 * body has become derivable
				 */
				for (PrologKnowledgeRule rule : rules) {
					Substitution theta = PrologEquals.areEqual(atom, rule.head);

					/*
					 * If such an rule head indeed exists, check if we have not
					 * applied this rule; if not, determine whether the body of
					 * this rule becomes derivable
					 */
					if (theta != null && !info.appliedPos.contains(rule)) {
						info.appliedPos.add(rule);
						if (plderpos(plus, min, JPLUtils.applySubst(
								((PrologSubstitution) theta).getJPLSolution(),
								rule.body), info)) {
							return true;
						}
					}
				}
			}

			/*
			 * If this point is ever reached, all efforts to find some evidence
			 * that term may become derivable have failed, hence it does not
			 * become derivable
			 */
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

/**
 * Maintains information about which knowledge rules have already been applied
 * during a derivation process.
 *
 * @author sungshik
 *
 */
final class PrologDerInfo {

	//
	// Fields
	//

	/**
	 * The rules that have already been applied during invocations of
	 * {@link PrologDerives#plderneg}.
	 */
	LMHashSet<PrologKnowledgeRule> appliedNeg = new LMHashSet<PrologKnowledgeRule>();

	/**
	 * The rules that have already been applied during invocations of
	 * {@link PrologDerives#plderpos}.
	 */
	LMHashSet<PrologKnowledgeRule> appliedPos = new LMHashSet<PrologKnowledgeRule>();

	//
	// Methods
	//

	/**
	 * Clears all information about applied rules.
	 *
	 * @return <code>this</code>.
	 */
	PrologDerInfo clearApplied() {
		appliedNeg.clear();
		appliedPos.clear();
		return this;
	}
}

/**
 * Represents a knowledge rule when the Prolog KRT is used.
 *
 * @author sungshik
 *
 */
final class PrologKnowledgeRule {

	//
	// Private fields
	//

	/**
	 * The body of this knowledge rule (possibly <code>null</code> if this rule
	 * is actually a fact).
	 */
	final jpl.Term body;

	/**
	 * The original formula representing the knowledge rule.
	 */
	final PrologDBFormula f;

	/**
	 * The head of this rule.
	 */
	final jpl.Atom head;

	//
	// Constructors
	//

	/**
	 * Constructs an instance of this class according to the specified database
	 * formula.
	 *
	 * @param f
	 *            - The original knowledge rule to which the object to be
	 *            created should correspond.
	 */
	public PrologKnowledgeRule(PrologDBFormula f) {
		this.f = f;
		jpl.Term term = f.getTerm();
		if (JPLUtils.getSignature(term).equals(":-/2")) {
			this.head = (jpl.Atom) term.args()[0];
			this.body = term.args()[1];
		} else {
			this.head = (jpl.Atom) term;
			this.body = null;
		}
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		return f.equals(((PrologKnowledgeRule) o).f);
	}

	@Override
	public int hashCode() {
		return f.hashCode();
	}

	/**
	 * Returns true if this knowledge rule is a fact (i.e. has no body).
	 *
	 * @return <tt>true</tt> if the rule is a fact; <tt>false</tt> otherwise.
	 */
	public boolean isFact() {
		return body == null;
	}

	@Override
	public String toString() {
		return head + " :- " + body;
	}
}
