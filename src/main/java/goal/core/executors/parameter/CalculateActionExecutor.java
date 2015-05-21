package goal.core.executors.parameter;

import goal.core.executors.ParameterActionExecutor;
import goal.core.mentalstate.MentalState;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import jpl.Compound;
import krTools.language.Term;
import languageTools.program.agent.actions.parameter.CalculateAction;
import mentalState.BASETYPE;
import plugin.Calculator;
import swiprolog.language.PrologUpdate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by svenpopping on 29/04/15.
 */
public class CalculateActionExecutor extends ParameterActionExecutor<CalculateAction> {

    private final Calculator calculator;

    @Override
    protected Result executeActionWithParameters(List<Term> terms, RunState<?> runState, Debugger debugger) {
        MentalState mentalState = runState.getMentalState();
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

    public CalculateActionExecutor(CalculateAction act) {
        super(act);
        this.calculator = new Calculator();
    }

}
