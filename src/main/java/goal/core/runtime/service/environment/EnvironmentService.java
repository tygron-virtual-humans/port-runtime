package goal.core.runtime.service.environment;

import eis.EILoader;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.EnvironmentInterfaceException;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import goal.core.runtime.MessagingService;
import goal.core.runtime.service.environment.events.EnvironmentPortAddedEvent;
import goal.core.runtime.service.environment.events.EnvironmentPortRemovedEvent;
import goal.core.runtime.service.environment.events.EnvironmentServiceEvent;
import goal.core.runtime.service.environment.events.RemovedLocalEnvironment;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.logging.InfoLog;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import languageTools.program.mas.MASProgram;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.client.MessagingEvent;
import nl.tudelft.goal.messaging.client.MessagingListener;
import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId;
import nl.tudelft.goal.messaging.messagebox.MessageBoxId.Type;

/**
 * Launches the environment in the MAS and maintains a registry of other
 * environments in the system. This can either be a remote environment or a
 * local environment started by this environment service.
 * <p>
 * The connection is provided in the form of a {@link EnvironmentPort} that can
 * be used as an interface for a the environment. The environment service
 * creates an environment port for every environment that is known to the
 * messaging service when the environment service is started.
 * <p>
 * FIXME: It is currently not possible to create environment ports for
 * environments that are created after the environment service starts.
 *
 * @author K.Hindriks
 * @author M.P. Korstanje
 */
public class EnvironmentService {
	/**
	 * If this service loads an environment interface, it is wrapped in a
	 * connector. The connector is stored here. A connector runs in a separate
	 * thread. Communication is maintained via an {@link EnvironmentPort}.
	 */
	private LocalMessagingEnvironment localEnvironment = null;

	/**
	 * The environment ports that connect to a local or remote environment.
	 */
	private final Map<MessageBoxId, EnvironmentPort> environmentPorts = new ConcurrentHashMap<>();

	/**
	 * MAS program used to launch environments or connect to remote
	 * environments.
	 */
	private final MASProgram masProgram;
	/**
	 * The messaging service.
	 *
	 */
	private final MessagingService messagingService;

	private final List<EnvironmentServiceObserver> observers = new LinkedList<>();

