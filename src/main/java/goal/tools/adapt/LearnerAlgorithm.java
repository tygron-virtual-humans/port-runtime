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

package goal.tools.adapt;

import goal.core.mentalstate.MentalState;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * General interface through which specific learner algorithms can be hooked
 * into the {@link FileLearner}.
 *
 * @author dsingh
 * @author W.Pasman made Serializable #2246
 *
 */
public interface LearnerAlgorithm extends Serializable {

	/**
	 * Called at the start of each episode
	 */
	void start();

	/**
	 * ask the learner which action to execute next. Learner should return
	 * action or null.
	 *
	 * @param state
	 *            is the current state number. See {@link FileLearner#stateid}
	 * @param actions
	 *            is an array with the possible actions at this point. see
	 *            {@link FileLearner#actionid}.
	 * @return returns the action index to be executed in the next action. next
	 *         action or null if no action is available.
	 */
	Integer nextAction(Integer state, Integer[] actions);

	/**
	 * This is called after each action of the agent.
	 *
	 * @param reward
	 *            The reward is as indicated by {@link MentalState#getReward()}.
	 * @param newstate
	 *            a state number, see {@link FileLearner#stateid}
	 */
	void update(double reward, Integer newstate);

	/**
	 * Returns the learnt action values for a given state
	 *
	 * @param state
	 * @return
	 */
	Hashtable<Integer, Double> actionValues(Integer state);

	/**
	 * Called at the end of each episode.
	 *
	 * @param reward
	 *            The reward is as indicated by {@link MentalState#getReward()}
	 */
	void finish(double reward);

}
