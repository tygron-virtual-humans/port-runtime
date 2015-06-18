package goal.core.executors.parameter.helpers;

import goal.core.mentalstate.MentalState;
import goal.tools.debugger.Debugger;
import goal.tools.errorhandling.exceptions.GOALDatabaseException;
import jpl.Compound;
import jpl.Variable;
import krTools.language.Substitution;
import krTools.parser.SourceInfo;
import mentalState.BASETYPE;
import swiprolog.language.PrologDBFormula;
import swiprolog.language.PrologQuery;
import swiprolog.language.PrologUpdate;
import vh3.goalgamygdala.GoalGamygdala;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
            Set<Substitution> substitutions = mentalState.getOwnBase(BASETYPE.BELIEFBASE).query(new PrologQuery(
                    new Compound("emotion", new jpl.Term[]{
                            new Variable("A"),
                            new Variable("B")
                    }
                    ), sourceInfo
            ),debugger);

            Iterator<Substitution> substitutionIterator = substitutions.iterator();

            while(substitutionIterator.hasNext()){
                Substitution substitution = substitutionIterator.next();
                mentalState.delete(
                        new PrologDBFormula(
                                new Compound("emotion", new jpl.Term[]{
                                        new Variable("A"),
                                        new Variable("B")
                                }
                                ), sourceInfo
                        ).applySubst(substitution),
                        BASETYPE.BELIEFBASE,
                        debugger
                );
            }

        } catch (GOALDatabaseException e) {
            e.printStackTrace();
        }

        try {
            List<Compound> emotions = goalGamygdala.getAgentByName(mentalState.getAgentId().getName()).getEmotions();
            for(Compound c : emotions){
                try {
                    mentalState.insert(new PrologUpdate(c,sourceInfo),BASETYPE.BELIEFBASE,debugger);
                } catch (GOALDatabaseException e) {
                    e.printStackTrace();
                }
            }
        }catch(NullPointerException e){
            System.out.println(mentalState.getAgentId().getName());
            e.printStackTrace();
        }
    }
}
