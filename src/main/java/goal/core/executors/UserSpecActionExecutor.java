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

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Update;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.UserSpecAction;
import mentalState.BASETYPE;

public class UserSpecActionExecutor extends ActionExecutor {
	private final UserSpecAction action;

	public UserSpecActionExecutor(UserSpecAction act) {
		this.action = act;
	}

	@Override
	public ActionExecutor evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		MentalStateConditionExecutor check = new MentalStateConditionExecutor(
				this.action.getPrecondition());
		final Set<Substitution> solutions1 = check.evaluate(mentalState,
				debugger);

		// Create instantiations and add to options.
		final Set<Substitution> solutions = new HashSet<>(solutions1.size());
		for (Substitution substitution : solutions1) {
			// Check if first action is closed.
			if (this.action.applySubst(substitution).isClosed()) {
				solutions.add(substitution);
			}
		}

		// Return null if the precondition did not hold; otherwise return
		// an instantiated action with the action specification that was found
		// first for which the precondition holds and use a randomly selected
		// substitution to instantiate the action.
		if (solutions.isEmpty()) {
			if (last) {
				debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION_USERSPEC,
						this.action, this.action.getSourceInfo(),
						"Preconditions of action %s failed.", this.action);
			}
			return null;
		} else {
			// Select a random substitution
			List<Substitution> substitutions = new ArrayList<>(solutions);
			Collections.shuffle(substitutions);
			Substitution solution = substitutions.get(0);
			// Report success
			debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION_USERSPEC,
					this.action, this.action.getSourceInfo(),
					"Precondition { %s } of action %s holds for: %s.",
					this.action.getPrecondition(), this.action, solution);
			return new UserSpecActionExecutor(this.action.applySubst(solution));
		}
	}

	/**
	 * Executes this {@link UserSpecAction}.
	 *
	 * @param runState
	 *            The {@link RunState} in which this action is executed.
	 * @throws GOALActionFailedException
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger)
			throws GOALActionFailedException {
		// Send the action to the environment if it should be sent.
		if (this.action.getExernal()) {
			runState.doPerformAction(this.action);
		}

		// Apply the action's postcondition.
		Update postcondition = this.action.getPostcondition();
		runState.getMentalState().insert(postcondition, BASETYPE.BELIEFBASE,
				debugger);

		// Check if goals have been achieved and, if so, update goal base.
		runState.getMentalState().updateGoalState(debugger);

		// Report action was performed.
		report(debugger);

		return new Result(this.action);
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		/*
		 * #3430 focus the global context substi , pass through only variables
		 * that are still open in the module parameter list
		 */
		Substitution focusedSubst = subst.clone();
		focusedSubst.retainAll(getVariables(this.action.getParameters()));
		return new UserSpecActionExecutor(this.action.applySubst(focusedSubst));
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}
