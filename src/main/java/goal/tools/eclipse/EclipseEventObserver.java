package goal.tools.eclipse;

import goal.core.agent.Agent;
import goal.core.runtime.RuntimeEvent;
import goal.core.runtime.RuntimeEventObserver;
import goal.core.runtime.RuntimeManager;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.tools.IDEGOALInterpreter;
import goal.tools.eclipse.DebugCommand.Command;

import java.util.HashMap;
import java.util.Map;

import languageTools.program.agent.AgentId;

public class EclipseEventObserver implements RuntimeEventObserver {
	private final Map<AgentId, EclipseDebugObserver> observers;
	private InputReaderWriter writer;
	private EnvironmentPort environment;

	public EclipseEventObserver() {
		this.observers = new HashMap<>();
	}

	public void setWriter(final InputReaderWriter writer) {
		this.writer = writer;
	}

	/**
	 * Handles events from a {@link RuntimeManager}.
	 *
	 * @param observable
	 *            The {@link RuntimeManager}.
	 * @param event
	 *            A {@link RuntimeEvent} event.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eventOccured(RuntimeManager observable, RuntimeEvent event) {
		switch (event.getType()) {
		case ENVIRONMENT_LAUNCHED:
			final EnvironmentPort env1 = (EnvironmentPort) event.getSource();
			this.writer.write(new DebugCommand(Command.ENV_CREATED, env1
					.getMessageBoxId()));
			this.writer.write(new DebugCommand(Command.ENV_STATE, env1
					.getMessageBoxId(), env1.getEnvironmentState().name()));
			break;
		case ENVIRONMENT_RUNMODE_CHANGED:
			final EnvironmentPort env2 = (EnvironmentPort) event.getSource();
			this.writer.write(new DebugCommand(Command.ENV_STATE, env2
					.getMessageBoxId(), env2.getEnvironmentState().name()));
			break;
		case AGENT_IS_LOCAL_AND_READY:
			final Agent<IDEGOALInterpreter> agent = (Agent<IDEGOALInterpreter>) event
			.getSource();
			final EclipseDebugObserver debugobserver = new EclipseDebugObserver(
					agent, this.writer);
			debugobserver.subscribe();
			this.observers.put(agent.getId(), debugobserver);
			break;
		default:
			break;
		}
	}

	public EclipseDebugObserver getObserver(
			final Agent<IDEGOALInterpreter> agent) {
		return this.observers.get(agent.getId());
	}
}
