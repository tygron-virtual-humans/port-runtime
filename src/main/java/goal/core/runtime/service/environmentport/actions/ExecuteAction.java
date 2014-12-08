package goal.core.runtime.service.environmentport.actions;

import java.io.Serializable;

import languageTools.program.agent.actions.UserSpecAction;
import eis.iilang.Action;
import goal.core.runtime.service.environment.LocalMessagingEnvironment.Messages2Environment;

/**
 * Executes the EIS executeAction command.
 *
 * @author W.Pasman
 * @modified K.Hindriks No longer uses A parser to convert action to EIS.
 */
public class ExecuteAction extends
goal.core.runtime.service.environmentport.actions.Action {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = 296779255592185534L;
	private final eis.iilang.Action eisAction;
	private final String agentName;

	/**
	 * Create new ExecuteAction action for the {@link EnvMsgConnector}.
	 *
	 * @param agentName
	 *            The agent.
	 * @param act
	 *            A {@link UserSpecAction} to be executed by the environment.
	 */
	public ExecuteAction(String agentName, Action act) {
		this.agentName = agentName;
		this.eisAction = act;
	}

	/**
	 * @return the EIS action
	 */
	public Action getAction() {
		return this.eisAction;
	}

	@Override
	public Serializable invoke(Messages2Environment messages2Environment)
			throws Exception {
		return messages2Environment.invoke(this);
	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return this.agentName;
	}
}