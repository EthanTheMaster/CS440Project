import java.util.ArrayList;

// This class is for internal use only.
public class StandardForm {
    // Standard form variables
    // Let
    //      m be the number of constraints and
    //      n be the number of variables
    public ArrayList<ArrayList<Double>> A; // m x n matrix
    public ArrayList<Double> b; // m vector
    public ArrayList<Double> c; // n vector

    // This optional constant term in the objective function does not change solutions
    // to the linear program and will be useful for the simplex algorithm.
    public double objConst;

    private final int n;

    public StandardForm(int numVariables) {
        n = numVariables;

        A = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>(numVariables);
        objConst = 0.0;

        // Initialize c vectors
        for (int i = 0; i < numVariables; i++) {
            c.add(0.0);
        }
    }

    // Helper function to update A matrix
    public void updateA(int i, int j, double v) {
        A.get(i).set(j, v);
    }

    public double getA(int i, int j) {
        return A.get(i).get(j);
    }

    // Adds an empty row to A and b and returns the index of that row
    public int addEmptyConstraint() {
        int index = A.size();
        ArrayList<Double> row = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            row.add(0.0);
        }
        A.add(row);
        b.add(0.0);
        return index;
    }

    public String prettyPrint() {
        StringBuilder res = new StringBuilder();
        // Print objective
        res.append("MAXIMIZE:\n\t");

        ArrayList<String> terms = new ArrayList<>();
        terms.add(objConst + "");
        for (int i = 0; i < n; i++) {
            double w = c.get(i);
            if (w != 0) {
                terms.add(String.format("(%f)x_%d", w, i));
            }
        }
        res.append(String.join(" + ", terms));
        res.append("\n\n");
        res.append("CONSTRAINTS:\n");

        // Print constraints
        for (int i = 0; i < A.size(); i++) {
            terms.clear();
            for (int j = 0; j < n; j++) {
                double w = getA(i, j);
                if (w != 0) {
                    terms.add(String.format("(%f)x_%d", w, j));
                }
            }
            res.append("\t" + String.join(" + ", terms));
            res.append(" <= " + b.get(i) + "\n");
        }

        return res.toString();
    }
}
