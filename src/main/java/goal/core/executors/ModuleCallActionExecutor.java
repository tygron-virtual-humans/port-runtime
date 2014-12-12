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

import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalModel;
import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.List;

import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.Module;
import languageTools.program.agent.Module.TYPE;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.msc.AGoalLiteral;
import languageTools.program.agent.msc.GoalLiteral;
import languageTools.program.agent.msc.MentalLiteral;
import languageTools.program.agent.msc.MentalStateCondition;

public class ModuleCallActionExecutor extends ActionExecutor {
	private final ModuleCallAction action;
	private MentalStateCondition context;
	private Substitution substitutionToPassOnToModule;

	public ModuleCallActionExecutor(ModuleCallAction act) {
		this.action = act;
	}

	public void setContext(MentalStateCondition ctx) {
		this.context = ctx;
		this.substitutionToPassOnToModule = null;
	}

	@Override
	public ModuleCallActionExecutor evaluatePrecondition(MentalState runState,
			Debugger debugger, boolean last) {
		debugger.breakpoint(Channel.CALL_MODULE, this,
				"Going to enter module: %s.", this.action.getTarget().getName());
		return this;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		// Get and process substitution that should be passed on to module
		// that is to be entered; the corresponding variable is set in
		// #applySubst(Substitution).
		Substitution substitution = this.substitutionToPassOnToModule;
		// Use module parameters to filter bindings in substitution. Only
		// values for free variables in the module's parameters are passed on
		// in case of a non-anonymous module; an anonymous module is completely
		// transparent and all bindings are passed on to these modules.
		Substitution moduleSubstitution = getModuleSubsti(substitution);

		// TODO: move creation of attentionset into Module class itself.

		// Check whether new attention set needs to be created.
		GoalBase newAttentionSet = getNewFocus(runState.getMentalState(),
				debugger, substitution, runState.getFocusGoal());
		if (newAttentionSet != null) {
			runState.getMentalState().focus(newAttentionSet, debugger);
		}
		// Goal has been used; Reset the goal to focus on to {@code null}.
		runState.setFocusGoal(null);

		// Run target module.
		Result result = new ModuleExecutor(this.action.getTarget())
				.executeFully(runState, moduleSubstitution);
		// TODO: the module is run entirely here, bypassing the default
		// task-based scheduling; I'm not sure that is the desired effect here
		// -Vincent
		if (newAttentionSet != null) {
			runState.getMentalState().getOwnModel().defocus(debugger);
		}
		return result;
	}

	/**
	 * Get the new focus, considering the focus method and available goals and
	 * substis.
	 *
	 * @param subst
	 * @param goal
	 * @return new {@link GoalBase} to use for the focus action. May be null if
	 *         no refocus is needed (reuse the existing {@link GoalBase}.
	 * @throws GOALActionFailedException
	 */
	private GoalBase getNewFocus(MentalState mentalstate, Debugger debugger,
			Substitution subst, SingleGoal goal) {
		switch (this.action.getTarget().getFocusMethod()) {
		case NEW:
			// Create new empty goal base to construct a new attention set.
			return new GoalBase(mentalstate.getState(),
					mentalstate.getAgentId(), mentalstate.getOwner(),
					this.action.getTarget().getName());
		case SELECT:
			GoalBase newAttentionSet = new GoalBase(mentalstate.getState(),
					mentalstate.getAgentId(), mentalstate.getOwner(),
					this.action.getTarget().getName());
			newAttentionSet.addGoal(goal, debugger);
			return newAttentionSet;
		case FILTER:
			// from the goals that validate the context,
			// choose one randomly
			return getNewFilterGoals(mentalstate, debugger, subst);
		default:
			return null;
		}
	}

	/**
	 * Determine the substitution for the new module, given the arguments of
	 * this focus action and the parameters of the target module. This filters
	 * away all substi's that were needed to make this rule true (the subst that
	 * is given to us) and copies a few of them into the new returned
	 * substitution under a new name.
	 *
	 * @param substi
	 *            the substitution holding for this focus action.
	 *
	 * @return substitution to use for the target module.
	 */
	private Substitution getModuleSubsti(Substitution subst) {
		Substitution modulesubst;
		Module target = this.action.getTarget();
		if (target.getType() == TYPE.ANONYMOUS) {
			// anonymous modules are completely transparent
			modulesubst = (subst == null) ? target.getKRInterface()
					.getSubstitution(null) : subst;
		} else {
			// non-anonymous modules let through only specific vars.
			modulesubst = target.getKRInterface().getSubstitution(null);
			List<Term> moduleparams = target.getParameters();
			for (int i = 0; i < moduleparams.size(); i++) {
				// Assumes that module parameters are variables.
				modulesubst.addBinding((Var) moduleparams.get(i), this.action
						.getParameters().get(i).applySubst(subst));
			}
			// CHECK should we check for non-closed arguments?
		}
		return modulesubst;
	}

	/**
	 * get new goals in case focus=FILTER. Contains all positive goal literals
	 * in the parent rule.
	 *
	 * @param agent
	 *            the agent
	 * @param subst
	 *            the complete substi holding for this focus action
	 * @return new goalbase for the target module
	 * @throws GOALActionFailedException
	 */
	private GoalBase getNewFilterGoals(MentalState mentalstate,
			Debugger debugger, Substitution subst)
					throws GOALActionFailedException {
		MentalModel agentModel = mentalstate.getOwnModel();

		GoalBase newAttentionSet = new GoalBase(mentalstate.getState(),
				mentalstate.getAgentId(), mentalstate.getOwner(), this.action
				.getTarget().getName());

		// get the goals as obtained from the context, and add them to
		// the goalbase
		for (MentalLiteral literal : this.context.getAllLiterals()) {
			// only positive literals can result in new goals
			if (literal == null || !literal.isPositive()) {
				continue;
			}
			// only (positive) goal and a-goal literals can result in
			// new goals
			if (literal instanceof AGoalLiteral
					|| literal instanceof GoalLiteral) {
				Query query = literal.applySubst(subst).getFormula();
				// do not insert if the goal has already been achieved
				if (agentModel.beliefQuery(query, debugger).isEmpty()) {
					// by using the substitution obtained from the
					// action rule, we should have forced the literals
					// to be closed.
					if (!query.isClosed()) {
						throw new GOALActionFailedException("A goal-literal "
								+ "in the condition of rule " + this
								+ " is not "
								+ "closed after applying the subst of the "
								+ "focus action", null);
					}
					// FIXME seems better to just fail application instead of
					// throwing, but how to best do that since we don't return
					// Result here?
					newAttentionSet.insert(query.toUpdate(), debugger);
				}
			}
		}
		return newAttentionSet;
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		this.substitutionToPassOnToModule = subst;
		ModuleCallActionExecutor returned = new ModuleCallActionExecutor(
				(ModuleCallAction) this.action.applySubst(subst));
		returned.setContext(this.context.applySubst(subst));
		return returned;
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}
