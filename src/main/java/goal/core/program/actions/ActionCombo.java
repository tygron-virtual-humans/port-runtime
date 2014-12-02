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

import goal.core.kr.language.Substitution;
import goal.core.mentalstate.MentalState;
import goal.core.program.Module.FocusMethod;
import goal.core.program.rules.Rule;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * An {@link ActionCombo} is a list of actions that have been combined by the +
 * operator in a {@link Rule} of an agent program.
 * <p>
 * An action combo is executed whenever the first action in the sequence can be
 * performed, i.e., the precondition of that action holds. Preconditions of
 * other actions are only inspected after the actions that precede the actions
 * in the list have been executed. If a precondition that is evaluated fails,
 * the execution of the action combo is terminated and the remaining actions are
 * not performed.
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public class ActionCombo extends ParsedObject implements Iterable<Action> {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -6637427330237052342L;
	/**
	 * A list of ordered actions that are part of this {link ActionCombo}.
	 */
	private final List<Action> actions;

	/**
	 * Creates an {@link ActionCombo} with a single {@link Action}.
	 *
	 * @param action
	 *            A single action to add to this combo
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public ActionCombo(Action action, InputStreamPosition source) {
		super(source);
		this.actions = new ArrayList<>(1);
		this.actions.add(action);
	}

	/**
	 * Creates an {@link ActionCombo} from a list of {@link Action}s.
	 *
	 * @param actions
	 *            The {@link Action}s that should be part of this new action
	 *            combo.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public ActionCombo(Collection<? extends Action> actions,
			InputStreamPosition source) {
		super(source);
		if (actions.size() > 0) {
			this.actions = new ArrayList<>(actions);
		} else {
			throw new UnsupportedOperationException(
					"List of actions is empty, "
							+ "but cannot create an empty ActionCombo.");
		}
	}

	/**
	 * Returns the {@link Action}s of this {@link ActionCombo}.
	 *
	 * @return The actions that are part of this action combo.
	 */
	public Collection<? extends Action> getActions() {
		return actions;
	}

	/**
	 * Adds an {@link Action} to the end of the list of actions that are part of
	 * this {@link ActionCombo}.
	 *
	 * @param action
	 *            The action to be added.
	 */
	public void addAction(Action action) {
		this.actions.add(action);
	}

	/**
	 * Returns the (index)th {@link Action} in this {@link ActionCombo}.
	 *
	 * @param index
	 *            The index into the action combo.
	 * @return The (index)th action of this action combo.
	 */
	public Action getAction(int index) {
		return this.actions.get(index);
	}

	/**
	 * Replaces the (index)th {@link Action} in this {@link ActionCombo} by the
	 * parameter action.
	 *
	 * @param index
	 *            The index into the action combo where the action will be
	 *            inserted.
	 * @param action
	 *            The action that will replace the (index)th action.
	 */
	public void setAction(int index, Action action) {
		this.actions.set(index, action);
	}

	/**
	 * Checks whether one of the action in the {@link ActionCombo} is a call to
	 * a module, i.e., a {@link ModuleCallAction} with associated
	 * {@link FocusMethod#FILTER}.
	 *
	 * @return {@code true} if at least one of the actions in the action combo
	 *         is a call to a module, i.e., a focus action, with filter as
	 *         associated focus method.
	 */
	public boolean hasFilterFocusAction() {
		for (Action action : this) {
			if (action instanceof ModuleCallAction) {
				if (((ModuleCallAction) action).getTarget().getFocusMethod() == FocusMethod.FILTER) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Applies the given substitution to this {@link ActionCombo} by applying it
	 * to each of the {@link Action}s that are part of this combo.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The instantiated combo action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	public ActionCombo applySubst(Substitution substitution) {
		List<Action> instantiatedActions = new ArrayList<>(actions.size());
		for (Action action : actions) {
			instantiatedActions.add(action.applySubst(substitution));
		}
		return new ActionCombo(instantiatedActions, this.getSource());
	}

	/**
	 * Searches linearly through the precondition(s) of the first action in this
	 * action combo until it finds one that holds. ALL instances of the combo
	 * for which that precondition holds are returned.
	 * <p>
	 * Note that preconditions of second, third, etc. actions in the combo are
	 * NOT evaluated. An action combo can be performed if (one of) the
	 * precondition(s) of the first action holds; if preconditions of other
	 * actions in the combo fail when they are evaluated, execution of the acion
	 * combo is simply terminated.
	 * </p>
	 *
	 * @param mentalState
	 *            The run state in which the precondition(s) is (are) evaluated.
	 * @param debugger
	 *            DOC
	 * @return A list of all available options for this action, i.e., instances
	 *         for which a precondition (the first found) of the first action
	 *         holds.
	 */
	public List<ActionCombo> getOptions(MentalState mentalState,
			Debugger debugger) {
		List<ActionCombo> options = new LinkedList<>();
		Set<Substitution> solutions;

		// Get the first action from the list of actions of this combo.
		Action firstaction = this.actions.get(0);

		// TWO CASES: Distinguish user-specified actions from all other actions.
		if (firstaction instanceof UserSpecAction) {
			// USER-SPECIFIED ACTION MAY HAVE MULTIPLE ACTION SPECS
			// (PRECONDITIONS).
			UserSpecAction userspec = (UserSpecAction) firstaction;

			// Find the first action specification whose precondition holds.
			solutions = userspec.getOptions(mentalState, debugger);

			// If solutions were found, return list of instantiated action
			// combos where
			// all action specifications other than the one found have been
			// removed.
			if (!solutions.isEmpty()) {
				// Create new action which only has the action specification
				// found
				// by calling #getOptions(runState).
				UserSpecAction singleActionSpec = userspec
						.getSelectedActionSpec();
				// Create action combo using new user specified action.
				ActionCombo option = new ActionCombo(singleActionSpec,
						singleActionSpec.getSource());
				// Add other actions that follow first action.
				for (int i = 1; i < this.actions.size(); i++) {
					option.addAction(this.actions.get(i));
				}
				// Create instantiations and add to options.
				for (Substitution substitution : solutions) {
					// Check if first action is closed.
					if (singleActionSpec.applySubst(substitution).isClosed()) {
						options.add(option.applySubst(substitution));
					} else {
						throw new GOALActionFailedException(
								"Attempt to execute "
										+ option.applySubst(substitution)
										+ " @" + this.getSource()
										+ " with free variables.");
					}
				}
			}
		} else {
			// ALL ACTIONS OTHER THAN USER-SPECIFIED ACTIONS.
			// These actions have a single precondition, and, if the
			// precondition
			// holds and the action is closed, the action is an option (and so
			// is
			// the action combo).

			// Evaluate the precondition of first action in combo.
			if (firstaction.evaluatePrecondition(mentalState, debugger, false) != null) {
				// If action is not closed throw exception.
				if (firstaction.isClosed()) {
					// Action is an option, add the combo as option.
					options.add(this);
				} else {
					throw new GOALActionFailedException("Attempt to execute "
							+ this + " @" + this.getSource()
							+ " with free variables.");
				}
			}
		}

		return options;
	}

	/**
	 * Performs the {@link ActionCombo}.
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 * @param substitution
	 *            The substitution provided by the action's context.
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return The {@link Result} of this action.
	 */
	public Result run(RunState<?> runState, Substitution substitution,
			boolean last) {
		Result comboResult = new Result();

		for (Action action : this) {
			// FIXME is this ok if action is a ModuleCallAction??
			Result result = action.run(runState, substitution,
					runState.getDebugger(), last);
			comboResult.merge(result);
			// If module needs to be terminated, i.e., {@link ExitModuleAction}
			// has been performed, then exit execution of combo action.
			// Also, stop executing combo if last action failed, but not if
			// action was {@link FocusAction}, i.e., entering of a module.
			if ((!comboResult.justPerformedAction() && !(action instanceof ModuleCallAction))
					|| comboResult.isModuleTerminated()) {
				break;
			}
		}

		runState.getDebugger().breakpoint(Channel.ACTIONCOMBO_FINISHED, this,
				"Performed %s.", comboResult.getActions());

		return comboResult;
	}

	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		if (this.actions.size() > 0) {
			sBuilder.append(this.actions.get(0).toString());
			for (int i = 1; i < this.actions.size(); i++) {
				sBuilder.append(" + " + this.actions.get(i).toString());
			}
		}
		return sBuilder.toString();
	}

	@Override
	public Iterator<Action> iterator() {
		return this.actions.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
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
		ActionCombo other = (ActionCombo) obj;
		if (actions == null) {
			if (other.actions != null) {
				return false;
			}
		} else if (!actions.equals(other.actions)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true when all actions are closed. An action is closed when it has
	 * no free variables.
	 *
	 * @return true when all actions are closed
	 */
	public boolean isClosed() {
		for (Action action : actions) {
			if (!action.isClosed()) {
				return false;
			}
		}

		return true;
	}

}
