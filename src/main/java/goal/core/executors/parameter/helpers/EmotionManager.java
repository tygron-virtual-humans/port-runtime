package goal.core.executors.parameter.helpers;

import goal.core.mentalstate.MentalState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import jpl.Compound;
import jpl.Variable;
import krTools.parser.SourceInfo;
import mentalState.BASETYPE;
import swiprolog.language.PrologDBFormula;
import swiprolog.language.PrologUpdate;
import vh3.goalgamygdala.GoalGamygdala;
import vh3.goalgamygdala.GoalGamygdalaAgent;

import java.util.List;

/**
 * Created by wouter on 29/05/15.
 */
public class EmotionManager {
    private static EmotionManager ourInstance = new EmotionManager();

    public static EmotionManager getInstance() {
        return ourInstance;
    }

    GoalGamygdala goalGamygdala;

    private EmotionManager() {
        goalGamygdala = GoalGamygdala.getInstance();
    }

    public void updateEmotions(MentalState mentalState, Debugger debugger, SourceInfo sourceInfo){
        try {
            mentalState.delete(
                    new PrologDBFormula(
                            new Compound("emotion", new jpl.Term[]{
                                    new Variable("A"),
                                    new Variable("B")
                            }
                            ), sourceInfo
                    ),
                    BASETYPE.BELIEFBASE,
                    debugger
            );
        } catch (GOALDatabaseException e) {
            e.printStackTrace();
        }

        List<Compound> emotions = goalGamygdala.getAgentByName(mentalState.getAgentId().getName()).getEmotions();
        for(Compound c : emotions){
            try {
                mentalState.insert(new PrologUpdate(c,sourceInfo),BASETYPE.BELIEFBASE,debugger);
            } catch (GOALDatabaseException e) {
                e.printStackTrace();
            }
        }
    }
}
