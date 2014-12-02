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

package goal.core.program;

import goal.core.agent.AgentId;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalModel;
import goal.core.mentalstate.MentalState;
import goal.core.program.SelectExpression.SelectorType;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A {@link Selector} is used for indicating the mental model(s) that a mental
 * literal should be evaluated on or an action should be applied to. A selector
 * is a list of {@link SelectExpression}s, which need to be resolved into agent
 * names at runtime.
 *
 * @author W. de Vries
 * @author W.Pasman
 * @modified W.Pasman 12mar10: renamed AgentSelector to Selector as it now also
 *           allows you to select modules. See TRAC #999. W.Pasman 20sept12:
 *           made ParsedObject.
 * @modified K.Hindriks
 */
public class Selector extends ParsedObject {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -7869413594631301039L;
	/**
	 * The {@link SelectExpression}s that are part of this {@link Selector}.
	 */
	private final List<SelectExpression> selectExpressions = new LinkedList<>();
	/**
	 * Indicates whether any agent in the set of agents would do, or, if set to
	 * <code>false</code>, whether the {@link MentalModel}s of all agents need
	 * to be evaluated or modified. By default set to <code>true</code>.
	 */
	private boolean any = true;
	/**
	 * Indicates whether the current attention set, i.e. {@link GoalBase}, needs
	 * to be used for evaluation or modification purposes. By default set to
	 * {@code true}.
	 */
	private boolean focus = true;

	/**
	 * @param source
	 *            The source of the selector in the code. Creates an 'empty'
	 *            {@link Selector}.
	 */
	public Selector(InputStreamPosition source) {
		super(source);
	}

	/**
	 * Create a {@link Selector} of a particular {@link SelectorType}.
	 *
	 * @param type
	 *            The selector type that determines what type of selector to
	 *            create.
	 * @param source
	 *            The source of the selector in the code.
	 */
	public Selector(SelectorType type, InputStreamPosition source) {
		super(source);
		this.add(new SelectExpression(type));
	}

	/**
	 * Returns the expressions that are part of this selector.
	 *
	 * @return one or more select expressions part of this selector.
	 */
	public List<SelectExpression> getExpressions() {
		return selectExpressions;
	}

	/**
	 * Adds a {@link SelectExpression} to this {@link Selector}.
	 *
	 * @param expression
	 *            The select expression that is added.
	 */
	public void add(SelectExpression expression) {
		this.selectExpressions.add(expression);
		// Resolve those things that can be resolved statically.
		switch (expression.getType()) {
		case ALL:
		case ALLOTHER:
			any = false;
			break;
		case SELF:
			focus = false;
			break;
		default:
			// Do nothing in all other cases.
			break;
		}
	}

	/**
	 * Applies a {@link Substitution} to this {@link Selector} and returns a new
	 * instantiated selector.
	 *
	 * @param subst
	 *            The substitution that is applied to this selector.
	 * @return A selector instantiated by the substitution.
	 */
	public Selector applySubst(Substitution subst) {
		Selector selector = new Selector(this.getSource());
		for (SelectExpression selectExpression : selectExpressions) {
			selector.add(selectExpression.applySubst(subst));
		}

		return selector;
	}

