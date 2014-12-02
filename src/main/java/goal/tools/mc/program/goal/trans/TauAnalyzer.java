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

package goal.tools.mc.program.goal.trans;

import goal.core.agent.Agent;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.Module.TYPE;
import goal.core.program.actions.ActionCombo;
import goal.core.program.literals.MentalStateCond;
import goal.tools.IDEGOALInterpreter;
import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.goal.GOALConversionUniverse;
import goal.tools.mc.program.goal.GOALMentalStateConverter;
import goal.tools.mc.program.goal.GOALState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

/**
 * Represents a transition analysis tool set consisting of slicing and partial
 * order reduction methods. This works only for flat agents, i.e. agents that do
 * not have nested modules.
 *
 * @author Sungshik
 *
 * @param <T>
 *            The type of term associated with the KRT that the agent to analyze
 *            uses.
 * @param <A>
 *            The type of atom associated with the KRT that the agent to analyze
 *            uses.
 * @param <U>
 *            The type of update associated with the KRT that the agent to
 *            analyze uses.
 */
@SuppressWarnings("unused")
public abstract class TauAnalyzer<T extends Term, A extends Atom, U extends Update> {

	//
	// Public fields
	//

	/**
	 * Indicates which heuristic for ample set computation should be used. At
	 * current, two heuristics have been implemented.
	 * <ol>
	 * <li>Every transition class is considered in isolation.
	 * <li>Connected components in the dependence graph are sought.
	 * </ol>
	 */
	public static int ample = 1;

	//
	// Private fields
	//

	/**
	 * The base transition module of this analyzer.
	 */
	private final TauModule<T, A, U> baseModule;

	/**
	 * The connected components in the dependence graph. This data structure is
	 * only used in combination with partial order reduction.
	 */
	private LMHashSet<LMHashSet<TauClass<T, A, U>>> comps;

	/**
	 * Indicates whether {@link #initDependence} has already been invoked.
	 */
	private boolean initDependenceInvoked = false;

	/**
	 * Indicates whether {@link #initEnabledBy} has already been invoked.
	 */
	private boolean initEnabledByInvoked = false;

	/**
	 * Indicates whether {@link #initVisible} has already been invoked.
	 */
	private boolean initVisibleInvoked = false;

	/**
	 * The vocabulary of the property under investigation.
	 */
	private final MscSet voc;

	//
	// Constructors
	//

	/**
	 * Constructs a transition analyzer that analyzes the specified program,
	 * with respect to the specified vocabulary.
	 *
	 * @param program
	 *            - The program under analysis.
	 * @param voc
	 *            - The vocabulary to be used during the analysis.
	 */
	public TauAnalyzer(GOALProgram program, MscSet voc) {

		/* Print output */
		long time = System.currentTimeMillis();
		System.out.println("\n[TRANSITION ANALYSIS]");

		/* Initialize */
		this.baseModule = this.createTauModule(program.getModule()
				.getModuleOfType(TYPE.MAIN), program.getAllKnowledge());
		this.voc = voc;

		/* Print output */
		long elapsedTime = System.currentTimeMillis() - time;
		System.out.println("\nElapsed time: " + elapsedTime + " ms = "
				+ (elapsedTime / 1000) + " s = " + (elapsedTime / (1000 * 60))
				+ " min");
	}

	//
	// Abstract methods
	//

	/**
	 * Determines whether two transition classes are independent.
	 *
	 * @param tauClass1
	 *            - The one transition class.
	 * @param tauClass2
	 *            - The other transition class.
	 */
	protected abstract boolean areIndependent(TauClass<T, A, U> tauClass1,
			TauClass<T, A, U> tauClass2);

	/**
	 * Determines whether <code>tauClass1</code> can be enabled by
	 * <code>tauClass2</code>.
	 *
	 * @param tauClass1
	 *            - The one transition class.
	 * @param tauClass2
	 *            - The other transition class.
	 */
	protected abstract boolean canEnabledBy(TauClass<T, A, U> tauClass1,
			TauClass<T, A, U> tauClass2);

