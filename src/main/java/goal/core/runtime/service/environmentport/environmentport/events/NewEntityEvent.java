package goal.core.runtime.service.environmentport.environmentport.events;

/**
 * event to indicate new entity. TODO how to use this.
 *
 * @author W.Pasman
 */
public class NewEntityEvent extends EnvironmentEvent {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6387717146943403919L;
	private final String entity;
	private final String type;

	public NewEntityEvent(String entity, String type) {
		this.entity = entity;
		this.type = type;
	}

	/**
	 * @return The entity
	 */
	public String getEntity() {
		return this.entity;
	}

	/**
	 * @return The type of the new entity
	 */
	public String getType() {
		return this.type;
	}

}