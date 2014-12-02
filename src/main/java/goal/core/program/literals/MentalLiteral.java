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

import goal.core.kr.KRlanguage;
import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.core.program.Selector;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;

import java.util.Set;

/**
 * A mental literal is an object of the form <selector>.bel(...) or
 * <selector>.goal(...), (or goal-a or a-goal). It can also be of the negated
 * form, so not(...).
 *
 * @author K.Hindriks
 * @modified N.Kraayenbrink - Created generic toString method now that selectors
 *           can also be used for goal queries.
 * @modified V.Koeman - Made class abstract to create OO literaltype classes
 *           (instead of using an enum-type field)
 */

public abstract class MentalLiteral extends ParsedObject implements
		MentalFormula {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = -8432959139892030035L;
	/** False: : the literal is of the form not(...) */
	protected boolean polarity;
	protected Query formula;
	protected Selector selector;

	/**
	 * A mental literal is an atomic query on the mental state. Examples:
	 * bel(...), goal(...) or not(bel(...)). The 'not' is implemented via the
	 * pPos argument.
	 *
	 * @param posLiteral
	 *            The polarity of the mental literal. Default is true. If set to
	 *            false, the literal is of the form not(...).
	 * @param formula
	 *            The {@link KRlanguage} query inside the mental literal.
	 * @param selector
	 *            The mental model {@link Selector} prefix of the mental
	 *            literal. Eg, the 'self' in self.bel(...).
	 * @param type
	 *            The type of this {@link MentalLiteral}, i.e. either a belief,
	 *            goal, achievement (a-)goal, or a goal(-a) achieved.
	 * @param source
	 *            The position in the source code where this mental literal is
	 *            located.
	 */
	public MentalLiteral(boolean posLiteral, Query formula, Selector selector,
			InputStreamPosition source) {
		super(source);

		this.polarity = posLiteral;
		this.formula = formula;
		this.selector = selector;
	}

	public MentalLiteral(InputStreamPosition source) {
		super(source);
		this.polarity = true;
	}

	@Override
	public abstract MentalLiteral applySubst(Substitution pSubst);

	/*
	 * public MentalLiteral applySubst(Substitution pSubst) { return new
	 * MentalLiteral(this.polarity, this.formula.applySubst(pSubst),
	 * this.selector.applySubst(pSubst), this.getSource()); }
	 */

	/**
	 * Returns whether this {@link MentalLiteral} is a positive literal.
	 *
	 * @return <code>true</code> if the literal is positive; <code>false</code>
	 *         otherwise.
	 */
	public boolean isPositive() {
		return this.polarity;
	}

	public void setPolarity(boolean positive) {
		this.polarity = positive;
	}

	public Query getFormula() {
		return this.formula;
	}

	public void setFormula(Query formula) {
		this.formula = formula;
	}

	public Selector getSelector() {
		return this.selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	@Override
	public Set<Var> getFreeVar() {
		return this.formula.getFreeVar();
	}

	public boolean isClosed() {
		return formula.isClosed();
	}

	public abstract String getLiteralTypeString();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (!this.isPositive()) {
			builder.append("not(");
		}

		if (this.selector != null) {
			String selectorStr = this.selector.toString();
			if (!selectorStr.isEmpty() && !selectorStr.equals("this")) {
				builder.append(selectorStr);
				builder.append(".");
			}
		}

		builder.append(getLiteralTypeString());

		builder.append("(");
		builder.append(this.formula.toString());
		builder.append(")");

		if (!this.isPositive()) {
			builder.append(")");
		}

		return builder.toString();
	}

}
