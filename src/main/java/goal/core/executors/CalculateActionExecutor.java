package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Update;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.CalculateAction;

import java.util.List;

/**
 * Created by svenpopping on 29/04/15.
 */
public class CalculateActionExecutor extends ActionExecutor {

    private final CalculateAction action;

    public CalculateActionExecutor(CalculateAction act) {
        this.action = act;
    }

    @Override
    protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
        MentalState mentalState = runState.getMentalState();
        List<Update> terms = this.action.getParameters();

        Double[] variables = new Double[terms.size() - 2];
        for (int i = 2; i < terms.size(); i++) {
            variables[i - 2] = Double.valueOf(terms.get(i).toString());
        }

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
