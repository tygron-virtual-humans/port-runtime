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

import goal.core.program.GOALProgram;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.PlatformManager;

/**
 * A launch is the part of a {@link LaunchRule} after the condition.
 *
 * @author tristanbehrens
 */
public class Launch extends ParsedObject {
	private static final long serialVersionUID = -1964084433547407698L;

	/**
	 * The agent-base-label is used to generate a unique identifier.
	 */
	private String agentBaseName = "";
	/**
	 * The agent-file-reference points to a agent-file in the respective section
	 * of the MAS-file.
	 */
	private String agentFileRef = null;
	/**
	 * Where in the MAS file the agent-file reference was defined.
	 */
	private InputStreamPosition agentFileRefSource = null;
	/**
	 * Denotes the number of agents that should be instantiated.
	 */
	private int numberOfAgentsToLaunch = 1;
	/**
	 * Denotes the agent-file. Is resolved after parsing, thus not by the
	 * parser.
	 */
	private AgentFile agentFile = null;
	/**
	 * Module to start the agent program from
	 */
	private String entryModule = null;

	/**
	 * Creates a new launch descriptor
	 *
	 * @param source
	 *            Where in the .mas file the definition of the launch starts.
	 */
	public Launch(InputStreamPosition source) {
		super(source);
	}

	/**
	 * An agent can be given a name in two ways. Either the agent takes the name
	 * from the entity it connects to. Or it uses the name can either be a name
	 * provided in the launch.
	 *
	 * Given the suggested name by the environment this method will return the
	 * desired name.
	 *
	 * @param entityName
	 *            The suggested name.
	 *
	 * @return the desired name for the agent.
	 */
	public String getAgentBaseName(String entityName) {
		if (entityName != null && takesAgentNameFromEnvironment()) {
			return entityName;
		}
		return agentBaseName;
	}

	/**
	 *
	 * @return true if the agent takes the name from the entity provided by the
	 *         environment.
	 */
	public boolean takesAgentNameFromEnvironment() {
		return agentBaseName.equals("*");
	}

	/**
	 * @return the agent suggested by the by launch rule or null when the agent
	 *         takes the name from the entity it will be connected to.
	 */
	public String getAgentBaseName() {
		if (takesAgentNameFromEnvironment()) {
			return null;
		}
		return agentBaseName;
	}

	/**
	 * Sets the agent-base-label.
	 *
	 * @param agentBaseName
	 *            The label
	 */
	public void setAgentBaseName(String agentBaseName) {
		this.agentBaseName = agentBaseName;
	}

	/**
	 * @return the agent-file-reference.
	 */
	public String getAgentFileRef() {
		assert this.agentFileRef != null : "Implement!"; // TODO get from
		// filename
		return this.agentFileRef;
	}

	/**
	 * @return A pointer to where the agent file reference was defined in the
	 *         .mas file.
	 */
	public InputStreamPosition getAgentFileRefSource() {
		return this.agentFileRefSource;
	}

	/**
	 * Sets the agent-file-reference.
	 *
	 * @param agentFileRef
	 *            The reference
	 * @param source
	 *            The corresponding source position in the code
	 */
	public void setAgentFileRef(String agentFileRef, InputStreamPosition source) {
		this.agentFileRef = agentFileRef;
		this.agentFileRefSource = source;
	}

	/**
	 *
	 * @return the agent-number
	 */
	public int getNumberOfAgentsToLaunch() {
		return this.numberOfAgentsToLaunch;
	}

	/**
	 * Sets the agent-number.
	 *
	 * @param agentNumber
	 *            The number
	 */
	public void setAgentNumber(int agentNumber) {
		this.numberOfAgentsToLaunch = agentNumber;
	}

	/**
	 * @return the agent-file
	 */
	public AgentFile getAgentFile() {
		return this.agentFile;
	}

	/**
	 * Sets the agent-file-label.
	 *
	 * @param af
	 *            The AgentFile object
	 */
	public void setAgentFile(AgentFile af) {
		this.agentFile = af;
	}

	/**
	 *
	 * @return the entry module
	 */
	public String getEntryModule() {
		return this.entryModule;
	}

	/**
	 * Sets the module from which the agent will start execution.
	 *
	 * @param entryModule
	 *            to start the agent from.
	 */
	public void setEntryModule(String entryModule) {
		this.entryModule = entryModule;
	}

	/**
	 * @return A printable version of the Launch rule.
	 */
	@Override
	public String toString() {
		return "Launch[basename=" + agentBaseName + ", max="
				+ numberOfAgentsToLaunch + " file=" + agentFileRef + "]";
	}

	/** @return The GOALProgram associated to this launch */
	public GOALProgram getGOALProgram() {
		// FIXME: Launch should have a reference to the MASProgram it is part of
		// This in turn should have a reference to the GOAL files that are part
		// of it.
		return PlatformManager.getCurrent().getGOALProgram(
				agentFile.getAgentFile());
	}
}
