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

import goal.core.kr.KRlanguage;
import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.actions.Action;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.rules.RuleSet;
import goal.core.program.validation.masfile.MASProgramValidator;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.PlatformManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A MAS-program consists of
 * <ul>
 * <li>optionally: an environment info block, containing:</li>
 * <ul>
 * <li>a non-empty list of jar files each being an EIS environment
 * <li>an initializer for the first environment.
 * </ul>
 * <li>a list of paths to GOAL-programs,</li> <li>a list of launches, and</li>
 * <li>a list of launch-rules.</li> </ul>
 *
 * <p/>
 *
 * It is the MAS-parser that translates <i>.mas</i>-files to MAS-programs.
 *
 * <p/>
 *
 * Launches are applied only once and instantiate agents without connections to
 * environment-interfaces. Launch-rules can be applied during the execution of
 * the MAS, as long/often as they are applicable.
 *
 * <p/>
 *
 * After parsing you have to process the MAS-program with the respective method.
 * This resolves file-names.
 *
 * @author tristanbehrens
 * @author W.Pasman 2mar10 added multiple jar and initializer support
 * @modified K.Hindriks
 */
@SuppressWarnings("serial")
public class MASProgram extends ParsedObject {
	/**
	 * The program has to be processed before it can be used.
	 *
	 * FIXME: This is too error prone... We need a proper method for preventing
	 * use of this MASProgram.
	 */
	private boolean processed = false;
	private boolean validated = false;
	/**
	 * The source file used to create this {@link MASProgram}.
	 */
	private final File masfile;
	/**
	 * Container for the environment info present in the MAS file source.
	 * Information extracted from the <i>environment</i> section in the MAS
	 * file.
	 */
	private EnvironmentInfo environmentInfo = null;
	/**
	 * The agent (.goal) files references in the MAS file.
	 */
	private List<AgentFile> agentFiles = new LinkedList<>();
	/**
	 * The source code location where the agent files section starts in the MAS
	 * file.
	 */
	private InputStreamPosition agentFilesSource = null;
	/**
	 * A list of launches. Each a MultiLaunch or a LaunchRule
	 */
	private final List<MultiLaunch> launches = new LinkedList<>();
	/**
	 * A list of launch-rules.
	 */
	private final List<LaunchRule> launchRules = new LinkedList<>();
	/**
	 * The source code location where the launch policy section starts in the
	 * MAS file.
	 */
	private InputStreamPosition launchPolicySource = null;

	/**
	 * DOC
	 *
	 * @param masFile
	 * @param source
	 */
	public MASProgram(File masFile, InputStreamPosition source) {
		super(source);
		this.masfile = masFile;
	}

	/**
	 * DOC
	 *
	 * @param source
	 */
	public MASProgram(InputStreamPosition source) {
		super(source);
		this.masfile = source.getSourceFile();
	}

