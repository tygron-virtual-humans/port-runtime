package goal.core.agent;

import java.util.HashSet;
import java.util.Set;

import eis.iilang.Action;
import eis.iilang.Percept;
import goal.tools.errorhandling.Resources;
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
	public void performAction(Action action) {
		// No environment is attached.
		throw new IllegalStateException(String.format(
				Resources.get(WarningStrings.FAILED_ACTION_AGENT_NOT_ATTACHED),
				action.toProlog()));
	}

	@Override
	public void dispose() {
		// Does nothing.
	}

}
