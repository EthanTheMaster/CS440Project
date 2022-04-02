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
}
