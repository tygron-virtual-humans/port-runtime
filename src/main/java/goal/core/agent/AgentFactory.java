package goal.core.agent;

import goal.core.runtime.service.agent.AgentService;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.io.File;

import nl.tudelft.goal.messaging.exceptions.MessagingException;
import nl.tudelft.goal.messaging.messagebox.MessageBox;

/**
 * Constructs agents with a specific {@link GOALInterpreter} and
 * {@link Debugger}. For ease of use consider using the
 * {@link AbstractAgentFactory}.
 *
 * @see AgentService
 * @see AbstractAgentFactory
 *
 * @author M.P. Korstanje
 *
 * @param <DEBUGGER>
 *            a subclass {@link Debugger}
 * @param <CONTROLLER>
 *            a subclass of {@link GOALInterpreter}
 */
public interface AgentFactory<DEBUGGER extends Debugger, CONTROLLER extends GOALInterpreter<DEBUGGER>> {

	/**
	 * Builds an Agent. Before throwing an exception upwards all clean up should
	 * have been done.
	 *
	 * @param program
	 *            the agent executes
	 * @param goalProgramFile
	 *            containing the agents program ()
	 * @param agentBaseName
	 *            base name for the agent. This name does not need to be unique.
	 * @param environment
	 *            in which the agent should be launched. May be null when no
	 *            environment is available.
	 * @return a new agent.
	 * @throws MessagingException
	 *             thrown when the agent could not setup a {@link MessageBox}
	 * @throws KRInitFailedException
	 *             thrown when the agent could not initialize the
	 *             {@link KRlanguage}.
	 */
	// FIXME: GOALProgram should have a reference to its .goal file.
	public Agent<CONTROLLER> build(GOALProgram program, File goalProgramFile,
			String agentBaseName, EnvironmentPort environment)
			throws MessagingException, KRInitFailedException;

}
