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

import goal.tools.mc.core.Automaton;
import goal.tools.mc.core.State;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.program.Program;
import goal.tools.mc.property.PropState;
import goal.tools.mc.property.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the product automaton used with Tauriainen's NDFS algorithm. It
 * may be possible to reuse this automaton for more other NDFS algorithms as
 * well, and maybe also for strongly connected component algorithms.
 *
 * @author sungshik
 *
 */
public class Product implements Automaton {

	//
	// Private fields
	//

	/**
	 * The transition function of this automaton.
	 */
	private final Map<ProductState, ProductState[]> delta = new HashMap<ProductState, ProductState[]>();

	/**
	 * The program automaton.
	 */
	private final Program prog;

	/**
	 * The property automaton.
	 */
	private final Property prop;

	/**
	 * The number of acceptance conditions.
	 */
	private final int sizeCurlyF;

	/**
	 * The states of this automaton. In principle, this information can also be
	 * derived from {@link delta}, but as we frequently need to check whether a
	 * new {@link ProductState} object already exists, it is more efficient to
	 * maintain the set of all known states in a separate data structure.
	 */
	private final LMHashSet<ProductState> states = new LMHashSet<ProductState>();

	//
	// Constructors
	//

	/**
	 * Constructs a product automaton according to the specified parameters.
	 *
	 * @param prog
	 *            - The program automaton.
	 * @param prop
	 *            - The property automaton.
	 * @param sizeCurlyF
	 *            - The number of acceptance conditions.
	 */
	public Product(Program prog, Property prop, int sizeCurlyF) {
		this.prog = prog;
		this.prop = prop;
		this.sizeCurlyF = sizeCurlyF;
	}

	//
	// Public methods
	//

	@Override
	public void generate() {

		try {

			/* This method has not been implemented */
			throw new Exception();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

	@Override
	public ProductState getInitial() {
		return new ProductState(prog.getInitial(),
				(PropState) prop.getInitial(), 0);
	}

	@Override
	public int getSize() {
		return delta.size();
	}

	@Override
	public ProductState[] getSuccessors(State _q) {

		try {

			/*
			 * Define successors only if they have not been defined for _q
			 * before
			 */
			ProductState q = (ProductState) _q;
			ProductState[] successors = delta.get(q);
			if (successors == null) {

				/* Get successors of the property part of q */
				State[] propSuccessors = prop.getSuccessors(q.qProp());

				/* Special sink state case */
				if (propSuccessors.length == 1
						&& ((PropState) propSuccessors[0]).getFormulas()
						.isEmpty()) {
					setNoSuccessors(q);
					return null;
				} else {
					/* Get successors of the program part of q */
					State[] progSuccessors = prog.getSuccessors(q.qProg());

					/* Compute product */
					successors = product(progSuccessors, propSuccessors);
				}

				/* Update delta */
				delta.put(q, successors);
			}
			return successors;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Determines whether successors have already been defined for the specified
	 * state.
	 *
	 * @param q
	 *            - The state to check successor definition for.
	 * @return <code>true</code> if successors have been defined;
	 *         <code>false</code> otherwise.
	 */
	public boolean hasSuccessorsYet(ProductState q) {
		return delta.containsKey(q);
	}

	/**
	 * Sets the set of successors of the specified state to empty.
	 *
	 * @param q
	 *            - The state to set successors for.
	 * @return The specific empty array now associated with <code>q</code>.
	 */
	public ProductState[] setNoSuccessors(ProductState q) {
		ProductState[] successors = new ProductState[0];
		delta.put(q, successors);
		return successors;
	}

	//
	// Private methods
	//

	/**
	 * Computes the product of states in <tt>progSuccessors</tt> with states in
	 * <tt>propSuccessors</tt>.
	 *
	 * @param progSuccessors
	 *            - A set of states of the program automaton.
	 * @param propSuccessors
	 *            - A set of states of the property automaton.
	 */
	private ProductState[] product(State[] progSuccessors,
			State[] propSuccessors) {

		try {

			/* Return variable */
			ArrayList<ProductState> product = new ArrayList<ProductState>(
					progSuccessors.length * propSuccessors.length);

			/*
			 * Compute all combinations of program states in progSuccessors and
			 * property states in propSuccessors
			 */
			ProductState qSucc, qSuccX;
			for (State qProg : progSuccessors) {
				for (State qProp : propSuccessors) {

					/*
					 * Create the new successor, and check whether this
					 * successor already exists; if this check is not carried
					 * out, multiple instances of conceptually the same state
					 * may exist, which is undesirable
					 */
					qSucc = new ProductState(qProg, (PropState) qProp,
							sizeCurlyF);
					qSuccX = states.containsAndGet(qSucc);
					if (qSuccX != null) {
						product.add(qSuccX);
					} else {
						product.add(qSucc);
						states.add(qSucc);
					}
				}
			}

			/* Return */
			ProductState[] array = new ProductState[product.size()];
			product.toArray(array);
			return array;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
