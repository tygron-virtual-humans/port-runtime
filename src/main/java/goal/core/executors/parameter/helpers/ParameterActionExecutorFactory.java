package goal.core.executors.parameter.helpers;

import goal.core.executors.ParameterActionExecutor;
import languageTools.program.agent.actions.ParameterAction;

/**
 * Created by wouter on 19/06/15.
 */
public interface ParameterActionExecutorFactory {

    ParameterActionExecutor createParameterActionExecutor(ParameterAction action);
}
