package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.ParameterAction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class ParameterActionExecutor<ParamAction extends ParameterAction> extends ActionExecutor {

    protected final ParamAction action;

    protected abstract Result executeActionWithParameters(List<Term> terms, RunState<?> runState, Debugger debugger);

    public ParameterActionExecutor(ParamAction action){
        this.action = action;
    }

    @Override
    protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
        List<Term> terms = this.action.getParameters();

        return executeActionWithParameters(terms,runState,debugger);
    }

    @Override
    protected ActionExecutor applySubst(Substitution subst) {
        try {
            return this.getClass().getDeclaredConstructor(action.getClass()).newInstance(
                    (ParamAction) action.applySubst(subst));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Action<?> getAction() {
        return this.action;
    }
}
