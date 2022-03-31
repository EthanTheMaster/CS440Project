package solver;

import java.util.ArrayList;

public class Solution {
    private SolutionResult status;
    private ArrayList<Double> solution;
    private double objectiveValue;

    public Solution(SolutionResult status, ArrayList<Double> solution, double objectiveValue) {
        this.status = status;
        this.solution = solution;
        this.objectiveValue = objectiveValue;
    }

    public SolutionResult getStatus() {
        return status;
    }

    public ArrayList<Double> getSolution() {
        return solution;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }
}
