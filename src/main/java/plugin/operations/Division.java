package plugin.operations;

import plugin.Operation;

import java.util.List;

/**
 * Division Class.
 */
public class Division extends Operation {

    /**
     * Constructor of the Division Class.
     */
    public Division() {
        super.setString("Division");
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
        if (vars.get(1) == 0) {
            throw new ArithmeticException();
        }

        return vars.get(0) / vars.get(1);
    }
}
