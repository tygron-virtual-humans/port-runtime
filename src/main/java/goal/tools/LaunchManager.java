package goal.tools;

import goal.core.agent.AgentFactory;
import krTools.errors.exceptions.ParserException;
import languageTools.program.mas.LaunchRule;
import languageTools.program.mas.MASProgram;
import goal.core.runtime.MessagingService;
import goal.core.runtime.RemoteRuntimeService;
import goal.core.runtime.RuntimeManager;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.environment.EnvironmentService;
import goal.preferences.PMPreferences;
import goal.preferences.RunPreferences;
import goal.tools.errorhandling.exceptions.GOALCommandCancelledException;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.InfoLog;

import java.awt.Component;

import javax.swing.JOptionPane;

import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.MessagingFactory;
import rmimessaging.RmiMessaging;

public class LaunchManager {
	/**
	 * The current LaunchManager
	 */
	private static LaunchManager current;
	/**
	 * Runtime environment that provides various runtimeManager services. If not
	 * null, runtimeManager is running.
	 */
	private RuntimeManager<IDEDebugger, IDEGOALInterpreter> runtimeManager = null;

	public static LaunchManager getCurrent() {
		if (current == null) {
			current = new LaunchManager();
		}
		return current;
	}

	public static LaunchManager createNew() {
		current = new LaunchManager();
		return current;
	}

	private LaunchManager() {

	}

	/**
	 * Launches a MAS program. Can only be killed if there is no runtime running
	 * right now #2666
	 *
	 * @param masProgram
	 *            The registry that specifies how the MAS is to be launched.
	 * @return DOC
	 * @throws ParserException
	 * @throws GOALCommandCancelledException
	 * @throws GOALLaunchFailureException
	 */
	public RuntimeManager<IDEDebugger, IDEGOALInterpreter> launchMAS(
			MASProgram masProgram) throws ParserException,
			GOALCommandCancelledException, GOALLaunchFailureException {
		// Determine where to host middleware; ask user if needed.
		String host = getMiddlewareHostName();

		// FIXME: find a better way to prevent launching a MAS with errors...
		if (!masProgram.isValid()) {
			throw new GOALLaunchFailureException("Cannot launch MAS "
					+ masProgram + " because it (or a child) has errors.");
		}

		// FIXME: we are still using "parsed" objects; therefore we need to
		// reset 'runtime' objects such as the launchrule objects here...
		for (LaunchRule launchRule : masProgram.getLaunchRules()) {
			launchRule.resetApplicationCount();
		}

		// Launch the multi-agent system. and start the runtime environment.
		new InfoLog("Launching MAS " + masProgram.getSourceFile() + ".");

		// Initialize Messaging support.
		MessagingFactory.add(new LocalMessaging());
		MessagingFactory.add(new RmiMessaging());

		Messaging messaging = MessagingFactory.get(RunPreferences
				.getUsedMiddleware().toLowerCase());

		MessagingService messagingService = new MessagingService(host,
				messaging);
		AgentFactory<IDEDebugger, IDEGOALInterpreter> agentFactory = new IDEAgentFactory(
				messagingService);
		AgentService<IDEDebugger, IDEGOALInterpreter> runtimeService = new AgentService<>(
				masProgram, agentFactory);
		EnvironmentService environmentService = new EnvironmentService(
				masProgram, messagingService);

		RemoteRuntimeService<IDEDebugger, IDEGOALInterpreter> remoteRuntimeService = new RemoteRuntimeService<>(
				messagingService);

		runtimeManager = new RuntimeManager<>(messagingService, runtimeService,
				environmentService, remoteRuntimeService);

		return runtimeManager;
	}

	/**
	 * Ask user for middleware host.
	 */
	private static String getMiddlewareHostName()
			throws GOALCommandCancelledException {
		if (PMPreferences.getAlwaysMiddlewareLocal()
				|| RunPreferences.getUsedMiddleware().equals("LOCAL")) { //$NON-NLS-1$
			return "localhost"; //$NON-NLS-1$
		}

		/*
		 * launch requester and ask user. Give "localhost" as default value.
		 * TODO get root frame for centering
		 */
		String s = JOptionPane.showInputDialog((Component) null,
				"Enter middleware host ('localhost' to select this machine)", //$NON-NLS-1$
				"localhost"); //$NON-NLS-1$
		if (s == null) {
			throw new GOALCommandCancelledException(
					"Hostname entry dialog was cancelled"); //$NON-NLS-1$
		}
		return s;
	}

	/**
	 * Returns the runtime service manager.
	 *
	 * @return The runtime service manager, or {@code null} if not available.
	 */
	public RuntimeManager<IDEDebugger, IDEGOALInterpreter> getRuntimeManager() {
		return runtimeManager;
	}

	/**
	 * Returns whether runtime services are available and a runtime environment
	 * has been created.
	 *
	 * @return {@code true} if runtime services are available via
	 *         {@link RuntimeManager}; {@code false} otherwise.
	 */
	public boolean isRuntimeEnvironmentAvailable() {
		return runtimeManager != null;
	}

	/**
	 * DOC Removes all observers, and kills environment, scheduler, MAS and
	 * agents in MAS.
	 *
	 * @throws GOALLaunchFailureException
	 */
	public void shutDownRuntime() throws GOALLaunchFailureException {
		if (runtimeManager != null) {
			runtimeManager.shutDown();
		}
		runtimeManager = null;
	}
}
