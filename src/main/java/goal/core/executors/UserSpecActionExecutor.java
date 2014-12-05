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

package goal.core.executors;

import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.UserSpecAction;

public class UserSpecActionExecutor extends ActionExecutor {

	private UserSpecAction action;
	private Substitution solution = null;
	private int indexIntoSpecifications;

	public UserSpecActionExecutor(UserSpecAction act) {
		this.action = act;
	}
	
	/**
	 * @return The latest substitution from calling evaluatePrecondition
	 */
	public Substitution getLatestSubstitution() {
		return this.solution;
	}

	@Override
	public ActionExecutor evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		// Find the first action specification whose precondition holds.
		final Set<Substitution> solutions = getOptions(mentalState, debugger);

		// Return null if no precondition was found that holds; otherwise return
		// an  instantiated action with the action specification that was found
		// first for which the precondition holds and use a randomly selected 
		// substitution to instantiate the action.
		if (solutions.isEmpty()) {
			// None of the preconditions holds.
			if (last) {
				debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION_USERSPEC,
						this, "Preconditions of action %s failed.",
						action.getName());
			}
			return null;
		} else {
			// Select a random substitution that satisfies the precondition we
			// found.
			ArrayList<Substitution> substitutions = new ArrayList<>(solutions);
			Collections.shuffle(substitutions);
			Substitution solution = substitutions.get(0);
			UserSpecAction action = getSelectedActionSpec();
			// Report success.
			debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION_USERSPEC,
					action, "Precondition { %s } of action %s holds for: %s.",
					action.getPrecondition(), action.getName(), solution);
			return new UserSpecActionExecutor(action.applySubst(this.solution));
		}
	}

	/**
	 * Searches for the first action specification of this action whose
	 * precondition holds in the given mental state.
	 *
	 * @param mentalState
	 *            The mental state used for evaluating the precondition.
	 * @param debugger
	 *            The current debugger
	 * @return The solutions, i.e., set of substitutions that satisfy the
	 *         precondition that was found, or the empty set otherwise.
	 */
	protected Set<Substitution> getOptions(MentalState mentalState,
			Debugger debugger) {
		Query precondition = null;
		// Reset index into specifications.
		indexIntoSpecifications = -1;

		// Search for first precondition in list of action specifications that
		// holds.
		// We also keep track of the corresponding postcondition.
		Set<Substitution> solutions = new HashSet<>();
		for (int i = 0; i < specifications.size(); i++) {
			PrePost prepost = specifications.get(i);
			// Get precondition and postcondition.
			precondition = prepost.getPrecondition();
			// Check whether precondition holds.
			solutions = mentalState.query(precondition, BASETYPE.BELIEFBASE,
					debugger);
			if (!solutions.isEmpty()) {
				// We found a precondition that holds; stop searching.
				// Remember which action specification is satisfied by setting
				// index into action specifications.
				indexIntoSpecifications = i;
				break;
			}
		}
		return solutions;
	}

	/**
	 * Returns this user-specified action where all action specifications other
	 * than the one found by {@link #getOptions(MentalState, SteppingDebugger)}
	 * have been removed.
	 * <p>
	 * Only call this AFTER a call to
	 * {@link #getOptions(MentalState, SteppingDebugger)} .
	 * </p>
	 *
	 * @return This user-specified action where all action specifications other
	 *         than the one found by
	 *         {@link #getOptions(MentalState, SteppingDebugger)} have been
	 *         removed.
	 */
	protected UserSpecAction getSelectedActionSpec() {
		// Check if call to #getOptions(RunState) has been made first.
		if (indexIntoSpecifications == -1) {
			throw new UnsupportedOperationException(
					"Calling #getSelectedActionSpec "
							+ "is only supported after first calling #getOptions(RunState).");
		}

		// Create new action that only has specification found by
		// #getOptions(RunState).
		UserSpecAction action = new UserSpecAction(action.getName(),
				action.getParameters(), action.getExernal(), null, null, 
				action.getSourceInfo());
		// Get first action specification found for which precondition holds.
		PrePost actionspec = specifications.get(indexIntoSpecifications);
		// Do NOT use addSpecification as this renames variables.
		action.specifications.add(actionspec);

		return action;
	}

	/**
	 * Executes this {@link UserSpecAction}.
	 *
	 * @param runState
	 *            The {@link RunState} in which this action is executed.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		// Send the action to the environment if it should be sent.
		if (action.getExernal()) {
			runState.doPerformAction(action);
		}

		// Apply the action's postcondition.
		Update postcondition = specifications.get(0).getPostcondition();
		runState.getMentalState().insert(postcondition, BASETYPE.BELIEFBASE,
				debugger);

		// Check if goals have been achieved and, if so, update goal base.
		runState.getMentalState().updateGoalState(debugger);

		// Report action was performed.
		report(debugger);

		return new Result(action);
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		this.solution = subst;
		return new UserSpecActionExecutor(action.applySubst(subst));
	}
	
	@Override
	public Action<?> getAction() {
		return action;
	}
}
