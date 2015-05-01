package main;

import java.util.List;

/**
 * Abstract class Operation.
 */
public abstract class Operation {

    /**
     * private String name.
     */
    private String name;

    /**
     *
     * @return String name.
     */
    public final String getString() {
        return this.name;
    }

    /**
     *
     * @param newName Name of the operation
     */
    public final void setString(final String newName) {
        this.name = newName;
    }

    /**
     *
     * @param vars the variables needed for calculation.
     * @return answer in double.
     */
    public abstract double calc(final List<Double> vars);

}
