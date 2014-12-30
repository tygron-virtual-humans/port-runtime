package goal.tools;

import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.AgentFactory;
import goal.core.agent.GOALInterpreter;
import goal.core.runtime.MessagingService;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.LoggingObserver;
import goal.tools.debugger.NOPDebugger;
import goal.tools.debugger.ObservableDebugger;

import java.io.File;

import krTools.errors.exceptions.ParserException;
import languageTools.program.mas.MASProgram;

/**
 * A single run of a {@link MASProgram}. This class can be used to launch, run
 * and terminate a mas program for a single iteration. The result of the run can
 * be inspected by setting a {@link ResultInspector}.
 *
 * During the run the MAS will use agents running the {@link GOALInterpreter}
 * which will use the {@link NOPDebugger}.
 *
 * @author M.P. Korstanje
 * @modified K.Hindriks
 */
public class SingleRun extends AbstractRun<Debugger, GOALInterpreter<Debugger>> {

	/**
	 * Constructs an instance of SingleRun using the given file as the
	 * MASProgram.
	 *
	 * @param masFile
	 *            file to use as a MASProgram
	 * @throws ParserException
	 *             when the mas file could not be parsed.
	 */
	public SingleRun(File masFile) throws ParserException {
		super(PlatformManager.createNew().parseMASFile(masFile),
				PlatformManager.getCurrent().getParsedAgentPrograms());
	}

	private class SingleRunAgentFactory extends
	AbstractAgentFactory<Debugger, GOALInterpreter<Debugger>> {

		public SingleRunAgentFactory(MessagingService messaging) {
			super(messaging);
		}

		@Override
		protected Debugger provideDebugger() {
			if (SingleRun.this.debuggerOutput) {
				ObservableDebugger observabledebugger = new ObservableDebugger(
						this.agentId, this.environment);
				new LoggingObserver(observabledebugger);
				return observabledebugger;
			} else {
				return new NOPDebugger(this.agentId);
			}
		}

		@Override
		protected GOALInterpreter<Debugger> provideController(
				Debugger debugger, Learner learner) {
			return new GOALInterpreter<>(this.program, debugger, learner);
		}

	}

	@Override
	protected AgentFactory<Debugger, GOALInterpreter<Debugger>> buildAgentFactory(
			MessagingService messaging) {
		return new SingleRunAgentFactory(messaging);
	}
}