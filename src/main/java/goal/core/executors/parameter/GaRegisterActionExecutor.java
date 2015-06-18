package goal.core.executors.parameter;

import goal.core.executors.ParameterActionExecutor;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import krTools.language.Term;
import languageTools.program.agent.actions.parameter.GaRegisterAction;
import vh3.goalgamygdala.GoalGamygdala;

import java.util.List;

/**
 * Created by wouter on 29/05/15.
 */
public class GaRegisterActionExecutor extends ParameterActionExecutor<GaRegisterAction> {

    private GoalGamygdala goalGamygdala;

    public GaRegisterActionExecutor(GaRegisterAction action) {
        super(action);
        goalGamygdala = GoalGamygdala.getInstance();
    }

    @Override
    protected Result executeActionWithParameters(List<Term> terms, RunState<?> runState, Debugger debugger) {
        goalGamygdala.createAgent(runState.getMentalState().getAgentId().getName());
        return new Result();
    }
}
