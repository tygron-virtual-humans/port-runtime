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

package goal.core.program.validation.masfile;

import goal.core.mas.AgentFile;
import goal.core.mas.Launch;
import goal.core.mas.LaunchRule;
import goal.core.mas.MASProgram;
import goal.core.mas.MultiLaunch;
import goal.core.program.validation.Validator;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorWarning;
import goal.tools.PlatformManager;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Validator} for a {@link MASProgram}.
 * <p>
 * Checks for the following errors:
 * <ul>
 * <li>Absence of a link to an environment file when the environment description
 * is non-null</li>
 * <li>Absence of an agent file definition.</li>
 * <li>Multiple definitions of an agent file with the same name.</li>
 * <li>Absence of a launch or launch rule definition.</li>
 * <li>A {@link Launch} or {@link LaunchRule} that has a null agent file</li>
 * </ul>
 * </p>
 *
 * @author N.Kraayenbrink
 * @author K.Hindriks
 */
public class MASProgramValidator extends Validator<MASProgram> {

	@Override
	protected void doValidate(MASProgram subject) {
		// Check whether environment section is not empty.
		if (subject.getEnvironmentInfo() != null) {
			if (subject.getEnvironmentName() == null) {
				report(new ValidatorWarning(
						MASWarning.ENVIRONMENT_NO_REFERENCE,
						subject.getEnvironmentInfo()));
			} else {
				// Check that the environment file exists (if it's a .jar file)
				String masDir = subject.getMASFile().getParentFile()
						.getAbsolutePath();
				String environmentName = subject.getEnvironmentName();
				if (environmentName.endsWith("jar")) {
					try {
						subject.getEnvironmentInfo().setEnvironmentFile(
								PlatformManager.resolveFileReference(masDir,
										environmentName));
					} catch (Exception e) {
						report(new ValidatorError(
								MASError.ENVIRONMENT_COULDNOT_OPEN,
								subject.getEnvironmentInfo(), environmentName,
								e.getMessage()));
					}
					if (subject.getEnvironmentInfo().getEnvironmentFile() == null) {
						report(new ValidatorError(
								MASError.ENVIRONMENT_CANNOT_FIND,
								subject.getEnvironmentInfo(), environmentName));
					}
				}
			}
		}

		// Check that agent files are only specified once, and that there is at
		// least one.
		List<AgentFile> agentFiles = new ArrayList<>(subject.getAgentFiles());
		if (agentFiles.isEmpty()) {
			report(new ValidatorWarning(MASWarning.AGENTFILES_NO_AGENTS,
					subject.getAgentFilesSource()));
		} else {
			// find agent files with the same name
			for (int i = 0; i < agentFiles.size(); i++) {
				for (int j = i + 1; j < agentFiles.size(); j++) {
					final AgentFile afI = agentFiles.get(i);
					final AgentFile afJ = agentFiles.get(j);
					if (afI.getName().equals(afJ.getName())) {
						report(new ValidatorError(
								MASError.AGENTFILES_DUPLICATE_NAME,
								afJ.getSource(), afJ.getName()));
					}
				}
			}
		}

		// Check that agent files referenced exist.
		for (AgentFile agentFile : agentFiles) {
			if (agentFile.getAgentFile() == null) {
				report(new ValidatorError(MASError.AGENTFILE_CANNOT_FIND,
						agentFile, agentFile.getName(), agentFile.getPath()));
			}
		}

		// Check that there is at least one launch rule.
		if (subject.getLaunches().isEmpty()
				&& subject.getLaunchRules().isEmpty()) {
			report(new ValidatorWarning(MASWarning.LAUNCH_NONEFOUND,
					subject.getLaunchPolicySource()));
		}

		// Check that all launch rules have a non-null agent file.
		for (MultiLaunch mlaunch : subject.getLaunches()) {
			for (Launch launch : mlaunch.getLaunches()) {
				if (launch.getAgentFile() == null) {
					report(new ValidatorError(
							MASError.AGENTFILE_NONEXISTANT_REFERENCE,
							launch.getAgentFileRefSource(),
							launch.getAgentFileRef()));
				} else if (launch.takesAgentNameFromEnvironment()) {
					report(new ValidatorError(MASError.LAUNCH_INVALID_WILDCARD,
							launch.getAgentFileRefSource(), launch.toString()));
				} else {
					agentFiles.remove(launch.getAgentFile());
				}
			}
		}

		for (LaunchRule rule : subject.getLaunchRules()) {
			for (Launch launch : rule.getLaunches()) {
				if (launch.getAgentFile() == null) {
					report(new ValidatorError(
							MASError.AGENTFILE_NONEXISTANT_REFERENCE,
							launch.getAgentFileRefSource(),
							launch.getAgentFileRef()));
				} else if (rule.getEntityName() == null
						&& launch.takesAgentNameFromEnvironment()) {
					report(new ValidatorError(MASError.LAUNCH_INVALID_WILDCARD,
							launch.getAgentFileRefSource(), launch.toString()));
				} else {
					agentFiles.remove(launch.getAgentFile());
				}
			}
		}

		for (AgentFile agentFile : agentFiles) {
			report(new ValidatorWarning(MASWarning.AGENTFILE_UNUSED,
					agentFile.getSource(), agentFile.getName()));
		}
	}
}
