package goal.core.program;

import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.core.agent.MessagingCapabilities;
import goal.core.agent.NoMessagingCapabilities;
import goal.tools.IDEDebugger;
import goal.tools.IDEGOALInterpreter;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import krTools.errors.exceptions.KRInitFailedException;
import languageTools.program.agent.AgentProgram;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

/**
 * Base for simple tests that don't need an environment or messaging.
 *
 * @author mpkorstanje
 */
public class SimpleProgramTest extends ProgramTest {

	/**
	 * Abstract base for build agents without messaging or an environment.
	 * Subclasses can provide different Messaging- and EnvironmentCapabilities,
	 * Debuggers, Learners and Controllers. This can be done by overriding or
	 * implementing the proper methods.
	 *
	 * During the construction the class fields will be initialized to assist
	 * the creation of different classes.
	 *
	 * @author mpkorstanje
	 *
	 * @param <D>
	 *            class of the Debugger to provide.
	 * @param <C>
	 *            class of the GOALInterpreter to provide.
	 */
	private abstract class SimpleAgentFactory<D extends Debugger, C extends GOALInterpreter<D>>
			extends AbstractAgentFactory<D, C> {
		/**
		 * Constructs a factory for agents withouth messaging.
		 */
		public SimpleAgentFactory() {
			super();
		}

		@Override
		protected MessagingCapabilities provideMessagingCapabilities() {
			return new NoMessagingCapabilities();
		}
	}

	@Override
	protected Agent<IDEGOALInterpreter> buildAgent(String id,
			AgentProgram program) throws GOALLaunchFailureException,
			MessagingException, KRInitFailedException {
		SimpleAgentFactory<IDEDebugger, IDEGOALInterpreter> factory = new SimpleAgentFactory<IDEDebugger, IDEGOALInterpreter>() {
			@Override
			protected IDEDebugger provideDebugger() {
				return new IDEDebugger(this.agentId, this.program, null);
			}

			@Override
			protected IDEGOALInterpreter provideController(
					IDEDebugger debugger, Learner learner) {
				return new IDEGOALInterpreter(this.program, debugger, learner);
			}
		};
		return factory.build(program, id, null);
	}
}
