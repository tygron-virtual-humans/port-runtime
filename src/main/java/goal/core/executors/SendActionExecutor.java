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

import goal.core.agent.Agent;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.Set;

import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.DatabaseFormula;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.actions.SendAction;
import languageTools.program.agent.msg.Message;
import languageTools.program.agent.msg.SentenceMood;

/**
 * Sends a message to one or more {@link Agent}s.
 * <p>
 * A {@link Selector} is used to indicate to which agent(s) the message should
 * be sent.
 * </p>
 * <p>
 * A message can have a {@link SentenceMood} and has content. The content of a
 * message is represented as a {@link DatabaseFormula} as it should be possible
 * to store a message in a database (i.e., the agent's mail box).
 * </p>
 *
 * @author K.Hindriks
 * @author W.Pasman
 */
public class SendActionExecutor extends ActionExecutor {

	private SendAction action;

	public SendActionExecutor(SendAction act) {
		action = act;
	}

	/**
	 * Checks whether the precondition of {@link SendActionExecutor} holds. See
	 * also: {@link SendActionExecutor#getPrecondition(KRlanguage)}.
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
	 * @return always enabled. CHECK original code javadoc said "This action if
	 *         a non-empty set of receivers are associated with the message;
	 *         {@code null} otherwise." However in the code it was always
	 *         enabled.
	 */
	@Override
	public SendActionExecutor evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;

	}

	/**
	 * Returns the list of agent names that should receive the message.
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the action is executed.
	 * @param debugger
	 *            The current debugger
	 * @return A list of agents that should receive the message.
	 */
	public Set<AgentId> determineReceivers(MentalState mentalState,
			Debugger debugger) {
		try {
			return action.getSelector().resolve(mentalState);
		} catch (KRInitFailedException e) {
			throw new GOALActionFailedException(
					"Could not determine receivers: " + e.getMessage(), e);
		}
	}

	/**
	 * Executes this {@link SendActionExecutor}.
	 * <p>
	 * First computes the set of agents that should receive the message by
	 * resolving the {@link Selector} and then constructs a {@link Message} for
	 * each agent that should receive the message. For each message constructed
	 * a corresponding 'sent' record is inserted into the mail box of the
	 * sender.
	 * </p>
	 * <p>
	 * Finally sends the messages.
	 * </p>
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		Set<AgentId> receivers = determineReceivers(mentalState, debugger);
		Message message = action.getMessage();

		message.setReceivers(receivers);
		message.setSender(mentalState.getAgentId());

		// Send message.
		runState.postMessage(message);

		mentalState.getOwnBase(BASETYPE.MAILBOX).insert(message, false,
				debugger);
		// TODO: implement functionality below but then efficiently!!
		// Identifier eisname = new Identifier(receiver.getName());
		// try{
		// // Get the KR language used for representing the content.
		// language = this.message.getContent().getLanguage();
		// } catch (KRInitFailedException e) {
		// throw new GOALActionFailedException("Failed to get receiver name "
		// + " due to KR language failure: " + e.getMessage(), e);
		// }
		// AgentId name = new
		// AgentId(language.ConvertEISParameterToTerm(eisname).toString());
		//
		// // Create 'sent' formula to be inserted into mail box.
		// try{
		// sent = language.parseDBFormula(this.message.toString(true, name));
		// } catch (GOALParseException e) {
		// throw new GOALActionFailedException("Failed to create record of"
		// + "message to be sent for: " + this.message.toString(true, name) +
		// ".", e);
		// }

		// Check if goals have been achieved and, if so, update goal base.
		mentalState.updateGoalState(debugger); // TRAC #749

		// Report action was performed.
		report(debugger);

		return new Result(action);
	}

}
