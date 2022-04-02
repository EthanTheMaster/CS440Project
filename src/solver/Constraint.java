package solver;

import java.util.ArrayList;

public class Constraint {
    private final ArrayList<Variable> variables;
    // LHS
    private final ArrayList<Double> weights;
    private final Relation relation;
    // RHS
    private final double b;

    /**
     * Creates a linear program constraint characterized by an inequality involving
     * a linear combination of variables. More specifically, it creates a constraint
     * of the form c * x R b where c and x are vectors, * is the dot product, R is
     * a relation, and b is a real number.
     * @param variables A list of variables that are involved in the constraint
     * @param weights A list of coefficients for each variable where the ith value
     *                corresponds to the coefficient for the ith variable
     * @param relation Specifies the type of constraint
     * @param b A number that restricts the linear combination of variables
     * @exception IllegalArgumentException if the number of variables and weights do not match or if b is not finite
     */
    public Constraint(ArrayList<Variable> variables, ArrayList<Double> weights, Relation relation, double b) {
        if (variables.size() != weights.size()) {
            throw new IllegalArgumentException("The list of variables and weights must have the same size.");
        }
        if (Double.isInfinite(b)) {
            throw new IllegalArgumentException("b must be finite.");
        }

        this.variables = variables;
        this.weights = weights;
        this.relation = relation;
        this.b = b;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public ArrayList<Double> getWeights() {
        return weights;
    }

    public Relation getRelation() {
        return relation;
    }

    public double getB() {
        return b;
    }
}
