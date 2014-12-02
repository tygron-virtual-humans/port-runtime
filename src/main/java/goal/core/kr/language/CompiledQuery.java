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

package goal.core.kr.language;

/**
 * Represents an expression that can be queried on a database. Since a compiled
 * expression may be entirely KR language based, this does not extend
 * Expression.
 * <p>
 * A KR Language may support compilation. But if it does so, not all queries are
 * necessarily precompiled. Therefore it is important to distinguish compiled
 * queries from normal queries. On the surface they may look very similar but
 * under the hood there can be nasty to recognise differences (eg, an extra
 * term). Mixing them up in the inference engine could lead to hard to trace
 * difficulties.
 *
 * <p>
 * The expression should be a formula that may evaluate to true or false,
 * possibly after instantiating any free variables. The corresponding
 * InferenceEngine should support the evaluation on a database. Typically, as a
 * result of the evaluation, a substitution - binding free variables in the
 * QueryExpression with terms - may be returned by the InferenceEngine.
 *
 *
 * Since a CompiledQuery is usually made up of both a MSC and an action
 * precondition, and to avoid any accidental use with normal queries, we don't
 * extend the Query object.
 *
 * @author W.Pasman 12jan2012
 *
 */

public interface CompiledQuery {

	/**
	 * Instantiates (or renames) any free variables that are bound by
	 * substitution by applying substitution.
	 *
	 * @param pSubst
	 *            The substitution to apply.
	 * @return The new Query with the substitution applied.
	 */
	CompiledQuery applySubst(Substitution pSubst);

}
