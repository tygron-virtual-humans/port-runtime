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

package goal.core.program.literals;

import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Var;
import goal.core.program.validation.agentfile.RuleValidator;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.errorhandling.exceptions.GOALBug;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A representation of a macro definition:<br>
 * <code>    #define macroname(arglist) definition</code>
 *
 * @author N.Kraayenbrink
 *
 */
public class Macro extends ParsedObject implements MentalFormula {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 7519167225401561752L;

	/**
	 * The name/label of the macro
	 */
	private final String name;
	/**
	 * The arguments of this macro (can be empty)
	 */
	private final List<Term> parameters;
	/**
	 * The MSC this {@link Macro} is a shorthand for
	 */
	private MentalStateCond definition;
	/**
	 * A flag indicating whether this {@link Macro} is used in the program. Used
	 * during validation in {@link RuleValidator}.
	 */
	private boolean isUsed;

	/**
	 * Creates a new macro definition.
	 *
	 * @param name
	 *            The name/label of the macro
	 * @param parameters
	 *            The parameters of the macro (can be the empty list but not
	 *            null).
	 * @param definition
	 *            The {@link MentalStateCond} the macro is a shorthand for. If
	 *            null, this macro reference still needs to be resolved. See
	 *            {@link RuleValidator#doValidate} of Should only be null for
	 *            macro instances.
	 * @param source
	 *            The location of the start of the new {@link Macro}'s
	 *            definition. May be null if not created by a parser.
	 */
	public Macro(String name, List<Term> parameters,
			MentalStateCond definition, InputStreamPosition source) {
		super(source);

		this.name = name;
		if (parameters == null) { // Parser may return null value
			this.parameters = new ArrayList<>(0);
		} else {
			this.parameters = parameters;
		}
		this.definition = definition;
		this.isUsed = false;
	}

	/**
	 * The name of this {@link Macro}.
	 *
	 * @return The name of this macro.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the parameters of this {@link Macro}.
	 *
	 * @return The list of parameters of this macro.
	 */
	public List<Term> getParameters() {
		return this.parameters;
	}

	/**
	 * Returns the definition of this {@link Macro}.
	 *
	 * @return The definition of this macro.
	 */
	public MentalStateCond getDefinition() {
		return this.definition;
	}

	/**
	 * Sets the definition of this {@link Macro}, using the definition provided
	 * by the parameter macro.
	 *
	 * @param macro
	 *            The macro definition used to set the definition for this
	 *            macro.
	 * @throws GOALBug
	 *             DOC
	 */
	public void setDefinition(Macro macro) throws GOALBug {
		// TODO: unification code partly copied from {@link UserSpecAction};
		// REMOVE duplication of code.
		if (!this.name.equals(macro.getName())
				|| this.parameters.size() != macro.getParameters().size()) {
			// label or arity mismatch
			throw new GOALBug("Attempt to set macro definition for "
					+ this.getSignature() + " using definition for "
					+ macro.getSignature());
		}
		if (!this.parameters.isEmpty()) {
			// Unify parameters.
			Substitution mgu = macro.getParameters().get(0)
					.mgu(this.parameters.get(0));
			for (int i = 1; i < this.parameters.size(); i++) {
				mgu = mgu.combine(macro.getParameters().get(i)
						.mgu(this.parameters.get(i)));
			}
			if (mgu == null) {
				throw new GOALBug("Could not unify macro "
						+ this.getSignature() + " at " + this.getSource()
						+ " using definition for " + macro.getSignature()
						+ " at " + macro.getSource());
			}
			// Apply substitution to macro definition and set this macro's
			// definition.
			this.definition = macro.getDefinition().applySubst(mgu);
		} else {
			this.definition = macro.getDefinition();
		}
	}

	/**
	 * Returns whether this {@link Macro} is used in the corresponding agent
	 * program. {@link RuleValidator}s check whether a macro is actually used.
	 *
	 * @return {@code true} if the macro is used in the agent program;
	 *         {@code false} otherwise.
	 */
	public boolean isUsed() {
		return this.isUsed;
	}

	/**
	 * Marks this {@link Macro} as used, see {@link RuleValidator#doValidate}.
	 */
	public void markUsed() {
		this.isUsed = true;
	}

	@Override
	public Macro applySubst(Substitution subst) {
		Substitution copy = subst.clone();
		// Make sure to only substitute bound variables.
		copy.retainAll(this.getFreeVar());

		List<Term> parameters = new ArrayList<>(this.parameters.size());
		for (Term term : this.parameters) {
			parameters.add(term.applySubst(copy));
		}
		MentalStateCond definition = this.definition.applySubst(copy);

		return new Macro(this.name, parameters, definition, this.getSource());
	}

	@Override
	public Set<Var> getFreeVar() {
		Set<Var> freeVars = new LinkedHashSet<>();
		// the set of free variables for a macro is the variables
		// used in the parameters.
		for (Term t : this.parameters) {
			freeVars.addAll(t.getFreeVar());
		}
		return freeVars;
	}

	/**
	 * Two macros are considered equal iff they share the same name/label and
	 * have the same number of parameters.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Macro)) {
			return false;
		}
		Macro other = (Macro) o;
		if (!other.getName().equals(this.getName())) {
			return false;
		}
		if (other.getParameters().size() != this.getParameters().size()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return 31 * this.getName().hashCode() + this.getParameters().size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.definition != null) {
			// if the definition is null, this macro represents a referenced
			// macro. If the scope is null, this is an instantiated macro
			// in an MSC
			builder.append("#define ");
		}
		builder.append(this.toShortString());
		if (this.definition != null) {
			builder.append(" ");
			builder.append(this.definition.toString());
		}
		return builder.toString();
	}

	/**
	 * A string representation of this {@link Macro} which consists only of the
	 * name and parameters of the macro.
	 *
	 * @return A string representation of the name and parameters of this macro.
	 */
	public String toShortString() {
		StringBuilder builder = new StringBuilder();

		builder.append(this.getName());
		if (this.getParameters().size() > 0) {
			builder.append("(");
			builder.append(this.parameters.get(0).toString());
			for (int i = 1; i < this.parameters.size(); i++) {
				builder.append(", ");
				builder.append(this.parameters.get(i));
			}
			builder.append(")");
		}

		return builder.toString();
	}

	/**
	 * Gets a string representing the signature of this {@link Macro}.
	 *
	 * @return A string of the format {macro name}/{number of parameters}
	 */
	public String getSignature() {
		return this.getName().concat("/")
				.concat(String.valueOf(this.getParameters().size()));
	}

}
