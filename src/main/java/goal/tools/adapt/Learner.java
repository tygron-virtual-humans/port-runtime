package goal.tools.adapt;

import goal.core.mentalstate.MentalState;
import languageTools.program.agent.actions.ActionCombo;

import java.util.List;

/**
 * To make decisions in adaptive sections of the GOAL program the interpreter
 * consults a Learner. A Learner can make decisions based on past decisions and
 * rewards received from these decisions.
 *
 * @author mpkorstanje
 *
 */
public interface Learner {

	/**
	 * Selects an action from the list of options. The Learner can make this
	 * choice based on the current module, mental state and prior experiences.
	 *
	 * @param module
	 *            the current module
	 * @param ms
	 *            the current mental state
	 * @param options
	 *            from which the learner can choose an action
	 * @return the selected action
	 */
	public abstract ActionCombo act(String module, MentalState ms,
			List<ActionCombo> options);

	/**
	 * Update the reward based on the last action.
	 *
	 * @param module
	 *            the current module
	 * @param ms
	 *            the current mental state
	 * @param reward
	 *            the reward for executing the last action selected by
	 *            {@link #act(String, MentalState, List)}.
	 */

	public abstract void update(String module, MentalState ms, double reward);

	/**
	 * Terminate all learning.
	 *
	 * @param ms
	 * @param envReward
	 */
	public abstract void terminate(MentalState ms, Double envReward);

}