package solver;

import java.util.ArrayList;
import java.util.TreeSet;

/*
All references to "CLRS" refers to:
Introduction to Algorithms Third Edition by Cormen, Leiserson, Rivest, and Stein
 */
public class SimplexState {
    private static final double EPSILON = 0.0000001;

    // Let
    //      m be the number of constraints and
    //      n be the number of variables (basic + nonbasic variables)
    private int m;
    private int n;

    public ArrayList<ArrayList<Double>> A; // n x n matrix
    public ArrayList<Double> b; // n vector
    public ArrayList<Double> c; // n vector
    private double objConst;

    // Set of nonbasic and basic variables
    private final TreeSet<Integer> nonBasic;
    private final TreeSet<Integer> basic;

    /**
     * Creates a linear program in slack form given a linear program in
     * standard form
     * @param standardForm Linear program in standard form
     */
    public SimplexState(StandardForm standardForm) {
        // The nonbasic variables occur in the objective function
        int numNonBasicVars = standardForm.c.size();
        // A basic slack variable is added for each constraint
        int numBasicVars = standardForm.b.size();

        // Convert standard form into slack form
        m = standardForm.b.size();
        n = numBasicVars + numNonBasicVars;


        objConst = standardForm.objConst;
        nonBasic = new TreeSet<>();
        basic = new TreeSet<>();

        A = getEmptyMatrix(n, n);
        b = new ArrayList<>(n);
        // Clone original c vector which we will extend later to
        // include entries for the basic slack variables
        c = new ArrayList<>(standardForm.c);

        // Mark which variables are currently nonbasic and basic
        // Set appropriate entries in b and extend c vector
        // Adapt A matrix to include slack variables
        for (int i = 0; i < numNonBasicVars; i++) {
            nonBasic.add(i);
            b.add(0.0);
        }

        for (int i = 0; i < numBasicVars; i++) {
            b.add(standardForm.b.get(i));
            // Extend c vector
            c.add(0.0);

            // ith row holding basic slack variable
            // should hold weights of nonbasic variables
            //
            // We follow the convention in CLRS where initially the
            // basic slack variables have index numNonBasicVars + i
            // where 0 <= i < m
            for (int j = 0; j < numNonBasicVars; j++) {
                updateA(numNonBasicVars + i, j, standardForm.getA(i, j));
            }
            basic.add(numNonBasicVars + i);
        }
    }

    /**
     * Pivots the linear program according to Pivot function on CLRS pp. 869
     * but pivots in-place without returning a new modified linear program
     *
     * @param enteringVariable the entering variable
     * @param leavingVariable the leaving variable
     */
    public void pivot(int enteringVariable, int leavingVariable) {
        // Rename to be similar to CLRS
        int e = enteringVariable;
        int l = leavingVariable;

        // Create new constraint where entering variable is a new basic slack variable
        b.set(e, b.get(l) / getA(l, e));
        for (int j : nonBasic) {
            if (j != e) {
                updateA(e, j, getA(l, j) / getA(l, e));
            }
        }
        updateA(e, l, 1.0/getA(l, e));

        // With our new basic variable e, perform substitution in the other basic variable
        // equations
        for (int i : basic) {
            if (i != l) {
                b.set(i, b.get(i) - getA(i, e)*b.get(e));
                for (int j : nonBasic) {
                    if (j != e) {
                        updateA(i, j, getA(i, j) - getA(i, e)*getA(e, j));
                    }
                }
                updateA(i, l, -getA(i, e)*getA(e, l));
            }
        }

        // Update objective function
        objConst = objConst + c.get(e) * b.get(e);
        for (int j : nonBasic) {
            if (j != e) {
                c.set(j, c.get(j) - c.get(e)*getA(e, j));
            }
            c.set(l, -c.get(e)*getA(e, l));
        }

        // Update our basic and nonbasic variables
        basic.remove(l);
        nonBasic.add(l);
        nonBasic.remove(e);
        basic.add(e);
    }

