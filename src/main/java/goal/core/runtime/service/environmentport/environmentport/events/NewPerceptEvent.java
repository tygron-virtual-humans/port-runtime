package goal.core.runtime.service.environmentport.environmentport.events;

import eis.iilang.Percept;

/**
 * event to indicate new percept. Coupled to the
 * {@link eis.AgentListener#handlePercept(String, eis.iilang.Percept)}
 *
 * @author W.Pasman
 */
public class NewPerceptEvent extends EnvironmentEvent {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6387717146943403919L;
	private final Percept percept;
	private final String agent;

	public NewPerceptEvent(String agent, Percept percept) {
		this.agent = agent;
		this.percept = percept;
	}

	/**
	 * @return The agent
	 */
	public String getAgent() {
		return agent;
	}

	/**
	 * @return The percept
	 */
	public Percept getPercept() {
		return percept;
	}
}