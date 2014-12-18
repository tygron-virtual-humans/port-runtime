package goal.tools.eclipse;

import goal.core.agent.Agent;
import goal.core.mentalstate.GoalBase;
import goal.core.mentalstate.MentalState;
import goal.core.mentalstate.SingleGoal;
import goal.preferences.DebugPreferences;
import goal.preferences.LoggingPreferences;
import goal.tools.IDEDebugger;
import goal.tools.IDEGOALInterpreter;
import goal.tools.debugger.Channel;
import goal.tools.debugger.DebugEvent;
import goal.tools.debugger.DebugObserver;
import goal.tools.debugger.SteppingDebugger.RunMode;
import goal.tools.eclipse.DebugCommand.Command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.language.DatabaseFormula;
import krTools.language.Substitution;
import krTools.parser.SourceInfo;
import languageTools.parser.InputStreamPosition;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.Module;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.actions.UserSpecAction;
import mentalState.BASETYPE;

public class EclipseDebugObserver implements DebugObserver {
	private final Agent<IDEGOALInterpreter> agent;
	private final InputReaderWriter writer;
	private SourceInfo source;
	private boolean initialized = false;

	/**
	 * Handles events from an {@link Agent} to put pre-defined output on a
	 * {@link InputReaderWriter}
	 *
	 * @param agent
	 *            The {@link Agent}.
	 * @param writer
	 *            The {@link InputReaderWriter}.
	 */
	public EclipseDebugObserver(final Agent<IDEGOALInterpreter> agent,
			final InputReaderWriter writer) {
		this.agent = agent;
		this.writer = writer;
		this.source = agent.getController().getProgram().getSourceInfo();
	}

	/**
	 * Subscribe to everything we want to listen to
	 */
	public void subscribe() {
		final IDEDebugger debugger = this.agent.getController().getDebugger();
		for (Channel channel : Channel.values()) {
			// Listen to all channels to update the agent's source position
			debugger.subscribe(this, channel);
		}
		// Let the world know we're here
		this.writer
				.write(new DebugCommand(Command.LAUNCHED, this.agent.getId()));
	}

