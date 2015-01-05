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

import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.tools.adapt.FileLearner;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import krTools.language.DatabaseFormula;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.Module;
import mentalState.BASETYPE;

/**
 * Represents a mental state converter. The task of instances of this class is
 * to translate a {@link MentalState} object to the binary representation, and
 * back. Each instance has a single {@link MentalState} object associated with
 * it, whose contents often changes. <h1>Serialization</h1>
 * <p>
 * The serialization is currently made to support loading a previously learned
 * behaviour (see {@link FileLearner}) for execution of a GOAL program. A
 * de-serialized {@link GOALMentalStateConverter} is partial: {@link Module} and
 * {@link GoalBase} are not restored fully. Also, we don't save all info stored
 * here that is relevant for learning. Therefore, a de-serialized
 * {@link GOALMentalStateConverter} can not be used to resume model checking or
 * resume learning.
 *
 * </p>
 *
 * @author sungshik
 * @author W.Pasman added serialization #2246
 * @modified K.Hindriks
 */
public class GOALMentalStateConverter implements Serializable {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6591769350769988803L;
	/**
	 * The mental state associated with this converter.
	 */
	transient private MentalState mentalState;
	/**
	 * The conversion universe that this converter has access to, which contains
	 * all beliefs and goals that have occurred in the mental state in the past.
	 */
	private GOALConversionUniverse universe;
	/**
	 * The goal bases that have occurred in the attention stack of the agent.
	 */
	transient private List<GoalBase> goalBases;
	/**
	 * Empty debugger for reuse (this object is nowhere really used, but rather
	 * instantiated to pass as parameter to methods that require so).
	 */
	transient protected Debugger debugger = new SteppingDebugger("converter",
			null);

	//
	// Constructors
	//

	/**
	 * Creates a converter for the specified mental state.
	 *
	 * @param mentalState
	 *            The mental state to be associated with this converter.
	 */
	public GOALMentalStateConverter(MentalState mentalState) {
		this.mentalState = mentalState;
		this.universe = new GOALConversionUniverse();
		this.goalBases = new ArrayList<GoalBase>();
		// this.goalBases.add(ms.getAttentionSet());
	}

	//
	// Public methods
	//

	/**
	 * Returns the mental state associated with this converter.
	 *
	 * @return The associated mental state.
	 */
	public MentalState getMentalState() {
		return this.mentalState;
	}

	/**
	 * Returns the universe associated with this converter.
	 *
	 * @return The universe.
	 */
	public GOALConversionUniverse getUniverse() {
		return this.universe;
	}

	/**
	 * Sets the universe associated with this converter.
	 *
	 * @param The
	 *            new universe.
	 */
	public void setUniverse(GOALConversionUniverse universe) {
		this.universe = universe;
	}

	/**
	 * Translates the contents of {@link #mentalState} to a binary
	 * representation.
	 *
	 * This method is used only by model checker which also uses this class to
	 * store mental state and focus stack...
	 *
	 * @return
	 */
	public GOALState translate() {
		Set<DatabaseFormula> beliefs = this.mentalState
				.getOwnBase(BASETYPE.BELIEFBASE).getTheory().getFormulas();

		return translate(beliefs, this.mentalState.getAttentionStack());
	}