	/**
	 * Creates a step according to the specified parameters.
	 *
	 * @param tauClass
	 *            - See {@link TauStep#tauClass}.
	 * @param universe
	 *            - See {@link TauStep#universe}.
	 * @param source
	 *            - See {@link TauStep#source}.
	 * @param destination
	 *            - See {@link TauStep#destination}.
	 * @param act
	 *            - See {@link TauStep#act}.
	 * @return The new step.
	 */
	protected abstract TauStep<T, A, U> createTauStep(
			TauClass<T, A, U> tauClass, GOALConversionUniverse universe,
			GOALState source, GOALState destination, ActionCombo act);

	/**
	 * Creates a transition module corresponding to the specified module.
	 *
	 * @param module
	 *            - The module to which the transition module to be constructed
	 *            should correspond.
	 * @return The new transition module.
	 */
	protected abstract TauModule<T, A, U> createTauModule(Module module,
			Collection<DatabaseFormula> knowledge);

	/**
	 * Determines whether the specified transition class is visible to the
	 * specified set of mental state conditions.
	 *
	 * @param tauClass
	 *            - The transition class to check visibility for.
	 * @param voc
	 *            - The mental state conditions to which the specified
	 *            transition class might be visible.
	 * @return <code>true</code> if the specified transition class is visible;
	 *         </code>false</code> otherwise.
	 */
	protected abstract boolean isVisible(TauClass<T, A, U> tauClass, MscSet voc);

	//
	// Public methods
	//

