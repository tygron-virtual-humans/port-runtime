package main;

import java.util.List;

/**
 * Negation class.
 */
public class Negation extends Operation {

    /**
     * Constructor of the Negation class.
     */
    public Negation() {
        setString("Negation");
    }

    /**
     *
     * @param list variables for the calculation.
     * @return double is returned with the answer of the operation.
     */
    public final double calc(final List<Double> list) {
        if (list.size() < 1) {
            throw new ArithmeticException();
        }
        return -1 * list.get(0);
    }
}
