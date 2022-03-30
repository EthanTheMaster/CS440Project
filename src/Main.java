import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        test1();
//        test2();
//        test3();
//        test4();
    }

    private static void test1() {
        // Checks slack form against example on CLRS pp. 855
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerUnboundedVariable("x2");

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(1.0, 1.0)),
                Relation.EQ,
                7
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(1.0, -2.0)),
                Relation.LEQ,
                4
        ));

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MINIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(-2.0, 3.0))
        ));

        System.out.println("------------------------------ Standard Form ------------------------------");
        StandardForm form = p.buildStandardForm();
        System.out.println(form.prettyPrint());
        System.out.println(x1.getAuxiliaryVariableIds());
        System.out.println(x2.getAuxiliaryVariableIds());
        System.out.println("------------------------------ Slack Form ------------------------------");
        SimplexState simplex = new SimplexState(form);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Solution ------------------------------");
        p.solve();
        System.out.println(String.format("Objective Value: %s", p.getObjectiveValue()));
        System.out.println(String.format("x1: %s", p.evaluateVariable(x1).toString()));
        System.out.println(String.format("x2: %s", p.evaluateVariable(x2).toString()));
    }

    private static void test2() {
        // Test pivot against example on CLRS pp. 865-868
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerNonnegativeVariable("x2");
        Variable x3 = p.registerNonnegativeVariable("x3");

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, 3.0)),
                Relation.LEQ,
                30
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(2.0, 2.0, 5.0)),
                Relation.LEQ,
                24
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(4.0, 1.0, 2.0)),
                Relation.LEQ,
                36
        ));

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(3.0, 1.0, 2.0))
        ));

        System.out.println("------------------------------ Standard Form ------------------------------");
        StandardForm form = p.buildStandardForm();
        System.out.println(form.prettyPrint());
        System.out.println("------------------------------ Slack Form ------------------------------");
        SimplexState simplex = new SimplexState(form);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Pivot 1 ------------------------------");
        // CLRS Entering and leaving variables were x1 and x6 but when 0-indexed are x0 and x5
        simplex.pivot(0, 5);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Pivot 2 ------------------------------");
        // CLRS Entering and leaving variables were x3 and x5 but when 0-indexed are x2 and x4
        simplex.pivot(2, 4);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Pivot 3 ------------------------------");
        // CLRS Entering and leaving variables were x2 and x3 but when 0-indexed are x1 and x2
        simplex.pivot(1, 2);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Solution ------------------------------");
        p.solve();
        System.out.println(String.format("Objective Value: %s", p.getObjectiveValue()));
        System.out.println(String.format("x1: %s", p.evaluateVariable(x1).toString()));
        System.out.println(String.format("x2: %s", p.evaluateVariable(x2).toString()));
        System.out.println(String.format("x3: %s", p.evaluateVariable(x3).toString()));
    }

    private static void test3() {
        // Test simplex algorithm with basic solution against
        // example on CLRS pp. 865-868
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerNonnegativeVariable("x2");
        Variable x3 = p.registerNonnegativeVariable("x3");

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, 3.0)),
                Relation.LEQ,
                30
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(2.0, 2.0, 5.0)),
                Relation.LEQ,
                24
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(4.0, 1.0, 2.0)),
                Relation.LEQ,
                36
        ));

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2, x3)),
                new ArrayList<>(Arrays.asList(3.0, 1.0, 2.0))
        ));

        System.out.println("------------------------------ Standard Form ------------------------------");
        StandardForm form = p.buildStandardForm();
        System.out.println(form.prettyPrint());
        System.out.println("------------------------------ Slack Form ------------------------------");
        SimplexState simplex = new SimplexState(form);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ After Simplex ------------------------------");
        simplex.simplexPivot();
        System.out.println(simplex.prettyPrint());
    }

    private static void test4() {
        // Test initialize simplex procedure against example on CLRS
        // pp. 888-890
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerNonnegativeVariable("x2");

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(2.0, -1.0))
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(2.0, -1.0)),
                Relation.LEQ,
                2.0
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(1.0, -5.0)),
                Relation.LEQ,
                -4.0
        ));

        System.out.println("------------------------------ Standard Form ------------------------------");
        StandardForm form = p.buildStandardForm();
        System.out.println(form.prettyPrint());
        System.out.println("------------------------------ Slack Form ------------------------------");
        SimplexState simplex = new SimplexState(form);
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Initialize Simplex ------------------------------");
        simplex.initializeSimplex();
        System.out.println(simplex.prettyPrint());
        System.out.println("------------------------------ Solution ------------------------------");
        p.solve();
        System.out.println(String.format("Objective Value: %s", p.getObjectiveValue()));
        System.out.println(String.format("x1: %s", p.evaluateVariable(x1).toString()));
        System.out.println(String.format("x2: %s", p.evaluateVariable(x2).toString()));
    }
}