	@Override
	public String getObserverName() {
		return "EclipseDebugObserver";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyBreakpointHit(DebugEvent event) {
		final Object object = event.getAssociatedObject();
		final AgentId agentId = this.agent.getId();
		if (DebugPreferences.getChannelState(event.getChannel()).canView()
				&& LoggingPreferences.getEclipseAgentConsoles()) {
			this.writer.write(new DebugCommand(Command.LOG, agentId, event
					.getMessage()));
		}
		this.source = event.getAssociatedSource();
		switch (event.getChannel()) {
		case RUNMODE:
			this.writer.write(new DebugCommand(Command.RUNMODE, agentId, event
					.getRunMode().toString()));
			if (event.getRunMode().equals(RunMode.PAUSED)
					|| event.getRunMode().equals(RunMode.KILLED)) {
				suspendAtSource();
			}
			break;
		case INIT_MODULE_ENTRY:
		case EVENT_MODULE_ENTRY:
		case MAIN_MODULE_ENTRY:
		case USER_MODULE_ENTRY:
			final Module module1 = (Module) event.getAssociatedObject();
			this.writer.write(new DebugCommand(Command.MODULE_ENTRY, agentId,
					module1.getName()));
			break;
		case INIT_MODULE_EXIT:
		case EVENT_MODULE_EXIT:
		case MAIN_MODULE_EXIT:
		case USER_MODULE_EXIT:
			final Module module2 = (Module) event.getAssociatedObject();
			this.writer.write(new DebugCommand(Command.MODULE_EXIT, agentId,
					module2.getName()));
			break;
		case BB_UPDATES:
			final DatabaseFormula belief = (DatabaseFormula) object;
			if (event.getMessage().contains("has been inserted")) {
				this.writer.write(new DebugCommand(Command.INSERTED_BEL,
						agentId, belief.toString()));
			} else {
				this.writer.write(new DebugCommand(Command.DELETED_BEL,
						agentId, belief.toString()));
			}
			break;
		case PERCEPTS_CONDITIONAL_VIEW:
			final DatabaseFormula percept = (DatabaseFormula) object;
			if (event.getMessage().contains("has been inserted")) {
				this.writer.write(new DebugCommand(Command.INSERTED_PERCEPT,
						agentId, percept.toString()));
			} else {
				this.writer.write(new DebugCommand(Command.DELETED_PERCEPT,
						agentId, percept.toString()));
			}
			break;
		case MAILS_CONDITIONAL_VIEW:
			final DatabaseFormula mail = (DatabaseFormula) object;
			if (event.getMessage().contains("has been inserted")) {
				this.writer.write(new DebugCommand(Command.INSERTED_MAIL,
						agentId, mail.toString()));
			} else {
				this.writer.write(new DebugCommand(Command.DELETED_MAIL,
						agentId, mail.toString()));
			}
			break;
		case GB_UPDATES:
		case GOAL_ACHIEVED:
			final SingleGoal goal = (SingleGoal) object;
			String name = "main";
			final String msg = event.getMessage();
			final String find = "goal base: ";
			final int start = msg.indexOf(find);
			if (start > 0) {
				final int end = msg.lastIndexOf('.');
				name = msg.substring(start + find.length(), end);
			}
			name = goal + " [" + name + "]";
			if (event.getMessage().contains("has been adopted")) {
				this.writer.write(new DebugCommand(Command.ADOPTED, agentId,
						name));
			} else {
				this.writer.write(new DebugCommand(Command.DROPPED, agentId,
						name));
			}
			break;
		case GB_CHANGES:
			final GoalBase base = (GoalBase) object;
			if (event.getMessage().contains("focused to")) {
				this.writer.write(new DebugCommand(Command.FOCUS, agentId, base
						.getName()));
			} else {
				this.writer.write(new DebugCommand(Command.DEFOCUS, agentId,
						base.getName()));
			}
			break;
		case RULE_CONDITION_EVALUATION:
			// final MentalStateCondition cond = (MentalStateCondition) object;
			final List<String> rAsList = new LinkedList<>();
			if (event.getRawArguments().length > 1) {
				Set<Substitution> substset = (Set<Substitution>) event
						.getRawArguments()[1];
				for (final Substitution sub : substset) {
					if (sub != null) {
						rAsList.add(sub.toString());
					}
				}
				if (rAsList.isEmpty()) {
					rAsList.add("[]");
				}
			} else {
				rAsList.add("no solutions");
			}
			this.writer.write(new DebugCommand(Command.RULE_EVALUATION,
					agentId, rAsList));
			break;
		case ACTION_PRECOND_EVALUATION_USERSPEC:
			final UserSpecAction action = (UserSpecAction) event
			.getAssociatedObject();
			final List<String> aAsList = new LinkedList<>();
			if (event.getRawArguments().length > 1) {
				aAsList.add("selected: " + action);
				Substitution subst = (Substitution) event.getRawArguments()[2];
				aAsList.add("precondition: " + subst.toString());
			} else {
				aAsList.add("precondition does not hold");
			}
			this.writer.write(new DebugCommand(Command.PRECOND_EVALUATION,
					agentId, aAsList));
			break;
		case CALL_MODULE:
			final ModuleCallAction call = (ModuleCallAction) event
			.getAssociatedObject();
			final List<String> cAsList = new LinkedList<>();
			if (call.getParameters() != null) {
				cAsList.add(call.getParameters().toString());
			}
			if (cAsList.isEmpty()) {
				cAsList.add("[]");
			}
			this.writer.write(new DebugCommand(Command.PRECOND_EVALUATION,
					agentId, cAsList));
			break;
		case ACTION_EXECUTED_USERSPEC:
			final Action<?> executed = (Action<?>) event.getAssociatedObject();
			if (LoggingPreferences.getEclipseActionHistory()) {
				this.writer.write(new DebugCommand(Command.EXECUTED, agentId,
						executed.toString()));
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Send a message to the stream to suspend the agent at its last known
	 * source position (code that has been run)
	 */
	public void suspendAtSource() {
		if (!this.initialized) {
			final SourceInfo saved = this.source;
			final MentalState init = this.agent.getController().getRunState()
					.getMentalState();
			final Set<SingleGoal> goalSet = init.getAttentionSet().getGoals();
			final SingleGoal[] goals = goalSet.toArray(new SingleGoal[goalSet
			                                                          .size()]);
			for (final SingleGoal goal : goals) {
				notifyBreakpointHit(new DebugEvent(null, "",
						Channel.GB_UPDATES, goal, goal.getGoal()
						.getSourceInfo(), "%s has been adopted", goal));
			}
			final Set<DatabaseFormula> beliefSet = init
					.getOwnBase(BASETYPE.BELIEFBASE).getTheory().getFormulas();
			final DatabaseFormula[] beliefs = beliefSet
					.toArray(new DatabaseFormula[beliefSet.size()]);
			for (final DatabaseFormula belief : beliefs) {
				notifyBreakpointHit(new DebugEvent(null, "",
						Channel.BB_UPDATES, belief, belief.getSourceInfo(),
						"%s has been inserted", belief));
			}
			this.source = saved;
			this.initialized = true;
		}
		int diff = 0;
		if (this.source instanceof InputStreamPosition) {
			final InputStreamPosition isource = (InputStreamPosition) this.source;
			diff = isource.getStopIndex() - isource.getStartIndex();
		}
		final String[] params = new String[] {
				this.source.getSource().getPath(),
				Integer.toString(this.source.getLineNumber()),
				Integer.toString(this.source.getCharacterPosition()),
				Integer.toString(this.source.getCharacterPosition() + diff) };
		this.writer.write(new DebugCommand(Command.SUSPEND, this.agent.getId(),
				Arrays.asList(params)));
	}
}