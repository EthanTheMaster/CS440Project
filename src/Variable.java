import java.util.ArrayList;

public class Variable {
    // Properties that are maintained by LinearProgram and should
    // not be touched by users
    private int id;
    private ArrayList<Integer> auxiliaryVariableIds = new ArrayList<>();

    // Properties that users are free to modify and see and only come into
    // effect at the moment the LinearProgram "compiles" the program
    public String name;
    public double lowerBound;
    public double upperBound;

    public Variable(int id, String name, double lowerBound, double upperBound) {
        assert lowerBound <= upperBound;
        this.id = id;
        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    // Default standard form variable
    public Variable(int id, String name) {
        this.id = id;
        this.name = name;
        this.lowerBound = 0;
        this.upperBound = Double.POSITIVE_INFINITY;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getAuxiliaryVariableIds() {
        return auxiliaryVariableIds;
    }
}
