package goal.core.runtime.service.environmentport.environmentport.events;

import java.util.Collection;

/**
 * event to indicate free entity. TODO how to use this.
 *
 * @author W.Pasman
 */
public class FreeEntityEvent extends EnvironmentEvent {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6387717146943403919L;
	private final String entity;
	private final Collection<String> agents;
	private final String type;

	public FreeEntityEvent(String entity, Collection<String> agents, String type) {
		this.entity = entity;
		this.agents = agents;
		this.type = type;
	}

	/**
	 * @return The entity
	 */
	public String getEntity() {
		return this.entity;
	}

	/**
	 * @return The agents
	 */
	public Collection<String> getAgents() {
		return this.agents;
	}

	/**
	 * @return The type of the free entity
	 */
	public String getType() {
		return this.type;
	}
}