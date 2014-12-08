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

import goal.core.agent.Agent;
import goal.core.mentalstate.MentalState;
import goal.preferences.CorePreferences;
import goal.tools.logging.InfoLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import krTools.language.DatabaseFormula;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.Module;
import languageTools.program.agent.Module.RuleEvaluationOrder;
import languageTools.program.agent.actions.ActionCombo;
import mentalState.BASETYPE;

/**
 * The generic learner. This class makes the link between GOAL core and the
 * {@link LearnerAlgorithm} by hooking in to the {@link Agent} through the
 * {@link AdaptiveResultInstr} and {@link AdaptiveSelectInstr}. <h1>Learning</h1>
 * <p>
 * Learning is done through scripts, using the Batch runner to run an agent many
 * runs through the same problem, each time updating the score when the learning
 * is finished (see {@link BatchRunner}). The score is taken by the
 * {@link LearnerAlgorithm} to update the learnparameters. After the runs, the
 * entire Learner is saved to a file.
 * </p>
 * <h1>running after learning</h1>
 * <p>
 * After running, you can run GOAL to use a previously learned model. The
 * learner is then loaded from disk and hooked into GOAL, after which GOAL can
 * ask the learner to recommend actions.
 * </p>
 *
 * @author dsingh
 * @author W.Pasman made class Serializable #2246
 *
 */
public class FileLearner implements Serializable, Learner {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 4158712238978167789L;
	/**
	 * Provides each adaptive module with its own learner.
	 */
	private Map<String, LearnerInstance> learners = new HashMap<>();
	/**
	 * Provides each adaptive module with its own filter.
	 */
	private final Map<String, Set<String>> filters = new HashMap<>();

	/**
	 * The list of action IDs. actions are stored as strings using
	 * {@link ActionCombo#toString()}.
	 */
	private Map<String, Integer> actionid = new TreeMap<>();
	/**
	 * The list of GOAL state IDs. GOALState is a number representing a
	 * {@link MentalState}. See also
	 * {@link GOALMentalStateConverter#translate(MentalState, java.util.Stack)}.
	 */
	private Map<String, Integer> stateid = new TreeMap<>();

	private Map<Integer, String> actionstr = new TreeMap<>();
	private Map<String, String> statestr = new TreeMap<>();

	/**
	 *
	 *
	 private GOALMentalStateConverter converter;
	 * 
	 * /** Used to save the converter universe
	 *
	 * private List<String> universe;
	 * 
	 * /* The program that this learner is associated with
	 */
	private final AgentProgram program;

	/*
	 * File name prefix for .lrn file
	 */
	private final String lrnPrefix;

	/**
	 *
	 */
	private Integer runCount;
	private boolean finishedEpisode;
	private boolean updateCalled;

	/**
	 * Creates a new learner
	 *
	 * @param name
	 * @param program
	 */
	public FileLearner(String name, AgentProgram program) {
		// this.converter = new GOALMentalStateConverter(null);

		String filename = null;
		boolean loaded = false;
		/*
		 * If a file with a learned model has been specified then load this
		 * file.
		 */
		if (new File(filename = CorePreferences.getLearnFile()).exists()) {
			loaded = loadLearner(filename, program);
		}
		/*
		 * else if a agentname.lrn file exists in the current directory then
		 * load this file.
		 */
		else if (new File(filename = name + ".lrn").exists()) {
			loaded = loadLearner(filename, program);
		}

		if (!loaded) {
			this.runCount = new Integer(0);
		}

		/*
		 * Now for each adaptive module in the program initialise a new learning
		 * instance and start a new learning episode
		 */
		for (Module module : program.getModules()) {
			if (module.getRuleEvaluationOrder() == RuleEvaluationOrder.ADAPTIVE) {
				init(module, getAlgorithm(module.getName()));
				startEpisode(module.getName());
			}
		}
		this.lrnPrefix = name;
		this.program = program;
	}

