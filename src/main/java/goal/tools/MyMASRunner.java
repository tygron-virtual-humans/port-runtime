package goal.tools;

import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.Agent;
import goal.core.agent.AgentFactory;
import goal.core.agent.GOALInterpreter;
import goal.core.runtime.MessagingService;
import goal.tools.adapt.FileLearner;
import goal.tools.adapt.Learner;
import goal.tools.debugger.Channel;
import goal.tools.debugger.NOPDebugger;
import goal.tools.errorhandling.exceptions.GOALCommandCancelledException;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;

import java.io.File;
import java.util.Collection;

import languageTools.program.agent.AgentId;
import languageTools.program.mas.MASProgram;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

public class MyMASRunner {

	public static void main(String[] args)
			throws GOALCommandCancelledException, GOALLaunchFailureException,
			MessagingException, InterruptedException, Exception {
		// Loggers.addConsoleLogger();

		File file = new File(
				"src/test/resources/goal/parser/unittest/blocksworld/blocksworld.mas2g");
		MASProgram program = PlatformManager.createNew().parseMASFile(file);

		MyRun run = new MyRun(program);
		MyInspector resultInspector = new MyInspector();
		run.setResultInspector(resultInspector);

		run.run();

		resultInspector.reportResults();
	}

	private static class MyRun extends
			AbstractRun<MyDebugger, GOALInterpreter<MyDebugger>> {

		public MyRun(MASProgram program) {
			super(program);
		}

		@Override
		protected AgentFactory<MyDebugger, GOALInterpreter<MyDebugger>> buildAgentFactory(
				MessagingService messaging) {
			/*
			 * Construct and configure the factory that constructs the agents
			 * for your run time.
			 */
			return new MyAgentFactory(messaging);
		}
	}

	private static class MyAgentFactory extends
			AbstractAgentFactory<MyDebugger, GOALInterpreter<MyDebugger>> {

		public MyAgentFactory(MessagingService messaging) {
			super(messaging);
		}

		@Override
		protected MyDebugger provideDebugger() {
			/*
			 * Construct the debugger to use here. If you want to just run your
			 * agent use the NOPDebugger. If you want to use features of your
			 * debugger in the result inspector don't forget to change the Type.
			 */
			return new MyDebugger(agentId);
		}

		@Override
		protected Learner provideLearner() {
			FileLearner learner = new FileLearner(agentId.getName(), program);
			/*
			 * Configure the learner here.
			 */
			return learner;
		}

		@Override
		protected GOALInterpreter<MyDebugger> provideController(
				MyDebugger debugger, Learner learner) {
			/*
			 * Construct the GOALInterpreter here. For regular use you can the
			 * GOALInterpreter. If you want to use features of your controller
			 * in the result inspector don't forget to change the Type.
			 */
			return new GOALInterpreter<MyDebugger>(program, debugger, learner);
		}
	}

	private static class MyDebugger extends NOPDebugger {
		private int actionCount = 0;

		public MyDebugger(AgentId id) {
			super(id);
		}

		public MyDebugger(String id) {
			super(id);
		}

		@Override
		public void breakpoint(Channel channel, Object associate,
				String message, Object... args) {
			super.breakpoint(channel, associate, message, args);
			if (channel == Channel.ACTION_EXECUTED_USERSPEC
					|| channel == Channel.ACTION_EXECUTED_BUILTIN) {
				actionCount++;
			}
		}

	}

	private static class MyInspector implements
			ResultInspector<GOALInterpreter<MyDebugger>> {

		public MyInspector() {
		}

		@Override
		public void handleResult(
				Collection<Agent<GOALInterpreter<MyDebugger>>> agents) {
			/*
			 * Inspect your agents here. Stores the results in the inspector and
			 * ask it to report later on.
			 */
			for (Agent<GOALInterpreter<MyDebugger>> a : agents) {
				System.out.println(a.getId() + " executed "
						+ a.getController().getDebugger().actionCount
						+ " actions.");
			}
		}

		public void reportResults() {
		}
	}
}
