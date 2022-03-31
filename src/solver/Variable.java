package solver;

import java.util.ArrayList;

public class Variable {
    // Properties that are maintained by solver.LinearProgram and should
    // not be touched by users
    private ArrayList<Integer> auxiliaryVariableIds = new ArrayList<>();

    // Properties that users are free to modify and see and only come into
    // effect at the moment the solver.LinearProgram "compiles" the program
    public String name;
    public double lowerBound;
    public double upperBound;

    public Variable(String name, double lowerBound, double upperBound) {
        assert lowerBound <= upperBound;
        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    // Default standard form variable
    public Variable(String name) {
        this.name = name;
        this.lowerBound = 0;
        this.upperBound = Double.POSITIVE_INFINITY;
    }

    public ArrayList<Integer> getAuxiliaryVariableIds() {
        return auxiliaryVariableIds;
    }
}
