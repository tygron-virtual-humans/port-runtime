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

package goal.tools.codeanalysis;

/**
 * A SummaryItem stores one line that can be included in the code analysis
 * overview.
 *
 * @author W.Pasman
 * @modified Koen dd110404
 */
public class CodeAnalysisItem {
	private final String label;
	private final String value;
	private ItemType type = ItemType.OCCURRENCECOUNT;

	/**
	 * @param label
	 *            the label of the item that indicates the meaning of the value.
	 * @param value
	 *            the value of the item.
	 * @param type
	 *            the type of the item.
	 */
	public CodeAnalysisItem(String label, String value, ItemType type) {
		this.label = label;
		this.value = value;
		this.type = type;
	}

	/**
	 * Returns the label of the item.
	 *
	 * @return label of the item.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Returns the value associated with the item.
	 *
	 * @return value of the item, i.e. a string, double (e.g. average), or
	 *         integer (e.g. occurrence count).
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Returns the type of the item. See class ItemType.
	 *
	 * @return type of the item.
	 */
	public ItemType getType() {
		return this.type;
	}
}
