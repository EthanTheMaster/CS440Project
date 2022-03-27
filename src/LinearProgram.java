import java.util.ArrayList;
import java.util.stream.Collectors;

public class LinearProgram {
    private ArrayList<Variable> userVariables;
    private ArrayList<Constraint> userConstraints;
    private ObjectiveFunction objective;

    private StandardForm computedStandardForm;

    private int currentVariableId = 0;

    public LinearProgram() {
        userVariables = new ArrayList<>();
        userConstraints = new ArrayList<>();
    }

    public Variable registerVariable(String name, double lowerBound, double upperBound) {
        Variable res = new Variable(currentVariableId, name, lowerBound, upperBound);
        currentVariableId++;
        userVariables.add(res);
        return res;
    }

    public Variable registerNonnegativeVariable(String name) {
         Variable res = new Variable(currentVariableId, name);
         currentVariableId++;
         userVariables.add(res);
         return res;
    }

    public Variable registerUnboundedVariable(String name) {
        Variable res = new Variable(currentVariableId, name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        currentVariableId++;
        userVariables.add(res);
        return res;
    }

    public void addConstraint(Constraint c) {
        userConstraints.add(c);
    }

    public void setObjective(ObjectiveFunction objective) {
        this.objective = objective;
    }

    // Add nonnegative auxiliary variables that aid in representing variable
    private int addAuxiliaryVariables() {
        // Start fresh with variables
        for (Variable x : userVariables) {
            x.getAuxiliaryVariableIds().clear();
        }

        // Add auxiliary variables
        int numVariables = 0;
        for (Variable x : userVariables) {
            if (Double.isInfinite(x.lowerBound) && Double.isInfinite(x.upperBound)) {
                // x is an unbounded variable so assign two auxiliary variables
                //
                // x = x1 - x2 where x1, x2 >= 0
                x.getAuxiliaryVariableIds().add(numVariables);
                numVariables++;
                x.getAuxiliaryVariableIds().add(numVariables);
                numVariables++;
            } else {
                // Suppose a, b are real numbers and a < b.
                // CASE 1:
                // x has a finite lower bound: a <= x <= b or a <= x
                // Then,
                //      0 <= x' <= b - a
                // or   0 <= x'
                //
                //      where x' >= 0 and x' = x - a
                //
                // CASE 2:
                // x has a finite upper bound: x <= b
                // Then,
                //      0 <= x'
                //
                //      where x' >= 0 and x' = b - x
                //
                // In all of the above cases, we can use one auxiliary variable

                x.getAuxiliaryVariableIds().add(numVariables);
                numVariables++;
            }
        }

        return numVariables;
    }

    // A variable's lower and upper bounds may require
    // that we add an extra constraint
    private void addVariableConstraints(StandardForm state) {
        for (Variable x : userVariables) {
            if (Double.isFinite(x.lowerBound) && Double.isFinite(x.upperBound)) {
                // a <= x <= b => 0 <= x' <= b - a
                // where x' = x - a
                int i = state.addEmptyConstraint();
                int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                state.updateA(i, auxVariableId, 1.0);
                state.b.set(i, x.upperBound - x.lowerBound);
            }
        }
    }

    // Because some variables are transformed to be expressed
    // in a form using a nonnegative variable, we must reexpress
    // each constraint in terms of these new nonnegative variables.
    //
    //
    private void addConstraint(Constraint c, StandardForm state) {
        if (c.getRelation() == Relation.LEQ) {
            // Logic for adding constraint is only added for the <= case
            int i = state.addEmptyConstraint();
            state.b.set(i, c.getB());

            for (int k = 0; k < c.getVariables().size(); k++) {
                Variable x = c.getVariables().get(k);
                double w = c.getWeights().get(k);

                boolean finiteLowerBound = Double.isFinite(x.lowerBound);
                boolean finiteUpperBound = Double.isFinite(x.upperBound);
                // Perform substitution of variable x in terms of auxiliary variables
                if (finiteLowerBound) {
                    // a <= x <= b or a <= x
                    // => x = a + x' where x' >= 0
                    int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                    state.updateA(i, auxVariableId, w);
                    state.b.set(i, state.b.get(i) - w * x.lowerBound);
                } else if (finiteUpperBound) {
                    // x <= b
                    // => x = b - x' where x' >= 0
                    int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                    state.updateA(i, auxVariableId, -w);
                    state.b.set(i, state.b.get(i) - w * x.upperBound);
                } else {
                    // x is an unbounded real number
                    // => x = x1 - x2 where x1, x2 >= 0
                    int x1 = x.getAuxiliaryVariableIds().get(0);
                    int x2 = x.getAuxiliaryVariableIds().get(1);
                    state.updateA(i, x1, w);
                    state.updateA(i, x2, -w);
                }
            }
        } else {
            // If the constraint is not using <=, then convert to an equivalent
            // version.
            Constraint flippedConstraint = new Constraint(
                    c.getVariables(),
                    new ArrayList<Double>(c.getWeights().stream().map(w -> -w).collect(Collectors.toList())),
                    Relation.LEQ,
                    -c.getB()
            );
            if (c.getRelation() == Relation.GEQ) {
                addConstraint(flippedConstraint, state);
            } else if (c.getRelation() == Relation.EQ) {
                // = is the same as <= and >=
                addConstraint(new Constraint(
                        c.getVariables(),
                        c.getWeights(),
                        Relation.LEQ,
                        c.getB()
                ), state);
                addConstraint(flippedConstraint, state);
            }
        }
    }

    private void addObjectiveFunction(StandardForm state) {
        state.objConst = 0;
        // Minimization is the same as negating the objective function and maximizing
        int sign = (objective.getGoal() == ObjectiveGoal.MAXIMIZE) ? 1 : -1;
        for (int i = 0; i < objective.getObjectiveVariables().size(); i++) {
            Variable x = objective.getObjectiveVariables().get(i);
            double w = sign * objective.getObjectiveWeights().get(i);

            boolean finiteLowerBound = Double.isFinite(x.lowerBound);
            boolean finiteUpperBound = Double.isFinite(x.upperBound);
            // Perform substitution of variable x in terms of auxiliary variables
            if (finiteLowerBound) {
                // a <= x <= b or a <= x
                // => x = a + x' where x' >= 0
                int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                state.c.set(auxVariableId, w);
                state.objConst += w*x.lowerBound;
            } else if (finiteUpperBound) {
                // x <= b
                // => x = b - x' where x' >= 0
                int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                state.c.set(auxVariableId, -w);
                state.objConst += w*x.upperBound;
            } else {
                // x is an unbounded real number
                // => x = x1 - x2 where x1, x2 >= 0
                int x1 = x.getAuxiliaryVariableIds().get(0);
                int x2 = x.getAuxiliaryVariableIds().get(1);
                state.c.set(x1, w);
                state.c.set(x2, -w);
            }
        }
    }

    public void buildStandardForm() {
        assert objective != null;
        // Number of variables needed to represent linear program in standard form
        int n = addAuxiliaryVariables();
        computedStandardForm = new StandardForm(n);
        addVariableConstraints(computedStandardForm);
        for (Constraint c : userConstraints) {
            addConstraint(c, computedStandardForm);
        }
        addObjectiveFunction(computedStandardForm);

        System.out.println(computedStandardForm.prettyPrint());
    }

}
