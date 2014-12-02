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

package goal.tools.mc.program.goal;

import eis.iilang.Percept;
import goal.core.agent.Agent;
import goal.core.agent.AgentId;
import goal.core.agent.EnvironmentCapabilities;
import goal.core.agent.LoggingCapabilities;
import goal.core.agent.MessagingCapabilities;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.mentalstate.MentalState;
import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.Module.TYPE;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.SendAction;
import goal.core.program.actions.SendOnceAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.rules.Rule;
import goal.tools.IDEDebugger;
import goal.tools.IDEGOALInterpreter;
import goal.tools.PlatformManager;
import goal.tools.adapt.FileLearner;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.KRInitFailedException;
import goal.tools.mc.core.Controller;
import goal.tools.mc.core.State;
import goal.tools.mc.program.Program;
import goal.tools.mc.program.goal.trans.Atom;
import goal.tools.mc.program.goal.trans.MscSet;
import goal.tools.mc.program.goal.trans.TauAnalyzer;
import goal.tools.mc.property.ltl.Formula;
import goal.tools.mc.property.ltl.goal.GOALVocabulary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import swiprolog3.engines.SWIPrologLanguage;

/**
 * Represents a program component for a single-agent GOAL system in the absence
 * of an environment.
 *
 * @author sungshik
 *
 */
