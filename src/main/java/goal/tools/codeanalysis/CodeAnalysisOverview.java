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

import java.util.LinkedList;
import java.util.List;

/**
 * Produces a string of text summarizing code analysis results. The option
 * 'verbose' can be checked in which case all text labels are included, or, if
 * unchecked all text labels are excluded.
 *
 * @author W.Pasman
 * @modified: Koen dd110404
 */
public class CodeAnalysisOverview {
	/**
	 * The items for this summary, see {@link CodeAnalysisItem}.
	 */
	private final List<CodeAnalysisItem> items = new LinkedList<>();

	/**
	 * Get all items available in the overview.
	 *
	 * @return the items.
	 */
	public List<CodeAnalysisItem> getItems() {
		return this.items;
	}

	/**
	 * Add all items in a given overview to this overview
	 *
	 * @param overview
	 *            is the summary to be added.
	 */
	public void add(CodeAnalysisOverview overview) {
		this.items.addAll(overview.getItems());
	}

	/**
	 * Get the overview in text form.
	 *
	 * @param brief
	 *            true to make it brief.
	 * @param verbose
	 *            true to get also the labels.
	 * @return the overview text.
	 */
	public String getText(boolean brief, boolean verbose) {
		String overviewText = "";

		for (CodeAnalysisItem item : this.items) {
			// only display statistics results if brief overview is set to false
			if (!brief || item.getType() != ItemType.STATISTICS) {
				if (verbose) {
					overviewText += item.getLabel() + ": ";
				}
				overviewText += item.getValue() + "\n";
			}
		}

		return overviewText;
	}

	/**
	 * Add a label and a string value to the overview.
	 *
	 * @param label
	 *            a label that indicates the meaning of the value.
	 * @param value
	 *            a string.
	 * @param type
	 *            the type of the item.
	 *
	 */
	public void add(String label, String value, ItemType type) {
		this.items.add(new CodeAnalysisItem(label, value, type));
	}

	/**
	 * Add a label and a string value to the overview.
	 *
	 * @param label
	 *            a label that indicates the meaning of the value.
	 * @param value
	 *            a string.
	 */
	public void add(String label, String value) {
		this.items.add(new CodeAnalysisItem(label, value, ItemType.LABEL));
	}

	/**
	 * Add a label to the overview.
	 *
	 * @param label
	 *            a label to be added to the overview.
	 */
	public void add(String label) {
		this.items.add(new CodeAnalysisItem(label, "", ItemType.LABEL));
	}

	/**
	 * Add a label and a double to the overview.
	 *
	 * @param label
	 *            a label that indicates the meaning of the value.
	 * @param value
	 *            a double that represents e.g. a statistical average.
	 */
	public void add(String label, Double value) {
		this.items.add(new CodeAnalysisItem(label, "" + value,
				ItemType.STATISTICS));
	}

	/**
	 * Add a label and an integer to the overview.
	 *
	 * @param label
	 *            a label that indicates the meaning of the value.
	 * @param value
	 *            an integer that represents an occurrence count.
	 */
	public void add(String label, int value) {
		this.items.add(new CodeAnalysisItem(label, "" + value,
				ItemType.OCCURRENCECOUNT));
	}

	/**
	 * Adds a separator to the overview.
	 */
	public void addSeparator() {
		add("******************************");
	}
}