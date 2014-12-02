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

import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.program.actions.ActionCombo;
import goal.tools.mc.program.goal.GOALConversionUniverse;
import goal.tools.mc.program.goal.GOALState;

/**
 * Represents a step.
 * 
 * @author sungshik
 *
 * @param <T>
 *            The type of term associated with the KRT that the agent to which
 *            the step belongs uses.
 * @param <A>
 *            The type of atom associated with the KRT that the agent to which
 *            the step belongs uses.
 * @param <U>
 *            The type of update associated with the KRT that the agent to which
 *            the step belongs uses.
 */
public abstract class TauStep<T extends Term, A extends Atom, U extends Update> {

	//
	// Protected fields
	//

	/**
	 * The action that generated this step.
	 */
	protected ActionCombo act;

	/**
	 * The mental state resulting from executing this step.
	 */
	protected GOALState destination;

	/**
	 * The mental state in which this step was executed.
	 */
	protected GOALState source;

	/**
	 * The transition class to which the transition that contains this step
	 * belongs.
	 */
	protected TauClass<T, A, U> tauClass;

	/**
	 * The conversion universe associated with the mental state in which this
	 * step was executed.
	 */
	protected GOALConversionUniverse universe;

	//
	// Constructors
	//

	/**
	 * Constructs a step according to the specified parameters.
	 * 
	 * @param tauClass
	 *            - See {@link #tauClass}.
	 * @param universe
	 *            - See {@link #universe}.
	 * @param source
	 *            - See {@link #source}.
	 * @param destination
	 *            - See {@link #destination}.
	 * @param combo
	 *            - See {@link #act}.
	 */
	public TauStep(TauClass<T, A, U> tauClass, GOALConversionUniverse universe,
			GOALState source, GOALState destination, ActionCombo combo) {

		this.tauClass = tauClass;
		this.universe = universe;
		this.source = source;
		this.destination = destination;
		this.act = combo;
	}

	//
	// Abstract methods
	//

	/**
	 * Gets the specific updates that this step brings about, and which may be
	 * represented differently depending on the KRT in use.
	 * 
	 * @return The updates.
	 */
	public abstract TauUpdate<A> getUpdates();

	//
	// Public methods
	//

	/**
	 * Gets the action that generated this step.
	 */
	public ActionCombo getAction() {
		return act;
	}

	/**
	 * Gets the mental state resulting from executing this step.
	 * 
	 * @return The destination.
	 */
	public GOALState getDestination() {
		return destination;
	}

	/**
	 * Get the transition class to which the transition that contains this step
	 * belongs.
	 * 
	 * @return The transition class.
	 */
	public TauClass<T, A, U> getTauClass() {
		return tauClass;
	}
}
