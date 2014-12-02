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

import goal.core.agent.AgentId;
import goal.core.kr.KRlanguage;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Update;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalState;
import goal.core.program.Selector;
import goal.core.program.SentenceMood;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.parser.InputStreamPosition;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.errorhandling.exceptions.GOALParseException;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@link SendOnceAction} is a special kind of send action that requires
 * that the message to be sent has not been sent before yet (to the intended
 * recipients). It checks this by inspecting the agent's mail box for
 * corresponding {@code sent} records.
 * <p>
 * If only some of the intended recipients have not yet received the message, it
 * will be sent to those agents only.
 * </p>
 *
 * @author Wouter de Vries
 * @modified K.Hindriks
 */
public class SendOnceAction extends SendAction {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -7704868530651082166L;

	/**
	 * Creates a {@link SendOnceAction} that sends a message (content) to one or
	 * more agents if the message has not yet been sent before.
	 *
	 * @param selector
	 *            The {@link Selector} of this action.
	 * @param mood
	 *            The {@link SentenceMood} of the message.
	 * @param formula
	 *            The content of the message.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public SendOnceAction(Selector selector, SentenceMood mood,
			DatabaseFormula formula, InputStreamPosition source) {
		super(selector, mood, formula, source);
	}

	/**
	 * Returns the name of this {@link SendOnceAction}.
	 *
	 * @return The name of this action as a {@link String} "sendonce".
	 */
	@Override
	public String getName() {
		return "sendonce";
	}

	// We cannot use applySubst in SendAction because we need a result of type
	// SendOnceAction
	// to ensure we call SendOnceAction#determineReceivers when
	// evaluatePrecondition is called.
	// We do not need to override evaluatePrecondition in SendAction as
	// everything after evaluating
	// the precondition of both actions is the same.
	@Override
	public SendOnceAction applySubst(Substitution substitution) {
		SendOnceAction sendonce = new SendOnceAction(this.getSelector()
				.applySubst(substitution), this.getMessage().getMood(), this
				.getMessage().getContent().applySubst(substitution),
				this.getSource());
		sendonce.getMessage().setSender(this.getMessage().getSender());
		sendonce.getMessage().setReceivers(this.getMessage().getReceivers());
		return sendonce;
	}

	/**
	 * The precondition of a {@link SendOnceAction} is that there is a non-empty
	 * set of receivers of the message to be sent that should receive this
	 * message but did not receive it yet; otherwise the precondition fails.
	 *
	 * @param language
	 *            The KR language used for representing the precondition.
	 * @return The {@link MentalStateCond} TODO ... .
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
		// throw new
		// UnsupportedOperationException("getPrecondition of SendOnceAction not supported.");
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
	@Override
	public Set<AgentId> determineReceivers(MentalState mentalState,
			Debugger debugger) {
		Set<AgentId> receivers;
		try {
			receivers = this.getSelector().resolve(mentalState);
		} catch (KRInitFailedException e) {
			throw new GOALActionFailedException(
					"Could not determine receivers: " + e.getMessage(), e);
		}
		Set<AgentId> done = new LinkedHashSet<>();

		// Check which intended recipients already have received the message.
		KRlanguage language = mentalState.getKRLanguage();
		Update update;
		Query query;
		for (AgentId receiver : receivers) {
			try {
				update = language.parseUpdate(getMessage().toString(true,
						receiver));
			} catch (GOALParseException e) {
				throw new GOALActionFailedException(
						"Failed to create record of"
								+ "message to be sent for: "
								+ getMessage().toString(true, receiver) + ".",
								e);
			}
			query = update.toQuery();
			if (!mentalState.query(query, BASETYPE.MAILBOX, debugger).isEmpty()) {
				done.add(receiver);
			}
		}
		// Remove all agents that already received message.
		receivers.removeAll(done);
		return receivers;
	}

}
