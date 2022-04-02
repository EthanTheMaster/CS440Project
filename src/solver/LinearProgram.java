package solver;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class LinearProgram {
    private final ArrayList<Variable> userVariables;
    private final ArrayList<Constraint> userConstraints;
    private ObjectiveFunction objective;

    private Solution currentSolution;

    public LinearProgram() {
        userVariables = new ArrayList<>();
        userConstraints = new ArrayList<>();
    }

    /**
     * Invalidates the current solution for the linear program
     */
    private void evictCurrentSolution() {
        currentSolution = null;
    }

    /**
     * Adds a new variable to the linear program
     * @param name Name of the variable
     * @param lowerBound Lower bound on the variable
     * @param upperBound Upper bound on the variable
     * @return A Variable linked to the linear program
     */
    public Variable registerVariable(String name, double lowerBound, double upperBound) {
        evictCurrentSolution();
        Variable res = new Variable(name, lowerBound, upperBound);
        userVariables.add(res);
        return res;
    }

    /**
     * Adds a variable to the linear program that can only take on
     * nonnegative values
     * @param name Name of the variable
     * @return A Variable linked to the linear program
     */
    public Variable registerNonnegativeVariable(String name) {
        evictCurrentSolution();
        Variable res = new Variable(name);
        userVariables.add(res);
        return res;
    }

    /**
     * Adds a new variable to the linear program that can take on
     * any value
     * @param name Name of the variable
     * @return A Variable linked to the linear program
     */
    public Variable registerUnboundedVariable(String name) {
        evictCurrentSolution();
        Variable res = new Variable(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        userVariables.add(res);
        return res;
    }

    /**
     * Adds a new constraint to the linear program
     * @param c The constraint to add
     */
    public void addConstraint(Constraint c) {
        evictCurrentSolution();
        userConstraints.add(c);
    }

    /**
     * Sets the objective function for the linear program
     * @param objective The objective function to use
     */
    public void setObjective(ObjectiveFunction objective) {
        evictCurrentSolution();
        this.objective = objective;
    }

    /**
     * The linear program as currently configured may involve variables
     * that do not conform to the rules of standard form. Some variables
     * may not be nonnegative variables. We need to create auxiliary
     * variables that can only take on nonnegative values and express
     * nonconforming variables in terms of these auxiliary variables.
     * We also create "auxiliary" variables for conforming variables
     * as those will be the new variables used in the standard form
     * representation.
     * @return The number of variables created for the standard form
     * representation
     */
    private int addAuxiliaryVariables() {
        // Start fresh with variables
        for (Variable x : userVariables) {
            x.getAuxiliaryVariableIds().clear();
        }

        // Add auxiliary variables
        int numVariables = 0;
        for (Variable x : userVariables) {
            if (Double.isInfinite(x.getLowerBound()) && Double.isInfinite(x.getUpperBound())) {
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

    /**
     * Variables that have a finite lower and finite upper bound require
     * an auxiliary constraint added into the standard form linear program
     * to ensure we get a proper solution
     * @param state Standard form linear program being constructed
     */
    private void addVariableConstraints(StandardForm state) {
        for (Variable x : userVariables) {
            if (Double.isFinite(x.getLowerBound()) && Double.isFinite(x.getUpperBound())) {
                // a <= x <= b => 0 <= x' <= b - a
                // where x' = x - a
                int i = state.addEmptyConstraint();
                int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                // x' by construction satisfies the nonnegativity constraint
                // 0 <= x so we add the other constraint x' <= b - a
                state.updateA(i, auxVariableId, 1.0);
                state.b.set(i, x.getUpperBound() - x.getLowerBound());
            }
        }
    }

    /**
     * The linear program as currently configured may have constraints
     * that do not conform to the standard form rules. We need to
     * reexpress each constraint to not only be in terms of a
     * less than or equals constraint but also to be in terms
     * of the auxiliary variables created. Thus all user supplied
     * variables in the constraints must be substituted with
     * equivalent expressions using nonnegative auxiliary variable(s).
     * @param c The constraint to be added into the standard form linear
     *          program
     * @param state The standard form linear program being constructed
     */
    private void addConstraint(Constraint c, StandardForm state) {
        if (c.getRelation() == Relation.LEQ) {
            // Logic for adding constraint is only added for the <= case
            int i = state.addEmptyConstraint();
            state.b.set(i, c.getB());

            for (int k = 0; k < c.getVariables().size(); k++) {
                Variable x = c.getVariables().get(k);
                double w = c.getWeights().get(k);

                boolean finiteLowerBound = Double.isFinite(x.getLowerBound());
                boolean finiteUpperBound = Double.isFinite(x.getUpperBound());
                // Perform substitution of variable x in terms of auxiliary variables
                if (finiteLowerBound) {
                    // a <= x <= b or a <= x
                    // => x = a + x' where x' >= 0
                    int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                    state.updateA(i, auxVariableId, w);
                    state.b.set(i, state.b.get(i) - w * x.getLowerBound());
                } else if (finiteUpperBound) {
                    // x <= b
                    // => x = b - x' where x' >= 0
                    int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                    state.updateA(i, auxVariableId, -w);
                    state.b.set(i, state.b.get(i) - w * x.getUpperBound());
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
                    new ArrayList<>(c.getWeights().stream().map(w -> -w).collect(Collectors.toList())),
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

    /**
     * Objective function provided by the user needs to be modified to be
     * in terms of a maximization and be in terms of nonnegative auxiliary variables
     * @param state The standard form linear program being constructed
     */
    private void addObjectiveFunction(StandardForm state) {
        state.objConst = 0;
        // Minimization is the same as negating the objective function and maximizing
        int sign = (objective.getGoal() == ObjectiveGoal.MAXIMIZE) ? 1 : -1;
        for (int i = 0; i < objective.getObjectiveVariables().size(); i++) {
            Variable x = objective.getObjectiveVariables().get(i);
            double w = sign * objective.getObjectiveWeights().get(i);

            boolean finiteLowerBound = Double.isFinite(x.getLowerBound());
            boolean finiteUpperBound = Double.isFinite(x.getUpperBound());
            // Perform substitution of variable x in terms of auxiliary variables
            if (finiteLowerBound) {
                // a <= x <= b or a <= x
                // => x = a + x' where x' >= 0
                int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                state.c.set(auxVariableId, w);
                state.objConst += w*x.getLowerBound();
            } else if (finiteUpperBound) {
                // x <= b
                // => x = b - x' where x' >= 0
                int auxVariableId = x.getAuxiliaryVariableIds().get(0);
                state.c.set(auxVariableId, -w);
                state.objConst += w*x.getUpperBound();
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

    /**
     * Constructs a linear program in standard form
     * @return A standard form representation of the current linear program
     * @exception RuntimeException if there is no objective function set
     */
    public StandardForm buildStandardForm() {
        if (objective == null) {
            throw new RuntimeException("Objective function must be specified.");
        }
        // Number of variables needed to represent linear program in standard form
        int n = addAuxiliaryVariables();
        StandardForm state = new StandardForm(n);
        addVariableConstraints(state);
        for (Constraint c : userConstraints) {
            addConstraint(c, state);
        }
        addObjectiveFunction(state);

        return state;
    }

    /**
     * Solves the linear program
     */
    public void solve() {
        StandardForm standardForm = buildStandardForm();
        SimplexState simplexState = new SimplexState(standardForm);
        // This should not be exposed to users as the solution
        // is in terms of auxiliary variables which are meaningless
        // to users.
        currentSolution = simplexState.solve();
    }

    /**
     * Evaluates the value of a linear program variable in a solution
     * @param x The variable to be evaluated
     * @return The value of the variable if the linear program
     * has a finite feasible solution and None if the linear program
     * has an unbounded solution or is infeasible
     */
    public Optional<Double> evaluateVariable(Variable x) {
        // Solve an unsolved linear program
        if (currentSolution == null) {
            solve();
        }

        if (currentSolution.getStatus() != SolutionResult.FEASIBLE) {
            return Optional.empty();
        }

        // The solution is feasible so reconstruct variable value from auxiliary
        // variable values
        boolean finiteLowerBound = Double.isFinite(x.getLowerBound());
        boolean finiteUpperBound = Double.isFinite(x.getUpperBound());
        // Perform substitution of variable x in terms of auxiliary variables
        if (finiteLowerBound) {
            // a <= x <= b or a <= x
            // => x = a + x' where x' >= 0
            int aux = x.getAuxiliaryVariableIds().get(0);
            double auxValue = currentSolution.getSolution().get(aux);
            double res = x.getLowerBound() + auxValue;
            return Optional.of(res);
        } else if (finiteUpperBound) {
            // x <= b
            // => x = b - x' where x' >= 0
            int aux = x.getAuxiliaryVariableIds().get(0);
            double auxValue = currentSolution.getSolution().get(aux);
            double res = x.getUpperBound() - auxValue;
            return Optional.of(res);
        } else {
            // x is an unbounded real number
            // => x = x1 - x2 where x1, x2 >= 0
            int x1 = x.getAuxiliaryVariableIds().get(0);
            int x2 = x.getAuxiliaryVariableIds().get(1);
            double x1Value = currentSolution.getSolution().get(x1);
            double x2Value = currentSolution.getSolution().get(x2);

            double res = x1Value - x2Value;
            return Optional.of(res);
        }
    }

    /**
     * Computes the objective function value in a linear program
     * solution
     * @return the objective function if the the linear program is
     * feasible and None otherwise
     */
    public Optional<Double> getObjectiveValue() {
        // Solve an unsolved linear program
        if (currentSolution == null) {
            solve();
        }

        if (currentSolution.getStatus() == SolutionResult.INFEASIBLE) {
            return Optional.empty();
        } else {
            if (objective.getGoal() == ObjectiveGoal.MAXIMIZE) {
                return Optional.of(currentSolution.getObjectiveValue());
            } else {
                // Simplex algorithm does maximization and we turned our minimization
                // problem into a maximization one by negating the objective function
                return Optional.of(-currentSolution.getObjectiveValue());
            }
        }
    }
}
