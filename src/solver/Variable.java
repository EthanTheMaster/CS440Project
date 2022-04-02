package solver;

import java.util.ArrayList;

public class Variable {
    // This list is maintained by solver.LinearProgram to remember
    // which auxiliary variables are connected to this variable.
    // The value(s) of the auxiliary variable(s) determine this
    // variable's value.
    private final ArrayList<Integer> auxiliaryVariableIds = new ArrayList<>();

    public String name;
    private final double lowerBound;
    private final double upperBound;


    /**
     * Creates a linear program variable parameterized by lower and upper bounds
     * @param name Name of the variable
     * @param lowerBound Lower bound of the variable
     * @param upperBound Upper bound of the variable
     * @exception IllegalArgumentException if lowerBound > upperBound
     */
    public Variable(String name, double lowerBound, double upperBound) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Lower bound must be smaller than the upper bound.");
        }

        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Creates a standard linear program variable that is nonnegative
     * @param name Name of the variable
     */
    public Variable(String name) {
        this.name = name;
        this.lowerBound = 0;
        this.upperBound = Double.POSITIVE_INFINITY;
    }

    public ArrayList<Integer> getAuxiliaryVariableIds() {
        return auxiliaryVariableIds;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }
}
