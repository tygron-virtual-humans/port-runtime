package goal.core.executors;

import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALActionFailedException;
import jpl.Compound;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Update;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.CalculateAction;
import mentalState.BASETYPE;
import plugin.Calculator;
import swiprolog.language.PrologTerm;
import swiprolog.language.PrologUpdate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by svenpopping on 29/04/15.
 */
public class CalculateActionExecutor extends ActionExecutor {

    private final CalculateAction action;

    private final Calculator calculator;

    public CalculateActionExecutor(CalculateAction act) {
        this.action = act;
        this.calculator = new Calculator();
    }

    @Override
    protected Result executeAction(RunState<?> runState, Debugger debugger) throws GOALActionFailedException {
        MentalState mentalState = runState.getMentalState();
        List<Term> terms = this.action.getParameters();

        Double[] variables = new Double[terms.size() - 2];
        for (int i = 2; i < terms.size(); i++) {
            variables[i - 2] = Double.valueOf(terms.get(i).toString());
        }
        String operator = terms.get(1).toString();
        int id = Integer.valueOf(terms.get(0).toString());

        try{
            double result = calculator.calc(operator, Arrays.asList(variables));
            mentalState.insert(
                    new PrologUpdate(
                        new Compound("calculated",new jpl.Term[]{
                            new jpl.Integer(id),
                            new jpl.Float((float)result)
                        }),
                        action.getSourceInfo()
                    ),
                    BASETYPE.BELIEFBASE,
                    debugger
            );
        }catch(Exception e){
            System.out.println("This operator does not exist");
            e.printStackTrace();
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
