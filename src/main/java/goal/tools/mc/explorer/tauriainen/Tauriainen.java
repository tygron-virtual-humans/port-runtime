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

package goal.tools.mc.explorer.tauriainen;

import goal.tools.mc.core.Directives;
import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.evaluator.Evaluator;
import goal.tools.mc.explorer.Explorer;
import goal.tools.mc.program.Program;
import goal.tools.mc.property.Property;
import goal.tools.mc.property.ltl.Formula;
import goal.tools.mc.property.ltl.Next;
import goal.tools.mc.property.ltl.Proposition;
import goal.tools.mc.property.ltl.Until;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents an explorer component of a model checker that uses Tauriainen's
 * generalized NDFS algorithm as exploration algorithm of the product automaton.
 *
 * @author sungshik
 */
public class Tauriainen implements Explorer {

	//
	// Private fields
	//

	/**
	 * The set of acceptance conditions.
	 */
	private final Map<Next, LMHashSet<ProductState>> curlyF;

	/**
	 * Reference {@link BitSet} for fast comparison with another {@link BitSet}
	 * object indicating which acceptance conditions are satisfied by the
	 * product state to which it belongs. This instance has only 1-bits.
	 */
	private BitSet curlyFComperator;

	/**
	 * Indicates whether exploration is done.
	 */
	private boolean done;

	/**
	 * The current path being traversed by the first search as an unordered set
	 * of states (in contrast to the ordered stack {@link #path}.
	 */
	private LMHashSet<State> firstSearchPath;

	/**
	 * The evaluator component.
	 */
	private final Evaluator eval;

	/**
	 * The current path being traversed by the first search.
	 */
	private Stack<ProductState> path;

	/**
	 * The product automaton.
	 */
	private final Product prod;

	/**
	 * The program automaton.
	 */
	private final Program prog;

	/**
	 * The property automaton.
	 */
	private final Property prop;

	/**
	 * Maintains statistics (on resource consumption) during the search.
	 */
	private Statistics stats;

	/**
	 * Special reference to the "trivial acceptance condition" that contains all
	 * product states (exists because the only acceptance condition of the
	 * program automaton contains all program states).
	 */
	private LMHashSet<ProductState> trivialF;

	//
	// Constructors
	//

	/**
	 * Constructs an instance of this class, given the specified program,
	 * property and evaluator components.
	 *
	 * @param prog
	 *            - The program component.
	 * @param prop
	 *            - The property component.
	 * @param eval
	 *            - The evaluator component.
	 */
	public Tauriainen(Program prog, Property prop, Evaluator eval) {
		this.prog = prog;
		this.prop = prop;
		this.eval = eval;

		/* Initialize acceptance conditions */
		this.curlyF = initCurlyF(prop.getProperty());

		/* Initialize product automaton */
		this.prod = new Product(this.prog, this.prop, this.curlyF.size());
	}

	//
	// Public methods
	//

	@Override
	public LMHashSet<State> getFirstSearchPath() {
		return firstSearchPath;
	}

