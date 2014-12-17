package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Channel;
import goal.tools.debugger.Debugger;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.AdoptAction;
import languageTools.program.agent.selector.Selector;
import languageTools.program.agent.selector.Selector.SelectorType;

/**
 *
 * @author W.Pasman
 *
 */
public class AdoptActionExecutor extends ActionExecutor {
	private final AdoptAction action;

	public AdoptActionExecutor(AdoptAction act) {
		this.action = act;
	}

	@Override
	public ActionExecutor evaluatePrecondition(MentalState mentalState,
			Debugger debugger, boolean last) {
		Set<Substitution> evaluation = new MentalStateConditionExecutor(
				this.action.getPrecondition()).evaluate(mentalState, debugger);
		if (!evaluation.isEmpty()) {
			// precondition holds for at least one instance.
			Selector selector = this.action.getSelector();
			if (selector.getType() == SelectorType.SELF
					|| selector.getType() == SelectorType.THIS) {
				debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION,
						this.action, "Precondition of action %s holds.",
						this.action);
				return this;
			} else {
				throw new UnsupportedOperationException(
						"Only 'SELF' and 'THIS' are allowed right now.");
			}
		}

		debugger.breakpoint(Channel.ACTION_PRECOND_EVALUATION, this.action,
				"Precondition of action %s does not hold.", this.action);
		return null;
	}

	@Override
	protected Result executeAction(RunState<?> runState, Debugger debugger) {
		MentalState mentalState = runState.getMentalState();

		// TODO: handle selector.
		// Set<String> agentNames = this.getSelector().resolve(mentalState);

		boolean topLevel = this.action.getSelector().getType() == SelectorType.SELF;
		mentalState.adopt(this.action.getUpdate(), !topLevel, debugger,
				mentalState.getAgentId());

		// Report action was performed.
		report(debugger);

		return new Result(this.action);
	}

	@Override
	public ActionExecutor applySubst(Substitution substitution) {
		return new AdoptActionExecutor(this.action.applySubst(substitution));
	}

	@Override
	public Action<?> getAction() {
		return this.action;
	}
}