public abstract class GOALProg<T extends Term, A extends Atom, U extends Update>
		implements Program {

	// Class fields

	/**
	 * Empty debugger for reuse (this object is nowhere really used, but rather
	 * instantiated to pass as parameter to methods that require so).
	 */
	private IDEDebugger debugger;

	/**
	 * The mental state. The contents of the mental state change a lot over time
	 * by means of {@link #conv} (which has its own reference to this class
	 * field).
	 */
	private MentalState ms;

	/**
	 * Representation of the GOAL agent that is under investigation.
	 */
	private Agent<IDEGOALInterpreter> agent;

	/**
	 * Transition analysis tool used for slicing and partial order reduction.
	 */
	private TauAnalyzer<T, A, U> analyzer;

	/**
	 * The controller that controls and creates this program automaton.
	 */
	private Controller cont;

	/**
	 * Converter for the translation of mental states to their binary
	 * representation, and back. Maintains its own reference to {@link #ms}
	 */
	private final GOALMentalStateConverter conv;

	/**
	 * The transition function of this program automaton.
	 */
	private Map<GOALState, GOALState[]> delta = new HashMap<GOALState, GOALState[]>();

	/**
	 * The dummy source state of this automaton (not the initial mental state of
	 * the agent).
	 */
	private GOALState initial;

	/**
	 * Flag indicating whether POR is enabled or not.
	 */
	private final boolean por;

	/**
	 * The GOAL program from which {@link #agent} is extracted.
	 */
	private final GOALProgram program;

	/**
	 * The vocabulary of the property under investigation, i.e. the set of all
	 * mental state conditions that occur in it.
	 */
	private final GOALVocabulary voc;

	//
	// Constructors
	//

	/**
	 * Constructs a {@link GOALProg} object according to the agent file at the
	 * specified path, given the formula under investigation, the controller
	 * that invokes this constructor, and a flag indicating whether partial
	 * order reduction should be applied.
	 *
	 * @param path
	 *            - The path at which the agent file should be found.
	 * @param f
	 *            - The formula under investigation.
	 * @param cont
	 *            - The controller that controls the object to be created.
	 * @param por
	 *            - Flag indicating whether POR should be applied.
	 */
	public GOALProg(String path, Formula f, Controller cont, boolean por)
			throws Exception {
		// NOTE: This is all hard linked to SWI prolog!
		this.program = PlatformManager.getCurrent().parseGOALFile(
				new File(path), SWIPrologLanguage.getInstance());
		AgentId id = new AgentId("MCAgent");
		this.debugger = new IDEDebugger(id, program, new File(path), null);
		// CHECK is this good enough or do we have to create Dummy's?
		Learner learner = new FileLearner(id.getName(), program);

		DummyCapabilities dummy = new DummyCapabilities();
		this.agent = new Agent<IDEGOALInterpreter>(id, dummy, dummy, dummy,
				new IDEGOALInterpreter(program, debugger, learner));
		this.setMentalState(this.agent.getController().getRunState()
				.getMentalState());
		this.conv = new GOALMentalStateConverter(this.getMentalState());
		this.initial = new GOALState(this.conv);
		GOALState[] array = { this.conv.translate() };
		this.delta.put(this.initial, array);
		this.voc = new GOALVocabulary(f.getPropositions());
		this.cont = cont;
		this.por = por;
	}

	//
	// Abstract methods
	//

	/**
	 * Gets the specific instance of the analyzer to be used for slicing and
	 * partial order reduction.
	 *
	 * @param program
	 *            - The program that the analyzer should analyze.
	 * @param voc
	 *            - The vocabulary of the property under investigation.
	 */
	protected abstract TauAnalyzer<T, A, U> getAnalyzer(GOALProgram program,
			MscSet voc);

	//
	// Public methods
	//

	/**
	 * Checks whether the agent represented by this automaton contains percept
	 * rules.
	 *
	 * @return <code>true</code> if percept rules are present;
	 *         <code>false</code> otherwise.
	 */
	public boolean checkPerceptRules() {
		return this.agent.getController().getProgram().getModule()
				.hasModuleOfType(TYPE.EVENT);
	}

	/**
	 * Checks whether the agent represented by this automaton contains nested
	 * modules.
	 *
	 * @return <code>true</code> if nested modules are present;
	 *         <code>false</code> otherwise.
	 */
	public boolean checkNestedModules() {

		try {
			int i = 0;
			for (Module mod : this.agent.getController().getProgram()
					.getAllModules()) {
				if (!mod.getName().equals("main")
						&& !mod.getName().equals("events")
						&& !mod.getName().equals("init")) {
					i++;
				}
			}
			return i != 0;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Checks whether the agent represented by this automaton contains
	 * communication primitives.
	 *
	 * @return <code>true</code> if communication primitives are present;
	 *         </code>false</code> otherwise.
	 */
	public boolean checkCommunication() {

		try {

			/*
			 * Check for communication in the base program / for (Rule rule :
			 * this.agent.getGOALProgram().getProgramSection()) { for (Action
			 * act : rule.getResult()) { if (act instanceof SendAction || act
			 * instanceof SendOnceAction) return true; } }/*
			 */

			/* Check for communication in nested modules */
			for (Module mod : this.agent.getController().getProgram()
					.getAllModules()) {
				if (mod.getRuleSet() == null) {
					continue;
				}
				for (Rule rule : mod.getRuleSet()) {
					for (Action act : rule.getAction()) {
						if (act instanceof SendAction
								|| act instanceof SendOnceAction) {
							return true;
						}
					}
				}
			}

			/*
			 * If this point is reached, no communication primitives have been
			 * detected
			 */
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void generate() {

		try {

			/*
			 * Initialize a stack containing states for which successors need be
			 * defined
			 */
			Stack<GOALState> todo = new Stack<GOALState>();
			todo.add(this.initial);

			/*
			 * While the to-do stack is not empty, define successors for all
			 * states on the to-do stack
			 */
			while (!todo.empty()) {

				/* Fetch the state on top of the stack */
				GOALState q = todo.pop();

				/*
				 * Define successors (the call to getSuccessors already adds
				 * these successors to delta), and iterate
				 */
				for (GOALState qSucc : this.getSuccessors(q)) {
					if (!this.delta.containsKey(qSucc)) {
						todo.push(qSucc);
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the mental state converter stored in {@link #conv}.
	 *
	 * @return The mental state converter belonging to this object.
	 */
	public GOALMentalStateConverter getConv() {
		return this.conv;
	}

	@Override
	public GOALState getInitial() {
		return this.initial;
	}

	@Override
	public String getPerformedAction(State state, State successorState) {

		try {
			GOALState q = (GOALState) state;
			GOALState qSucc = (GOALState) successorState;

			/*
			 * If q and qSucc are unequal, determine whether there exists an
			 * action whose performance in q can yield qSucc
			 */
			if (!q.equals(qSucc)) {

				/* Update the mental state such that it corresponds to q */
				this.conv.update(q);

				/* Process (non-percept) percept rules */
				this.agent.getController().processPercepts(
						new LinkedHashSet<Percept>(),
						new LinkedHashSet<Percept>());
				GOALState qq = this.conv.translate();
				if (qq.equals(qSucc)) {
					return "only-perceive";
				}

				/* Get name of goal base to get module of same name. TODO HACKY */
				Module module = null;
				String name = this.agent.getController().getRunState()
						.getMentalState().getAttentionSet().getName();
				for (Module mod : program.getAllModules()) {
					if (mod.getName().equals(name)) {
						module = mod;
						break;
					}
				}

				/* Iterate actions */
				List<ActionCombo> actions = module.getRuleSet()
						.getActionOptions(
								this.agent.getController().getRunState()
										.getMentalState(),
								this.agent.getController().getDebugger());
				for (ActionCombo act : actions) {

					/* Perform action */
					ActionCombo result = new ActionCombo(this.agent
							.getController().doPerformAction(act).getActions(),
							null);
					// ASSUMES that no send actions are performed.
					for (Action action : result) {
						if (action instanceof UserSpecAction) {
							this.agent.getController().doPerformAction(action);
						}
					}

					/* Translate successor to binary representation, and add */
					GOALState successor = this.conv.translate();

					/* Return if the right action is found */
					if (qSucc.equals(successor)) {
						return act.toString();
					}

					/* Revert changes */
					this.conv.update(qq);
				}

				/* If this point is reached, the sought action does not exist */
				return null;
			} else {
				return "skip";
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSize() {
		return this.delta.size();
	}

	@Override
	public GOALState[] getSuccessors(State state) {

		try {
			GOALState q = (GOALState) state;

			/* Assume that successors have already been defined for q */
			GOALState[] array = this.delta.get(q);

			/* If the assumption was wrong, define them */
			if (array == null) {
				List<GOALState> successors = new ArrayList<GOALState>();

				/* Update the mental state such that it corresponds to q */
				this.conv.update(q);

				/* Process (non-percept) percept rules */
				this.agent
						.getController()
						.getRunState()
						.processPercepts(new LinkedHashSet<Percept>(),
								new LinkedHashSet<Percept>());
				GOALState qq = this.conv.translate();

				/* Restore module TODO HACKY */
				Module module = null;
				String name = this.agent.getController().getRunState()
						.getMentalState().getAttentionSet().getName();
				for (Module mod : program.getAllModules()) {
					if (mod.getName().equals(name)) {
						module = mod;
						break;
					}
				}

				/*
				 * If POR is enabled, delegate entire successor computation to
				 * the POR method, which are maintained by analyzer
				 */
				if (this.por) {
					if (this.analyzer == null) {
						this.analyzer = this.getAnalyzer(this.program,
								this.voc.toMscSet());
					}
					successors = this.analyzer.por(qq, this.conv,
							this.cont.getFirstSearchPath(), agent);
				}

				/*
				 * If POR is disabled, compute successors straightforwardly by
				 * requesting the interpreter for all action options in the
				 * mental state corresponding to qq, and executing each of them.
				 */
				else {

					/* Iterate actions */
					List<ActionCombo> actions = module.getRuleSet()
							.getActionOptions(
									this.agent.getController().getRunState()
											.getMentalState(),
									this.agent.getController().getDebugger());
					for (ActionCombo act : actions) {

						/* Perform action */
						// ASSUMES action is not needing SingleGoal
						// ASSUMES module is at top level.
						ActionCombo result = new ActionCombo(this.agent
								.getController().doPerformAction(act)
								.getActions(), null);
						// ASSUMES that no send actions are performed.
						// ASSUMES action is not needing SingleGoal
						for (Action action : (result)) {
							if (action instanceof UserSpecAction) {
								this.agent.getController().doPerformAction(
										action);
							}
						}

						/*
						 * Translate successor to binary representation, and add
						 */
						GOALState successor = this.conv.translate();
						successors.add(successor);

						/* Revert changes */
						this.conv.update(qq);
					}
				}

				/* If this mental state has no successors, it can stutter */
				if (successors.isEmpty()) {
					successors.add(qq);
				}

				/* Update array */
				array = new GOALState[successors.size()];
				successors.toArray(array);
				this.delta.put(q, array);
			}

			/* Return */
			return array;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the debugger
	 */
	public Debugger getDebugger() {
		return debugger;
	}

	/**
	 * @param debugger
	 *            the debugger to set
	 */
	public void setDebugger(IDEDebugger debugger) {
		this.debugger = debugger;
	}

	/**
	 * @return the ms
	 */
	public MentalState getMentalState() {
		return ms;
	}

	/**
	 * @param ms
	 *            the ms to set
	 */
	public void setMentalState(MentalState ms) {
		this.ms = ms;
	}

	@Override
	public void slice() {
		if (this.analyzer == null) {
			this.analyzer = this.getAnalyzer(this.program, this.voc.toMscSet());
		}
		this.analyzer.slice();
	}

	/**
	 * Free up all resources and dispose.
	 *
	 * @throws MessagingException
	 * @throws KRInitFailedException
	 */
	public void dispose() throws MessagingException, KRInitFailedException {
		agent.stop(); // FIXME: This used to be a hard kill. We may have some
		// clean up to do.
		agent = null;
		cont = null;
		debugger = null;
		ms = null;
		analyzer = null;
		delta = null;
		initial = null;
	}

}

/**
 * dummy messagebox for agents
 *
 * @author W.Pasman 30jan13 #2374
 *
 */
class DummyCapabilities implements MessagingCapabilities,
		EnvironmentCapabilities, LoggingCapabilities {

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public Double getReward() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Percept> getPercepts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<goal.core.program.Message> getAllMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postMessage(goal.core.program.Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performAction(UserSpecAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void log(String message) {
		// TODO Auto-generated method stub

	}

}

/**
 * Dummy messagebox id for {@link DummyMBox}
 *
 * @author W.Pasman
 *
 */
@SuppressWarnings("serial")
class DummyMBoxId extends MessageBoxId {
	private final String name;

	public DummyMBoxId(String nm) {
		name = nm;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return null;
	}

}