	/**
	 * Resolves the selector to agent names by expanding quantors. If a fixed
	 * list of agent names has been set, returns that instead.
	 *
	 * Notice that we are handling Strings as agent names. This means the EIS
	 * strings that can contain upper case characters etc. Because the
	 * selectExpressions that we have are language dependent Terms, they will
	 * have to be converted with language dependent translator. This
	 * particularly happens when the eis entities have name starting with upper
	 * case character, and the PrologTerm in that case has quotes around it.
	 *
	 * @param mentalState
	 *            The mental state of the agent who runs the code containing
	 *            this selector.
	 * @return The set of agent names that this selector refers to.
	 * @throws IllegalArgumentException
	 * @throws KRInitFailedException
	 * @throws GOALRuntimeErrorException
	 *             If a SelectExpression is found that is not closed.
	 */
	@SuppressWarnings("fallthrough")
	public Set<AgentId> resolve(MentalState mentalState)
			throws IllegalArgumentException, KRInitFailedException {

		// Resolve the selector expressions.
		HashSet<AgentId> agentNames = new HashSet<>();
		for (SelectExpression expression : selectExpressions) {
			switch (expression.getType()) {
			case ALL:
			case SOME:
				agentNames.addAll(mentalState.getKnownAgents());
				break;
			case ALLOTHER:
			case SOMEOTHER:
				agentNames.addAll(mentalState.getKnownAgents());
				agentNames.remove(mentalState.getAgentId());
				break;
			case VARIABLE:
				if (!expression.isClosed()) {
					// A free variable was found in the selector.
					// This is a runtime error. See TRAC #988.
					throw new GOALRuntimeErrorException("Agent selector @"
							+ this.getSource() + " contains a free "
							+ "variable which is not allowed: " + expression);
				}
			case CONSTANT:
				String signature = expression.getTerm().getSignature();
				String name = signature
						.substring(0, signature.lastIndexOf("/"));

				// #2825 find the agentId of this agent to ensure it exists.
				AgentId existingId = null;
				for (AgentId id : mentalState.getKnownAgents()) {
					if (id.getName().equals(name)) {
						existingId = id;
						break;
					}
				}
				if (existingId == null) {
					throw new IllegalArgumentException("Agent selector @"
							+ this.getSource()
							+ " refers to non-existent agent " + name);
				}
				agentNames.add(existingId);
				break;
			default:
			case THIS:
			case SELF:
				agentNames.add(mentalState.getAgentId());
				break;
			}
		}
		return agentNames;
	}

	/**
	 * @return True or false.
	 */
	public boolean getAny() {
		return any;
	}

	/**
	 * Returns whether the focus should be on the current attention set, i.e.
	 * {@link GoalBase} of the agent or not.
	 *
	 * @return whether the focus should be on the current attention set of the
	 *         agent or not.
	 */
	public boolean getFocus() {
		return focus;
	}

	/**
	 * Returns the set of free variables that occur in this {@link Selector}.
	 *
	 * @return the set of free variables that occur in this selector.
	 */
	public Set<Var> getFreeVar() {
		Set<Var> s = new LinkedHashSet<>();
		for (SelectExpression ae : selectExpressions) {
			s.addAll(ae.getFreeVar());
		}
		return s;
	}

	/**
	 * Checks whether this {@link Selector} is closed, i.e. does not contain any
	 * occurrences of free variables.
	 *
	 * @return {@code true} if this selector is closed; {@code false} otherwise.
	 */
	public boolean isClosed() {
		for (SelectExpression expression : selectExpressions) {
			if (!expression.isClosed()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether this {@link Selector} is equal to a specific
	 * {@link SelectorType}.
	 *
	 * @param type
	 *            The desired {@link SelectorType}.
	 *
	 * @return {@code true} if selector consists of a single
	 *         {@link SelectExpression} that is of the specified
	 *         {@link SelectorType}; {@code false} otherwise.
	 */
	public boolean isType(SelectorType type) {
		return this.selectExpressions.size() == 1
				&& this.selectExpressions.get(0).getType() == type;
	}

	/**
	 * Returns a {@link String} representation of this {@link Selector}.
	 * Suppresses the default selector {@link SelectorType#THIS}.
	 *
	 * @return String representation of this selector.
	 */
	@Override
	public String toString() {
		if (selectExpressions.isEmpty() || isType(SelectorType.THIS)) {
			return "";
		} else if (selectExpressions.size() == 1) {
			return selectExpressions.get(0).toString();
		} else {
			return selectExpressions.toString();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (any ? 1231 : 1237);
		result = prime * result + (focus ? 1231 : 1237);
		result = prime
				* result
				+ ((selectExpressions == null) ? 0 : selectExpressions
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Selector other = (Selector) obj;
		if (any != other.any) {
			return false;
		}
		if (focus != other.focus) {
			return false;
		}
		if (selectExpressions == null) {
			if (other.selectExpressions != null) {
				return false;
			}
		} else if (!selectExpressions.equals(other.selectExpressions)) {
			return false;
		}
		return true;
	}

}
