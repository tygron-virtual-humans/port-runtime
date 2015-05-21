package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import languageTools.program.agent.actions.Action;

import java.util.List;

public abstract class ParameterActionExecutor extends ActionExecutor {

    private final ParameterAction action;

    public abstract void executeActionParameters(List<Term> terms);

    @Override
    protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
        MentalState mentalState = runState.getMentalState();
        List<Term> terms = this.action.getParameters();

        executeActionParameters(terms);

        return new Result();
    }

    @Override
    protected ActionExecutor applySubst(Substitution subst) {
        return new CalculateActionExecutor(this.action.applySubst(subst));
    }

    @Override
    public Action<?> getAction() {
        return this.action;
    }
}
