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

package goal.core.program.actions;

import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalModel;
import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.ArrayList;
import java.util.List;

import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.Module;
import languageTools.program.agent.Module.FocusMethod;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.msc.AGoalLiteral;
import languageTools.program.agent.msc.GoalLiteral;
import languageTools.program.agent.msc.MentalLiteral;

/**
 * Action that makes the agent focus on a module, using one of four methods;
 * <ol>
 * <li>When the action is executed, and the agent focuses on the module, the new
 * attention set will consist of one goal, which validates the entire context of
 * the {@link Module}.<br>
 * To use this method, focus on a Module with the {@link FocusMethod#SELECT}
 * focus method.<br>
 * <br>
 * Note that this method has better performance, as no new databases need to be
 * created due to the context. (there still might be some module-specific goals
 * to be created however)<br>
 * <br>
 * </li>
 * <li>When the action is executed, and the agent focuses on the module, the new
 * attention set will consist of one goal for each of the positive
 * {@link GoalLiteral} and {@link AGoalLiteral}s in the instantiated context.<br>
 * To use this method, focus on a Module with the {@link FocusMethod#FILTER}
 * focus method.<br>
 * <br>
 * Note that this method makes the agent forget any information on its goals on
 * a lower level than the information provided in the positive goal and a-goal
 * literals. Eg (in blocksworld): a context with only
 * <code>a-goal(tower[T])</code> will make the agent forget that its original
 * goal was a conjunction of <code>on(.,.)</code> predicates.</li>
 * <li>When the action is executed, and the agent focuses on the module, the new
 * attention set will be empty.<br>
 * To use this method, focus on a Module with the {@link FocusMethod#NEW} focus
 * method.</li>
 * <li>When the action is executed, and the agent focuses on the module, the new
 * attention set will be the same attention set as before (aside from any added
 * goals from the <code>goals { }</code> section). This means that any changes
 * in the attention set will be propagated back to the parent module when the
 * agent de-focuses.<br>
 * To use this method, focus on a Module with the {@link FocusMethod#NONE} focus
 * method.
 * </ol>
 * Note that regardless of the focus method, the new attention set will always
 * contain the goals defined in the Module's <code>goals { }</code> section.
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class ModuleCallActionExecutor extends ActionExecutor {

	private ModuleCallAction action;

	public ModuleCallActionExecutor(ModuleCallAction act) {
		action = act;
	}

	/**
	 * Applies the substitution to the parameters of the target module (only).
	 * <p>
	 * Does not apply the substitution to the target module itself. Instead
	 * substitution is passed on to module itself via
	 * {@link #run(RunState, Substitution)}.
	 * </p>
	 *
	 * @return Instantiated focus action, where free variables in parameters
	 *         have been substituted with terms from the substitution.
	 */
	@Override
	public ActionExecutor applySubst(Substitution substitution) {
		List<Term> parameters;
		if (action.getTarget().isAnonymous()) {
			parameters = new ArrayList<>(0);
		} else {
			parameters = new ArrayList<>(action.getParameters().size());
			for (Term term : action.getParameters()) {
				parameters.add(term.applySubst(substitution));
			}
		}

		// Create new focus action with instantiated parameters.
		ModuleCallAction focus = new ModuleCallAction(getTarget(),
				this.parentRule, parameters, getSource());
		// Store substitution for later reference when we call the target
		// module.
		focus.substitutionToPassOnToModule = substitution;

		return focus;
	}

	/**
	 * Checks whether the precondition of {@link ModuleCallAction} holds. See
	 * also: {@link ModuleCallAction#getPrecondition(KRlanguage)}.
	 *
	 * @return {@code true} since entering module has "empty" precondition.
	 */
	@Override
	public ModuleCallActionExecutor evaluatePrecondition(MentalState runState,
			Debugger debugger, boolean last) {
		debugger.breakpoint(Channel.CALL_MODULE, this,
				"Going to enter module: %s.", action.getTarget().getName());
		return this;
	}

	/**
	 * Executes the {@link ModuleCallAction} by entering and initializing the
	 * target module and running that module.
	 * <p>
	 * Depending on the module's focus options a new attention set needs to be
	 * created and a goal may need to be added to that attention set.
	 * </p>
	 */
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
		Result result = getTarget().executeFully(runState, moduleSubstitution);
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
		switch (targetModule.getFocusMethod()) {
		case NEW:
			// Create new empty goal base to construct a new attention set.
			return new GoalBase(mentalstate.getKRLanguage(),
					mentalstate.getAgentId(), targetModule.getName());
		case SELECT:
			GoalBase newAttentionSet = new GoalBase(
					mentalstate.getKRLanguage(), mentalstate.getAgentId(),
					targetModule.getName());
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
		Module target = action.getTarget();
		if (target.isAnonymous()) {
			// anonymous modules are completely transparent
			modulesubst = subst;
		} else {
			// non-anonymous modules let through only specific vars.
			modulesubst = target.getKRLanguage().getEmptySubstitution();
			List<Term> moduleparams = target.getParameters();

			for (int i = 0; i < moduleparams.size(); i++) {
				// Assumes that module parameters are variables.
				modulesubst.addBinding((Var) moduleparams.get(i), parameters
						.get(i).applySubst(subst));
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

		GoalBase newAttentionSet = new GoalBase(mentalstate.getKRLanguage(),
				mentalstate.getAgentId(), targetModule.getName());

		// get the goals as obtained from the context, and add them to
		// the goalbase
		for (MentalLiteral literal : parentRule.getCondition().getLiterals()) {
			// only positive literals can result in new goals
			if (!literal.isPositive()) {
				continue;
			}

			// only (positive) goal and a-goal literals can result in
			// new goals
			if (literal instanceof AGoalLiteral
					|| literal instanceof GoalLiteral) {
				Query formula = literal.applySubst(subst).getFormula();
				// do not insert if the goal has already been achieved
				if (agentModel.beliefQuery(formula, debugger).isEmpty()) {
					// by using the substitution obtained from the
					// action rule, we should have forced the literals
					// to be closed.
					if (!formula.isClosed()) {
						throw new GOALActionFailedException("A goal-literal "
								+ "in the condition of rule " + this
								+ " is not "
								+ "closed after applying the subst of the "
								+ "focus action", null);
					}
					// FIXME seems better to just fail application instead of
					// throwing.
					// But how to best do that since we don't return Result
					// here.

					newAttentionSet.insert(formula.toUpdate(), debugger);
				}
			}
		}
		return newAttentionSet;
	}

	@Override
	public String toString() {
		if (this.getTarget().isAnonymous()) {
			return "{ ... }";
		}
		StringBuilder builder = new StringBuilder();

		builder.append(this.targetModule.getName());

		if (this.parameters.size() > 0) {
			builder.append("(");
			builder.append(this.parameters.get(0).toString());
			for (int i = 1; i < this.parameters.size(); i++) {
				builder.append(",");
				builder.append(this.parameters.get(i).toString());
			}
			builder.append(")");
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result
				+ ((parentRule == null) ? 0 : parentRule.hashCode());
		result = prime * result
				+ ((targetModule == null) ? 0 : targetModule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModuleCallAction other = (ModuleCallAction) obj;
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (parentRule == null) {
			if (other.parentRule != null) {
				return false;
			}
		} else if (!parentRule.equals(other.parentRule)) {
			return false;
		}
		if (targetModule == null) {
			if (other.targetModule != null) {
				return false;
			}
		} else if (!targetModule.equals(other.targetModule)) {
			return false;
		}
		return true;
	}

}
