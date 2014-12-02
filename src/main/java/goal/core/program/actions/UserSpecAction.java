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

package goal.core.program.actions;

import goal.core.kr.KRlanguage;
import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.kr.language.Var;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalState;
import goal.core.program.ActionSpecification;
import goal.core.program.SelectExpression.SelectorType;
import goal.core.program.Selector;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A user-specified action of the form 'name(parameters)' with one or more
 * associated action specifications (i.e., precondition, postcondition pairs).
 * Parameters are optional.
 * <p>
 * A user-specified action should at least have one associated action
 * specification. In case an action has multiple action specifications the order
 * of the specifications in the program is taken into account: a specification
 * that occurs before another one is used whenever it is applicable.
 * </p>
 *
 * @author K.Hindriks
 */
public class UserSpecAction extends Action {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -5226368216601304352L;
	/**
	 * The parameters of this action. By default, a user-specified action has no
	 * parameters.
	 */
	private final List<Term> parameters;
	/**
	 * Representing whether the action should be sent to an external
	 * environment. Default value is {@code true} meaning that an attempt should
	 * be made to sent the action to an external environment. In case value is
	 * {@code false} no such attempt should be made.
	 */
	private boolean external = true;
	/**
	 * The specifications, i.e. pre- and post-conditions, associated with this
	 * action.
	 */
	private final List<PrePost> specifications = new LinkedList<>();
	private Substitution solution = null;
	/**
	 * Index into the action specifications; set in
	 * {@link #getSatisfiedActionSpecification(RunState)} and used in
	 * {@link #getOptions(MentalState, SteppingDebugger)}.
	 */
	private int indexIntoSpecifications;

	/**
	 * Creates a {@link UserSpecAction} with name, parameter list, and sets flag
	 * whether action should be sent to external environment or not.
	 *
	 * @param name
	 *            The name of the action.
	 * @param parameters
	 *            The action parameters.
	 * @param external
	 *            Parameter indicating whether action should be sent to external
	 *            environment or not. {@code true} indicates that action should
	 *            be sent to environment; {@code false} indicates that action
	 *            should not be sent to environment.
	 * @param source
	 *            The source code location of this action, if available;
	 *            {@code null} otherwise.
	 */
	public UserSpecAction(String name, List<Term> parameters, boolean external,
			InputStreamPosition source) {
		super(name, source);

		if (parameters == null) { // Parser may return null value
			this.parameters = new ArrayList<>(0);
		} else {
			this.parameters = parameters;
		}
		// Set flag whether to send action to external environment or not.
		this.external = external;
	}

	/**
	 * Returns the parameters of this {@link UserSpecAction}.
	 *
	 * @return The list of action parameters.
	 */
	public List<Term> getParameters() {
		return this.parameters;
	}

	/**
	 * @return The latest substitution from calling evaluatePrecondition
	 */
	public Substitution getLatestSubstitution() {
		return this.solution;
	}

	/**
	 * Sets whether this user-specified action should be sent to environment.
	 *
	 * @param external
	 *            If {@code true}, action is sent to environment.
	 */
	public void setExternal(boolean external) {
		this.external = external;
	}

	/**
	 * Attempts to add an {@link ActionSpecification}, i.e., a precondition and
	 * postcondition, to the list of action specifications associated with this
	 * {@link UserSpecAction}. Fails if the action parameters of the
	 * specification do NOT unify with parameters of this {@link UserSpecAction}
	 * .
	 * <p>
	 * Also renames variables in the action specification to avoid clashes with
	 * variables used by the action call itself!
	 * </p>
	 *
	 * @param specification
	 *            The action specification that contains the pre- and
	 *            post-condition to be associated with this action.
	 * @return {@code true}, indicating successful addition of the
	 *         specification, if parameters of the specified action unify with
	 *         parameters of this {@link UserSpecAction}; {@code false}
	 *         otherwise.
	 * @throws KRInitFailedException
	 */
	public boolean addSpecification(ActionSpecification specification)
			throws KRInitFailedException {
		// Need to check whether actual parameters of this action unify with
		// the formal parameters of the action specification. In order to do so,
		// we standardize variables apart using an 'anonymizer'.
		ActionSpecification renamedspec = specification
				.applySubst(getAnonymizer(specification));

		// Find a unifier for parameters. Also checks whether action names are
		// equal
		// and number of parameters are equal.
		Substitution mgu = renamedspec.getAction().mgu(this,
				specification.getPreCondition().getLanguage());

		// If we found a unifier, then add the specification.
		if (mgu != null) {
			this.specifications.add(new PrePost(renamedspec.applySubst(mgu)));
			// Copy flag whether to sent action to environment or not from
			// action specification. FIXME: too hacky...
			this.external = renamedspec.getAction().external;
		}

		return (mgu != null);
	}

