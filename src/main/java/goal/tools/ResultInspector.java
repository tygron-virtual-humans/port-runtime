package goal.tools;

import goal.core.agent.Agent;
import goal.core.agent.Controller;

import java.util.Collection;

/**
 * This is a interface for a inspector callback function for a result of a
 * SingleRun. To be used in {@link SingleRun#run(ResultInspector)}
 *
 * @param <C>
 *            The controller type
 *
 * @author W.Pasman 10jul13
 * @author K.Hindriks
 */
public interface ResultInspector<C extends Controller> {

	/**
	 * This function is called after the agents terminated.
	 *
	 * @param agents
	 *            the final states of the agents in the run.
	 */
	public void handleResult(Collection<Agent<C>> agents);
}
