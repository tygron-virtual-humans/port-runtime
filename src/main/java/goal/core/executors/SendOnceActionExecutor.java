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

import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.SendOnceAction;

/**
 * The {@link SendOnceActionExecutor} is a special kind of send action that
 * requires that the message to be sent has not been sent before yet (to the
 * intended recipients). It checks this by inspecting the agent's mail box for
 * corresponding {@code sent} records.
 * <p>
 * If only some of the intended recipients have not yet received the message, it
 * will be sent to those agents only.
 * </p>
 *
 * @author Wouter de Vries
 * @modified K.Hindriks
 */
public class SendOnceActionExecutor extends SendActionExecutor {

	private SendOnceAction action;

	public SendOnceActionExecutor(SendOnceAction act) {
		action = act;
	}

	@Override
	public Action getAction() {
		return action;
	}

}
