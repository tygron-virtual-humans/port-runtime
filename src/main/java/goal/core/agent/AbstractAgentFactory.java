package goal.core.agent;

import goal.core.runtime.MessagingService;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.tools.adapt.FileLearner;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Debugger;
import goal.tools.logging.GOALLoggerDelayed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import krTools.errors.exceptions.KRInitFailedException;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import nl.tudelft.goal.messaging.exceptions.CommunicationFailureException;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId.Type;

/**
 * Abstract base for building Agents. Subclasses can provide different
 * Messaging- and EnvironmentCapabilities, Debuggers, Learners and Controllers.
 * This can be done by overriding or implementing the proper methods.
 *
 * During the construction the class fields will be initialized to assist the
 * creation of different classes.
 *
 * When extending this class and providing constructor withouth messaging
 * subclasses classes should be aware that messageBoxId and messageBox fields
 * are null when building agents. The provideMessagingCapabilities method should
 * be overridden accordingly.
 *
 * It might happen that multiple agents are constructed at the same time.
 * Therefore this class should be thread safe. #2953
 *
 * @author mpkorstanje
 *
 * @param <D>
 *            class of the Debugger to provide.
 * @param <C>
 *            class of the GOALInterpreter to provide.
 */
public abstract class AbstractAgentFactory<D extends Debugger, C extends GOALInterpreter<D>>
implements AgentFactory<D, C> {

	private final MessagingService messaging;

	/**
	 * AgentProgram for the agent.
	 */
	protected AgentProgram program;
	/**
	 * Environment in which the agent will be placed.
	 */
	protected EnvironmentPort environment;
	/**
	 * MessageBoxID of the agent.
	 */
	protected MessageBoxId messageBoxId;

	/**
	 * AgentId of the agent.
	 */
	protected AgentId agentId;
	/**
	 * MessageBox of the agent.
	 */
	protected MessageBox messageBox;

	/**
	 * Base name of the agent. Provided by launch rules or environment.
	 */
	protected String agentBaseName;

	/**
	 * This factory's creation time.
	 */
	private final Date factoryCreationTime = new Date();

	/**
	 * Constructs a new factory.
	 *
	 * @param messaging
	 *            used to construct agents.
	 */
	public AbstractAgentFactory(MessagingService messaging) {
		this.messaging = messaging;
	}

	/**
	 * Constructs factory without messaging.
	 */
	public AbstractAgentFactory() {
		this(null);
	}

	@Override
	public synchronized Agent<C> build(AgentProgram program,
			String agentBaseName, EnvironmentPort environment)
					throws MessagingException, KRInitFailedException {
		/*
		 * Initialize variables used in agent construction.
		 */
		this.program = program;
		this.environment = environment;
		this.agentBaseName = agentBaseName;

		if (this.messaging != null) {
			this.messageBoxId = provideMessageBoxId(agentBaseName);
			this.messageBox = provideMessageBox(this.messageBoxId);
		}

		this.agentId = provideAgentId(this.messageBoxId);

		/*
		 * Construct agent components.
		 */
		MessagingCapabilities messagingCapabilities = provideMessagingCapabilities();
		EnvironmentCapabilities environmentCapabilities = provideEnvironmentCapabilities();
		LoggingCapabilities loggingCapabilities = provideLoggingCapabilities();
		D debugger = provideDebugger();
		Learner learner = provideLearner();
		C controller = provideController(debugger, learner);

		/*
		 * Construct agent.
		 */
		try {
			return new Agent<>(this.agentId, environmentCapabilities,
					messagingCapabilities, loggingCapabilities, controller);
		} catch (KRInitFailedException e) {
			this.messaging.deleteMessageBox(this.messageBox);
			throw e;
		} finally {
			/*
			 * Clean up variables used in construction.
			 */
			this.program = null;
			this.environment = null;
			this.agentBaseName = null;

			this.messageBoxId = null;
			this.agentId = null;
			this.messageBox = null;
		}
	}

	private MessageBoxId provideMessageBoxId(String agentBaseName)
			throws CommunicationFailureException {
		return this.messaging.getNewUniqueID(agentBaseName, Type.GOALAGENT);
	}

	private MessageBox provideMessageBox(MessageBoxId messageBoxId)
			throws MessagingException {
		return this.messaging.getNewMessageBox(messageBoxId);
	}

	private AgentId provideAgentId(MessageBoxId messageBoxId) {
		if (messageBoxId != null) {
			return new AgentId(messageBoxId.getName());
		} else {
			return new AgentId(this.agentBaseName);
		}
	}

	/**
	 * Creates the messaging capabilities used by the agents. Subclasses can
	 * override this method to provide their own messaging capabilities.
	 *
	 * @return messaging capabilities used by the agent.
	 */
	protected MessagingCapabilities provideMessagingCapabilities() {
		return new DefaultMessagingCapabilities(this.messaging, this.messageBox);
	}

	/**
	 * Creates the environment capabilities used by the agents. Subclasses can
	 * override this method to provide their own environment capabilities.
	 *
	 * @return environment capabilities used by the agent.
	 */
	protected EnvironmentCapabilities provideEnvironmentCapabilities() {
		if (this.environment != null) {
			return new DefaultEnvironmentCapabilities(this.agentId,
					this.environment);
		} else {
			return new NoEnvironmentCapabilities();
		}
	}

	/**
	 * Creates the logging capabilities used by the agents. Subclasses can
	 * override this method to provide their own logging capabilities.
	 *
	 * @return logging capabilities used by the agent.
	 */
	protected LoggingCapabilities provideLoggingCapabilities() {
		DateFormat format = new SimpleDateFormat("MM-dd-HH.mm.ss");
		String fname = this.agentBaseName + "_"
				+ format.format(this.factoryCreationTime) + ".txt";
		GOALLoggerDelayed logActionsLogger = new GOALLoggerDelayed(fname, true);
		return new DefaultLoggingCapabilities(logActionsLogger);
	}

	/**
	 * Provides the debugger used by the agent.
	 *
	 * @return the debugger used by the agent
	 */
	protected abstract D provideDebugger();

	/**
	 * Provides the learner used by the agent. Subclasses can override this
	 * method to provide their own learner.
	 *
	 * @return the learner used by the agent
	 */
	protected Learner provideLearner() {
		return new FileLearner(this.agentId.getName(), this.program);
	}

	/**
	 * Provides the controller used by the agent.
	 *
	 * @param debugger
	 *            created by {@link #provideDebugger()}
	 * @param learner
	 *            created by {@link #provideLearner()}
	 *
	 * @return the controller used by the agent
	 */
	protected abstract C provideController(D debugger, Learner learner);
}
