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

import goal.core.kr.language.Query;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.program.SelectExpression;
import goal.core.program.Selector;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.AdoptOneAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.program.rules.Rule;

import java.util.ArrayList;
import java.util.List;

import swiprolog3.language.JPLUtils;
import swiprolog3.language.PrologQuery;

/**
 * Represents a transition class.
 *
 * @author sungshik
 *
 * @param <T>
 *            - The type of term associated with the KRT that the agent to which
 *            this transition class belongs uses.
 * @param <A>
 *            - The type of atom associated with the KRT that the agent to which
 *            this transition class belongs uses.
 * @param <U>
 *            - The type of update associated with the KRT that the agent to
 *            which this transition class belongs uses.
 */
public abstract class TauClass<T extends Term, A extends Atom, U extends Update> {

	//
	// Protected fields
	//

	/**
	 * The action combo that is the consequent of the action rule corresponding
	 * to this transition class.
	 */
	protected ActionCombo combo;

	/**
	 * The transition module to which this transition class belongs.
	 */
	protected TauModule<T, A, U> tauModule;

	//
	// Private methods
	//

	/**
	 * The transition classes on which this transition class depends. Initially
	 * empty, and only initialized if some force external to this transition
	 * class does so.
	 */
	private final List<TauClass<T, A, U>> dependence = new ArrayList<TauClass<T, A, U>>();

	/**
	 * The transition classes that can enable this transition class. Initially
	 * empty, and only initialized if some force external to this transition
	 * class does so.
	 */
	private final List<TauClass<T, A, U>> enabledBy = new ArrayList<TauClass<T, A, U>>();

	/**
	 * The transition classes that this transition class can enable. Initially
	 * empty, and only initialized if some force external to this transition
	 * class does so.
	 */
	private final List<TauClass<T, A, U>> enables = new ArrayList<TauClass<T, A, U>>();

	/**
	 * The read set of this transition class.
	 */
	private MscSet readset = new MscSet();

	/**
	 * The rule corresponding to this transition class.
	 */
	private final Rule rule;

	/**
	 * Flag indicating whether transitions in this transition class are visible
	 * to the property under investigation. By default <code>true</code> for
	 * safety.
	 */
	private boolean visible = true;

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
	public TauClass(TauModule<T, A, U> tauModule, Rule rule) {
		this.tauModule = tauModule;
		this.rule = rule;
		this.combo = this.rule.getAction();
		// this.readset.add(tauModule.module.getContext());
		this.readset = this.readset; // TODO fix above line. (this line is just
		// a marker)
		this.readset.add(rule.getCondition());
		this.readset.add(preToMsc(combo));
	}

	//
	// Abstract methods
	//

	/**
	 * Gets the specific updates that this step brings about, and which may be
	 * represented differently depending on the KRT in use.
	 *
	 * @return The updates.
	 */
	public abstract TauUpdate<A> getUpdates();

	//
	// Public methods
	//

	/**
	 * Adds a transition class to the dependence list of this transition class.
	 *
	 * @param tauClass
	 *            - The transition class to be added.
	 */
	public void addDependence(TauClass<T, A, U> tauClass) {
		if (!dependence.contains(tauClass)) {
			dependence.add(tauClass);
		}
	}

	/**
	 * Adds a transition class to the enabled-by list of this transition class.
	 *
	 * @param tauClass
	 *            - The transition class to be added.
	 */
	public void addEnabledBy(TauClass<T, A, U> tauClass) {
		if (!enabledBy.contains(tauClass)) {
			enabledBy.add(tauClass);
		}
	}

	/**
	 * Adds a transition class to the enables list of this transition class.
	 *
	 * @param tauClass
	 *            - The transition class to be added.
	 */
	public void addEnables(TauClass<T, A, U> tauClass) {
		if (!enables.contains(tauClass)) {
			enables.add(tauClass);
		}
	}

	/**
	 * Gets the transition classes that depend on this transition class.
	 *
	 * @return {@link #dependence}
	 */
	public List<TauClass<T, A, U>> getDependence() {
		return dependence;
	}

	/**
	 * Gets the transition classes that can enable this transition class.
	 *
	 * @return {@link #enabledBy}
	 */
	public List<TauClass<T, A, U>> getEnabledBy() {
		return enabledBy;
	}

	/**
	 * Gets the transition classes that this transition class can enable.
	 *
	 * @return {@link #enabledBy}
	 */
	public List<TauClass<T, A, U>> getEnables() {
		return enables;
	}

	/**
	 * Gets the transition module to which this transition class belongs.
	 *
	 * @return The transition module.
	 */
	public TauModule<T, A, U> getModule() {
		return tauModule;
	}

	/**
	 * Gets the read set of this transition class.
	 *
	 * @return The read set.
	 */
	public MscSet getReadset() {
		return readset;
	}

	/**
	 * Gets the rule corresponding to this transition class.
	 *
	 * @return The rule.
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * Hides this transition class from the transition module such that it is no
	 * long considered for execution. This is typically used with slicing.
	 */
	public void hide() {
		tauModule.hideRule(rule);
	}

	/**
	 * Checks if this transition class is visible.
	 *
	 * @return <code>true</code> if this transition class is visible;
	 *         <code>false</code> otherwise.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets visibility of this transition class to the specified value.
	 *
	 * @param visible
	 *            - Value representing whether this transition class is visible.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		return rule.toRuleString();
	}

	//
	// Private methods
	//

	/**
	 * Converts the preconditions in the specified action combo to a mental
	 * state condition, where each adopt action according to GOAL's action
	 * semantics.
	 *
	 * @param combo
	 *            - The action combo to extract preconditions form.
	 * @return The mental state condition covering all preconditions.
	 */
	private MentalStateCond preToMsc(ActionCombo combo) {

		try {

			/* Create a selector */
			Selector sel = new Selector(null);
			sel.add(new SelectExpression(SelectExpression.SelectorType.SELF));

			/* Data structure for the mental state condition to be created */
			ArrayList<MentalFormula> formulas = new ArrayList<MentalFormula>();

			/* Iterate actions in the combo */
			for (Action act : combo) {
				Query query;
				boolean polarity;

				/*
				 * If act is a user-defined action, the precondition(s) of this
				 * action becomes the argument of a positive belief literal
				 */
				if (act instanceof UserSpecAction) {
					UserSpecAction uda = (UserSpecAction) act;
					List<jpl.Term> terms = new ArrayList<jpl.Term>();
					for (Query pre : uda.getPreconditions()) {
						terms.add(((PrologQuery) pre).getTerm());
					}
					query = new PrologQuery(JPLUtils.termsToConjunct(terms),
							null);
					polarity = true;

					/*
					 * If act is an adopt action, the argument of this action
					 * becomes the argument of a negative belief literal
					 */
				} else if (act instanceof AdoptAction) {
					query = ((AdoptAction) act).getGoal().toQuery();
					polarity = false;
				} else if (act instanceof AdoptOneAction) {
					query = ((AdoptOneAction) act).getGoal().toQuery();
					polarity = false;

					/* For all other types of action, the precondition is empty */
				} else {
					continue;
				}

				/* Add new literal to literals */
				formulas.add(new BelLiteral(polarity, query, sel, null));
			}

			/* Return */
			return new MentalStateCond(formulas, null);
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
