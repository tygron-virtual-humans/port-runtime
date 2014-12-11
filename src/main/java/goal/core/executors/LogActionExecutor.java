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
import goal.tools.debugger.Debugger;
import krTools.language.Substitution;
import krTools.language.Term;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.LogAction;

public class LogActionExecutor extends ActionExecutor {
	private final LogAction action;

	public LogActionExecutor(LogAction act) {
		this.action = act;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		for (final Term param : this.action.getParameters()) {
			boolean bb = false, gb = false, kb = false, mb = false, pb = false;
			switch (LogOptions.fromString(param.toString())) {
			case BB:
				bb = true;
				break;
			case GB:
				gb = true;
				break;
			case KB:
				kb = true;
				break;
			case MB:
				mb = true;
				break;
			case PB:
				pb = true;
				break;
			default:
			case TEXT:
				runState.doLog(param.toString());
				break;
			}
			if (kb || bb || pb || mb || gb) {
				String ms = mentalState.toString(kb, bb, pb, mb, gb, true);
				runState.doLog(ms);
			}
		}

		// Report action was performed.
		report(debugger);

		return new Result(this.action);
	}

	/**
	 * Available options for logging.
	 *
	 * @author W.Pasman
	 * @modified K.Hindriks
	 */
	private enum LogOptions {
		/** export belief base */
		BB,
		/** export goal base */
		GB,
		/** export percept base */
		PB,
		/** export mailbox */
		MB,
		/** export knowledge base */
		KB,
		/** export plain text */
		TEXT;

		/**
		 * Maps a string to an {@link LogOptions}.
		 *
		 * @param type
		 *            A string representing the type of logging to be done.
		 * @return The action type that corresponds with the parameter.
		 */
		public static LogOptions fromString(String type) {
			try {
				return valueOf(type.toUpperCase());
			} catch (Exception e) {
				return TEXT;
			}
		}
	}

	@Override
	protected ActionExecutor applySubst(Substitution subst) {
		return new LogActionExecutor(this.action.applySubst(subst));
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}
