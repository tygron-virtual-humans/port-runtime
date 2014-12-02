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

import goal.tools.mc.core.Jenkins;
import goal.tools.mc.core.State;
import goal.tools.mc.property.PropState;

import java.util.BitSet;

/**
 * Represents a product state (consisting of a program state and a property
 * state) of a product automaton as represented by {@link Product}.
 * 
 * @author sungshik
 *
 */
public class ProductState implements State {

	//
	// Private fields
	//

	/**
	 * The "check list" indicating which acceptance conditions have been
	 * fulfilled by this state, where a 1-bit indicates that the acceptance
	 * condition with corresponding index has been fulfilled (this requires that
	 * acceptance conditions are each associated with a unique index).
	 */
	private final BitSet fulfilled;

	/**
	 * The program state.
	 */
	private final State qProg;

	/**
	 * The property state.
	 */
	private final PropState qProp;

	//
	// Constructors
	//

	/**
	 * Constructs a product state according to the specified program and
	 * property states, and the number of acceptance conditions.
	 * 
	 * @param qProg
	 *            - The program state.
	 * @param qProp
	 *            - The property state.
	 * @param sizeCurlyF
	 *            - The number of acceptance conditions.
	 */
	public ProductState(State qProg, PropState qProp, int sizeCurlyF) {
		this.qProg = qProg;
		this.qProp = qProp;
		this.fulfilled = new BitSet(sizeCurlyF);
	}

	//
	// Public methods
	//

	@Override
	public boolean equals(Object o) {
		ProductState state = (ProductState) o;
		return qProg.equals(state.qProg()) && qProp.equals(state.qProp());
	}

	@Override
	public int hashCode() {
		byte[] key = qProg.jenkinsKey();
		int b = qProp.jenkinsB();
		int c = qProp.jenkinsC();
		int code = Jenkins.apply(key, c, b)[0];
		return code;
	}

	@Override
	public int jenkinsB() {

		try {

			/* This method has not been implemented */
			throw new Exception();
		}

		catch (Exception e) {
			e.printStackTrace();

		}

		System.exit(0);
		return 0;
	}

	@Override
	public int jenkinsC() {

		try {

			/* This method has not been implemented */
			throw new Exception();
		}

		catch (Exception e) {
			e.printStackTrace();

		}

		System.exit(0);
		return 0;
	}

	@Override
	public byte[] jenkinsKey() {

		try {

			/* This method has not been implemented */
			throw new Exception();
		}

		catch (Exception e) {
			e.printStackTrace();

		}

		System.exit(0);
		return null;
	}

	/**
	 * Gets {@link #fulfilled}.
	 * 
	 * @return {@link #fulfilled}.
	 */
	public BitSet getFulfilled() {
		return fulfilled;
	}

	/**
	 * Gets the program state that constitutes this product state.
	 * 
	 * @return {@link #qProg}.
	 */
	public State qProg() {
		return qProg;
	}

	/**
	 * Gets the property state that constitutes this product state.
	 * 
	 * @return {@link #qProp}
	 */
	public PropState qProp() {
		return qProp;
	}

	/**
	 * Sets the acceptance condition associated with the specified index to
	 * being fulfilled, i.e. the bit in {@link #fulfilled} at the specified
	 * index is set to 1.
	 * 
	 * @param index
	 *            - The index of the acceptance condition.
	 */
	public void setFulfilled(int index) {
		fulfilled.set(index);
	}

	@Override
	public String toString(int offset) {

		try {

			/* This method has not been implemented */
			throw new Exception();
		}

		catch (Exception e) {
			e.printStackTrace();

		}

		System.exit(0);
		return null;
	}
}