	/**
	 * Initialize a new learning instance for the given adaptive module. Looks
	 * for a default property file in the working directory. The name of the
	 * file should be {module name}.adaptive.properties.
	 *
	 * @param module
	 */
	private void init(Module module, LearnerAlgorithm learner) {
		/**
		 * FIXME: This function should be moved to the LearnerInterface and
		 * should be handled by the particular implementation.
		 */
		double sarsa_alpha = 0.9;
		double sarsa_gamma = 0.9;
		double sarsa_epsilon = 0.1;
		double sarsa_epsilon_decay = 0.0;

		/* Use these defaults if we cannot load the properties file */
		Properties defaults = new Properties();
		defaults.setProperty("sarsa_alpha", new Double(sarsa_alpha).toString());
		defaults.setProperty("sarsa_gamma", new Double(sarsa_gamma).toString());
		defaults.setProperty("sarsa_epsilon",
				new Double(sarsa_epsilon).toString());
		defaults.setProperty("sarsa_epsilon_decay", new Double(
				sarsa_epsilon_decay).toString());
		try {
			Properties properties = new Properties(defaults);
			File file = new File(module.getName() + ".adaptive.properties");
			if (file.exists()) {
				try (FileInputStream fis = new FileInputStream(file.getName())) {
					properties.load(fis);
					new InfoLog("Learner: Loaded properties from `"
							+ file.getName() + "`.");
					new InfoLog(properties.toString());
				} catch (Exception e) {
					System.err
							.println("WARNING: Could not load learner properties from `"
									+ file.getName()
									+ "`. Will proceed with defaults.");
				}
			}
			sarsa_alpha = Double.parseDouble(properties
					.getProperty("sarsa_alpha"));
			sarsa_epsilon = Double.parseDouble(properties
					.getProperty("sarsa_epsilon"));
			sarsa_epsilon_decay = Double.parseDouble(properties
					.getProperty("sarsa_epsilon_decay"));
			sarsa_gamma = Double.parseDouble(properties
					.getProperty("sarsa_gamma"));

		} catch (Exception e) {
			System.err
					.println("WARNING: While loading learner properties got: "
							+ e.getMessage());
		}

		// Associate belief filter with corresponding rule set.
		this.setBeliefFilter(module);

		// Create a new Q-learner.
		if (learner == null) {
			learner = new QLearner(sarsa_alpha, sarsa_epsilon,
					sarsa_epsilon_decay, sarsa_gamma);
		}
		// Associate learner with module.
		setAlgorithm(module.getName(), learner);
	}

	/**
	 * Compute belief filter for rule set of the module.
	 *
	 * @param module
	 */
	private void setBeliefFilter(Module module) {
		new InfoLog("Computing filter for module " + module.getName() + ".");

		/*
		 * ModuleGraphGenerator moduleGraphGenerator = new
		 * ModuleGraphGenerator();
		 * moduleGraphGenerator.setKRlanguage(module.getKRInterface());
		 * moduleGraphGenerator.createGraph(module, null); DependencyGraph<?>
		 * filter = moduleGraphGenerator.getGraph(); List<? extends Expression>
		 * queried = filter.getQueries(); Set<String> signatures = new
		 * HashSet<>(queried.size()); for (Expression query : queried) {
		 * signatures.add(query.getSignature()); }
		 * this.filters.put(module.getName(), signatures);
		 */
		this.filters.put(module.getName(), new HashSet<String>(0));

		// new InfoLog("Filter = " + signatures);
	}

	private Set<String> getBeliefFilters(String module) {
		return this.filters.get(module);
	}

	/**
	 * Starts a new learning episode for the given module.
	 *
	 * @param module
	 */
	private void startEpisode(String module) {
		this.learners.get(module).instance.start();
		this.learners.get(module).totalreward = 0;
		this.learners.get(module).totalactions = 0;
		this.finishedEpisode = false;
	}

	/**
	 * Sets an algorithm for the learner. You can change the learning algorithm
	 * without loosing the {@link GOALConversionUniverse} of known states.
	 * However, the things learned by the {@link LearnerAlgorithm} will get
	 * lost. So you usually do not want to change this after learning.
	 *
	 * @param algorithm
	 *            is an instance of {@link LearnerAlgorithm}
	 */
	private void setAlgorithm(String module, LearnerAlgorithm algorithm) {
		this.learners.put(module, new LearnerInstance(algorithm));
	}

