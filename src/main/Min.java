package main;

import java.util.List;

/**
 * Min class.
 */
public class Min extends Operation {

    /**
     * Constructor of Min class.
     */
    public Min() {
        super.setString("min");
    }

    /**
     *
     * @param list The list of variables for the operation.
     * @return The answer of the operation.
     */
    @Override
    public final double calc(final List<Double> list) {
        if (list.size() < 2) {
            throw new ArithmeticException();
        }
        return list.get(0) - list.get(1);
    }
}
