package goal.core.agent;

import nl.tudelft.goal.messaging.messagebox.MessageBoxId;

/**
 * DOC what is this? Unique ID for agents? We should not extend MessagBoxId
 * #2775
 *
 * @author Koen
 *
 */
public class AgentId extends MessageBoxId {

	/**
	 *
	 */
	private static final long serialVersionUID = 6255340990573465944L;

	private final String name;

	/**
	 * Constructs a new AgentId.
	 *
	 * @param name
	 *            unique name of the agent
	 * @deprecated you should not use this, as agent names always should match
	 *             the messaging name.
	 */

	@Deprecated
	public AgentId(String name) {
		this.name = name;
	}

	/**
	 * Construcsts a new AgentId.
	 *
	 * @param id
	 *            unique ID of this agents message box
	 */
	public AgentId(MessageBoxId id) {
		this(id.getName());
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Type getType() {
		return Type.GOALAGENT;
	}

}
