package goal.core.runtime.service.environmentport.environmentport.events;

import java.util.Collection;

/**
 * event to indicate deleted entity.
 *
 * @author W.Pasman
 */
public class DeletedEntityEvent extends EnvironmentEvent {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6387717146943403919L;
	private final String entity;
	private final Collection<String> agents;

	public DeletedEntityEvent(String entity, Collection<String> agents) {
		this.entity = entity;
		this.agents = agents;
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

}