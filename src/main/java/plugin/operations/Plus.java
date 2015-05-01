package plugin.operations;

import plugin.Operation;

import java.util.List;

/**
 * Plus class.
 */
public class Plus extends Operation {

    /**
     * Constructor.
     */
    public Plus() {
        setString("plus");
    }

    /**
     *
     * @param list The list of variables for the Plus operation.
     * @return The answer of the Plus operation.
     */
    public final double calc(final List<Double> list) {
        if (list.size() < 2) {
            throw new ArithmeticException();
        }
        return list.get(0) + list.get(1);
    }
}
