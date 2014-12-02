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

import goal.core.kr.KRlanguage;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.mentalstate.MentalState;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.errorhandling.exceptions.GOALParseException;

import java.util.Set;

/**
 * Abstract class for the actions that a GOAL agent can perform.
 * <p>
 * There are two types of actions: so-called <i>built-in</i> (also called
 * reserved) actions and so-called <i>user-specified</i> actions. Adopting and
 * dropping a goal, inserting and deleting beliefs, and sending a message are
 * examples of built-in actions.
 * </p>
 * <p>
 * By default, whenever an agent is connected to an environment, a
 * user-specified action is sent to that environment for execution. A programmer
 * can indicate using the "@int" option (inserted directly after the action name
 * and parameters in the action specification) that an action should NOT be sent
 * to an environment.
 * </p>
 * <p>
 * Every action has a precondition and a postcondition. A precondition specifies
 * the conditions that need to hold to be able to perform the action
 * (successfully); a postcondition specifies the (expected) effects of the
 * action. Only user-specified actions may have <i>multiple</i> action
 * specifications, i.e., preconditions and corresponding postconditions.
 * </p>
 *
 * @author K.Hindriks
 * @author N.Kraayenbrink {@link #executeAction(MentalState, SteppingDebugger)}
 *         now returns the executed action, and {@link #getActionType()} has
 *         been added. (19nov09)
 * @modified W.Pasman 27jul10 so that Action objects can be sent over the
 *           middleware
 * @modified K.Hindriks
 */
public abstract class Action extends ParsedObject {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -764269429295835352L;
	/**
	 * The name of the action.
	 */
	private final String actionName;

	/**
	 * Creates an "empty" action with a reference to a source location.
	 *
	 * @param name
	 *            The name of the action.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public Action(String name, InputStreamPosition source) {
		super(source);
		this.actionName = name;
	}

	/**
	 * Returns the name of this {@link Action}.
	 *
	 * @return The name of the action.
	 */
	public String getName() {
		return this.actionName;
	}

	/**
	 * Returns the set of (free) variables that occur in this {@link Action}'s
	 * parameters.
	 *
	 * @return The set of variables that occur (free) in the parameters of the
	 *         action.
	 */
	public abstract Set<Var> getFreeVar();

	/**
	 * Checks whether this action is closed. An action is closed if none of its
	 * parameters have any occurrences of (free) variables.
	 *
	 * @return {@code true} if all parameters of the action are closed;
	 *         {@code false} otherwise.
	 */
	public boolean isClosed() {
		return getFreeVar().isEmpty();
	}

	/**
	 * Creates a new {@link Action} by applying the {@link Substitution} to it,
	 * i.e. by replacing any free variables in the action's parameters that also
	 * occur in the substitution by the corresponding terms.
	 *
	 * @param substitution
	 *            The substitution to apply
	 *
	 * @return Instantiated action which is the result of applying the
	 *         substitution to this action.
	 */
	public abstract Action applySubst(Substitution substitution);

	/**
	 * Returns the precondition for this {@link Action}. The action is
	 * applicable only if the {@link MentalStateCond} has at least one solution.
	 * <p>
	 * Does not support multiple specifications for actions.
	 * </p>
	 *
	 * @param language
	 *            The KR language that is used for representing the action's
	 *            precondition.
	 * @return A {@link MentalStateCond} that represents the action's
	 *         precondition; or {@code null} if the precondition of the action
	 *         does not depend on the agent's {@link MentalState}.
	 * @throws GOALParseException
	 *             If a parser error is generated when constructing the the
	 *             precondition of {@link DeleteAction},
	 */
	public abstract MentalStateCond getPrecondition(KRlanguage language)
			throws GOALParseException;

	/**
	 * Checks whether the precondition of this {@link Action} holds and, if this
	 * is the case, returns the action selected for execution.
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the precondition is
	 *            evaluated.
	 * @param debugger
	 *            The current debugger
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return The action that is selected for execution if the precondition
	 *         holds; {@code null} otherwise.
	 */
	protected abstract Action evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last);

	/**
	 * Performs the {@link Action}. First applies the given {@link Substitution}
	 * to the action and checks whether the precondition holds and the action is
	 * closed. If it is closed, the action is executed using
	 * {@link #executeAction(RunState)}.
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 * @param substitution
	 *            The substitution provided by the action's context.
	 * @param debugger
	 *            the debugger to be used. #2813 we need this to handle calls
	 *            from the IDE to execute actions. Normally you set this to
	 *            {@link RunState#getDebugger()}.
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return The result of the action that was performed.
	 * @throws GOALActionFailedException
	 *             If the action is not closed, action is internal but this is
	 *             not indicated in the program, or other exeptions occurred.
	 */
	public final Result run(RunState<?> runState, Substitution substitution,
			Debugger debugger, boolean last) {
		Result result = new Result();

		// Apply the substitution provided by the (module and rule condition)
		// context.
		Action instantiatedAction = this.applySubst(substitution);

		// Evaluate the precondition of this {@link Action}.
		Action action = instantiatedAction.evaluatePrecondition(
				runState.getMentalState(), debugger, last);
		boolean canPerformAction = (action != null);

		if (canPerformAction) {
			// Check if action is closed.
			if (action.isClosed()) {
				debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION, action,
						"Precondition of %s holds", action);
				// Perform the action if precondition holds.
				result.merge(action.executeAction(runState, debugger));
			} else {
				throw new GOALActionFailedException(
						"Attempt to execute action " + action + "@"
								+ this.getSource() + " with free variables.");
			}
		} else {
			debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION, this,
					"Precondition of %s does not hold", instantiatedAction);
		}

		return result;
	}

	/**
	 * Executes the {@link Action}.
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 * @param debugger
	 *            The current debugger.
	 * @return The action that has been executed; never returns {@code null} but
	 *         throws runtime exceptions if executing the action fails.
	 */
	protected abstract Result executeAction(RunState<?> runState,
			Debugger debugger);

	/**
	 * Reports that action was performed on either the channel for reporting
	 * built-in or user specified actions.
	 *
	 * @param debugger
	 *            The current debugger.
	 */
	protected final void report(Debugger debugger) {
		boolean builtin = !(this instanceof UserSpecAction);
		debugger.breakpoint(builtin ? Channel.ACTION_EXECUTED_BUILTIN
				: Channel.ACTION_EXECUTED_USERSPEC, this, "Performed %s.", this);
	}

}