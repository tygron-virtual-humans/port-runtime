package goal.core.executors.parameter;

import goal.core.executors.ParameterActionExecutor;
import goal.core.executors.parameter.helpers.EmotionManager;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import krTools.language.Term;
import languageTools.program.agent.actions.parameter.GaAdoptAction;
import languageTools.program.agent.actions.parameter.GaCreateRelationAction;
import vh3.goalgamygdala.GoalGamygdala;

import java.util.List;

/**
 * Created by wouter on 29/05/15.
 */
public class GaCreateRelationActionExecutor extends ParameterActionExecutor<GaCreateRelationAction> {

    private GoalGamygdala goalGamygdala;

    public GaCreateRelationActionExecutor(GaCreateRelationAction action) {
        super(action);
        goalGamygdala = GoalGamygdala.getInstance();
    }

    @Override
    protected Result executeActionWithParameters(List<Term> terms, RunState<?> runState, Debugger debugger) {
        MentalState mentalState = runState.getMentalState();

        String name = mentalState.getAgentId().getName();

        goalGamygdala.createRelation(name,terms);

        return new Result();
    }
}