	/**
	 * Returns a substitution that 'anonymizes' variables by prefixing them with
	 * an underscore.
	 *
	 * @param specification
	 *            Action specification that needs to be standardized apart,
	 *            i.e., whose variables need to be anonymized.
	 * @return An anonymizer for standardizing variables apart. ASSUMES that
	 *         variables to be anonymized are not anonymous variables
	 *         themselves!
	 *
	 *         TODO: move to validator! We're only having this code here because
	 *         we would like to rename as few variables as possible (which we do
	 *         not now with the code below) but still this should be done in
	 *         validator. TODO: This is an easy but ADHOC way to standardize
	 *         variables apart. Instead we should use an infinite supply of
	 *         FRESH variables none of which occur in the 'context' of the
	 *         variables that need to be renamed.
	 * @throws KRInitFailedException
	 */
	private static Substitution getAnonymizer(ActionSpecification specification)
			throws KRInitFailedException {
		Set<Var> variables = specification.getAction().getFreeVar();
		variables.addAll(specification.getPreCondition().getFreeVar());

		// Rename the variables to 'anonymized' variables.
		Substitution anonymizer = specification.getPreCondition().getLanguage()
				.getEmptySubstitution();
		for (Var var : variables) {
			anonymizer = anonymizer.combine(var.renameVar("_", ""));
		}
		return anonymizer;
	}

	/**
	 * Computes a unifier for the actual parameters of this action with those of
	 * the given action. Also checks whether action names and number of
	 * parameters match.
	 *
	 * @param action
	 *            The action to unify with.
	 * @param language
	 *            The KR language used for representing the action parameters.
	 * @return A unifying substitution, if a unifier exists; {@code null}
	 *         otherwise.
	 */
	public Substitution mgu(Action action, KRlanguage language) {
		List<Term> parameters;

		if (!(action instanceof UserSpecAction)) {
			return null; // action type mismatch
		}
		if (!this.getName().equals(action.getName())) {
			return null; // action name mismatch
		}
		parameters = ((UserSpecAction) action).getParameters();
		if (this.parameters.size() != parameters.size()) {
			return null; // arity mismatch
		}
		// Compute unifier.
		Substitution mgu = language.getEmptySubstitution();
		for (int i = 0; i < this.parameters.size() && mgu != null; i++) {
			mgu = mgu.combine(this.parameters.get(i).mgu(parameters.get(i)));
		}

		return mgu;
	}

