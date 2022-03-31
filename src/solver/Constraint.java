package solver;

import java.util.ArrayList;

public class Constraint {
    private ArrayList<Variable> variables;
    // LHS
    private ArrayList<Double> weights;
    private Relation relation;
    // RHS
    private double b;

    public Constraint(ArrayList<Variable> variables, ArrayList<Double> weights, Relation relation, double b) {
        assert variables.size() == weights.size();
        assert Double.isFinite(b);

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
