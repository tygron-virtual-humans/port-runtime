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

import java.util.Hashtable;
import java.util.Random;

/**
 * The default learner to use. This is a simple Q-Learner. see also
 * http://en.wikipedia.org/wiki/Q-learning. Note: although some of the files
 * have the name 'sarsa', SARSA is a different learning algorithm.
 *
 * @author dsingh
 * @author W.Pasman modified name. Fixed serialization. #2246
 */
public class QLearner implements LearnerAlgorithm {
	/** Generated serialVersionUID */
	private static final long serialVersionUID = -8719214041255496994L;
	private final Random randGenerator;

	/**
	 * The algorithm uses a function Q(state,action)->value. See
	 * {@link #setValue(Integer, Integer, Double)}. The integers contain encoded
	 * (bitmap mappings) states and actions (see
	 * {@link GOALMentalStateConverter}). "Q(state,action)=value" is stored in
	 * the following two-dim table, with state as first index and action as
	 * second index.
	 */
	private final Hashtable<Integer, Hashtable<Integer, Double>> valueFunction;
	private final double alpha;
	private double epsilon;
	private final double epsilon_decay;
	private final double gamma;
	private Integer newaction;
	private Integer newstate;

	private final int DEFAULT_VALUE = 0;

	/**
	 * Constructs a new Q learner
	 *
	 * @param step
	 * @param epsilon
	 * @param decay
	 * @param gamma
	 */
	public QLearner(double step, double epsilon, double decay, double gamma) {
		this.randGenerator = new Random();
		this.valueFunction = new Hashtable<>();
		this.alpha = step;
		this.epsilon = epsilon;
		this.epsilon_decay = decay;
		this.gamma = gamma;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void start() {
		this.newstate = null;
		this.newaction = null;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public Integer nextAction(Integer state, Integer[] actions) {
		/**
		 * Choose an action e-greedily from the value function and store the
		 * action and observation. Update the valueFunction entry for the last
		 * state,action pair.
		 */
		this.newstate = state;
		this.newaction = egreedy(this.newstate, actions);
		return this.newaction;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void update(double reward, Integer resultingstate) {
		if (this.newstate != null && this.newaction != null
				&& resultingstate != null) {
			double Q_sa = getValue(this.newstate, this.newaction).doubleValue();
			double Q_sprime_aprime = getMaxValue(resultingstate).doubleValue(); // Q
			// Learning
			double new_Q_sa = Q_sa + this.alpha
					* (reward + (this.gamma * Q_sprime_aprime) - Q_sa);
			setValue(this.newstate, this.newaction, new_Q_sa);
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void finish(double reward) {
		if (this.newstate != null && this.newaction != null) {
			double Q_sa = getValue(this.newstate, this.newaction).doubleValue();
			double new_Q_sa = Q_sa + this.alpha * (reward - Q_sa);
			setValue(this.newstate, this.newaction, new_Q_sa);
			this.epsilon -= this.epsilon * this.epsilon_decay;
		}
	}

	/**
	 * Gives the value of taking action a in state s
	 *
	 * @param state
	 *            mentalstate as integer (see {@link GOALMentalStateConverter}
	 * @param action
	 *            as integer (see
	 * @return
	 */
	private Double getValue(Integer s, Integer a) {
		Double v = null;
		if (this.valueFunction.containsKey(s)) {
			Hashtable<Integer, Double> actions = this.valueFunction.get(s);
			if (actions.containsKey(a)) {
				v = actions.get(a);
			}
		}
		if (v == null) {
			v = new Double(this.DEFAULT_VALUE);
			setValue(s, a, v);
		}
		return v;
	}

	/**
	 * Gives the maximum value of taking an action in state s
	 *
	 * @param state
	 * @param action
	 * @return
	 */
	private Double getMaxValue(Integer s) {
		Double max = null;
		if (this.valueFunction.containsKey(s)) {
			Double[] values = this.valueFunction.get(s).values()
					.toArray(new Double[0]);
			max = values[this.randGenerator.nextInt(values.length)];
			for (Double value : values) {
				if (value.doubleValue() > max.doubleValue()) {
					max = value;
				}
			}
		} else {
			max = new Double(this.DEFAULT_VALUE); // Return this when s does not
			// exist
		}
		return max;
	}

	/**
	 * Sets the value v for the given state s and action a. So Q(s,a)=v.
	 *
	 * @param s
	 *            is the state as integer. See XXX
	 * @param a
	 *            is the action as integer, see XXX
	 * @param val
	 *            is the new value associated with
	 */
	private void setValue(Integer s, Integer a, Double v) {
		Hashtable<Integer, Double> actions = (this.valueFunction.containsKey(s)) ? this.valueFunction
				.get(s) : new Hashtable<Integer, Double>();
				actions.put(a, v);
				this.valueFunction.put(s, actions);
	}

	/**
	 *
	 * Selects a random action with probability 1-epsilon, and the action with
	 * the highest value otherwise. This is a quick'n'dirty implementation, it
	 * does not do tie-breaking.
	 *
	 * @param theState
	 * @param options
	 * @return
	 */
	private Integer egreedy(Integer theState, Integer[] options) {

		if (options == null || options.length == 0) {
			return null;
		}

		if (options.length == 1) {
			return options[0];
		}

		/* Choose an action randomly */
		Integer max = options[this.randGenerator.nextInt(options.length)];

		/* Return the random choice with probability epsilon */
		if (this.randGenerator.nextDouble() <= this.epsilon) {
			return max;
		}

		/*
		 * Otherwise choose greedily. Here it is important to choose the initial
		 * max randomly in order to break ties between actions with equal value.
		 * This is particularly critical in the early stages when all values are
		 * the same.
		 */
		for (Integer a : options) {
			Double v = getValue(theState, a);
			Double m = getValue(theState, max);

			if (v != null && m != null && v > m) {
				max = a;
			}
		}
		return max;
	}

	@Override
	public Hashtable<Integer, Double> actionValues(Integer state) {
		return this.valueFunction.containsKey(state) ? this.valueFunction
				.get(state) : new Hashtable<Integer, Double>();
	}
}
