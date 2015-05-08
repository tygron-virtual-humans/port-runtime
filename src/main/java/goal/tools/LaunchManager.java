package goal.tools;

import goal.core.agent.AgentFactory;
import goal.core.runtime.MessagingService;
import goal.core.runtime.RemoteRuntimeService;
import goal.core.runtime.RuntimeManager;
import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.environment.EnvironmentService;
import goal.preferences.PMPreferences;
import goal.preferences.RunPreferences;
import goal.tools.adapt.Learner;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALCommandCancelledException;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.InfoLog;
import goal.tools.unittest.UnitTestInterpreter;

import java.awt.Component;
import java.io.File;
import java.util.Map;

import javax.swing.JOptionPane;

import krTools.KRInterface;
import krTools.errors.exceptions.ParserException;
import languageTools.program.agent.AgentProgram;
import languageTools.program.mas.MASProgram;
import languageTools.program.test.AgentTest;
import languageTools.program.test.UnitTest;
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
	 * @param agents
	 *            the agents that are relevant for this MAS.
	 * @return DOC
	 * @throws ParserException
	 * @throws GOALCommandCancelledException
	 * @throws GOALLaunchFailureException
	 */
	public RuntimeManager<IDEDebugger, IDEGOALInterpreter> launchMAS(
			MASProgram masProgram, Map<File, AgentProgram> agents)
			throws ParserException, GOALCommandCancelledException,
			GOALLaunchFailureException {
		// Determine where to host middleware; ask user if needed.
		String host = getMiddlewareHostName();

		if (!masProgram.isValid()) {
			// do we ever get here? Something else is catching this earlier on
			throw new GOALLaunchFailureException(String.format(
					Resources.get(WarningStrings.FAILED_LAUNCH_MAS_ERRORS),
					masProgram.getSourceFile().getName()));
		}

		for (AgentProgram agent : agents.values()) {
			if (!agent.isValid()) {
				throw new GOALLaunchFailureException(String.format(Resources
						.get(WarningStrings.FAILED_LAUNCH_MAS_CHILD_ERRORS),
						masProgram.getSourceFile().getName(), agent
								.getSourceFile().getName()));
			}
		}

		// Launch the multi-agent system. and start the runtime environment.
		new InfoLog("Launching MAS " + masProgram.getSourceFile() + ".");

		// Initialize Messaging support.
		MessagingFactory.add(new LocalMessaging());
		MessagingFactory.add(new RmiMessaging());

		Messaging messaging = MessagingFactory.get(RunPreferences
				.getUsedMiddleware().toLowerCase());

		if (messaging.requiresSerialization()) {
			for (AgentProgram agent : agents.values()) {
				KRInterface kr = agent.getKRInterface();
				if (!kr.supportsSerialization()) {
					throw new GOALLaunchFailureException(
							String.format(
									Resources
											.get(WarningStrings.FAILED_LAUNCH_NO_SERIALIZATION_SUPPORT),
									messaging.getName(), kr.getName(), agent
											.getSourceFile().getName()));
				}
			}
		}

		MessagingService messagingService = new MessagingService(host,
				messaging);
		AgentFactory<IDEDebugger, IDEGOALInterpreter> agentFactory = new IDEAgentFactory(
				messagingService);
		AgentService<IDEDebugger, IDEGOALInterpreter> runtimeService = new AgentService<>(
				masProgram, agents, agentFactory);
		EnvironmentService environmentService = new EnvironmentService(
				masProgram, messagingService);

		RemoteRuntimeService<IDEDebugger, IDEGOALInterpreter> remoteRuntimeService = new RemoteRuntimeService<>(
				messagingService);

		this.runtimeManager = new RuntimeManager<>(messagingService,
				runtimeService, environmentService, remoteRuntimeService);

		return this.runtimeManager;
	}

	/**
	 * Launches a MAS program. Can only be killed if there is no runtime running
	 * right now #2666
	 *
	 * @param masProgram
	 *            The registry that specifies how the MAS is to be launched.
	 * @param agents
	 *            the agents that are relevant for this MAS.
	 * @return DOC
	 * @throws ParserException
	 * @throws GOALCommandCancelledException
	 * @throws GOALLaunchFailureException
	 */
	public RuntimeManager<IDEDebugger, IDEGOALInterpreter> launchTest(
			UnitTest test) throws ParserException,
			GOALCommandCancelledException, GOALLaunchFailureException {
		// Determine where to host middleware; ask user if needed.
		String host = getMiddlewareHostName();

		if (!test.isValid() || !test.getMasProgram().isValid()) {
			// do we ever get here? Something else is catching this earlier on
			throw new GOALLaunchFailureException(String.format(
					Resources.get(WarningStrings.FAILED_LAUNCH_MAS_ERRORS),
					test.getSourceFile().getName()));
		}

		for (AgentProgram agent : test.getAgents().values()) {
			if (!agent.isValid()) {
				throw new GOALLaunchFailureException(String.format(Resources
						.get(WarningStrings.FAILED_LAUNCH_MAS_CHILD_ERRORS),
						test.getSourceFile().getName(), agent.getSourceFile()
								.getName()));
			}
		}

		// Launch the multi-agent system. and start the runtime environment.
		new InfoLog("Launching MAS " + test.getSourceFile() + ".");

		// Initialize Messaging support.
		MessagingFactory.add(new LocalMessaging());
		MessagingFactory.add(new RmiMessaging());

		Messaging messaging = MessagingFactory.get(RunPreferences
				.getUsedMiddleware().toLowerCase());

		if (messaging.requiresSerialization()) {
			for (AgentProgram agent : test.getAgents().values()) {
				KRInterface kr = agent.getKRInterface();
				if (!kr.supportsSerialization()) {
					throw new GOALLaunchFailureException(
							String.format(
									Resources
											.get(WarningStrings.FAILED_LAUNCH_NO_SERIALIZATION_SUPPORT),
									messaging.getName(), kr.getName(), agent
											.getSourceFile().getName()));
				}
			}
		}

		MessagingService messagingService = new MessagingService(host,
				messaging);
		TestRunAgentFactory agentFactory = new TestRunAgentFactory(test,
				messagingService);
		AgentService<IDEDebugger, IDEGOALInterpreter> runtimeService = new AgentService<>(
				test.getMasProgram(), test.getAgents(), agentFactory);
		EnvironmentService environmentService = new EnvironmentService(
				test.getMasProgram(), messagingService);

		RemoteRuntimeService<IDEDebugger, IDEGOALInterpreter> remoteRuntimeService = new RemoteRuntimeService<>(
				messagingService);

		this.runtimeManager = new RuntimeManager<>(messagingService,
				runtimeService, environmentService, remoteRuntimeService);

		return this.runtimeManager;
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
		return this.runtimeManager;
	}

	/**
	 * Returns whether runtime services are available and a runtime environment
	 * has been created.
	 *
	 * @return {@code true} if runtime services are available via
	 *         {@link RuntimeManager}; {@code false} otherwise.
	 */
	public boolean isRuntimeEnvironmentAvailable() {
		return this.runtimeManager != null;
	}

	/**
	 * DOC Removes all observers, and kills environment, scheduler, MAS and
	 * agents in MAS.
	 *
	 * @throws GOALLaunchFailureException
	 */
	public void shutDownRuntime() throws GOALLaunchFailureException {
		if (this.runtimeManager != null) {
			this.runtimeManager.shutDown();
		}
		this.runtimeManager = null;
	}

	private class TestRunAgentFactory extends IDEAgentFactory {
		private final UnitTest test;

		public TestRunAgentFactory(UnitTest test, MessagingService messaging) {
			super(messaging);
			this.test = test;
		}

		@Override
		protected UnitTestInterpreter provideController(IDEDebugger debugger,
				Learner learner) {
			AgentTest test = this.test.getTest(this.agentBaseName);
			return new UnitTestInterpreter(this.program, test, debugger,
					learner);
		}
	}
}
