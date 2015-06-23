package goal.core.executors.parameter.helpers;

import goal.core.executors.ActionExecutor;
import goal.core.executors.ParameterActionExecutor;
import languageTools.program.agent.actions.ParameterAction;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by wouter on 19/06/15.
 */
public class ReflectionParameterActionExecutorFactory implements ParameterActionExecutorFactory {

    private HashMap<Class,Class<? extends ParameterActionExecutor>> paramActionExecutors;

    public ReflectionParameterActionExecutorFactory(){
        if(paramActionExecutors == null) {
            paramActionExecutors = new HashMap<>();
            Reflections ref = new Reflections("goal.core.executors.parameter");
            Set<Class<? extends ParameterActionExecutor>> classes = ref.getSubTypesOf(ParameterActionExecutor.class);
            for (Class<? extends ParameterActionExecutor> paeClass : classes) {
                Class paramType = (Class) ((ParameterizedType) paeClass.getGenericSuperclass()).getActualTypeArguments()[0];
                paramActionExecutors.put(paramType, paeClass);
            }
        }
    }

    public ParameterActionExecutor createParameterActionExecutor(ParameterAction action){
        Class actionClass = action.getClass();

        try {
            Class<? extends ParameterActionExecutor> executor = paramActionExecutors.get(actionClass);
            Constructor<? extends ParameterActionExecutor> constructor = executor.getDeclaredConstructor(actionClass);
            return constructor.newInstance(action);

        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

}
