package goal.core.executors;

import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import goal.tools.unittest.result.testcondition.TestBoundaryException;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;

import java.util.concurrent.Callable;

import krTools.language.DatabaseFormula;
import krTools.language.Query;
import krTools.language.Substitution;
import languageTools.program.agent.Module;
import languageTools.program.agent.Module.TYPE;
import languageTools.program.agent.actions.AdoptAction;
import languageTools.program.agent.selector.Selector;
import languageTools.program.agent.selector.Selector.SelectorType;
import mentalState.BASETYPE;

public class ModuleExecutor {
	private final Module module;
	/**
	 * Channel to report the entry of this {@link Module} on. Should be
	 * {@code null} for anonymous modules (and no reports should be generated).
	 */
	private Channel entrychannel = null;
	/**
	 * Channel to report the exit of this {@link Module} on. Should be
	 * {@code null} for anonymous modules (and no reports should be generated).
	 */
	private Channel exitchannel = null;
	/**
	 * The last result of an execute-call
	 */
	private Result result;

	public ModuleExecutor(Module mod) {
		this.module = mod;
		switch (mod.getType()) {
		case MAIN:
			this.entrychannel = Channel.MAIN_MODULE_ENTRY;
			this.exitchannel = Channel.MAIN_MODULE_EXIT;
			break;
		case EVENT:
			this.entrychannel = Channel.EVENT_MODULE_ENTRY;
			this.exitchannel = Channel.EVENT_MODULE_EXIT;
			break;
		case INIT:
			this.entrychannel = Channel.INIT_MODULE_ENTRY;
			this.exitchannel = Channel.INIT_MODULE_EXIT;
			break;
		case USERDEF:
			this.entrychannel = Channel.USER_MODULE_ENTRY;
			this.exitchannel = Channel.USER_MODULE_EXIT;
			break;
		default:
			break;
		}
	}

	public Module getModule() {
		return this.module;
	}

	@SuppressWarnings("unchecked")
	public Result executeFully(final RunState<?> runState,
			final Substitution substitution) throws GOALActionFailedException {
		Callable<Callable<?>> call = execute(runState, substitution, true);
		while (call != null) {
			try {
				call = (Callable<Callable<?>>) call.call();
			} catch (GOALActionFailedException gafe) {
				throw gafe; // module failed user error, pass upwards.
			} catch (RuntimeException e) {
				throw e; // goal bugs, let fall through.
			} catch (Exception e) {
				// we should never get here.
				throw new GOALBug("module is throwing unexpected exception", e);
			}
		}
		return this.result;
	}

	/**
	 * Executes one step of the {@link Module}, and returns a Runnable for
	 * executing the next.
	 *
	 * @param runState
	 *            The current run state of the agent.
	 * @param substitution
	 *            The substitution that has been passed on to this module when
	 *            it was called, already with the variables renamed according to
	 *            the focus call so that the module can use it without renaming.
	 * @return {@link Runnable} for continuing to execute this module. Null when
	 *         we should stop.
	 * @throws GOALActionFailedException
	 */
	public Callable<Callable<?>> execute(final RunState<?> runState,
			final Substitution substitution) throws GOALActionFailedException {
		return execute(runState, substitution, true);
	}

	private Callable<Callable<?>> execute(final RunState<?> runState,
			final Substitution substitution, final boolean first)
			throws GOALActionFailedException {
		if (first) {
			/*
			 * Push (non-anonymous) modules that were just entered onto stack
			 * that keeps track of modules that have been entered but not yet
			 * exited again.
			 */
			runState.enteredModule(this.module);

			/*
			 * Add all initial beliefs defined in the beliefs section of this
			 * module to the agent's belief base.
			 */
			for (DatabaseFormula belief : this.module.getBeliefs()) {
				try {
					runState.getMentalState().insert(belief, BASETYPE.BELIEFBASE,
							runState.getDebugger(), runState.getId());
				} catch (GOALDatabaseException e) {
					throw new IllegalStateException("insert "+belief+ "failed unexpectedly", e);
				}
			}

			// Add all goals defined in the goals section of this module to the
			// current attention set.
			for (Query goal : this.module.getGoals()) {
				ActionExecutor adopt = new AdoptActionExecutor(new AdoptAction(
						new Selector(SelectorType.THIS), goal.applySubst(
								substitution).toUpdate(), goal.getSourceInfo(),
						module.getKRInterface()));
				try {
					adopt = adopt.evaluatePrecondition(runState.getMentalState(),
							runState.getDebugger(), false);
				} catch (GOALDatabaseException e) {
					throw new IllegalStateException("adopt of "+goal+ "failed unexpectedly", e);
				}
				if (adopt != null) {
					adopt.run(runState, substitution, runState.getDebugger(),
							false);
				}
			}
		}
		// Report (re)entry of non-anonymous module on debug channel.
		if (this.module.getType() != TYPE.ANONYMOUS) {
			runState.getDebugger().breakpoint(this.entrychannel, this.module,
					this.module.getSourceInfo(),
					"Entering " + this.module.getNamePhrase());
		}

		// Evaluate and apply the rules of this module
		this.result = new RulesExecutor(this.module.getRules(),
				this.module.getRuleEvaluationOrder()).run(runState,
				substitution);

		// exit module if {@link ExitModuleAction} has been performed.
		boolean exit = this.result.isModuleTerminated();

		// Evaluate module's exit condition.
		switch (this.module.getExitCondition()) {
		case NOGOALS:
			exit |= runState.getMentalState().getAttentionSet().isEmpty();
			break;
		case NOACTION:
			exit |= !this.result.hasPerformedAction();
			break;
		case ALWAYS:
			exit = true;
			break;
		default:
		case NEVER:
			// exit whenever module has been terminated (see above)
			break;
		}
		exit |= !runState.getParent().isRunning(); // FIXME: check for
		// executeFully

		// Check whether we need to start a new cycle. We do so if we do NOT
		// exit this module, NO action has been performed while evaluating the
		// module's rules (otherwise a new cycle would already have been
		// initiated), and we're currently running within the main module's
		// context (never start a new cycle when running the init/event or a
		// module called from either of these two modules).
		if (!exit && !this.result.hasPerformedAction()
				&& runState.isMainModuleRunning()) {
			runState.startCycle(this.result.hasPerformedAction());
		}
		if (exit) {
			// If module termination flag has been set, reset it except when
			// this is an anonymous module. In that case, module termination
			// needs to be propagated to enclosing module(s).
			// Also report the module exit on the module's debug channel.
			if (this.module.getType() != TYPE.ANONYMOUS) {
				this.result.setModuleTerminated(false);
				runState.getDebugger().breakpoint(this.exitchannel,
						this.module, this.module.getSourceInfo(),
						"Exiting " + this.module.getNamePhrase());
			}

			// Remove module again from stack of modules that have been entered
			// and possibly update top level context in which we run
			runState.exitModule(this.module);

			return null;
		} else {
			return new Callable<Callable<?>>() {
				@Override
				public Callable<?> call() throws Exception {
					return execute(runState, substitution, false);
				}
			};
		}
	}
}
