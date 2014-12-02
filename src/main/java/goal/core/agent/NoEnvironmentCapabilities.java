package goal.core.agent;

import java.util.HashSet;
import java.util.Set;

import eis.iilang.Percept;
import goal.core.program.actions.UserSpecAction;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;

/**
 * NOP implementation of {@link EnvironmentCapabilities}.
 *
 * @author mpkorstanje
 *
 */
public class NoEnvironmentCapabilities implements EnvironmentCapabilities {

	@Override
	public Double getReward() {
		return 0.0;
	}

	@Override
	public Set<Percept> getPercepts() {
		return new HashSet<>(0);
	}

	@Override
	public void performAction(UserSpecAction action) {
		// No environment is available, make a warning.
		new Warning(String.format(
				Resources.get(WarningStrings.FAILED_ACTION_AGENT_NOT_ATTACHED),
				action.toString()));
	}

	@Override
	public void dispose() {
		// Does nothing.
	}

}
