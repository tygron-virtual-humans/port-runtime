package goal.core.executors.parameter;

import goal.core.executors.ParameterActionExecutor;
import goal.core.executors.parameter.helpers.EmotionManager;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import jpl.Compound;
import jpl.Variable;
import krTools.language.Term;
import languageTools.program.agent.actions.parameter.GaAppraiseAction;
import mentalState.BASETYPE;
import swiprolog.language.PrologDBFormula;
import swiprolog.language.PrologUpdate;
import vh3.goalgamygdala.GoalGamygdala;

import java.util.List;

/**
 * Created by wouter on 29/05/15.
 */
public class GaAppraiseActionExecutor extends ParameterActionExecutor<GaAppraiseAction> {

    private GoalGamygdala goalGamygdala;
    private EmotionManager emotionManager;

    public GaAppraiseActionExecutor(GaAppraiseAction action) {
        super(action);
        goalGamygdala = GoalGamygdala.getInstance();
        emotionManager = EmotionManager.getInstance();
    }

    @Override
    protected Result executeActionWithParameters(List<Term> terms, RunState<?> runState, Debugger debugger) {
        MentalState mentalState = runState.getMentalState();

        String name = mentalState.getAgentId().getName();
        goalGamygdala.appraise(name,terms);

        emotionManager.updateEmotions(mentalState,debugger,action.getSourceInfo());

        return new Result();
    }
}