	/**
	 * Listens to messaging events. When an environment message box is removed,
	 * this is taken as evidence that the environment has been shut down. This
	 * will result in the shutdown and termination of the environment port
	 * connected to that environment.
	 *
	 * FIXME we may want to implement an observer/observable pattern with the
	 * {@link LocalMessagingEnvironment} instead of this.
	 */
	private final MessagingListener messagingListener = new MessagingListener() {
		@Override
		public void messagingEventOccured(MessagingEvent evt) {
			switch (evt.getType()) {
			case REMOVED_MESSAGE_BOX:
				if (evt.getId().getType() == Type.ENVIRONMENT) {
					removeEnvironmentPort(evt.getId());
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Creates a new {@link EnvironmentPort} that will communicate with the
	 * given messagebox id.
	 *
	 * @param id
	 *            of the environments messagebox.
	 */
	public void addEnvironmentPort(MessageBoxId id) {
		EnvironmentPort port = null;
		synchronized (this.environmentPorts) {
			// We already have a port for this
			// environment. Quitely ignore.
			if (this.environmentPorts.containsKey(id)) {
				return;
			}
			try {
				port = new EnvironmentPort(id, this.messagingService);
				this.environmentPorts.put(id, port);
			} catch (GOALLaunchFailureException e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_CREATE_ENV_PORT),
						id.getName()), e);
				return;
			}
		}

		notifyObservers(new EnvironmentPortAddedEvent(port));

		try {
			// Start port after people are ready to listen for it.
			port.startPort();
		} catch (MessagingException e) {
			new Warning(String.format(
					Resources.get(WarningStrings.FAILED_START_ENV_PORT),
					id.getName()), e);
		}
	}

	/**
	 * Removes the {@link EnvironmentPort} that is communicateing with the given
	 * messagebox id.
	 *
	 * @param id
	 *            of the environments messagebox.
	 */
	public void removeEnvironmentPort(MessageBoxId id) {
		EnvironmentPort port = null;
		synchronized (this.environmentPorts) {
			// We dont'have a port for this
			// environment. Quitely ignore.
			if (!this.environmentPorts.containsKey(id)) {
				return;
			}
			port = this.environmentPorts.remove(id);
			try {
				port.shutDown();
			} catch (Exception e) {
				new Warning(String.format(
						Resources.get(WarningStrings.FAILED_STOP_ENV_PORT),
						id.getName()), e);
			}
		}

		notifyObservers(new EnvironmentPortRemovedEvent(port));
	}

	/**
	 * Creates environment services.
	 *
	 * @param masProgram
	 *            The MAS to create the service for.
	 * @param messagingService
	 *            The messaging service to use.
	 * @throws MessagingException
	 */
	public EnvironmentService(MASProgram masProgram,
			MessagingService messagingService) {
		this.masProgram = masProgram;
		this.messagingService = messagingService;
	}

	/**
	 * Starts the environment service. Depending on the mas program this may
	 * create and initialize a local environment. For all available environments
	 * a {@link EnvironmentPort} is created.
	 *
	 * @throws GOALLaunchFailureException
	 * @throws EnvironmentInterfaceException
	 * @throws InterruptedException
	 * @throws MessagingException
	 */
	public void start() throws GOALLaunchFailureException,
	EnvironmentInterfaceException, InterruptedException,
	MessagingException {
		// If MAS file does not have environment section, there is nothing to do
		if (!this.masProgram.hasEnvironment()) {
			return;
		}

		// Get environment name, file (if it exists), and initialization
		// parameters.
		String environmentName = this.masProgram.getEnvironmentfile().getName();

		// FIXME this allows one to create a new env or to use an existing one
		// by specifying a name ending on ".jar" or not in the MAS file.
		if (environmentName.endsWith(".jar")) { //$NON-NLS-1$
			File environmentFile = this.masProgram.getEnvironmentfile();
			// Launch local environment.
			if (environmentFile != null) {
				Map<String, Parameter> initialization = convertMapToEIS(this.masProgram
						.getInitParameters());
				// Reference to environment file provided in MAS file; load it.
				environmentName = environmentName.substring(0,
						environmentName.lastIndexOf(".jar")); //$NON-NLS-1$
				this.localEnvironment = launchLocalMessaginEnvironment(
						environmentName, environmentFile, initialization);
				addEnvironmentPort(this.localEnvironment.getMessageBoxId());
			}

		} else {
			// Lookup already existing environments.
			try {
				// get the environment that has the exact name as we received
				// from the MAS. note that this may be different from the one we
				// just launched.
				List<MessageBoxId> boxes = this.messagingService.getClient()
						.getMessageBoxes(Type.ENVIRONMENT, environmentName);
				if (boxes.isEmpty()) {
					new Warning(
							"Environment "
									+ environmentName
									+ " is not running. It is not a jar file so the environment was not launched.");
				}
				for (MessageBoxId id : boxes) {
					addEnvironmentPort(id);
				}
			} catch (MessagingException e) {
				try {
					this.localEnvironment.shutDown();
				} catch (Exception ignore) {
				}
				throw e;
			}
		}

		// Start environment only after observers have had a chance to
		// subscribe. This is important because the environment may start
		// threads of its own. In combination with the late listener pattern
		// this may cause race conditions where events happen twice.
		if (this.localEnvironment != null) {
			try {
				this.localEnvironment.initialize();
			} catch (EnvironmentInterfaceException e) {
				try {
					this.localEnvironment.shutDown();
				} catch (Exception ignore) {
				}
				throw e;
			}
		}

		this.messagingService.getClient().addListener(this.messagingListener);
	}

	private static Map<String, Parameter> convertMapToEIS(
			Map<String, Object> init) {
		Map<String, Parameter> result = new HashMap<>(init.size());
		for (final String key : init.keySet()) {
			final Object value = init.get(key);
			result.put(key, convertValueToEIS(value));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static Parameter convertValueToEIS(Object value) {
		if (value instanceof Number) {
			return new Numeral((Number) value);
		} else if (value instanceof AbstractMap.SimpleEntry) {
			AbstractMap.SimpleEntry<String, Object[]> map = (AbstractMap.SimpleEntry<String, Object[]>) value;
			Parameter[] params = new Parameter[map.getValue().length];
			for (int i = 0; i < params.length; ++i) {
				params[i] = convertValueToEIS(map.getValue()[i]);
			}
			return new Function(map.getKey(), params);
		} else if (value instanceof List) {
			List<Object> oldlist = (List<Object>) value;
			Parameter[] newlist = new Parameter[oldlist.size()];
			for (int i = 0; i < newlist.length; ++i) {
				newlist[i] = convertValueToEIS(oldlist.get(i));
			}
			return new ParameterList(newlist);
		} else {
			return new Identifier(value.toString());
		}
	}

	/**
	 * Launch the environment on top of {@link Messaging} system.
	 *
	 * @throws GOALLaunchFailureException
	 */
	private LocalMessagingEnvironment launchLocalMessaginEnvironment(
			String environmentName, File environmentFile,
			Map<String, Parameter> initialization)
					throws GOALLaunchFailureException {
		new InfoLog("Launching environment service..."); //$NON-NLS-1$

		// Load environment interface.
		try {
			EnvironmentInterfaceStandard eis = EILoader
					.fromJarFile(environmentFile);
			// Launch environment interface locally.
			LocalMessagingEnvironment environment = new LocalMessagingEnvironment(
					eis, environmentName, initialization, this.messagingService);
			new InfoLog("OK."); //$NON-NLS-1$

			return environment;
		} catch (IOException e) {
			throw new GOALLaunchFailureException(
					Resources.get(WarningStrings.FAILED_LOAD_ENV), e);
		}
	}

	/**
	 * Returns the local Environment or null if the environment is started
	 * remotely.
	 *
	 * @return the local Environment or null if the environment is started
	 *         remotly.
	 */
	public LocalMessagingEnvironment getLocalEnvironment() {
		return this.localEnvironment;
	}

	/**
	 * Returns the environment port that connecets to the environment with the
	 * given message box id.
	 *
	 * @param id
	 *            of the environments message box
	 * @return the environment port connecting to the environment with the id.
	 */
	public EnvironmentPort getEnvironmentPort(MessageBoxId id) {
		synchronized (this.environmentPorts) {
			return this.environmentPorts.get(id);
		}
	}

	/**
	 * Returns the list of environment ports managed by this environment
	 * service. This collection is thread-safe.
	 *
	 * @return a list of environment ports
	 */
	public Collection<EnvironmentPort> getEnvironmentPorts() {
		synchronized (this.environmentPorts) {
			return new ArrayList<>(this.environmentPorts.values());
		}
	}

	/**
	 * Stops the environment service by closing all environment ports and
	 * shutting down the local environment.
	 *
	 * @throws MessagingException
	 * @throws EnvironmentInterfaceException
	 * @throws InterruptedException
	 */
	public void shutDown() throws MessagingException,
	EnvironmentInterfaceException, InterruptedException {
		if (this.localEnvironment != null) {
			notifyObservers(new RemovedLocalEnvironment(
					this.localEnvironment.getMessageBoxId()));
		}
		synchronized (this.environmentPorts) {
			Collection<EnvironmentPort> ports = new ArrayList<>(
					this.environmentPorts.values());
			this.environmentPorts.clear();
			for (EnvironmentPort port : ports) {
				port.shutDown();
			}
		}

		if (this.localEnvironment != null) {
			this.localEnvironment.shutDown();
			this.localEnvironment = null;
		}
	}

	/**********************************************/
	/*********** observer pattern *****************/
	/**********************************************/

	private void notifyObservers(EnvironmentServiceEvent evt) {
		for (EnvironmentServiceObserver obs : this.observers) {
			try {
				obs.environmentServiceEventOccured(this, evt);
			} catch (Exception e) {
				new Warning(String.format(
						Resources.get(WarningStrings.INTERNAL_PROBLEM),
						obs.toString(), evt.toString()), e);
			}
		}
	}

	public void addObserver(EnvironmentServiceObserver obs) {
		this.observers.add(obs);
	}
}
