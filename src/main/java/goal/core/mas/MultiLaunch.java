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

package goal.core.mas;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A multilaunch instantiates one or several agents at the beginning of the
 * execution. Those agents are not necessarily connected to an environment.
 * Basically it is a List of Launch objects
 *
 * @author W.Pasman 21mar2011
 *
 */
public class MultiLaunch {

	private final List<Launch> launches = new LinkedList<>();

	/**
	 * adds a new launch descriptor
	 *
	 * @param newLaunch
	 *            new additional launch for this multi-launch.
	 */
	public void addLaunch(Launch newLaunch) {
		launches.add(newLaunch);
	}

	/**
	 * Add given collection to the launches
	 *
	 * @param newLaunches
	 *            is List of new {@link Launch}es
	 */
	public void addLaunches(List<Launch> newLaunches) {
		launches.addAll(newLaunches);
	}

	/**
	 * get the list of launches in the multi-launch
	 *
	 * @return list of {@link Launch} objects
	 */
	public List<Launch> getLaunches() {
		return launches;
	}

	@Override
	public String toString() {
		return "MultiLaunch[" + launches + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Launch launch : launches) {
			result = result * prime + launch.toString().hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MultiLaunch other = (MultiLaunch) obj;
		Set<Launch> myLaunches = new HashSet<>(launches);
		Set<Launch> otherLaunches = new HashSet<>(other.getLaunches());
		return myLaunches.equals(otherLaunches);
	}
}
