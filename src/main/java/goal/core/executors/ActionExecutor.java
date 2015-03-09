package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.analyzer.agent.AgentValidatorSecondPass;
import languageTools.analyzer.module.ModuleValidatorSecondPass;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.AdoptAction;
import languageTools.program.agent.actions.DeleteAction;
import languageTools.program.agent.actions.DropAction;
import languageTools.program.agent.actions.ExitModuleAction;
import languageTools.program.agent.actions.InsertAction;
import languageTools.program.agent.actions.LogAction;
import languageTools.program.agent.actions.ModuleCallAction;
import languageTools.program.agent.actions.PrintAction;
import languageTools.program.agent.actions.SendAction;
import languageTools.program.agent.actions.SendOnceAction;
import languageTools.program.agent.actions.UserSpecAction;
import languageTools.program.agent.msc.MentalStateCondition;
import languageTools.program.agent.msg.SentenceMood;

/**
 * Abstract base class for part of the ActionExecutors
 *
 * @author W.Pasman 4dec14
 */
public abstract class ActionExecutor {
	/**
	 * Get the parsed {@link Action}.
	 *
	 * @return {@link Action}
	 */
	abstract public Action<?> getAction();

	/**
	 * Checks whether the precondition of this {@link Action} holds and, if this
	 * is the case, returns the {@link ActionExecutor} selected for execution.
	 *
	 * @param mentalState
	 *            The {@link MentalState} in which the precondition is
	 *            evaluated.
	 * @param debugger
	 *            The current debugger
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return The action that is selected for execution if the precondition
	 *         holds; {@code null} otherwise. This default implementation
	 *         returns the action executor itself unmodified, meaning the action
	 *         is enabled as it is
	 */
	protected ActionExecutor evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		return this;
	}

	/**
	 * create new ActionExecutor with the given substitution applied to the
	 * action
	 *
	 * @param subst
	 *            the substitution to be applied
	 */
	protected abstract ActionExecutor applySubst(Substitution subst);

	/**
	 * Performs the {@link Action}. First applies the given {@link Substitution}
	 * to the action and checks whether the precondition holds and the action is
	 * closed. If it is closed, the action is executed using
	 * {@link #executeAction(RunState)}.
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 * @param substitution
	 *            The substitution provided by the action's context.
	 * @param debugger
	 *            the debugger to be used. #2813 we need this to handle calls
	 *            from the IDE to execute actions. Normally you set this to
	 *            {@link RunState#getDebugger()}.
	 * @param last
	 *            If this is the last possible variation we are trying to
	 *            execute, e.g. after this there are no more possibilities and
	 *            the action will fail.
	 * @return The result of the action that was performed.
	 * @throws GOALActionFailedException
	 *             If the action is not closed, action is internal but this is
	 *             not indicated in the program, or other exceptions occurred.
	 */
	public final Result run(RunState<?> runState, Substitution substitution,
			Debugger debugger, boolean last) throws GOALActionFailedException {
		Result result = new Result();

		// Apply the substitution provided by the (module and rule condition)
		// context.
		ActionExecutor instantiatedAction = applySubst(substitution);

		// Evaluate the precondition of this {@link Action}.
		ActionExecutor action = instantiatedAction.evaluatePrecondition(
				runState.getMentalState(), debugger, last);

		if (action != null) {
			// Check if action is closed.
			if (isSufficientlyClosed(action.getAction())) {
				debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION,
						getAction(), getAction().getSourceInfo(),
						"precondition of %s holds", getAction());
				// Perform the action if precondition holds.
				result.merge(action.executeAction(runState, debugger));
			} else {
				throw new GOALActionFailedException(
						"attempt to execute action " + action.getAction()
								+ " with free variables, "
								+ action.getAction().getSourceInfo());
			}
		} else {
			debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION, getAction(),
					getAction().getSourceInfo(),
					"precondition of %s does not hold", getAction().getName());
		}

		return result;
	}

	/**
	 * Some actions do not need to be entirely closed. #3424.
	 *
	 * @param action
	 * @return
	 */
	private boolean isSufficientlyClosed(Action<?> action) {
		if (action.isClosed()) {
			return true;
		}
		if (action instanceof SendAction || action instanceof SendOnceAction) {
			// FIXME we should check that receiver is instantiated
			return getSendMood(action) == SentenceMood.INTERROGATIVE;
		}

		return false;
	}

	/**
	 * Get the mood of the given action. Assumes action is {@link SendAction} or
	 * {@link SendOnceAction}. Helper function to get around #3433. Code
	 * duplicated with {@link AgentValidatorSecondPass} and
	 * {@link ModuleValidatorSecondPass}.
	 *
	 * @param action
	 * @return mood of the given action.
	 */
	private SentenceMood getSendMood(Action<?> action) {
		if (action instanceof SendAction) {
			return ((SendAction) action).getMood();
		}
		return ((SendOnceAction) action).getMood();
	}

	/**
	 * Executes the {@link Action}.
	 *
	 * @param runState
	 *            The {@link RunState} in which the action is executed.
	 * @param debugger
	 *            The current debugger.
	 * @return The action that has been executed; never returns {@code null} but
	 *         throws runtime exceptions if executing the action fails.
	 * @throws GOALActionFailedException
	 */
	protected abstract Result executeAction(RunState<?> runState,
			Debugger debugger) throws GOALActionFailedException;

	/**
	 * Reports that action was performed on either the channel for reporting
	 * built-in or user specified actions.
	 *
	 * @param debugger
	 *            The current debugger.
	 */
	protected final void report(Debugger debugger) {
		boolean builtin = !(this instanceof UserSpecActionExecutor);
		debugger.breakpoint(builtin ? Channel.ACTION_EXECUTED_BUILTIN
				: Channel.ACTION_EXECUTED_USERSPEC, getAction(), getAction()
				.getSourceInfo(), "Performed %s.", getAction());
	}

	@Override
	public String toString() {
		return "execute(" + getAction().toString() + ")";
	}

	// private static Map<Action<?>, ActionExecutor> executors = new
	// HashMap<>();

	public static ActionExecutor getActionExecutor(Action<?> action,
			MentalStateCondition context) {
		ActionExecutor returned = null;
		// executors.get(action); TODO: reusing this causes issues?!
		if (returned == null) {
			if (action instanceof AdoptAction) {
				returned = new AdoptActionExecutor((AdoptAction) action);
			} else if (action instanceof DeleteAction) {
				returned = new DeleteActionExecutor((DeleteAction) action);
			} else if (action instanceof DropAction) {
				returned = new DropActionExecutor((DropAction) action);
			} else if (action instanceof ExitModuleAction) {
				returned = new ExitModuleActionExecutor(
						(ExitModuleAction) action);
			} else if (action instanceof InsertAction) {
				returned = new InsertActionExecutor((InsertAction) action);
			} else if (action instanceof LogAction) {
				returned = new LogActionExecutor((LogAction) action);
			} else if (action instanceof ModuleCallAction) {
				returned = new ModuleCallActionExecutor(
						(ModuleCallAction) action);
			} else if (action instanceof PrintAction) {
				returned = new PrintActionExecutor((PrintAction) action);
			} else if (action instanceof SendAction) {
				returned = new SendActionExecutor((SendAction) action);
			} else if (action instanceof SendOnceAction) {
				return new SendOnceActionExecutor((SendOnceAction) action);
			} else if (action instanceof UserSpecAction) {
				returned = new UserSpecActionExecutor((UserSpecAction) action);
			}
			// executors.put(action, returned);
		}
		if (returned instanceof ModuleCallActionExecutor) {
			((ModuleCallActionExecutor) returned).setContext(context);
		}
		return returned;
	}

	/**
	 * Collect all the variables in a list of terms.
	 *
	 * @param terms
	 *            list of {@link Term}s
	 * @return set of vars inside the given terms
	 */
	public Set<Var> getVariables(List<Term> terms) {
		Set<Var> vars = new HashSet<Var>();
		for (Term t : terms) {
			vars.addAll(t.getFreeVar());
		}
		return vars;
	}

}
