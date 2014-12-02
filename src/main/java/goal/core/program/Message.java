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

package goal.core.program;

import goal.core.agent.AgentId;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Var;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A message consists of a sender, a receiver, a {@link SentenceMood} of the
 * message, and the content.
 *
 * @author Wouter de Vries
 * @modified K.Hindriks
 */
public class Message extends ParsedObject {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 5450752303996661675L;
	/**
	 * The name of the agent that sends this message.
	 */
	private AgentId sender = null;
	/**
	 * The name(s) of the agent(s) that should receive this message.
	 */
	private Set<AgentId> receivers = new LinkedHashSet<>();
	/**
	 * The mood of the message; either <i>indicative</i>, <i>imperative</i> or
	 * <i>interrogative</i>.
	 */
	private SentenceMood mood;
	/**
	 * Content of the message to be sent.
	 */
	private DatabaseFormula content;

	/**
	 * Creates a {@link Message} with content and mood. Sender and receiver
	 * cannot be established at compile time and are determined at runtime.
	 *
	 * @param content
	 *            The content of this message.
	 * @param mood
	 *            The mood of this message.
	 * @param source
	 *            The source location in the code of this message.
	 */
	public Message(DatabaseFormula content, SentenceMood mood,
			InputStreamPosition source) {
		super(source);
		this.content = content;
		this.mood = mood;
	}

	/**
	 * Creates a {@link Message} with sender, receiver, content and mood.
	 *
	 * @param sender
	 *            The name of the sender of this message.
	 * @param receiver
	 *            The name of the receiver of this message.
	 * @param content
	 *            The content of this message.
	 * @param mood
	 *            The mood of this message.
	 */
	private Message(AgentId sender, Set<AgentId> receivers,
			DatabaseFormula content, SentenceMood mood,
			InputStreamPosition source) {
		super(source);
		this.sender = sender;
		this.receivers = receivers;
		this.content = content;
		this.mood = mood;
	}

	/**
	 * Returns the name of the agent who sends this {@link Message}.
	 *
	 * @return The name of the sender of this message.
	 */
	public AgentId getSender() {
		return sender;
	}

	/**
	 * Sets the name of the agent who sends this {@link Message}.
	 *
	 * @param sender
	 *            A {@link String} representing the name of the sender of this
	 *            message.
	 */
	public void setSender(AgentId sender) {
		this.sender = sender;
	}

	/**
	 * Returns the name of the agent who should receive this {@link Message}.
	 *
	 * @return The name of the receiver of this message.
	 */
	public Set<AgentId> getReceivers() {
		return receivers;
	}

	/**
	 * Sets the names of agents who should receive this {@link Message}.
	 *
	 * @param receivers
	 *            The set of names of receivers of this message.
	 */
	public void setReceivers(Set<AgentId> receivers) {
		this.receivers = receivers;
	}

	/**
	 * @return The mood of the message.
	 */
	public SentenceMood getMood() {
		return mood;
	}

	/**
	 * @param mood
	 *            The mood of the message.
	 */
	public void setMood(SentenceMood mood) {
		this.mood = mood;
	}

	/**
	 * @return The message content.
	 */
	public DatabaseFormula getContent() {
		return content;
	}

	/**
	 * @param content
	 *            The message content.
	 */
	public void setContent(DatabaseFormula content) {
		this.content = content;
	}

	/**
	 * Returns the (free) variables that occur in the content of this message.
	 *
	 * @return The (free) variables that occur in the content of the message.
	 */
	public Set<Var> getFreeVar() {
		return this.content.getFreeVar();
	}

	/**
	 * A message is considered to be closed if its content is closed, or if it
	 * is an interrogative (and variables are allowed in the message content).
	 *
	 * @return {@code true} if the content of the message does not have any free
	 *         variables, or the message is an interrogative; {@code false}
	 *         otherwise.
	 */
	public boolean isClosed() {
		return this.content.isClosed()
				|| this.mood == SentenceMood.INTERROGATIVE;
	}

	/**
	 * Returns a {@link String} that can be inserted in the mail box of an agent
	 * for representing that this message has been sent.
	 * <p>
	 * Annotates the message content with the message mood in case the mood is
	 * either imperative or interrogative. In the former case, the content is
	 * wrapped inside "imp(...)", whereas in the latter case it is wrapped
	 * inside "int(...)".
	 * </p>
	 *
	 * @param sent
	 *            Is this a sent or received message?
	 * @param receiver
	 *            The receiving agent
	 *
	 * @return a {@link String} representing that the message has been sent.
	 */
	public String toString(boolean sent, AgentId receiver) {
		// #3109 Convert receiver name. (mis)used to insert messages into
		// Database... single quotes are to ensure weird receiver names are
		// handled properly in SWI.
		// ASSUMES: that brackets and commas can be parsed by KR language. See
		// also: SendAction#executeAction. FIXME this is not nice. #
		return (sent ? "sent" : "received") + "('" + receiver.getName() + "', "
		+ this.toString() + ")";
	}

	@Override
	public String toString() {
		// Construct string from content and mood.
		switch (mood) {
		case IMPERATIVE:
			return "imp(" + getContent() + ")";
		case INTERROGATIVE:
			return "int(" + getContent() + ")";
		default: // suppress mood expression for indicatives.
			return getContent().toString();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((mood == null) ? 0 : mood.hashCode());
		result = prime * result
				+ ((receivers == null) ? 0 : receivers.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Message other = (Message) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (mood != other.mood) {
			return false;
		}
		if (receivers == null) {
			if (other.receivers != null) {
				return false;
			}
		} else if (!receivers.equals(other.receivers)) {
			return false;
		}
		if (sender == null) {
			if (other.sender != null) {
				return false;
			}
		} else if (!sender.equals(other.sender)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a copy of this message.
	 *
	 * @return A copy of this message.
	 */
	@Override
	public Message clone() {
		return new Message(this.sender, this.receivers, this.content,
				this.mood, this.getSource());
	}

}