	/**
	 * check if MAS strongly validates. If the MAS file validates, this means
	 * that both the MAS and all the files it refers to are both syntactically
	 * and semantically correct. The MAS is ready to run.
	 *
	 * @return true if the MAS file validates, false if not.
	 */
	public boolean isValidated() {
		boolean valid = this.validated;
		if (valid) {
			// FIXME: not the right place here, not the right code either...
			// should be in validator or, maybe in MASProgram, but MASProgram
			// does not have access to its parsed GOAL file children...
			// In case there is no environment, make sure all actions are
			// considered as internal. {
			for (AgentFile agentFile : this.agentFiles) {
				GOALProgram agent = PlatformManager.getCurrent()
						.getGOALProgram(agentFile.getAgentFile());
				if (getEnvironmentInfo() == null) {
					for (Module module : agent.getAllModules()) {
						RuleSet rules = module.getRuleSet();
						for (int i = 0; i < rules.getRuleCount(); i++) {
							for (Action action : rules.getRule(i).getAction()) {
								if (action instanceof UserSpecAction) {
									UserSpecAction spec = (UserSpecAction) action;
									spec.setExternal(false);
								}
							}
						}
					}
				}
				if (!agent.isValidated()) {
					valid = false;
				}
			}
		}

		return valid;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * Returns the (original) file path to the MAS file.
	 *
	 * @return Path of the file used to create this {@link MASProgram}.
	 */
	public File getMASFile() {
		return masfile;
	}

	/**
	 * Returns all the KR languages that are used by the agents that are part of
	 * this MAS.
	 *
	 * @return A set of KR languages.
	 */
	public Set<KRlanguage> getKRlanguages() {
		HashSet<KRlanguage> languages = new HashSet<>(agentFiles.size());
		for (AgentFile agentFile : agentFiles) {
			languages.add(PlatformManager.getCurrent()
					.getGOALProgram(agentFile.getAgentFile()).getKRLanguage());
		}
		return languages;
	}

	/**
	 * Stores all environment related information.
	 *
	 * @param info
	 *            Reference to environment .jar file and initialization
	 *            information.
	 */
	public void setEnvironmentInfo(EnvironmentInfo info) {
		this.environmentInfo = info;
	}

	/**
	 * Returns the environment file path (full path to environment jar-file).
	 */
	public EnvironmentInfo getEnvironmentInfo() {
		return this.environmentInfo;
	}

	public boolean hasEnvironmentSection() {
		return (environmentInfo != null);
	}

	public String getEnvironmentName() {
		return environmentInfo.getName();
	}

	/**
	 * Sets the agent files references by the MAS program.
	 *
	 * @param agentFiles
	 *            The agent files.
	 * @param source
	 *            A pointer to where the agent file section was defined in the
	 *            MAS file.
	 */
	public void setAgentFiles(List<AgentFile> agentFiles,
			InputStreamPosition source) {
		this.agentFiles = agentFiles;
		this.agentFilesSource = source;
	}

	/**
	 * Returns the list of agent files referenced by this {@link MASProgram}.
	 *
	 * @return A list of agent files.
	 */
	public List<AgentFile> getAgentFiles() {
		return this.agentFiles;
	}

	/**
	 * @return A pointer to where the agentfiles-section was defined in the .mas
	 *         file.
	 */
	public InputStreamPosition getAgentFilesSource() {
		return this.agentFilesSource;
	}

	/**
	 * Returns the absolute paths to the agent files listed in the MAS file.
	 * This list of files will be present, even if the MAS file could not be
	 * parsed.
	 *
	 * @return list of (absolute) agent file paths.
	 */
	public List<File> getAgentPaths() {
		List<File> paths = new ArrayList<>(agentFiles.size());
		for (AgentFile agentFile : agentFiles) {
			paths.add(agentFile.getAgentFile());
		}
		return paths;
	}

	/**
	 * Sets the launch policy.
	 *
	 * @param l
	 *            a vector containing launches and launch-rules.
	 * @param source
	 *            A pointer to where the launchpolicy-section was defined in the
	 *            .mas file.
	 */
	public void setLaunchPolicy(List<MultiLaunch> l, InputStreamPosition source) {
		for (MultiLaunch o : l) {
			if (o instanceof LaunchRule) {
				this.launchRules.add((LaunchRule) o);
			} else /* if (o instanceof MultiLaunch) */{
				this.launches.add(o);
			}
		}
		this.launchPolicySource = source;
	}

	/**
	 * Returns all launches.
	 *
	 * @return a list of launches.
	 */
	public List<MultiLaunch> getLaunches() {
		if (this.processed) {
			return this.launches;
		} else {
			throw new UnsupportedOperationException(
					"MASProgram.postProcess() has to be called before it can be used");
		}
	}

	/**
	 * Returns all launch-rules.
	 *
	 * @return a list of launch-rules.
	 */
	public List<LaunchRule> getLaunchRules() {
		if (this.processed) {
			return this.launchRules;
		} else {
			throw new UnsupportedOperationException(
					"MASProgram.postProcess() has to be called before it can be used");
		}
	}

	/**
	 * @return A pointer to where in the .mas file the launchpoilicy section was
	 *         defined.
	 */
	public InputStreamPosition getLaunchPolicySource() {
		return this.launchPolicySource;
	}

	@Override
	public String toString() {
		return this.masfile.getName();
	}

	/**
	 * Resolves agent labels and file names.
	 *
	 * <p>
	 * Does not check for inconsistencies. A {@link MASProgramValidator} must be
	 * used for that.
	 * </p>
	 */
	public void postProcess() {
		// Make sure there is a name to reference the file.
		for (AgentFile agentFile : this.agentFiles) {
			// If no label is specified retrieve it from the path.
			if (agentFile.getName() == null) {
				// get the file-label sans folders and extension
				File file = new File(agentFile.getPath());
				String name = file.getName();
				name.lastIndexOf(".");
				name = name.substring(0, name.lastIndexOf("."));
				// set
				agentFile.setName(name);
			}
		}

		// Resolve references.
		for (MultiLaunch ml : this.launches) {
			for (Launch launch : ml.getLaunches()) {
				AgentFile agentFile = null;
				for (AgentFile af : this.agentFiles) {
					if (af.getName().equals(launch.getAgentFileRef())) {
						agentFile = af;
						break;
					}
				}
				launch.setAgentFile(agentFile);
			}
		}
		for (LaunchRule launchRule : this.launchRules) {
			for (Launch launch : launchRule.getLaunches()) {
				AgentFile agentFile = null;
				for (AgentFile af : this.agentFiles) {
					if (af.getName().equals(launch.getAgentFileRef())) {
						agentFile = af;
						break;
					}
				}
				launch.setAgentFile(agentFile);
			}
		}

		// Done, now you can use the getters.
		this.processed = true;
	}
}