	/**
	 * Translates the contents of the mental state to a binary representation.
	 *
	 * @return The binary representation.
	 */
	public GOALState translate(Set<DatabaseFormula> beliefs,
			Stack<GoalBase> goalBaseStack) {

		try {
			GOALState q = new GOALState(this);
			int index;

			// hack for #3057 to avoid ConcurrentModificationException
			List<DatabaseFormula> formulas = new ArrayList<DatabaseFormula>(
					beliefs);
			// Convert beliefs.
			for (DatabaseFormula formula : formulas) {
				GOALCE_Belief belief = new GOALCE_Belief(formula);
				index = this.universe.getIndex(belief);
				if (index == -1) {
					index = this.universe.addIfNotContains(belief);
				}
				q.set(index);
			}

			// Convert goals.
			int depth = 0;
			if (goalBaseStack.size() > this.goalBases.size()) {
				this.goalBases.add(goalBaseStack.peek());
			}
			for (GoalBase goalBase : goalBaseStack) {
				// Add goals per goal base on the attention stack.
				for (SingleGoal singleGoal : goalBase) {
					GOALCE_GoalAtDepth goal = new GOALCE_GoalAtDepth(
							singleGoal.getGoal(), depth);
					index = this.universe.getIndex(goal);
					if (index == -1) {
						index = this.universe.addIfNotContains(goal);
					}
					q.set(index);
				}

				// Increment depth.
				depth++;
			}

			// Add foci. TODO Check.
			depth = 0;
			for (GoalBase module : goalBaseStack) {
				GOALCE_FocusAtDepth focus = new GOALCE_FocusAtDepth(module,
						depth);
				index = this.universe.getIndex(focus);
				if (index == -1) {
					index = this.universe.addIfNotContains(focus);
				}
				q.set(index);

				// Increment depth.
				depth++;
			}

			// Return GOALState.
			return q;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the contents of {@link #mentalState} to correspond with the
	 * specified binary representation.
	 *
	 * @param q
	 *            The binary representation to which {@link #mentalState} should
	 *            correspond.
	 */
	public void update(GOALState q) {

		try {
			/* Prepare number of goal bases */
			Stack<GoalBase> attentionStack = this.mentalState
					.getAttentionStack();
			attentionStack.clear();
			for (GoalBase goalBase : this.goalBases) {
				attentionStack.push(goalBase);
			}

			/* Get ms BitSet representation */
			ArrayList<GoalBase> focuses = new ArrayList<GoalBase>(
					this.universe.nFocusses());
			BitSet msBitSet = translate();
			int index = -1;
			int maxIndex = Math.max(msBitSet.length(), q.length()) - 1;
			while (index++ < maxIndex) {
				/* Remove beliefs or goals */
				if (msBitSet.get(index) && !q.get(index)) {
					GOALConversionElement element = this.universe
							.getAtIndex(index);

					/* Belief */
					if (element instanceof GOALCE_Belief) {
						this.mentalState.delete(
								((GOALCE_Belief) element).belief,
								BASETYPE.BELIEFBASE, this.debugger);
					}

					/* Goal */
					if (element instanceof GOALCE_GoalAtDepth) {
						GOALCE_GoalAtDepth goal = (GOALCE_GoalAtDepth) element;
						attentionStack.get(goal.depth).remove(goal.goal,
								this.debugger);
					}
				}

				/* Add beliefs or goals */
				if (!msBitSet.get(index) && q.get(index)) {
					GOALConversionElement element = this.universe
							.getAtIndex(index);

					/* Belief */
					if (element instanceof GOALCE_Belief) {
						this.mentalState.getOwnBase(BASETYPE.BELIEFBASE)
								.insert(((GOALCE_Belief) element).belief,
										this.debugger);
					}

					/* Goal */
					if (element instanceof GOALCE_GoalAtDepth) {
						GOALCE_GoalAtDepth goal = (GOALCE_GoalAtDepth) element;
						attentionStack.get(goal.depth).insert(goal.goal,
								this.debugger);
					}
				}

				/* Accumulate focus names */
				if (q.get(index)) {
					GOALConversionElement element = this.universe
							.getAtIndex(index);
					if (element instanceof GOALCE_FocusAtDepth) {
						GOALCE_FocusAtDepth focus = (GOALCE_FocusAtDepth) element;
						focuses.add(focus.depth, focus.focus);
					}
				}
			}

			/* Remove redundant goals from attention stack */
			while (focuses.size() + 1 < attentionStack.size()) {
				attentionStack.pop();
			}

			/* Update focus names */
			for (int i = 0; i < focuses.size(); i++) {
				if (focuses.get(i) == null) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Translates the current contents of {@link #mentalState} to a string
	 * representation.
	 *
	 * @param indent
	 *            - The margin (i.e. number of white spaces) that the string to
	 *            be constructed should adhere to.
	 * @return The string representation of {@link #mentalState}.
	 */
	public String toString(int indent) {

		try {
			/* Process belief base and filter some redundant beliefs */
			AgentId name = this.mentalState.getAgentId();
			LinkedHashSet<DatabaseFormula> formulas = new LinkedHashSet<DatabaseFormula>();
			for (DatabaseFormula formula : this.mentalState
					.getOwnBase(BASETYPE.BELIEFBASE).getTheory().getFormulas()) {
				if (!formula.toString().equals("agent(" + name + ")")
						&& !formula.toString().equals("me(" + name + ")")) {
					formulas.add(formula);
				}
			}

			/* Process specified indentation to whitespace */
			String tab = "";
			for (int i = 0; i < indent; i++) {
				tab += " ";
			}

			/* Build binary representation and belief base */
			String string = tab + "Bit Repres.: " + translate().toString()
					+ "\n" + tab + "Belief base: " + formulas;

			/* Build stack of goal bases */
			int depth = 0;
			for (GoalBase goalBase : this.mentalState.getAttentionStack()) {
				String goals = "";
				for (SingleGoal goal : goalBase) {
					goals += "[" + goal.toString() + "] ";
				}
				string += "\n" + tab + "Goal base " + depth + ": \""
						+ goalBase.getName() + "\" " + goals + "";
				depth++;
			}

			/* Return */
			return string;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
