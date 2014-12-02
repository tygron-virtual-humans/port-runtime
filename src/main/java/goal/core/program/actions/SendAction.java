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
import goal.core.agent.AgentId;
import goal.core.kr.KRlanguage;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalState;
import goal.core.program.Message;
import goal.core.program.Selector;
import goal.core.program.SentenceMood;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.errorhandling.exceptions.GOALParseException;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
 */
public class SendAction extends MentalAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -7425339684731979382L;
	/**
	 * Message to be sent.
	 */
	private final Message message;

	/**
	 * Creates a {@link SendAction} that sends a message (content) to one or
	 * more agents.
	 *
	 * @param selector
	 *            The {@link Selector} of this action, indicating where the
	 *            message should be send to.
	 * @param mood
	 *            The {@link SentenceMood} of the message.
	 * @param content
	 *            The content of the message.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public SendAction(Selector selector, SentenceMood mood,
			DatabaseFormula content, InputStreamPosition source) {
		super("send", selector, source);
		InputStreamPosition contentSource = source;
		if (content != null) {
			contentSource = content.getSource();
		}
		this.message = new Message(content, mood, contentSource);
	}

	/**
	 * Returns the message of this send action.
	 *
	 * @return The message of this send action.
	 */
	public Message getMessage() {
		return this.message;
	}

	/**
	 * Returns the (free) variables that occur in the content of the message to
	 * be sent by this {@link SendAction} and the selector of this action.
	 *
	 * @return The (free) variables that occur in the selector and message
	 *         content of the action.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = this.message.getFreeVar();
		freeVars.addAll(this.getSelector().getFreeVar());
		return freeVars;
	}

	/**
	 * Checks whether send(once) action is considered closed. This is the case
	 * if the selector is closed and if the message is considered closed. The
	 * latter is the case if either the message does not contain any (free)
	 * variables or the message is of type interrogative, i.e., a question. See:
	 * {@link Message#isClosed()}.
	 *
	 * @return {@code true} if the send(once) action is considered closed;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean isClosed() {
		return this.getSelector().isClosed() && this.message.isClosed();
	}

	/**
	 * Applies the given {@link Substitution} to this {@link SendAction} by
	 * applying the substitution to the message content to be sent and to the
	 * {@link Selector} of this {@link SendAction}.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return The instantiated adopt action where (free) variables that are
	 *         bound by the substitution have been instantiated by the
	 *         corresponding terms in the substitution.
	 */
	@Override
	public SendAction applySubst(Substitution substitution) {
		SendAction send = new SendAction(this.getSelector().applySubst(
				substitution), this.message.getMood(), this.message
				.getContent().applySubst(substitution), this.getSource());
		send.getMessage().setSender(this.message.getSender());
		send.getMessage().setReceivers(this.message.getReceivers());
		return send;
	}

	/**
	 * The precondition of a {@link SendAction} is {@code true}, or, to be more
	 * precise "bel(true)"; this means it can always be performed if the action
	 * is closed.
	 *
	 * @param language
	 *            The KR language used for representing the precondition.
	 * @return The {@link MentalStateCond} "bel(true)".
	 * @throws GOALParseException
	 *             In case the KR parser had a problem parsing "true".
	 */
	@Override
	public MentalStateCond getPrecondition(KRlanguage language)
			throws GOALParseException {
		Query query;
		query = language.parseUpdate("true").toQuery();
		List<MentalFormula> formulaList = new ArrayList<>(1);
		Selector selector = new Selector(null);
		formulaList.add(new BelLiteral(true, query, selector, getSource()));
		return new MentalStateCond(formulaList, getSource());
	}

	/**
	 * Checks whether the precondition of {@link SendAction} holds. See also:
	 * {@link SendAction#getPrecondition(KRlanguage)}.
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
	 * @return This action if a non-empty set of receivers are associated with
	 *         the message; {@code null} otherwise.
	 */
	@Override
	public SendAction evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		// Determine receivers of message.
		Set<AgentId> receivers = determineReceivers(mentalState, debugger);
		getMessage().setReceivers(receivers);
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
			return this.getSelector().resolve(mentalState);
		} catch (KRInitFailedException e) {
			throw new GOALActionFailedException(
					"Could not determine receivers: " + e.getMessage(), e);
		}
	}

	/**
	 * Executes this {@link SendAction}.
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

		// Set sender of message.
		this.message.setSender(mentalState.getAgentId());

		// Send message.
		runState.postMessage(this.message);

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

		return new Result(this);
	}

	@Override
	public String toString() {
		return String.format("%1$s(%2$s, %3$s%4$s)", this.getName(), this
				.getSelector().toString(), this.message.getMood().toString(),
				this.message.getContent().toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		SendAction other = (SendAction) obj;
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		return true;
	}

}
