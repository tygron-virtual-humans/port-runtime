package goal.tools;

import eis.iilang.Percept;
import goal.core.agent.GOALInterpreter;
import krTools.language.DatabaseFormula;
import languageTools.program.agent.AgentProgram;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ActionCombo;
import languageTools.program.agent.actions.MentalAction;
import languageTools.program.agent.actions.UserSpecAction;
import goal.core.runtime.service.agent.Result;
import goal.tools.adapt.Learner;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.Set;

public class IDEGOALInterpreter extends GOALInterpreter<IDEDebugger> {

	public IDEGOALInterpreter(AgentProgram program, IDEDebugger debugger,
			Learner learner) {
		super(program, debugger, learner);
	}

	/**
	 * Executes an action.
	 *
	 * Sends a user-specified action through the given middleware/messaging
	 * system to be executed in the Environment.
	 *
	 * @param action
	 *            The action to be executed in the environment.
	 * @throws GOALActionFailedException
	 */
	public void doPerformAction(Action action) throws GOALActionFailedException {
		// Perform mental action.
		if (action instanceof MentalAction) {
			runState.getDebugger().setKeepRunning(true);
			action.run(runState,
					program.getKRInterface().getEmptySubstitution(),
					runState.getDebugger(), false);
			runState.getDebugger().setKeepRunning(false);
		}
		// Perform user-specified action.
		else if (action instanceof UserSpecAction) {
			UserSpecAction userspec = (UserSpecAction) action;
			runState.doPerformAction(userspec);
		}
	}

	/**
	 * Executes a combo action.
	 *
	 * @param action
	 *
	 * @return The of the action.
	 */
	public Result doPerformAction(ActionCombo action) {
		return action.run(runState, program.getKRInterface()
				.getEmptySubstitution(), false);
	}

	/**
	 * Processes {@link Percept}s received from the agent's environment.
	 * Converts EIS {@link Percept}s to {@link DatabaseFormula}s and inserts new
	 * and removes old percepts from the percept base.
	 * <p>
	 * Note that the agent's percept buffer is not used for this.
	 * </p>
	 *
	 * @param newPercepts
	 *            The percepts received from the agent's environment that need
	 *            to be processed.
	 * @param previousPercepts
	 *            the received percepts from last cycle.
	 */
	public void processPercepts(Set<Percept> newPercepts,
			Set<Percept> previousPercepts) {
		runState.processPercepts(newPercepts, previousPercepts);
	}

}
