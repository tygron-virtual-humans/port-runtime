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

import goal.core.mentalstate.MentalState;
import goal.core.program.SelectExpression;
import goal.core.program.SelectExpression.SelectorType;
import goal.core.program.Selector;
import goal.parser.InputStreamPosition;

/**
 * Parent class for all actions that *only* modify the {@link MentalState} of an
 * agent and do *not* have external side effects outside the multi-agent system
 * itself.
 * <p>
 * A mental action has a {@link Selector} that indicates the agent whose mental
 * model is affected, or the agent(s) to whom a message should be sent.
 * </p>
 * <p>
 * The mental actions include:
 * <ul>
 * <li>The adopt action {@link AdoptAction}</li>
 * <li>The adoptone action {@link AdoptOneAction}</li>
 * <li>The drop action {@link DropActionExecutor}</li>
 * <li>The insert action {@link InsertAction}</li>
 * <li>The delete action {@link DeleteAction}</li>
 * <li>The send action {@link SendAction}</li>
 * <li>The sendonce action {@link SendOnceActionExecutor}</li>
 * </ul>
 * </p>
 *
 * @author N.Kraayenbrink
 * @modified K.Hindriks
 */
public abstract class MentalAction extends Action {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -4606386254461534028L;
	/**
	 * The selector indicating on which mental model this action should be
	 * executed.
	 */
	private Selector selector;

	/**
	 * Creates a new {@link MentalAction}.
	 *
	 * @param name
	 *            The name of the action.
	 * @param selector
	 *            The selector indicating on which mental model(s) this action
	 *            should be executed. Can be {@code null}, in which case 'this'
	 *            is assumed.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	protected MentalAction(String name, Selector selector,
			InputStreamPosition source) {
		super(name, source);
		if (selector == null) {
			this.selector = new Selector(source);
			this.selector.add(new SelectExpression(SelectorType.THIS));
		} else {
			this.selector = selector;
		}
	}

	/**
	 * Returns the {@link Selector} for this {@link MentalAction}.
	 *
	 * @return The selector for this action.
	 */
	public Selector getSelector() {
		return this.selector;
	}
}