	private LearnerAlgorithm getAlgorithm(String module) {
		return this.learners.containsKey(module) ? this.learners.get(module).instance
				: null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goal.tools.adapt.Learner#act(java.lang.String,
	 * goal.core.mentalstate.MentalState, java.util.List, java.util.Set)
	 */
	@Override
	public ActionCombo act(String module, MentalState ms,
			List<ActionCombo> actionOptions) {
		this.updateCalled = false;
		ActionCombo chosen = null;
		// Stores the list of input action options */
		Vector<ActionCombo> options = new Vector<>();
		// Stores the action IDs associated with each ActionCombo
		Vector<Integer> optionids = new Vector<>();
		Hashtable<String, Boolean> added = new Hashtable<>();
		for (ActionCombo option : actionOptions) {
			if (!added.containsKey(option.toString())) {
				added.put(option.toString(), new Boolean(true));
				options.add(option);
				// Observe and save the new option if we haven't seen it before
				processOption(option);
				optionids.add(this.actionid.get(option.toString()));
			}
		}

		// Observe and save the new state if we haven't seen it before
		String newstate = processState(ms, getBeliefFilters(module));

		// Ask the module specific learner to pick the next action
		Integer newaction = this.learners.get(module).instance.nextAction(
				this.stateid.get(newstate), optionids.toArray(new Integer[0]));

		// Get the ActionCombo mapped to this action id
		chosen = options.elementAt(optionids.indexOf(newaction));

		// Increment the number of actions taken so far, for reporting
		this.learners.get(module).totalactions++;

		return chosen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goal.tools.adapt.Learner#update(java.lang.String,
	 * goal.core.mentalstate.MentalState, double, java.util.Set)
	 */
	@Override
	public void update(String module, MentalState ms, double reward) {
		this.updateCalled = true;
		// Observe and save the new state if we haven't seen it before
		String newstate = processState(ms, getBeliefFilters(module));
		// Call update on the module specific instance
		this.learners.get(module).instance.update(reward,
				this.stateid.get(newstate));
		// Accumulate the reward
		this.learners.get(module).totalreward += reward;
	}

	/**
	 * Returns a filtered set of beliefs whose signature matches one of those in
	 * filter.
	 *
	 * @param mentalState
	 * @param filter
	 *            A list of signatures.
	 * @return
	 */
	private static Set<DatabaseFormula> filteredBeliefs(
			MentalState mentalState, Set<String> filter) {
		Set<DatabaseFormula> beliefs = mentalState
				.getOwnBase(BASETYPE.BELIEFBASE).getTheory().getFormulas();
		Set<DatabaseFormula> remove = new LinkedHashSet<>();
		// hack around #3057 to prevent ConcurrentModificationException
		List<DatabaseFormula> formulas = new ArrayList<>(beliefs);
		for (DatabaseFormula belief : formulas) {
			if (!filter.contains(belief.getSignature())) {
				remove.add(belief);
			}
		}
		beliefs.removeAll(remove);
		return beliefs;
	}

	/**
	 * Finish the current learning episode for the given adaptive module.
	 *
	 * @param module
	 */
	private void finishEpisode(String module, MentalState ms, double reward) {
		this.finishedEpisode = true;
		// Observe and save the new state if we haven't seen it before
		processState(ms, getBeliefFilters(module));
		// Call finish on the module specific instance
		this.learners.get(module).instance.finish(reward);
		// Accumulate the reward
		this.learners.get(module).totalreward += reward;
	}