    /**
     * This function encodes the operation of lines 3-12 of the Simplex
     * function on CLRS pp. 871 and uses Bland's rule to prevent cycling.
     *
     * This function also assumes that the linear program at invocation
     * time has a basic solution. It will repeatedly pivot the linear
     * program against a variable with positive coefficient in the
     * objective function.
     *
     * @return false if during pivoting an unbounded solution was found
     * and true otherwise
     */
    public boolean simplexPivot() {
        while (true) {
            // Find entering variable
            int e = -1;
            for (int j : nonBasic) {
                if (c.get(j) > 0) {
                    e = j;
                    break;
                }
            }

            // The objective function cannot be increased
            if (e == -1) break;

            // Find leaving variable
            int l = -1;
            double deltaL = Double.POSITIVE_INFINITY;
            for (int i : basic) {
                if (getA(i, e) > 0) {
                    double limit = b.get(i) / getA(i, e);
                    // Must be < to get smallest index where min occurs for Bland's rule
                    if (limit < deltaL) {
                        l = i;
                        deltaL = limit;
                    }
                }
            }

            if (Double.isInfinite(deltaL)) {
                return false;
            } else {
                pivot(e, l);
            }
        }
        return true;
    }

    /**
     * Performs the Initialize-Simplex function on CLRS pp. 887.
     * It converts a linear program into a slack form where the basic
     * solution is feasible.
     *
     * @return false if it is not possible to perform conversion implying
     * that the original linear program is infeasible and returns true
     * otherwise
     */
    public boolean initializeSimplex() {
        int k = -1;
        double bk = Double.POSITIVE_INFINITY;
        for (int i : basic) {
            // The < is necessary for Bland's Rule
            if (b.get(i) < bk) {
                k = i;
                bk = b.get(i);
            }
        }
        // Basic solution is feasible
        if (bk >= 0) return true;

        // Create auxiliary linear program to determine feasibility

        // Clone objective function because in the auxiliary linear program we will
        // be using a different objective function
        ArrayList<Double> oldC = new ArrayList<>(c);
        double oldObjConst = objConst;
        // Remember what were the nonbasic variables
        TreeSet<Integer> oldNonBasic = new TreeSet<>(nonBasic);

        // In CLRS, the auxiliary variable is x0 which we will
        // assign id being the last index
        int auxVar = n;

        // New row in A matrix for the auxiliary variable
        ArrayList<Double> newRow = new ArrayList<>(n+1);
        for (int i = 0; i < n; i++) {
            if (basic.contains(i)) {
                // Add auxiliary variable to each constraint
                A.get(i).add(-1.0);
            } else {
                A.get(i).add(0.0);
            }
            // Add n entries for the new row (but add last column for
            // auxiliary variable later)
            newRow.add(0.0);

            // New objective function only has auxiliary variable in it
            // so zero out all other variables
            c.set(i, 0.0);
        }
        // Add new row in A matrix for the auxiliary variable
        newRow.add(0.0);
        A.add(newRow);

        // Set the new objective function
        objConst = 0.0;
        c.add(-1.0);

        // Bookkeep by "registering" this new auxiliary variable
        n++;
        nonBasic.add(auxVar);
        b.add(0.0);

        // Pivot the auxiliary linear program so that basic solution is feasible
        pivot(auxVar, k);
        // Solve the auxiliary linear program
        simplexPivot();
        if (Math.abs(objConst) < EPSILON) {
            // Simplex pivoting will always end with a slack form where the
            // basic solution is feasible and optimal.
            //
            // We know our auxiliary linear program is not unbounded as it
            // is at most 0. CLRS Lemma 29.11 says that our basic solution
            // has objective value objConst, which is (basically) 0, implies that
            // the current, final slack form is feasible in the original
            // linear program and we know the auxiliary variable is 0.
            if (basic.contains(auxVar)) {
                // Perform degenerate pivot with the auxiliary variable
                // to make it nonbasic so basic solution in the auxiliary
                // linear program is also feasible in the original linear
                // program. We also want the slack form of the auxiliary
                // linear program to be a valid slack form in the original
                // linear program when the auxiliary variable is set to zero
                // which is not the case when the auxiliary variable is
                // basic.
                //
                // Arbitrarily choose some non-zero nonbasic variable in the equality
                // with the auxiliary variable and pivot
                //
                // Choose the entering variable to pivot with the basic auxiliary variable
                // to have the largest coefficient magnitude for numerical stability
                int e = -1;
                double maxWeight = Double.NEGATIVE_INFINITY;
                for (int j : nonBasic) {
                    double w = Math.abs(getA(auxVar, j));
                    if (w > maxWeight) {
                        e = j;
                        maxWeight = w;
                    }
                }
                pivot(e, auxVar);
            }

            // Restore original linear program by popping off the last
            // entry/column which represents the auxiliary variable we
            // added
            b.remove(auxVar);
            nonBasic.remove(auxVar);
            n--;
            for (int i = 0; i < n; i++) {
                // Remove auxiliary variable from each constraint
                A.get(i).remove(auxVar);
            }
            // Remove last row with auxiliary variable
            A.remove(auxVar);

            // Restore objective function
            // Objective function may contain basic variables so substitute them
            ArrayList<Double> substitutedC = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                substitutedC.add(0.0);
            }
            for (int i : oldNonBasic) {
                // Check if that variable is basic or nonbasic in the transformed
                // linear program
                if (nonBasic.contains(i)) {
                    // The variable is still nonbasic so just add the weight
                    substitutedC.set(i, substitutedC.get(i) + oldC.get(i));
                } else {
                    // The variable became basic so perform substitution
                    oldObjConst = oldObjConst + oldC.get(i)*b.get(i);
                    for (int j : nonBasic) {
                        substitutedC.set(j, substitutedC.get(j) - oldC.get(i)*getA(i, j));
                    }
                }
            }

            c = substitutedC;
            objConst = oldObjConst;

            return true;
        } else {
            // Optimal solution to auxiliary linear program has non-zero objective value
            // which means there is no feasible solution
            return false;
        }

    }

    /**
     * Solves the current linear program
     * @return a solution to the linear program
     */
    public Solution solve() {
        boolean isFeasible = initializeSimplex();
        if (!isFeasible) {
            return new Solution(SolutionResult.INFEASIBLE, null, 0.0);
        }

        boolean isBounded = simplexPivot();
        if (!isBounded) {
            return new Solution(SolutionResult.UNBOUNDED, null, Double.POSITIVE_INFINITY);
        }
        // Optimal solution is the basic solution
        ArrayList<Double> solution = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            if (nonBasic.contains(i)) {
                solution.add(0.0);
            } else {
                solution.add(b.get(i));
            }
        }
        return new Solution(SolutionResult.FEASIBLE, solution, objConst);
    }

    /**
     * Creates an empty m by n matrix initialized to all zeroes
     * @param m number of rows
     * @param n number of columns
     * @return an empty m by n matrix
     */
    private ArrayList<ArrayList<Double>> getEmptyMatrix(int m, int n) {
        assert m >= 0 && n >= 0;
        ArrayList<ArrayList<Double>> res = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            ArrayList<Double> row = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                row.add(0.0);
            }
            res.add(row);
        }
        return res;
    }

    /**
     * Helper function to assign a value to A[i][j]
     * @param i the row
     * @param j the column
     * @param v the new value
     */
    public void updateA(int i, int j, double v) {
        A.get(i).set(j, v);
    }

    /**
     * Helper function get A[i][j]
     * @param i the row
     * @param j the column
     * @return the value of A[i][j]
     */
    public double getA(int i, int j) {
        return A.get(i).get(j);
    }

    /**
     * Convenient method to print out the linear program's
     * current configuration in slack form
     * @return a string holding a formatted linear program
     * in slack form
     */
    public String prettyPrint() {
        StringBuilder res = new StringBuilder();
        // Print objective
        res.append("MAXIMIZE:\n\t");

        ArrayList<String> terms = new ArrayList<>();
        terms.add(objConst + "");
        for (int i : nonBasic) {
            double w = c.get(i);
            if (w != 0) {
                terms.add(String.format("(%f)x_%d", w, i));
            }
        }
        res.append(String.join(" + ", terms));
        res.append("\n\n");
        res.append("CONSTRAINTS:\n");

        // Print slack equality constraints
        for (int i : basic) {
            terms = new ArrayList<>();
            for (int j : nonBasic) {
                double w = getA(i, j);
                if (w != 0) {
                    terms.add(String.format("(%f)x_%d", w, j));
                }
            }

            res.append(String.format("\tx_%d = %f - ( %s )", i, b.get(i), String.join(" + ", terms)));
            res.append("\n");
        }

        return res.toString();
    }
}
