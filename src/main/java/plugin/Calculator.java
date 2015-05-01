package plugin;

import java.util.List;

/**
 *
 */
public class Calculator {

    /**
     *
     */
    public Calculator() {

    }

    /**
     * calc method is used to call the right classes
     * for the operation and calculates the answer.
     *
     * @param op   String with the description of the operation.
     * @param vars List of variables to use.
     * @return Returns the answer.
     * @throws IllegalAccessException Based on the given access to the classes.
     * @throws InstantiationException When the instantiation failed
     *  of the given operation class.
     * @throws ClassNotFoundException If the given operation
     *  doesn't have a specified class.
     */
    public final double calc(final String op, final List<Double> vars)
            throws IllegalAccessException, InstantiationException,
                ClassNotFoundException {
        String operation = Character.toUpperCase(op.charAt(0))
                + op.substring(1).toLowerCase();

        ClassLoader loader = this.getClass().getClassLoader();
        Class opClass = loader.loadClass(Calculator.class.getPackage().getName()
                + "." + operation);
        return ((Operation) opClass.newInstance()).calc(vars);
    }
}
