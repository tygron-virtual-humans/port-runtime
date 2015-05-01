package plugin.operations;

import plugin.Operation;

import java.util.List;

/**
 * Multiply Class.
 */
public class Multiply extends Operation {

    /**
     * Constructor of multiply class.
     */
    public Multiply() {
        super.setString("Multiply");
    }

    /**
     *
     * @param vars the variables needed for calculation.
     * @return The answer of the operation.
     */
    @Override
    public final double calc(final List<Double> vars) {
        if (vars.size() < 2) {
            throw new ArithmeticException();
        }
        return vars.get(0) * vars.get(1);
    }
}
