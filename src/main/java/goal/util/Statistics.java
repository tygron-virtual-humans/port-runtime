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

package goal.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements simple statistic functionality.
 *
 * @author W.Pasman 11aug2010
 * @modified Koen dd110404 Statistics class no longer dependent on
 *           CodeAnalysisOverview class.
 */
public class Statistics {
	/**
	 * For convenience, an array list is used instead of a set.
	 */
	private final List<Double> dataSet = new LinkedList<>();

	/**
	 * DOC
	 */
	public Statistics() {
	}

	/**
	 * Initializes the statistics with given set of data.
	 *
	 * @param dataSet
	 *            A set of double values.
	 * @throws NullPointerException
	 *             When given data is null.
	 */
	public Statistics(List<Double> dataSet) {
		this.dataSet.addAll(dataSet);
	}

	/**
	 * Returns the data set used for this statistics.
	 *
	 * @return ArrayList<Double> of data elements.
	 */
	public List<Double> getData() {
		return this.dataSet;
	}

	/**
	 * Adds a new data entry to the data set.
	 *
	 * @param d
	 *            The new data element.
	 */
	public void add(Double d) {
		this.dataSet.add(d);
	}

	/**
	 * merge this statistics with other statistics. Effectively this means just
	 * addAll with the data from the other statistics.
	 *
	 * @param statistics
	 *            The statistics that is to be merged.
	 */
	public void merge(Statistics statistics) {
		this.dataSet.addAll(statistics.getData());
	}

	/**
	 * Checks whether data set is empty.
	 *
	 * @return true if empty, else false.
	 */
	public boolean isEmpty() {
		return this.dataSet.isEmpty();
	}

	/**
	 * Returns the size of the data set.
	 *
	 * @return The number of items in the data set.
	 */
	public int getSize() {
		return this.dataSet.size();
	}

	/**
	 * Returns the mean value of all of the data set elements.
	 *
	 * @return The mean of the data elements.
	 *
	 * @throws ArithmeticException
	 *             if there are 0 numbers in the data set.
	 */
	public Double getMean() {
		if (isEmpty()) {
			throw new ArithmeticException("data array is empty");
		}
		return getSum() / getSize();
	}

	/**
	 * Returns the sum of the data elements.
	 *
	 * @return The sum of the data elements, 0 if there is no data.
	 */
	public Double getSum() {
		Double sum = 0.0;
		for (Double d : this.dataSet) {
			sum = sum + d;
		}
		return sum;
	}

	/**
	 * Returns the smallest data element present in the data set.
	 *
	 * @return The minimal data element in the data set.
	 * @throws ArithmeticException
	 *             when data set is empty
	 */
	public Double getMinimum() {
		if (isEmpty()) {
			throw new ArithmeticException("Data set is empty.");
		}
		Double minValue = this.dataSet.get(0);
		for (Double d : this.dataSet) {
			if (d < minValue) {
				minValue = d;
			}
		}
		return minValue;
	}

	/**
	 * Returns the largest data element.
	 *
	 * @return The data element with the largest value.
	 *
	 * @throws ArithmeticException
	 *             when data set is empty
	 */
	public Double getMaximum() {
		if (isEmpty()) {
			throw new ArithmeticException("Data set is empty.");
		}
		Double maxValue = this.dataSet.get(0);
		for (Double d : this.dataSet) {
			if (d > maxValue) {
				maxValue = d;
			}
		}
		return maxValue;
	}

	/**
	 * DOC
	 */
	@Override
	public String toString() {
		return getSum() + "/" + getSize();
	}
}