	/**
	 * Writes the learning report for the given module to a file in the working
	 * directory. The name of the output file will be {module
	 * name}.adaptive.out.
	 *
	 * @param agentName
	 * @param module
	 */
	private void writeReportFile(String agentName, String module) {
		/* Write the performance results to file */
		String outfile = module + ".adaptive.out";
		try (BufferedWriter out = new BufferedWriter(new FileWriter(outfile,
				true))) {
			out.write(String.format("%s: %.2f %.2f %07d\n", agentName,
					this.learners.get(module).totalactions,
					this.learners.get(module).totalreward, this.stateid.size()));
		} catch (Exception e) {
			System.err.println("WARNING: Could not write " + outfile + ": "
					+ e.getMessage());
		}
		/* Write human readable learning output to file */
		outfile = module + ".lrn.txt";
		try (BufferedWriter out = new BufferedWriter(new FileWriter(outfile,
				false))) {
			String summary = "";
			summary += "-----------------------------------------\n";
			summary += String.format("%-30s: %d\n", "Number of runs",
					this.runCount);
			summary += String.format("%-30s: %d\n",
					"Situations encountered (below)", this.stateid.size());
			summary += "-----------------------------------------\n";
			out.write(summary);
			int index = 0;
			for (String state : this.stateid.keySet()) {
				out.write(String.format("s%07d %s\n", index,
						this.statestr.get(state)));
				Hashtable<Integer, Double> avpairs = this.learners.get(module).instance
						.actionValues(this.stateid.get(state));
				List<Integer> sortedByValue = new ArrayList<>(avpairs.keySet()
						.size());
				for (Integer i : avpairs.keySet()) {
					boolean added = false;
					for (int j = 0; j < sortedByValue.size(); j++) {
						if (avpairs.get(i) >= avpairs.get(sortedByValue.get(j))) {
							sortedByValue.add(j, i);
							added = true;
							break;
						}
					}
					if (!added) {
						sortedByValue.add(i);
					}
				}
				String s = "";
				for (Integer i : sortedByValue) {
					s += String.format("%20s : %+06.3f\n",
							this.actionstr.get(i), avpairs.get(i));
				}
				out.write(s);
				index++;
			}
		} catch (Exception e) {
			System.err.println("WARNING: Could not write " + outfile + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Returns the MentalState translated to a state vector string. The filter
	 * is applied to the MentalState before it is translated. The returned state
	 * is also added to the list of known states with a unique ID, if it is not
	 * already there.
	 *
	 * @param ms
	 * @param filter
	 * @return
	 */
	private String processState(MentalState ms, Set<String> filter) {
		String state = "";
		// this.converter.translate(filteredBeliefs(ms,
		// filter),ms.getAttentionStack()).toString();
		if (!this.stateid.containsKey(state)) {
			this.stateid.put(state, new Integer(this.stateid.size() + 1));
		}

		if (!this.statestr.containsKey(state)) {
			String s = "";
			Set<DatabaseFormula> beliefs = filteredBeliefs(ms, filter);
			List<String> strset = new ArrayList<>(beliefs.size());
			for (DatabaseFormula dbf : beliefs) {
				strset.add(dbf.toString());
			}
			Collections.sort(strset);
			s += strset.toString() + " ";
			s += ms.getAttentionStack().toString();
			this.statestr.put(state, s);
		}

		return state;
	}

	/**
	 * Adds the option to the list of known options if not already there
	 *
	 * @param option
	 */
	private void processOption(ActionCombo option) {
		if (!this.actionid.containsKey(option.toString())) {
			this.actionid.put(option.toString(),
					new Integer(this.actionid.size() + 1));
		}
		if (!this.actionstr.containsKey(this.actionid.get(option.toString()))) {
			this.actionstr.put(this.actionid.get(option.toString()),
					option.toString());
		}
	}

	/**
	 * Holds an instance of a LearningAlgorithm along with records of use
	 *
	 * @author dsingh
	 *
	 */
	class LearnerInstance implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = -8539363627078273749L;
		LearnerAlgorithm instance;
		/**
		 * Accumulates the total reward received from start to finish.
		 */
		transient double totalreward = 0;
		/**
		 * Counts the total number of actions performed from start to finish.
		 */
		transient double totalactions = 0;

		LearnerInstance(LearnerAlgorithm instance) {
			this.instance = instance;
			this.totalreward = 0;
			this.totalactions = 0;
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		// oos.defaultWriteObject();
		oos.writeObject(this.runCount);
		oos.writeObject(this.learners);
		oos.writeObject(this.actionid);
		oos.writeObject(this.stateid);
		oos.writeObject(this.actionstr);
		oos.writeObject(this.statestr);
		// this.universe = this.converter.getUniverse().toStringArray();
		// oos.writeObject(this.universe);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois)
			throws ClassNotFoundException, IOException {
		// is.defaultReadObject();
		this.runCount = (Integer) ois.readObject();
		this.learners = (Map<String, LearnerInstance>) ois.readObject();
		this.actionid = (Map<String, Integer>) ois.readObject();
		this.stateid = (Map<String, Integer>) ois.readObject();
		this.actionstr = (Map<Integer, String>) ois.readObject();
		this.statestr = (Map<String, String>) ois.readObject();
		// this.universe = (List<String>) ois.readObject();
		// this.converter = new GOALMentalStateConverter(null);
		// this.converter.getUniverse().setPreassignedIndices(this.universe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * goal.tools.adapt.Learner#terminateLearner(goal.core.mentalstate.MentalState
	 * , java.lang.Double)
	 */
	@Override
	public void terminate(MentalState ms, Double envReward) {

		boolean writeLearnerToFile = false;
		/*
		 * Learning episodes are always terminated here. We do this once for all
		 * ADAPTIVE modules going from RUNNING->KILLED.
		 */
		for (Module module : this.program.getModules()) {
			if (module.getRuleEvaluationOrder() == RuleEvaluationOrder.ADAPTIVE) {
				/*
				 * Learning was performed in this program so we will save the
				 * learner before we finish.
				 */
				writeLearnerToFile = true;

				// Increment the runCount;
				this.runCount++;

				if (!this.finishedEpisode || !this.updateCalled) {
					/*
					 * Obtain the reward from the environment. Or, if the
					 * environment does not support rewards, then create an
					 * internal reward based on whether the agent has achieved
					 * all its goals (reward +1) or not (it died instead, reward
					 * -1).
					 */
					boolean goalsEmpty = ms.getAttentionSet().getGoals()
							.isEmpty();
					double reward = (envReward != null) ? envReward
							: goalsEmpty ? 1.0 : -1.0;
					if (!this.updateCalled) {
						update(module.getName(), ms, reward);
					}
					if (!this.finishedEpisode) {
						finishEpisode(module.getName(), ms, reward);
					}

				}
				/* Save the learning performance report for this episode to file */
				writeReportFile(ms.getAgentId().getName(), module.getName());
			}
		}

		if (writeLearnerToFile) {
			String filename = null;
			/*
			 * If a file with a learned model has been specified then save to
			 * this file.
			 */
			if (new File(filename = CorePreferences.getLearnFile()).exists()) {
				saveLearner(filename);
			}
			/*
			 * else save to agentname.lrn
			 */
			else {
				saveLearner(this.lrnPrefix + ".lrn");
			}
		}

	}

	/**
	 * Saves the learning to file
	 *
	 * @param file
	 */
	private void saveLearner(String file) {
		try (ObjectOutputStream output = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(file)))) {
			output.writeObject(this);
		} catch (IOException e) {
			new InfoLog("File " + file + " could not be written ("
					+ e.getMessage() + "). Continuing.");
		}
	}

	/**
	 * Loads the learning from file
	 *
	 * @param file
	 * @param program
	 * @return a {@link FileLearner} object
	 */
	private boolean loadLearner(String file, AgentProgram program) {
		try (ObjectInputStream input = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(file)))) {
			Object obj = input.readObject();
			FileLearner l = (FileLearner) obj;
			this.runCount = l.runCount;
			this.learners = l.learners;
			this.actionid = l.actionid;
			this.stateid = l.stateid;
			this.actionstr = l.actionstr;
			this.statestr = l.statestr;
			// this.universe = l.universe;
			// this.converter = l.converter;
			new InfoLog("\nLoading learned model from file " + file);
			return true;
		} catch (Exception e) {
			new InfoLog("File " + file + " could not be read ("
					+ e.getMessage() + "). Continuing.");
		}
		return false;
	}
}
