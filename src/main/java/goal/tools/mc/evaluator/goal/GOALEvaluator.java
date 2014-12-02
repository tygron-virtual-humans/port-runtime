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

package goal.tools.mc.evaluator.goal;

import goal.core.kr.language.Substitution;
import goal.core.mentalstate.MentalState;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.evaluator.Evaluator;
import goal.tools.mc.program.goal.GOALMentalStateConverter;
import goal.tools.mc.program.goal.GOALProg;
import goal.tools.mc.program.goal.GOALState;
import goal.tools.mc.property.ltl.Formula;
import goal.tools.mc.property.ltl.Negation;
import goal.tools.mc.property.ltl.goal.MentalStateCondition;

import java.util.Set;

/**
 * Represents an evaluator component for GOAL by implementing the
 * {@link Evaluator} interface. To evaluate queries, the GOAL interpreter is
 * used through invocations to {@link MentalState#mscQuery}. The specific
 * {@link MentalState} object to evaluate queries in is passed to the
 * {@link GOALEvaluator} object during object construction by means of a
 * reference to the {@link GOALMentalStateConverter} object that is created and
 * used by a {@link GOALProg} object. As such, a {@link GOALEvaluator} instance
 * are linked to a {@link GOALProg} instance; maybe it would be nicer to remove
 * this link, i.e. give {@link GOALEvaluator} a {@link GOALMentalStateConverter}
 * of its own.
 *
 * @author sungshik
 *
 */
public class GOALEvaluator implements Evaluator {

	//
	// Private fields
	//

	/**
	 * The converter that provides a reference to the {@link MentalState} object
	 * to evaluate queries in.
	 */
	private final GOALMentalStateConverter conv;

	/**
	 * Maintains the number of distinct states in which evaluations have taken
	 * place.
	 */
	private final LMHashSet<GOALState> evaluated = new LMHashSet<GOALState>();

	/**
	 * An empty debugger (necessary for calls to {@link MentalState#mscQuery}).
	 */
	private final Debugger debugger = new SteppingDebugger("evaluator", null);

	//
	// Constructors
	//

	/**
	 * Constructs a {@link GOALEvaluator} instance, given a mental state
	 * converter.
	 *
	 * @param conv
	 *            - The mental state converter that provides a reference to the
	 *            {@link MentalState} object to evaluate queries in.
	 */
	public GOALEvaluator(GOALMentalStateConverter conv) {
		this.conv = conv;
	}

	//
	// Public methods
	//

	@Override
	public boolean entails(State state, Formula f) {

		try {
			GOALState q = (GOALState) state;
			evaluated.add(q);
			conv.update(q);
			return entails(f);
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean entailsAll(State state, LMHashSet<Formula> formulas) {

		try {
			GOALState q = (GOALState) state;
			evaluated.add(q);
			conv.update(q);
			for (Formula f : formulas) {
				if (!entails(f)) {
					return false;
				}
			}
			return true;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int getDistinctEvaluated() {
		return evaluated.size();
	}

	// Private methods

	/**
	 * Determines whether the current {@link MentalState} object as referenced
	 * by {@link conv} entails the specified formula. This method assumes that
	 * {@link MentalState} already has the "right" configuration, and does not
	 * change it before evaluation of the query.
	 *
	 * @param formula
	 *            - The formula to check entailment for.
	 */
	private boolean entails(Formula formula) {

		try {
			/* Determine polarity */
			boolean polarity = true;
			if (formula instanceof Negation) {
				polarity = false;
				formula = ((Negation) formula).getArg();
			}

			/* Fire query */
			if (formula instanceof MentalStateCondition) {
				Set<Substitution> theta = ((MentalStateCondition) formula)
						.getCondition().evaluate(conv.getMentalState(),
								debugger);
				return polarity ? !theta.isEmpty() : theta.isEmpty();
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