	/**
	 * Runs the partial order reduction algorithm to determine the successors of
	 * the specified mental state. The algorithm is based on the ample set
	 * methods, and tries to find a set of all the successors that is sufficient
	 * for model checking results to be preserved. Assumes an NDFS search regime
	 * of the state space.
	 *
	 * @param source
	 *            - The state whose successors are requested.
	 * @param conv
	 *            - The mental state converter.
	 * @param path
	 *            - The current search path of the nested depth-first search
	 *            algorithm.
	 * @param agent
	 *            the agent performing the action.
	 */
	public List<GOALState> por(GOALState source, GOALMentalStateConverter conv,
			LMHashSet<State> path, Agent<IDEGOALInterpreter> agent) {

		try {

			/* Initialize relations if necessary */
			long time = System.currentTimeMillis();
			if (!this.initEnabledByInvoked) {
				this.initEnabledBy();

				/* Print output */
				this.printEnabledBy();
				long elapsedTime = System.currentTimeMillis() - time;
				System.out.println("\nElapsed time: " + elapsedTime + " ms = "
						+ (elapsedTime / 1000) + " s = "
						+ (elapsedTime / (1000 * 60)) + " min");
			}
			if (!this.initVisibleInvoked) {
				this.initVisible();

				/* Print output */
				this.printVisible();
				long elapsedTime = System.currentTimeMillis() - time;
				System.out.println("\nElapsed time: " + elapsedTime + " ms = "
						+ (elapsedTime / 1000) + " s = "
						+ (elapsedTime / (1000 * 60)) + " min");
			}
			if (!this.initDependenceInvoked) {
				this.initDependence();

				/* Print output */
				this.printDependence();
				long elapsedTime = System.currentTimeMillis() - time;
				System.out.println("\nElapsed time: " + elapsedTime + " ms = "
						+ (elapsedTime / 1000) + " s = "
						+ (elapsedTime / (1000 * 60)) + " min");
			}

			/* Do some pre-processing */
			ArrayList<GOALState> allSuccessors = new ArrayList<GOALState>();
			conv.update(source);

			/* Get all action options per action rule */
			// OLD CODE USES CONVERTER: OptionMap<T, A, U> options =
			// this.baseModule.getActionOptions(conv
			// .getMentalState());

			OptionMap<T, A, U> options = this.baseModule.getActionOptions(agent
					.getController().getRunState().getMentalState(), agent
					.getController().getDebugger());

			/*
			 * Create data structures to store action options as transitions,
			 * ordered by by the action rule that generated the option
			 */
			LinkedHashMap<TauClass<T, A, U>, ArrayList<TauStep<T, A, U>>> tauMap = new LinkedHashMap<TauClass<T, A, U>, ArrayList<TauStep<T, A, U>>>();

			/*
			 * Execute action options per action rule, and store them as
			 * transitions
			 */
			ArrayList<TauStep<T, A, U>> taus;
			GOALConversionUniverse universe = conv.getUniverse();
			for (TauClass<T, A, U> tauClass : options.getOptions().keySet()) {

				/*
				 * Iterate the action options generated by the current action
				 * rule
				 */
				taus = new ArrayList<TauStep<T, A, U>>();
				for (ActionCombo act : options.getOptions().get(tauClass)) {

					/* Execute the current action option */
					ActionCombo combo = act;
					// HACK assuming action is executed at top level.
					// not clear if this makes any sense. #2585.
					// ASSUMES no SingleGoal is needed - no focus=select
					agent.getController().doPerformAction(combo);
					// combo.executeAction(conv.getMentalState(), debugger);
					GOALState destination = conv.translate();
					allSuccessors.add(destination);
					conv.update(source);

					/* Store the result of execution as a transition */
					taus.add(this.createTauStep(tauClass, universe, source,
							destination, combo));
				}
				tauMap.put(tauClass, taus);
			}

			/* Compute connected components if necessary */
			if (this.comps == null) {
				switch (TauAnalyzer.ample) {
				case 1:
					this.comps = this.porSingComps();
					break;
				case 2:
					this.comps = this.porMultComps();
					break;
				default:
					throw new Exception();
				}
				this.printComps();
			}

			/* Compute the ample set, and return */
			List<GOALState> porSuccessors = this.ample(tauMap, path);
			return porSuccessors.isEmpty() ? allSuccessors : porSuccessors;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Prints the dependence relation.
	 */
	public void printDependence() {

		try {

			/* Print output */
			System.out.println("\nDependence relation:");

			/* Initialize dependence relation if necessary */
			if (!this.initDependenceInvoked) {
				this.initDependence();
			}

			/* Fetch all classes */
			List<TauClass<T, A, U>> tauclasses = this.baseModule.getClasses();

			/* Print output */
			for (TauClass<T, A, U> tauclass : tauclasses) {
				System.out.println("\n  " + tauclass);
				for (TauClass<T, A, U> depclass : tauclass.getDependence()) {
					System.out.println("   + " + depclass);
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the connected components.
	 */
	public void printComps() {

		try {

			/* Print output */
			System.out.println("\nPOR components:");

			/* Initialize dependence connected components if necessary */
			if (this.comps == null) {
				switch (TauAnalyzer.ample) {
				case 1:
					this.comps = this.porSingComps();
					break;
				case 2:
					this.comps = this.porMultComps();
					break;
				default:
					throw new Exception();
				}
			}

			/* Print output */
			int i = 0;
			for (LMHashSet<TauClass<T, A, U>> c : this.comps) {
				System.out.println("\n  Comp. #" + i + ":");
				for (TauClass<T, A, U> tauClass : c) {
					System.out.println("  + " + tauClass);
				}
				i++;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the enabled-by relation.
	 */
	public void printEnabledBy() {

		try {

			/* Print output */
			System.out.println("\nEnabled-by relation:");

			/* Initialize enabled-by relation if necessary */
			if (!this.initEnabledByInvoked) {
				this.initEnabledBy();
			}

			/* Fetch all classes */
			List<TauClass<T, A, U>> tauclasses = this.baseModule.getClasses();

			/* Print output */
			for (TauClass<T, A, U> tauclass : tauclasses) {
				System.out.println("\n  " + tauclass);
				for (TauClass<T, A, U> enbyclass : tauclass.getEnabledBy()) {
					System.out.println("   + " + enbyclass);
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the visibility relation.
	 */
	public void printVisible() {

		try {

			/* Print output */
			System.out.println("\nVisibility relation:\n");

			/* Initialize enabled-by relation if necessary */
			if (!this.initVisibleInvoked) {
				this.initVisible();
			}

			/* Fetch all classes */
			List<TauClass<T, A, U>> tauclasses = this.baseModule.getClasses();

			/* Print output */
			boolean printed = false;
			for (TauClass<T, A, U> tauclass : tauclasses) {
				if (tauclass.isVisible()) {
					System.out.println("  " + tauclass);
					printed = true;
				}
			}
			if (!printed) {
				System.out.println("  empty");
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Slices the program with respect to the vocabulary of the property as
	 * passed to the constructor of this class.
	 */
	public void slice() {

		try {

			/* Set timer */
			long time = System.currentTimeMillis();

			/* Print output */
			System.out.println("\n[SLICER]");
			System.out.println("\nCriteria:\n");
			for (MentalStateCond msc : this.voc) {
				System.out.println("  " + msc);
			}

			/* Initialize visibility and enabled-by relation */
			if (!this.initVisibleInvoked) {
				this.initVisible();
				this.printVisible();
			}
			if (!this.initEnabledByInvoked) {
				this.initEnabledBy();
				this.printEnabledBy();
			}

			/* Fetch all classes */
			List<TauClass<T, A, U>> tauclasses = this.baseModule.getClasses();

			/* Compute visible classes */
			ArrayList<TauClass<T, A, U>> visible = new ArrayList<TauClass<T, A, U>>();
			for (TauClass<T, A, U> tauclass : tauclasses) {
				if (tauclass.isVisible()) {
					visible.add(tauclass);
				}
			}

			/* Reachability analysis of visible classes */
			LMHashSet<TauClass<T, A, U>> retained = new LMHashSet<TauClass<T, A, U>>();
			for (TauClass<T, A, U> tauclass : tauclasses) {
				boolean retain = false;

				/* Inspect reachability */
				for (TauClass<T, A, U> visibleclass : visible) {
					retained.add(visibleclass);
					if (this.isEnablsReachable(tauclass, visibleclass)) {
						retain = true;
						break;
					}
				}
				if (!retain) {
					break;
				}
				retained.add(tauclass);
			}

			/* Hide transition classes (i.e. action rules) to be sliced */
			LMHashSet<TauClass<T, A, U>> sliced = new LMHashSet<TauClass<T, A, U>>();
			for (TauClass<T, A, U> tauclass : tauclasses) {
				if (!retained.contains(tauclass)) {
					sliced.add(tauclass);
					tauclass.hide();
				}
			}

			/* Print output */
			System.out.println("\nRetained rules:\n");
			for (TauClass<T, A, U> tauclass : retained) {
				System.out.println("  " + tauclass);
			}
			System.out.println("\nSliced rules:\n");
			for (TauClass<T, A, U> tauclass : sliced) {
				System.out.println("  " + tauclass);
			}
			long elapsedTime = System.currentTimeMillis() - time;
			System.out.println("\nElapsed time: " + elapsedTime + " ms = "
					+ (elapsedTime / 1000) + " s = "
					+ (elapsedTime / (1000 * 60)) + " min");
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// Private methods
	//

	/**
	 * Computes an ample set (if one exists) given the steps, ordered by
	 * transition class, that are enabled. The specified path is used for
	 * checking condition C3.
	 *
	 * @param map
	 *            - The steps that are enabled in this mental state.
	 * @param path
	 *            - The current search path of the NDFS exploration.
	 */
	private List<GOALState> ample(
			LinkedHashMap<TauClass<T, A, U>, ArrayList<TauStep<T, A, U>>> map,
			LMHashSet<State> path) {

		try {
			ArrayList<GOALState> porSuccessors = new ArrayList<GOALState>();

			/* Iterate over all connected components */
			for (LMHashSet<TauClass<T, A, U>> c : this.comps) {
				boolean ample = true;

				/*
				 * Iterate over all transition classes in a connected component
				 */
				for (TauClass<T, A, U> tauClass : c) {

					/*
					 * If there are no enabled steps belonging to a transition
					 * in the transition class, continue
					 */
					if (!map.containsKey(tauClass)) {
						continue;
					}

					/* Check condition C2 (invisibility) */
					if (tauClass.isVisible()) {
						ample = false;
						break;
					}

					/* Check condition C3' (cycle closing) */
					boolean condition3 = true;
					for (TauStep<T, A, U> tau : map.get(tauClass)) {
						if (path.contains(tau.getDestination())) {
							condition3 = false;
							break;
						}
					}
					if (!condition3) {
						ample = false;
						break;
					}
				}

				if (!ample) {
					continue;
				}

				/*
				 * If this point is reached, the transition classes in the
				 * current connected component constitute an ample set
				 */
				for (TauClass<T, A, U> tauClass : c) {
					if (!map.containsKey(tauClass)) {
						continue;
					}
					for (TauStep<T, A, U> tau : map.get(tauClass)) {
						porSuccessors.add(tau.getDestination());
					}
				}

				/*
				 * We need this additional check because it may be that neither
				 * of the transition classes in the connected component have an
				 * enabled transition, in which case we should continue
				 * searching for an ample set
				 */
				if (!porSuccessors.isEmpty()) {
					break;
				}
			}

			/* Return */
			return porSuccessors;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Compute connected components according to Heur_POR^sing. This means that
	 * each transition class is treated in isolation, and the only relevant
	 * connected components are singletons. A connected component is relevant if
	 * the transitions it contains are guaranteed to satisfy condition C1.
	 *
	 * @return The relevant connected components.
	 */
	private LMHashSet<LMHashSet<TauClass<T, A, U>>> porSingComps() {

		try {
			long time = System.currentTimeMillis();

			/* Declare data structures */
			LMHashSet<LMHashSet<TauClass<T, A, U>>> comps = new LMHashSet<LMHashSet<TauClass<T, A, U>>>();
			LMHashSet<TauClass<T, A, U>> comp;

			/* Iterate over all transition classes */
			for (TauClass<T, A, U> tauClass : this.baseModule.getClasses()) {

				/*
				 * If this transition class is only dependent on itself, and
				 * cannot be enabled by transitions outside this transition
				 * class, then it constitutes a singleton component.
				 */
				if (tauClass.getDependence().size() == 1
						&& (tauClass.getEnabledBy().isEmpty() || (tauClass
								.getEnabledBy().size() == 1 && tauClass
								.getEnabledBy().contains(tauClass)))) {

					comp = new LMHashSet<TauClass<T, A, U>>();
					comp.add(tauClass);
					comps.add(comp);
				}
			}

			/* Print output */
			long elapsedTime = System.currentTimeMillis() - time;
			System.out.println("\nElapsed time: " + elapsedTime + " ms = "
					+ (elapsedTime / 1000) + " s = "
					+ (elapsedTime / (1000 * 60)) + " min");

			return comps;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Compute connected components according to Heur_POR^mult. This means that
	 * connected components are sought in the dependence graph, which are
	 * subsequently filtered to make sure that each such connected component
	 * does not have a transition class that can be enabled by a transition
	 * class outside the connected component. Only the connected components for
	 * which this holds, i.e. whose transition classes are guaranteed to satisfy
	 * condition C1, i.e. the relevant connected components, are returned,
	 *
	 * @return The relevant connected components.
	 */
	private LMHashSet<LMHashSet<TauClass<T, A, U>>> porMultComps() {

		try {
			long time = System.currentTimeMillis();

			/*
			 * Declare data structure that will contain all components to be
			 * computed
			 */
			LMHashSet<LMHashSet<TauClass<T, A, U>>> comps = new LMHashSet<LMHashSet<TauClass<T, A, U>>>();

			/*
			 * Initialize the set of seed transition classes from which
			 * connected components will be computed; initially, every
			 * transition class can serve as seed
			 */
			LMHashSet<TauClass<T, A, U>> seeds = new LMHashSet<TauClass<T, A, U>>();
			for (TauClass<T, A, U> tauClass : this.baseModule.getClasses()) {
				seeds.add(tauClass);
			}

			/* Compute connected components while there are seeds left */
			while (!seeds.isEmpty()) {

				/* Pick an arbitrary transition class as seed */
				TauClass<T, A, U> tauClass = seeds.iterator().next();

				/* Declare a new component */
				LMHashSet<TauClass<T, A, U>> c = new LMHashSet<TauClass<T, A, U>>();

				/*
				 * Create a to-do stack, and push the seed transition class on
				 * it
				 */
				Stack<TauClass<T, A, U>> todo = new Stack<TauClass<T, A, U>>();
				todo.add(tauClass);

				/*
				 * While the to-do stack is not empty, add the transition
				 * classes that are dependent on transition classes on the stack
				 * to the connected component; all transition classes that have
				 * already been added to a connected component cannot serve as a
				 * seed for another connected component anymore, and are hence
				 * removed from seeds.
				 */
				while (!todo.empty()) {
					TauClass<T, A, U> tauClassX = todo.pop();
					c.add(tauClassX);
					seeds.remove(tauClassX);
					for (TauClass<T, A, U> tauClassY : tauClassX
							.getDependence()) {
						if (!c.contains(tauClassY)) {
							todo.add(tauClassY);
						}
					}
				}

				/* Add the new component to the set of components */
				comps.add(c);
			}

			/*
			 * Filter the connected components by determining for each such
			 * component whether there exists a transition class outside the
			 * component that can enable a transition belonging to a transition
			 * class in the component; if this is the case, the respective
			 * connected component can never satisfy C1 and is hence useless
			 */
			LMHashSet<LMHashSet<TauClass<T, A, U>>> filtered = new LMHashSet<LMHashSet<TauClass<T, A, U>>>();
			for (LMHashSet<TauClass<T, A, U>> c : comps) {
				boolean useless = false;

				/* Iterate all transition classes in this connected component */
				for (TauClass<T, A, U> tauClass : c) {

					/*
					 * If there exists a transition class that can enable a
					 * transition class in the connected component, but that is
					 * not member of this component, the component is useless
					 */
					for (TauClass<T, A, U> enClass : tauClass.getEnabledBy()) {
						if (!c.contains(enClass)) {
							useless = true;
							break;
						}
					}
					if (useless) {
						break;
					}
				}

				/*
				 * If the connected component is not useless, add it to the set
				 * of filtered components
				 */
				if (!useless) {
					filtered.add(c);
				}
			}

			/* Print output */
			long elapsedTime = System.currentTimeMillis() - time;
			System.out.println("\nElapsed time: " + elapsedTime + " ms = "
					+ (elapsedTime / 1000) + " s = "
					+ (elapsedTime / (1000 * 60)) + " min");

			/* Return */
			return filtered;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Initialize the dependence relation between transition classes in the base
	 * transition module.
	 */
	private void initDependence() {
		for (TauClass<T, A, U> tauClass1 : this.baseModule.getClasses()) {
			tauClass1.addDependence(tauClass1);
			for (TauClass<T, A, U> tauClass2 : this.baseModule.getClasses()) {
				if (!this.areIndependent(tauClass1, tauClass2)) {
					tauClass1.addDependence(tauClass2);
					tauClass2.addDependence(tauClass1);
				}
			}
		}
		this.initDependenceInvoked = true;
	}

	/**
	 * Initialize the enabled-by relation between transition classes in the base
	 * transition module.
	 */
	private void initEnabledBy() {
		for (TauClass<T, A, U> tauClass1 : this.baseModule.getClasses()) {
			for (TauClass<T, A, U> tauClass2 : this.baseModule.getClasses()) {
				if (this.canEnabledBy(tauClass1, tauClass2)) {
					tauClass1.addEnables(tauClass2);
					tauClass2.addEnabledBy(tauClass1);
				}
			}
		}
		this.initEnabledByInvoked = true;
	}

	/**
	 * Initialize the visibility relation for transition classes in the base
	 * transition module with respect to the mental state conditions in
	 * {@link #voc}.
	 */
	private void initVisible() {
		for (TauClass<T, A, U> tauClass : this.baseModule.getClasses()) {
			tauClass.setVisible(this.isVisible(tauClass, this.voc));
		}
		this.initVisibleInvoked = true;
	}

	/**
	 * Determines whether there exists a route from <code>source</code> to
	 * <code>destination</code> in the influence graph.
	 *
	 * @param source
	 *            - The source transition class.
	 * @param destination
	 *            - The destination transition class.
	 * @return <code>true</code> if a route exists; <code>false</code>
	 *         otherwise.
	 */
	private boolean isEnablsReachable(TauClass<T, A, U> source,
			TauClass<T, A, U> destination) {

		try {

			/*
			 * Create data structure that maintains which transition classes
			 * have already been visited during the search
			 */
			LMHashSet<TauClass<T, A, U>> visited = new LMHashSet<TauClass<T, A, U>>();

			/*
			 * Declare to-do stack containing transition classes that have not
			 * yet been visited but that are reachable from source
			 */
			Stack<TauClass<T, A, U>> todo = new Stack<TauClass<T, A, U>>();
			todo.push(source);
			while (!todo.empty()) {
				TauClass<T, A, U> tauClass = todo.pop();

				/* If the destination if found, return true */
				if (tauClass == destination) {
					return true;
				}

				/*
				 * If we had not visit this transition class before, add it to
				 * the visited set, and consider the transition classes that are
				 * reachable from it
				 */
				if (!visited.contains(tauClass)) {
					visited.add(tauClass);
					for (TauClass<T, A, U> enabls : tauClass.getEnables()) {
						todo.add(enabls);
					}
				}
			}

			/*
			 * If this point is reached, a route from source to destination has
			 * not been found
			 */
			return false;
		}

		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}