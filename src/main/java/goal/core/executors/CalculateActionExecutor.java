package goal.core.executors;

import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import krTools.language.Substitution;
import languageTools.program.agent.actions.Action;

/**
 * Created by svenpopping on 29/04/15.
 */
public class CalculateActionExecutor extends ActionExecutor {

    private final CalculatenAction action;

    public CalculateActionExecutor(CalculateAction act) {
        this.action = act;
    }

    @Override
    protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
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
