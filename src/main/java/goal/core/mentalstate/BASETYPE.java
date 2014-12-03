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

package goal.core.mentalstate;

/**
 * <p>
 * Different (data)base types that are used in GOAL.
 * </p>
 * <p>
 * These types are useful for differentiating databases in the KR layer.
 * </p>
 *
 * @author K.Hindriks
 *
 */
public enum BASETYPE {

	/**
	 * A knowledge base. Note that GOAL does not explicitly store the knowledge
	 * base of an agent, but only a list of formulae in the KB, present in the
	 * agent's AgentProgram. This value is mostly there so that the DatabasePanel
	 * can identify that it contains information of the knowledge base.
	 */
	KNOWLEDGEBASE("knowledge base"),
	/**
	 * database storing data from the belief section, and insert()'ed objects.
	 * There is only 1 beliefbase for an agent. Beliefbase automatically
	 * inherits the Knowledge base.
	 */
	BELIEFBASE("belief base"),
	/**
	 * database storing a single goal, either a single line from a goal section
	 * or from single adopt() actions. Goal bases automatically inherit the
	 * knowledge base; Note that this feature is implemented by the KR layer.
	 */
	GOALBASE("goal base"),
	/**
	 * Database storing mails.
	 */
	MAILBOX("mailbox"),
	/**
	 * Database storing percepts.
	 */
	PERCEPTBASE("percept base");

	/**
	 * The proper string-representation of this {@link BASETYPE}.
	 */
	private String displayName;

	private BASETYPE(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return this.displayName;
	}

}
