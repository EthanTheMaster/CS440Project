import java.util.ArrayList;

public class ObjectiveFunction {
    private ObjectiveGoal goal;
    private ArrayList<Variable> objectiveVariables;
    private ArrayList<Double> objectiveWeights;

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
