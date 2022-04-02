package solver;

import java.util.ArrayList;

public class ObjectiveFunction {
    private final ObjectiveGoal goal;
    private final ArrayList<Variable> objectiveVariables;
    private final ArrayList<Double> objectiveWeights;

    /**
     * Creates a linear program objective function
     * @param goal Specifies the type of optimization: minimization or maximization
     * @param objectiveVariables A list of variables in the objective function
     * @param objectiveWeights A list of coefficients for each variable where the ith
     *                         value corresponds to the ith variable
     */
    public ObjectiveFunction(ObjectiveGoal goal, ArrayList<Variable> objectiveVariables, ArrayList<Double> objectiveWeights) {
        if (objectiveVariables.size() != objectiveWeights.size()) {
            throw new IllegalArgumentException("The number of variables must match the number of weights.");
        }
        this.goal = goal;
        this.objectiveVariables = objectiveVariables;
        this.objectiveWeights = objectiveWeights;
    }

    public ObjectiveGoal getGoal() {
        return goal;
    }

    public ArrayList<Variable> getObjectiveVariables() {
        return objectiveVariables;
    }

    public ArrayList<Double> getObjectiveWeights() {
        return objectiveWeights;
    }

    /**
     * Gets an empty objective function. Specifically, it returns the zero function
     * that can be used to check only the feasibility of a linear program.
     * @return an empty objective function
     */
    public static ObjectiveFunction empty() {
        return new ObjectiveFunction(ObjectiveGoal.MAXIMIZE, new ArrayList<>(), new ArrayList<>());
    }
}
