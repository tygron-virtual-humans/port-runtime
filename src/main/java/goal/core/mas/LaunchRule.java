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
 * A launch-rule is a launch with a precondition. Launch-rules connect agents
 * with entities from environment-interfaces.
 *
 * @author tristanbehrens
 */
public class LaunchRule extends MultiLaunch {
	/** maximum amount of applications of that rule, 0 means no maximum */
	private int entityMax = 0;
	/** required type, empty means no required type */
	private String entityType = "";
	/** required label, empty means no required label */
	private String entityName = "";
	/** increased when applied, related to entityMax */
	private int applicationCount = 0;

	/**
	 * Sets the entity-description and extracts the parameters.
	 *
	 * @param desc
	 *            DOC
	 */
	public void setEntityDesc(EntityDesc desc) {
		if (desc == null) {
			return;
		}

		this.entityMax = desc.getMax();
		this.entityType = desc.getType();
		this.entityName = desc.getName();
	}

	/**
	 * @return The entity max
	 */
	public int getEntityMax() {
		return this.entityMax;
	}

	/**
	 * @return The entity type
	 */
	public String getEntityType() {
		return this.entityType;
	}

	/**
	 * @return The entity name
	 */
	public String getEntityName() {
		return this.entityName;
	}

	/**
	 * If possible, Increments the application-count, that is how often the rule
	 * has been applied.
	 *
	 * @return true if the launch rule could be increased, or false if the
	 *         maximum has been reached.
	 */
	public synchronized boolean incrementApplicationCount() {
		// check if maximum amount has been reached
		if (getEntityMax() != 0 && getEntityMax() == getApplicationCount()) {
			return false;
		}
		this.applicationCount++;
		return true;
	}

	/**
	 * Set the rule application count to 0
	 *
	 * FIXME: this is "parsed" object, should be <b>immutable</b> but we need to
	 * reset stuff here...
	 */
	public void resetApplicationCount() {
		applicationCount = 0;
	}

	/**
	 * @return The rule application count
	 */
	public int getApplicationCount() {
		return this.applicationCount;
	}

	@Override
	public String toString() {
		return "LaunchRule[" + super.toString() + ", max=" + getEntityMax()
				+ ", type=" + getEntityType() + ", label=" + getEntityName()
				+ "]";
	}
}
