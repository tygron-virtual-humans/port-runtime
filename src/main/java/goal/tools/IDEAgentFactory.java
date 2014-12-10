package goal.tools;

import goal.core.agent.AbstractAgentFactory;
import goal.core.runtime.MessagingService;
import goal.preferences.PMPreferences;
import goal.tools.adapt.Learner;

public class IDEAgentFactory extends
AbstractAgentFactory<IDEDebugger, IDEGOALInterpreter> {

	public IDEAgentFactory(MessagingService messaging) {
		super(messaging);
	}

	@Override
	protected IDEDebugger provideDebugger() {
		return new IDEDebugger(this.agentId, this.program, this.environment);
	}

	@Override
	protected IDEGOALInterpreter provideController(IDEDebugger debugger,
			Learner learner) {
		IDEGOALInterpreter controller = new IDEGOALInterpreter(this.program,
				debugger, learner);
		if (PMPreferences.getRemoveKilledAgent()) {
			controller.setDisposeOnTermination();
		}
		return controller;
	}
}
