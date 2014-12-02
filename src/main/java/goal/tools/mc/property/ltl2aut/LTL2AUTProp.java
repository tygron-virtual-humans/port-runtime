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

package goal.tools.mc.property.ltl2aut;

import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.property.PropState;
import goal.tools.mc.property.Property;
import goal.tools.mc.property.ltl.Formula;
import goal.tools.mc.property.ltl.Next;
import goal.tools.mc.property.ltl.Proposition;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Represents a property automaton, computed by the algorithm of Daniele et al.
 * (1999).
 *
 * @author sungshik
 */
public class LTL2AUTProp implements Property {

	//
	// Private fields
	//

	/**
	 * The transition function of this automaton.
	 */
	private final Map<PropState, LTL2AUTState[]> delta = new HashMap<PropState, LTL2AUTState[]>();

	/**
	 * A mapping from states to unique indices for the sake of giving each state
	 * a unique identifier.
	 */
	private final Map<PropState, Integer> index = new HashMap<PropState, Integer>();

	/**
	 * The dummy source state of the property automaton.
	 */
	private LTL2AUTState initial;

	/**
	 * The (negated) property under investigation.
	 */
	private Formula property;

	//
	// Constructors
	//

	/**
	 * Constructs a property automaton for the specified LTL formula. The
	 * automaton itself, however, is not yet constructed as this may occur
	 * on-the-fly. To create the automaton, call {@link #generate()}.
	 */
	public LTL2AUTProp(Formula f) {

		try {
			this.property = f;

			/* Set dummy states */
			LMHashSet<Formula> formulas = new LMHashSet<Formula>();
			formulas.add(new Next(new Proposition("source")));
			this.initial = new LTL2AUTState(formulas);

			/* Initialize delta and index */
			LMHashSet<Formula> toCover = new LMHashSet<Formula>();
			toCover.add(property);
			LMHashSet<LMHashSet<Formula>> cover = LTL2AUTCover.cover(toCover);
			this.delta.put(this.initial, LTL2AUTState.toStates(cover));
			this.index.put(this.initial, this.delta.size() - 1);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	// Public methods
	//

	@Override
	public void generate() {

		try {

			/*
			 * Initialize a to-do stack containing states for which successors
			 * have not yet been defined
			 */
			Stack<State> todo = new Stack<State>();
			for (State q : delta.get(initial)) {
				todo.push(q);
			}

			/*
			 * As long as the to-do stack is not empty, define successors for
			 * states on this stack
			 */
			while (!todo.isEmpty()) {
				LTL2AUTState q = (LTL2AUTState) todo.pop();

				/*
				 * Define successors (the call to getSuccessors already adds
				 * these successors to delta), and iterate
				 */
				for (State qSucc : getSuccessors(q)) {
					if (!delta.containsKey(qSucc)) {
						todo.push(qSucc);
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public LTL2AUTState getInitial() {
		return initial;
	}

	@Override
	public Formula getProperty() {
		return property;
	}

	@Override
	public int getSize() {
		return delta.size();
	}

	@Override
	public LTL2AUTState[] getSuccessors(State q_) {

		try {
			LTL2AUTState q = (LTL2AUTState) q_;

			/* Fetch successors */
			LTL2AUTState[] successors = delta.get(q);

			/* If successors have not yet been defined yet, define them */
			if (successors == null) {

				/*
				 * Compute the cover of the stripped next formulas occurring in
				 * q
				 */
				LMHashSet<LMHashSet<Formula>> cover = LTL2AUTCover
						.cover(Formula.nextArgs(q.getFormulas()));

				/* Put successors in an array */
				successors = new LTL2AUTState[cover.size()];
				int i = 0;
				for (LMHashSet<Formula> set : cover) {
					successors[i] = new LTL2AUTState(set);
					i++;
				}

				/* Define delta and index */
				delta.put(q, successors);
				index.put(q, delta.size() - 1);
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
	 * Prints the (partially constructed) automaton that this object represents.
	 */
	public void printAutomaton() {

		try {

			/* Print states */
			System.out.println("\nStates:");
			for (State s : delta.keySet()) {
				System.out.println("    q" + index.get(s) + " : "
						+ s.toString());
			}

			/* Print transitions */
			System.out.println("\nTransitions:");
			for (State s : delta.keySet()) {
				System.out.print("    q" + index.get(s) + " -> ");
				for (State qSucc : delta.get(s)) {
					System.out.print("q" + index.get(qSucc));
					System.out.print(", ");
				}
				System.out.println();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}