	@Override
	public void start() {

		try {

			/* Print output */
			System.out.println();
			System.out.println("[STARTING SEARCH]");

			/* Initialize data structures */
			path = new Stack<ProductState>();
			firstSearchPath = new LMHashSet<State>();

			/* Start search */
			stats = new Statistics();
			Reporter.next = stats.start + Reporter.interval;
			done = false;
			firstSearch();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// Private methods
	//

	/**
	 * Aborts the model checking procedure, because a counterexample is found. A
	 * full counter example is subsequently generated, and stored.
	 */
	private void abort() {

		try {

			/* Print output */
			System.out.println();
			System.out.println("[SEARCH COMPLETE] Counterexample found");
			stats.exMemory = prog.getMemoryConsumption();
			Reporter.printReport(prod.getSize(), eval.getDistinctEvaluated(),
					stats);

			/* Generate and print counter example */
			Reporter.printCounterexample(counterexample());

			/* Print other information about the search */
			stats.exMemory = prog.getMemoryConsumption();
			Reporter.printReport(prod.getSize(), eval.getDistinctEvaluated(),
					stats);
			done = true;

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Does a depth-first search through the state space to check whether there
	 * exists a path on which the negated property holds. If this is the case, a
	 * counterexample is found, and the search halts. During the search, a
	 * nested depth-first search may be started.
	 */
	private void firstSearch() {

		try {

			/* Push the initial state as the first element on the path stack */
			path.push(prod.getInitial());
			if (Directives.POR) {
				firstSearchPath.add(path.peek().qProg());
			}

			/* Search the state space until all reachable states are visited */
			ProductState q, qSucc;
			ProductState[] successors;
			while (path.size() > 0) {

				/* Fetch the top element of the path stack and get successors */
				q = path.peek();
				successors = successors(q);
				if (done) {
					return;
				}

				/* Print output if required */
				if (Directives.PRINT_TREE) {
					Reporter.printState(q, path.size() * 2);
				}

				/*
				 * Explore the state space in a depth-first manner by iterating
				 * over the successors of q
				 */
				for (int i = 0; i < successors.length; i++) {
					qSucc = successors[i];

					/*
					 * If qSucc, a successor of q, has not yet been visited,
					 * continue the search from qSucc
					 */
					if (!visited(qSucc)) {
						q = qSucc;

						/* Push the new q, formerly qSucc, on the path stack */
						path.push(q);
						if (Directives.POR) {
							firstSearchPath.add(path.peek().qProg());
						}

						/* Get the successors of q */
						successors = successors(q);
						if (done) {
							return;
						}

						/* Print output if required */
						if (Directives.PRINT_TREE) {
							Reporter.printState(q, path.size() * 2);
						}

						/* Reset i, because successors are changed. */
						i = -1;
					}
				}

				/* Determine which acceptance conditions q already fulfills */
				BitSet condsFulfilled = q.getFulfilled();
				BitSet selfFulfilled = selfFulfilled(q);

				/*
				 * If q fulfills any acceptance condition (and is not the
				 * initial state), combine condsFulfilled and selfFulfilled, and
				 * start the nested search
				 */
				if (path.size() > 1
						&& (!condsFulfilled.isEmpty() || !selfFulfilled
								.isEmpty())) {
					selfFulfilled.or(condsFulfilled);
					secondSearch(q, selfFulfilled);
					if (done) {
						return;
					}

					/*
					 * If q fulfills all acceptance conditions, a counterexample
					 * is found, and the algorithm may abort
					 */
					if (curlyFComperator.equals(q.getFulfilled())) {
						abort();
						if (done) {
							return;
						}
					}
				}

				/*
				 * Remove the top element from the path stack, as all its
				 * successors have been explored
				 */
				if (Directives.PRINT_TREE) {
					System.out.println();
					String indent = "";
					for (int j = 0; j < (path.size() - 1) * 2; j++) {
						indent += " ";
					}
					System.out.println(indent + " <- Backtracking to previous");
				}
				if (Directives.POR) {
					firstSearchPath.remove(path.peek().qProg());
				}
				path.pop();
			}

			/*
			 * If this code is ever reached, no counterexample was found during
			 * the search
			 */
			System.out.println();
			System.out.println("[SEARCH COMPLETE] No counterexample found");
			stats.exMemory = prog.getMemoryConsumption();
			Reporter.printReport(prod.getSize(), eval.getDistinctEvaluated(),
					stats);
			done = true;
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the counterexample, provided that it has already been
	 * established that one exists (i.e. this method does not re-check whether
	 * the current search path indeed leads to a cycle on which the property is
	 * violated).
	 *
	 * @return The counter example.
	 */
	private Counterexample counterexample() {

		try {

			/* Return variable */
			Counterexample ce = new Counterexample();

			/*
			 * Get the prefix up to (but not including) the first state on the
			 * cycle. Drop the first step which is apparently symbolic anyway
			 * and just creates the GOAL initial state. #1495.
			 */
			for (int i = 1; i < path.size() - 1; i++) {
				String action = prog.getPerformedAction(path.get(i).qProg(),
						path.get(i + 1).qProg());
				ce.pre.put(path.get(i).qProg(), action);
			}

			/* Construct the cycle */
			Stack<ProductState> cycle = new Stack<ProductState>();
			LMHashSet<ProductState> visited = new LMHashSet<ProductState>();
			cycle.push(path.peek());
			boolean cycleFound = false;
			while (!cycle.empty() || cycleFound) {

				/* Get the current top of the cycle stack */
				ProductState q = cycle.peek();

				/* If it has successors, proceed */
				if (prod.getSuccessors(q) != null) {

					/* Iterate the q's successors */
					for (int i = 0; i < prod.getSuccessors(q).length; i++) {
						ProductState qSucc = prod.getSuccessors(q)[i];

						/*
						 * If the successor has already been visited during this
						 * cycle search, continue with the next
						 */
						if (visited.contains(qSucc)) {
							continue;
						}

						/*
						 * Mark this successor visited, and push it on the cycle
						 */
						visited.add(qSucc);
						cycle.push(qSucc);

						/* If the cycle is closed, the cycle is found */
						if (cycle.peek().equals(path.peek())) {
							cycleFound = true;
							break;
						}

						/* Otherwise, continue the depth-first traversal */
						else {
							q = qSucc;
							i = -1;
						}
					}

					/* If the cycle has been found, break */
					if (cycleFound) {
						break;
					} else {
						cycle.pop();
					}
				}
			}

			/* Add the cycle to the counterexample */
			for (int i = 0; i < cycle.size() - 1; i++) {
				String action = prog.getPerformedAction(cycle.get(i).qProg(),
						cycle.get(i + 1).qProg());
				ce.cyc.put(cycle.get(i).qProg(), action);
			}

			/* Return */
			return ce;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Computes the acceptance conditions that should be imposed by inspecting
	 * the specified formula.
	 *
	 * @return A mapping from containing all acceptance conditions.
	 */
	private Map<Next, LMHashSet<ProductState>> initCurlyF(Formula f) {

		try {

			/* Create the map of acceptance conditions */
			LinkedHashMap<Next, LMHashSet<ProductState>> map = new LinkedHashMap<Next, LMHashSet<ProductState>>();
			trivialF = new LMHashSet<ProductState>();
			map.put(new Next(new Proposition("TRIVIAL")), trivialF);

			/*
			 * Obtain the until formulas in the property to be checked, and add
			 * them to the map
			 */
			LMHashSet<Until> untils = f.untils();
			for (Until u : untils) {
				map.put(new Next(u), new LMHashSet<ProductState>());
			}

			/* Instantiate a BitSet with all bits set to 1 */
			curlyFComperator = new BitSet(map.size());
			curlyFComperator.set(0, map.size());

			/* Print output */
			System.out.print("Acceptance conditions: trivial condition");
			for (Until u : untils) {
				System.out.print(", " + u);
			}
			System.out.println();

			/* Return */
			return map;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Does a second search through states already visited during the first
	 * search, starting from the specified states. The aim is to update all
	 * states that are reachable from the specified state with the acceptance
	 * conditions that it fulfills.
	 *
	 * @param q
	 *            - The state to start the second search from.
	 * @param condsToPropagate
	 *            - The acceptance conditions to propagate.
	 */
	private void secondSearch(ProductState q, BitSet condsToPropagate) {

		try {

			/* Pre-definition of data structures */
			ProductState[] successors;
			ProductState qSucc;
			BitSet fulfilledConds, condsToPropagateClone;

			/* Start the second search from q */
			ArrayList<ProductState> todo = new ArrayList<ProductState>();
			todo.add(q);
			while (!todo.isEmpty()) {
				q = todo.remove(0);

				/* Get successors of the current state under investigation */
				successors = successors(q);
				if (done) {
					return;
				}

				/* Continue the search from successors */
				for (ProductState successor : successors) {
					qSucc = successor;

					/*
					 * Only consider successors that have already been visited
					 * during the first search
					 */
					if (visited(qSucc)) {

						/*
						 * Check if the acceptance conditions to propagate are
						 * not already marked fulfilled by qSucc
						 */
						fulfilledConds = qSucc.getFulfilled();
						condsToPropagateClone = (BitSet) condsToPropagate
								.clone();
						condsToPropagateClone.andNot(fulfilledConds);
						if (!condsToPropagateClone.isEmpty()) {

							/*
							 * Update acceptance conditions of successor with
							 * condsToPropagate
							 */
							fulfilledConds.or(condsToPropagate);

							/* Add qSucc to the to-do list */
							todo.add(qSucc);
						}
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines which acceptance conditions are fulfilled by the specified
	 * state in that itself.
	 *
	 * @param q
	 *            - The state to check acceptance condition fulfillment for.
	 * @return A {@link BitSet}, where the index of each 1-bit corresponds to
	 *         the index of an acceptance condition in {@link #curlyF} that is
	 *         fulfilled.
	 */
	private BitSet selfFulfilled(ProductState q) {

		try {

			/* Return variable */
			BitSet selfFulfilled = new BitSet();

			/* Iterate acceptance conditions */
			int i = 0;
			for (Next n : curlyF.keySet()) {
				if (curlyF.get(n).contains(q)) {
					selfFulfilled.set(i);
				}
				i++;
			}
			return selfFulfilled;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Compute the successors of the specified state.
	 *
	 * @param q
	 *            - The state whose successors are to be computed.
	 * @return The successors.
	 */
	private ProductState[] successors(ProductState q) {

		try {

			/* Check if thread is interrupted */
			if (Thread.currentThread().isInterrupted()) {
				stats.exMemory = prog.getMemoryConsumption();
				Reporter.printReport(prod.getSize(),
						eval.getDistinctEvaluated(), stats);
				done = true;
				return null;
			}

			/* Check if successors have already been defined for q */
			if (prod.hasSuccessorsYet(q)) {
				return prod.getSuccessors(q);
			}

			/* Run garbage collector if required */
			if (Directives.EXPLICIT_GC) {
				System.gc();
			}

			/* Report on progress */
			if (Reporter.reportNow()) {
				stats.exMemory = prog.getMemoryConsumption();
				Reporter.printReport(prod.getSize(),
						eval.getDistinctEvaluated(), stats);
			}

			/* Measure memory */
			stats.updateInMemory();

			/* Return variable */
			ProductState[] successors;

			/*
			 * Check whether the program state does not violate the
			 * propositional information contained in the property state (in
			 * automaton terms, this is label comparison)
			 */
			if (eval.entailsAll(q.qProg(), q.qProp().getLiterals())) {

				/* Compute successors */
				successors = prod.getSuccessors(q);
				if (successors == null) {
					abort();
					return null;
				}

				/* Iterate successors to update acceptance conditions */
				trivialF.add(q);
				for (ProductState qSucc : successors) {
					LMHashSet<Formula> formulas = qSucc.qProp().getFormulas();

					/* Update acceptance conditions in curlyF */
					for (Next n : curlyF.keySet()) {
						if (curlyF.get(n) != trivialF) {
							if (!formulas.contains(n)
									|| formulas.contains(((Until) n.getArg())
											.getRightArg())) {
								curlyF.get(n).add(qSucc);
							}
						}
					}
				}
			}

			/*
			 * If the program state does violate the propositional part of the
			 * property state, then q is not assigned any successors
			 */
			else {
				successors = prod.setNoSuccessors(q);
			}

			/* Return */
			return successors;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Determines whether the specified state has already been visited during
	 * the first search.
	 *
	 * @param q
	 *            - The state to determine whether it has been visited for.
	 * @return <code>true</code> if the state has been visited;
	 *         <code>false</code> otherwise.
	 */
	private boolean visited(ProductState q) {
		return prod.hasSuccessorsYet(q);
	}
}

/**
 * Represents a counterexample on which the property is violated.
 *
 * @author sungshik
 *
 */
class Counterexample {

	/**
	 * The prefix of the counterexample.
	 */
	Map<State, String> pre = new LinkedHashMap<State, String>();

	/**
	 * The cycle of the counter example.
	 */
	Map<State, String> cyc = new LinkedHashMap<State, String>();

	/**
	 * Gets the size of this counterexample.
	 *
	 * @return The size.
	 */
	int getLength() {
		return pre.size() + cyc.size();
	}
}
