package test;

import solver.*;

import java.util.ArrayList;
import java.util.Arrays;

public class LinearProgramTest {
    private final static double EPSILON = 0.0000001;

    private static void printTestStatus(String testName, boolean pass) {
        if (pass) {
            System.out.println(testName + ": PASS");
        } else {
            System.out.println(testName + ": FAIL");
        }
    }

    // Reference: Mathematical Applications For The Management, Life,
    // And Social Sciences 12 ed. by Ronald J Harshbarger and
    // James J Reynolds
    //
    // Chapter 4 Review Exercises Question 33
    public static void test1() {
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerNonnegativeVariable("x2");
        Variable x3 = p.registerNonnegativeVariable("x3");
        Variable x4 = p.registerNonnegativeVariable("x4");

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(88.0, 86.0, 100.0, 100.0))
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(3.0, 2.0, 2.0, 5.0)),
                Relation.LEQ,
                200.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(2.0, 2.0, 4.0, 5.0)),
                Relation.LEQ,
                100.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 1.0)),
                Relation.LEQ,
                200.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1)),
                new ArrayList<>(Arrays.asList(1.0)),
                Relation.LEQ,
                40.0
        ));

        p.solve();
        boolean passed =
            Math.abs(p.getObjectiveValue().get() - 4380) < EPSILON &&
            Math.abs(p.evaluateVariable(x1).get() - 40) < EPSILON &&
            Math.abs(p.evaluateVariable(x2).get() - 10) < EPSILON &&
            Math.abs(p.evaluateVariable(x3).get() - 0) < EPSILON &&
            Math.abs(p.evaluateVariable(x4).get() - 0) < EPSILON;
        printTestStatus("Test 1", passed);
    }

    // Reference: Mathematical Applications For The Management, Life,
    // And Social Sciences 12 ed. by Ronald J Harshbarger and
    // James J Reynolds
    //
    // Chapter 4 Review Exercises Question 34
    public static void test2() {
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerNonnegativeVariable("x2");
        Variable x3 = p.registerNonnegativeVariable("x3");
        Variable x4 = p.registerNonnegativeVariable("x4");

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(8.0, 8.0, 12.0, 14.0))
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(6.0, 3.0, 2.0, 1.0)),
                Relation.GEQ,
                350.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(3.0, 2.0, 5.0, 6.0)),
                Relation.LEQ,
                300.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(8.0, 3.0, 2.0, 1.0)),
                Relation.LEQ,
                400.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 1.0)),
                Relation.LEQ,
                100.0
        ));

        p.solve();
        boolean passed =
                Math.abs(p.getObjectiveValue().get() - 900) < EPSILON &&
                Math.abs(p.evaluateVariable(x1).get() - 25) < EPSILON &&
                Math.abs(p.evaluateVariable(x2).get() - 50) < EPSILON &&
                Math.abs(p.evaluateVariable(x3).get() - 25) < EPSILON &&
                Math.abs(p.evaluateVariable(x4).get() - 0) < EPSILON;
        printTestStatus("Test 2", passed);
    }

    // Reference: Mathematical Applications For The Management, Life,
    // And Social Sciences 12 ed. by Ronald J Harshbarger and
    // James J Reynolds
    //
    // Chapter 4 Review Exercises Question 35
    public static void test3() {
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerNonnegativeVariable("x1");
        Variable x2 = p.registerNonnegativeVariable("x2");
        Variable x3 = p.registerNonnegativeVariable("x3");
        Variable x4 = p.registerNonnegativeVariable("x4");

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MINIMIZE,
                new ArrayList<>(Arrays.asList(x1, x2, x3, x4)),
                new ArrayList<>(Arrays.asList(10.0, 9.0, 12.0, 8.0))
        ));

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x3)),
                new ArrayList<>(Arrays.asList(45.0, 58.5)),
                Relation.GEQ,
                4680.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x2, x4)),
                new ArrayList<>(Arrays.asList(36.0, 31.5)),
                Relation.GEQ,
                4230.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(1.0, 1.0)),
                Relation.LEQ,
                100.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x3, x4)),
                new ArrayList<>(Arrays.asList(1.0, 1.0)),
                Relation.LEQ,
                100.0
        ));

        p.solve();
        boolean passed =
                Math.abs(p.getObjectiveValue().get() - 2020) < EPSILON &&
                        Math.abs(p.evaluateVariable(x1).get() - 0) < EPSILON &&
                        Math.abs(p.evaluateVariable(x2).get() - 100) < EPSILON &&
                        Math.abs(p.evaluateVariable(x3).get() - 80) < EPSILON &&
                        Math.abs(p.evaluateVariable(x4).get() - 20) < EPSILON;
        printTestStatus("Test 3", passed);
    }

    public static void test4() {
        LinearProgram p = new LinearProgram();
        Variable corn = p.registerNonnegativeVariable("corn");
        Variable soybeans = p.registerNonnegativeVariable("soybeans");

        // Fertilizer/herbicide constraint
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(corn, soybeans)),
                new ArrayList<>(Arrays.asList(9.0, 3.0)),
                Relation.LEQ,
                40500
        ));

        // Labor constraint
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(corn, soybeans)),
                new ArrayList<>(Arrays.asList(3.0/4.0, 1.0)),
                Relation.LEQ,
                5250
        ));

        // Land constraint
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(corn, soybeans)),
                new ArrayList<>(Arrays.asList(1.0, 1.0)),
                Relation.LEQ,
                6000
        ));

        // Profit objective
        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(corn, soybeans)),
                new ArrayList<>(Arrays.asList(240.0, 160.0))
        ));
        p.solve();
        boolean passed =
                Math.abs(p.getObjectiveValue().get() - 1260000.0) < EPSILON
                && Math.abs(p.evaluateVariable(corn).get() - 3750.0) < EPSILON
                && Math.abs(p.evaluateVariable(soybeans).get() - 2250.0) < EPSILON;
        printTestStatus("Test 4", passed);
    }

    // Reference: CLRS ed 3 Section 24.4 Example (Inequalities 24.3 - 24.10)
    public static void differenceConstraintTest1() {
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerUnboundedVariable("x1");
        Variable x2 = p.registerUnboundedVariable("x2");
        Variable x3 = p.registerUnboundedVariable("x3");
        Variable x4 = p.registerUnboundedVariable("x4");
        Variable x5 = p.registerUnboundedVariable("x5");

        // Empty objective function
        p.setObjective(ObjectiveFunction.empty());

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                0.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x5)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -1.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x2, x5)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                1.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x3, x1)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                5.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x4, x1)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                4.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x4, x3)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -1.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x5, x3)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -3.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x5, x4)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -3.0
        ));

        p.solve();

        double x1Value = p.evaluateVariable(x1).get();
        double x2Value = p.evaluateVariable(x2).get();
        double x3Value = p.evaluateVariable(x3).get();
        double x4Value = p.evaluateVariable(x4).get();
        double x5Value = p.evaluateVariable(x5).get();
        // Feasibility test
        boolean passed =
                Math.abs(p.getObjectiveValue().get() - 0.0) < EPSILON &&
                p.getSolutionStatus() == SolutionResult.FEASIBLE &&
                x1Value - x2Value <= 0 &&
                x1Value - x5Value <= -1 &&
                x2Value - x5Value <= 1 &&
                x3Value - x1Value <= 5 &&
                x4Value - x1Value <= 4 &&
                x4Value - x3Value <= -1 &&
                x5Value - x3Value <= -3 &&
                x5Value - x4Value <= -3;

        printTestStatus("Difference Constraint Test 1", passed);
    }

    // Reference: CLRS ed 3 Exercise 24.4-2
    public static void differenceConstraintTest2() {
        LinearProgram p = new LinearProgram();
        Variable x1 = p.registerUnboundedVariable("x1");
        Variable x2 = p.registerUnboundedVariable("x2");
        Variable x3 = p.registerUnboundedVariable("x3");
        Variable x4 = p.registerUnboundedVariable("x4");
        Variable x5 = p.registerUnboundedVariable("x5");

        // Empty objective function
        p.setObjective(ObjectiveFunction.empty());

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x2)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                4.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x1, x5)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                5.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x2, x4)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -6.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x3, x2)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                1.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x4, x1)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                3.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x4, x3)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                5.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x4, x5)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                10.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x5, x3)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -4.0
        ));
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x5, x4)),
                new ArrayList<>(Arrays.asList(1.0, -1.0)),
                Relation.LEQ,
                -8.0
        ));

        p.solve();

        // Feasibility test ... Problem should be infeasible
        boolean passed =
                p.getObjectiveValue().isEmpty() &&
                p.getSolutionStatus() == SolutionResult.INFEASIBLE;

        printTestStatus("Difference Constraint Test 2", passed);
    }

    // Reference: CLRS ed 3 Example in Figure 26.4
    public static void maxFlowTest1() {
        LinearProgram p = new LinearProgram();
        Variable f_s_v1 = p.registerVariable("f_s_v1", 0.0, 16.0);
        Variable f_s_v2 = p.registerVariable("f_s_v2", 0.0, 13.0);
        Variable f_v1_v3 = p.registerVariable("f_v1_v3", 0.0, 12.0);
        Variable f_v2_v1 = p.registerVariable("f_v2_v1", 0.0, 4.0);
        Variable f_v2_v4 = p.registerVariable("f_v2_v4", 0.0, 14.0);
        Variable f_v3_v2 = p.registerVariable("f_v3_v2", 0.0, 9.0);
        Variable f_v3_t = p.registerVariable("f_v3_t", 0.0, 20.0);
        Variable f_v4_v3 = p.registerVariable("f_v4_v3", 0.0, 7.0);
        Variable f_v4_t = p.registerVariable("f_v4_t", 0.0, 4.0);

        // Maximize flow
        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(f_s_v1, f_s_v2)),
                new ArrayList<>(Arrays.asList(1.0, 1.0))
        ));

        // Conserve flow at v1
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(f_s_v1, f_v2_v1, f_v1_v3)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, -1.0)),
                Relation.EQ,
                0.0
        ));
        // Conserve flow at v2
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(f_s_v2, f_v3_v2, f_v2_v1, f_v2_v4)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, -1.0, -1.0)),
                Relation.EQ,
                0.0
        ));
        // Conserve flow at v3
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(f_v1_v3, f_v4_v3, f_v3_v2, f_v3_t)),
                new ArrayList<>(Arrays.asList(1.0, 1.0, -1.0, -1.0)),
                Relation.EQ,
                0.0
        ));
        // Conserve flow at v4
        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(f_v2_v4, f_v4_v3, f_v4_t)),
                new ArrayList<>(Arrays.asList(1.0, -1.0, -1.0)),
                Relation.EQ,
                0.0
        ));

        p.solve();

        boolean passed = Math.abs(p.getObjectiveValue().get() - 23.0) < EPSILON;
        printTestStatus("Maximum Flow Test 1", passed);
    }

    public static void stressTest1() {
        long time = 0;
        boolean passed = true;
        try {
            time = StressTester.maxFlow(10, 10);
        } catch (Exception e) {
            passed = false;
            e.printStackTrace();
        }
        printTestStatus("Stress Test (n = k = 10) in " + time + " ms", passed);
    }

    public static void runAllTests() {
        LinearProgramTest.test1();
        LinearProgramTest.test2();
        LinearProgramTest.test3();
        LinearProgramTest.test4();
        LinearProgramTest.differenceConstraintTest1();
        LinearProgramTest.differenceConstraintTest2();
        LinearProgramTest.maxFlowTest1();
        LinearProgramTest.stressTest1();
    }
}