	/**
	 * Returns the (free) variables that occur in the parameters of this
	 * {@link UserSpecAction}.
	 *
	 * @return The (free) variables that occur in the action's parameters.
	 */
	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = new LinkedHashSet<>();
		// Add free variables that occur in each parameter.
		for (Term term : this.parameters) {
			freeVars.addAll(term.getFreeVar());
		}
		return freeVars;
	}

	/**
	 * Applies the given {@link Substitution} to parameters of this
	 * {@link UserSpecAction} as well as to all its action specifications. It
	 * instantiates all occurrences of (free) variables in the action's
	 * parameters, preconditions and postconditions that are bound by the
	 * substitution by the corresponding terms.
	 *
	 * @param substitution
	 *            The substitution to be applied to the action.
	 * @return A new instantiated action where (free) variables that are bound
	 *         by the substitution have been instantiated by the corresponding
	 *         terms in the substitution.
	 */
	@Override
	public UserSpecAction applySubst(Substitution substitution) {
		ArrayList<Term> instantiatedParameters = new ArrayList<>(
				this.parameters.size());
		// Apply substitution to action parameters.
		for (Term term : this.parameters) {
			instantiatedParameters.add(term.applySubst(substitution));
		}

		// Create new instantiated action.
		UserSpecAction instantiatedAction = new UserSpecAction(this.getName(),
				instantiatedParameters, this.external, this.getSource());
		// Apply substitution to all action specifications.
		for (PrePost specification : this.specifications) {
			instantiatedAction.specifications.add(specification
					.applySubst(substitution));
		}

		return instantiatedAction;
	}

	/**
	 * Returns a condition of the form "bel(precondition)" where precondition is
	 * the precondition from the <i>first</i> action specification. Does not
	 * support multiple action specifications.
	 *
	 * @param language
	 *            The KR language used for representing the precondition.
	 * @return Precondition of this action of the form "bel(precondition)".
	 */
	@Override
	public MentalStateCond getPrecondition(KRlanguage language) {
		// Get the precondition from the *first* action specification.
		Query precondition = this.specifications.get(0).getPrecondition();

		// Create mental state condition of the form "self.bel(precondition)".
		List<MentalFormula> formulalist = new ArrayList<>(1);
		formulalist.add(new BelLiteral(true, precondition, new Selector(
				SelectorType.SELF, precondition.getSource()), precondition
				.getSource()));
		return new MentalStateCond(formulalist, precondition.getSource());
	}

	/**
	 * Returns all preconditions associated with this action.
	 *
	 * @return The preconditions as a list of {@link Query}s.
	 *
	 * @author Sungshik
	 */
	public List<Query> getPreconditions() {
		ArrayList<Query> preconditions = new ArrayList<>(
				this.specifications.size());
		for (PrePost specification : this.specifications) {
			preconditions.add(specification.getPrecondition());
		}
		return preconditions;
	}

	/**
	 * Returns all postconditions associated with this action.
	 *
	 * @return The postconditions as a list of {@link Update}s.
	 *
	 * @author Sungshik
	 */
	public List<Update> getPostconditions() {
		ArrayList<Update> postconditions = new ArrayList<>(
				this.specifications.size());
		for (PrePost spec : this.specifications) {
			postconditions.add(spec.getPostcondition());
		}
		return postconditions;
	}

	/**
	 * Evaluates the precondition(s) for this {@link UserSpecAction}. Returns an
	 * instantiated action with the first action specification for which the
	 * precondition holds. A substitution for instantiating the action is
	 * randomly selected from the set of substitutions that satisfy the
	 * precondition of that specification.
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the precondition is
	 *            evaluated.
	 * @param debugger
	 *            The current debugger
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return An instantiated action with the first action specification for
	 *         which the precondition holds; {@code null} otherwise.
	 */
	@Override
	public Action evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		// Find the first action specification whose precondition holds.
		final Set<Substitution> solutions = getOptions(mentalState, debugger);

		// Return null if no precondition was found that holds; otherwise return
		// an
		// instantiated action with the action specification that was found
		// first for
		// which the precondition holds and use a randomly selected substitution
		// to
		// instantiate the action.
		if (solutions.isEmpty()) {
			// None of the preconditions holds.
			if (last) {
				debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION_USERSPEC,
						this, "Preconditions of action %s failed.",
						this.getName());
			}
			return null;
		} else {
			// Select a random substitution that satisfies the precondition we
			// found.
			ArrayList<Substitution> substitutions = new ArrayList<>(solutions);
			Collections.shuffle(substitutions);
			this.solution = substitutions.get(0);
			UserSpecAction action = getSelectedActionSpec();
			// Report success.
			debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION_USERSPEC,
					action, "Precondition { %s } of action %s holds for: %s.",
					action.getPreconditions(), action.getName(), this.solution);
			return action.applySubst(this.solution);
		}
	}

	/**
	 * Searches for the first action specification of this action whose
	 * precondition holds in the given mental state.
	 *
	 * @param mentalState
	 *            The mental state used for evaluating the precondition.
	 * @param debugger
	 *            The current debugger
	 * @return The solutions, i.e., set of substitutions that satisfy the
	 *         precondition that was found, or the empty set otherwise.
	 */
	protected Set<Substitution> getOptions(MentalState mentalState,
			Debugger debugger) {
		Query precondition = null;
		// Reset index into specifications.
		indexIntoSpecifications = -1;

		// Search for first precondition in list of action specifications that
		// holds.
		// We also keep track of the corresponding postcondition.
		Set<Substitution> solutions = new HashSet<>();
		for (int i = 0; i < specifications.size(); i++) {
			PrePost prepost = specifications.get(i);
			// Get precondition and postcondition.
			precondition = prepost.getPrecondition();
			// Check whether precondition holds.
			solutions = mentalState.query(precondition, BASETYPE.BELIEFBASE,
					debugger);
			if (!solutions.isEmpty()) {
				// We found a precondition that holds; stop searching.
				// Remember which action specification is satisfied by setting
				// index into action specifications.
				indexIntoSpecifications = i;
				break;
			}
		}
		return solutions;
	}

	/**
	 * Returns this user-specified action where all action specifications other
	 * than the one found by {@link #getOptions(MentalState, SteppingDebugger)}
	 * have been removed.
	 * <p>
	 * Only call this AFTER a call to
	 * {@link #getOptions(MentalState, SteppingDebugger)} .
	 * </p>
	 *
	 * @return This user-specified action where all action specifications other
	 *         than the one found by
	 *         {@link #getOptions(MentalState, SteppingDebugger)} have been
	 *         removed.
	 */
	protected UserSpecAction getSelectedActionSpec() {
		// Check if call to #getOptions(RunState) has been made first.
		if (indexIntoSpecifications == -1) {
			throw new UnsupportedOperationException(
					"Calling #getSelectedActionSpec "
							+ "is only supported after first calling #getOptions(RunState).");
		}

		// Create new action that only has specification found by
		// #getOptions(RunState).
		UserSpecAction action = new UserSpecAction(this.getName(),
				this.getParameters(), this.external, this.getSource());
		// Get first action specification found for which precondition holds.
		PrePost actionspec = this.specifications.get(indexIntoSpecifications);
		// Do NOT use addSpecification as this renames variables.
		action.specifications.add(actionspec);

		return action;
	}

	/**
	 * Executes this {@link UserSpecAction}.
	 *
	 * @param runState
	 *            The {@link RunState} in which this action is executed.
	 */
	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		// Send the action to the environment if it should be sent.
		if (this.external) {
			runState.doPerformAction(this);
		}

		// Apply the action's postcondition.
		Update postcondition = this.specifications.get(0).getPostcondition();
		runState.getMentalState().insert(postcondition, BASETYPE.BELIEFBASE,
				debugger);

		// Check if goals have been achieved and, if so, update goal base.
		runState.getMentalState().updateGoalState(debugger);

		// Report action was performed.
		report(debugger);

		return new Result(this);
	}

	/**
	 * Converts this user specified action in GOAL to an action recognized by
	 * EIS.
	 *
	 * @return The EIS action.
	 */
	public eis.iilang.Action convert() {
		// Convert parameters.
		LinkedList<eis.iilang.Parameter> parameters = new LinkedList<>();
		for (Term term : this.parameters) {
			parameters.add(term.convert());
		}

		// Return converted EIS action.
		return new eis.iilang.Action(this.getName(), parameters);
	}

	/**
	 * Returns string representation of action. Includes parameters but not the
	 * pre- and post-condition of the action. If action has no parameters only
	 * action name without brackets () is returned.
	 *
	 * @return string representation of action.
	 */
	@Override
	public String toString() {
		String parameterList = "";

		if (!this.parameters.isEmpty()) {
			for (Term term : this.parameters) {
				if (parameterList.length() == 0) {
					parameterList += term.toString();
				} else {
					parameterList += "," + term.toString();
				}
			}
			parameterList = "(" + parameterList + ")";
		}
		return this.getName() + parameterList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (external ? 1231 : 1237);
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result
				+ ((specifications == null) ? 0 : specifications.hashCode());
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
		UserSpecAction other = (UserSpecAction) obj;
		if (external != other.external) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (specifications == null) {
			if (other.specifications != null) {
				return false;
			}
		} else if (!specifications.equals(other.specifications)) {
			return false;
		}
		return true;
	}

	/**
	 * Represents a precondition,postcondition pair as specified in the action
	 * specification section of the GOAL agent. Used as simple container.
	 *
	 * @author K.Hindriks
	 * @modified W.Pasman 27jul10 extends ParsedObject so that UserSpecAction
	 *           can be sent over middleware.
	 */
	public class PrePost extends ParsedObject {

		/** Auto-generated serial version UID */
		private static final long serialVersionUID = 7342612059311418400L;

		private final Query precondition;
		private final Update postcondition;

		/**
		 * Creates an action specification, i.e. a pre-post condition pair.
		 *
		 * @param precond
		 *            precondition of the action.
		 * @param postcond
		 *            postcondition of the action.
		 * @param source
		 *            code position of the action.
		 */
		public PrePost(Query precond, Update postcond,
				InputStreamPosition source) {
			super(source);
			this.precondition = precond;
			this.postcondition = postcond;
		}

		/**
		 * Creates a pre-post condition pair from an action specification.
		 *
		 * @param spec
		 *            The (instantiated) specification to get the pre and
		 *            post-conditions from.
		 */
		public PrePost(ActionSpecification spec) {
			this(spec.getPreCondition(), spec.getPostCondition(), spec
					.getSource());
		}

		/**
		 * @return precondition of the specification.
		 */
		public Query getPrecondition() {
			return this.precondition;
		}

		/**
		 * @return postcondition of the specification.
		 */
		public Update getPostcondition() {
			return this.postcondition;
		}

		/**
		 * @param subst
		 *            the substitution to apply.
		 * @return the PrePost with the substitution applied.
		 */
		public PrePost applySubst(Substitution subst) {
			return new PrePost(this.precondition.applySubst(subst),
					this.postcondition.applySubst(subst), this.getSource());
		}

		/**
		 * @return string representation of the action specification.
		 */
		@Override
		public String toString() {
			return "pre{ " + this.precondition.toString() + " } post{ "
					+ this.postcondition.toString() + " }";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 0; // #2120 BUG IN GENERATED CODE super.hashCode();
			// result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((postcondition == null) ? 0 : postcondition.hashCode());
			result = prime * result
					+ ((precondition == null) ? 0 : precondition.hashCode());
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
			PrePost other = (PrePost) obj;
			if (postcondition == null) {
				if (other.postcondition != null) {
					return false;
				}
			} else if (!postcondition.equals(other.postcondition)) {
				return false;
			}
			if (precondition == null) {
				if (other.precondition != null) {
					return false;
				}
			} else if (!precondition.equals(other.precondition)) {
				return false;
			}
			return true;
		}

	}

}
