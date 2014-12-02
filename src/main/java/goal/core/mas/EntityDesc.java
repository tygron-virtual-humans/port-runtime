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

/**
 * Contains an entity description, that is a label-parameter, a type-parameter,
 * and a maximum-parameter.
 *
 * @author tristanbehrens
 *
 */
public class EntityDesc {

	/** label-condition */
	private String name = "";

	/** type-condition */
	private String type = "";

	/** max-condition */
	private int max = 0;

	/**
	 * @return the label of the entity
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the label of the entity
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type of the entity
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type of the entity
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the maximum
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max
	 *            the maximum
	 */
	public void setMax(int max) {
		this.max = max;
	}
}
