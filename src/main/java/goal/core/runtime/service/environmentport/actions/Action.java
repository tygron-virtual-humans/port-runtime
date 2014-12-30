package goal.core.runtime.service.environmentport.actions;

import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

import java.io.Serializable;

import nl.tudelft.goal.messaging.Message;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

public abstract class Action implements Serializable {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -1176462250604499600L;
	private Message message;

	/**
	 * @return The message
	 */
	public Message getMessage() {
		return this.message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public abstract Serializable invoke(
			Messages2Environment messages2Environment) throws Exception;

	/**
	 * @return The ID of the message sender
	 * */
	public MessageBoxId getSender() {
		return this.message.getSender();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
