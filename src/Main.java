import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        LinearProgram p = new LinearProgram();
//        Variable x = p.registerVariable("x", -10.0, 10.0);
//        Variable x = p.registerNonnegativeVariable("x");
//        Variable x = p.registerUnboundedVariable("x");
        Variable x = p.registerVariable("x", Double.NEGATIVE_INFINITY, 10.0);

        p.addConstraint(new Constraint(
                new ArrayList<>(Arrays.asList(x)),
                new ArrayList<>(Arrays.asList(5.0)),
                Relation.EQ,
                7
        ));

        p.setObjective(new ObjectiveFunction(
                ObjectiveGoal.MAXIMIZE,
                new ArrayList<>(Arrays.asList(x)),
                new ArrayList<>(Arrays.asList(-3.0))
        ));

        p.buildStandardForm();
        System.out.println(x.getAuxiliaryVariableIds());
    }
}
