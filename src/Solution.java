import java.util.Optional;

public class Solution {
    public static final int STATUS_SOLVABLE = 0;
    public static final int STATUS_UNBOUNDED = 1;
    public static final int STATUS_INFEASIBLE = 2;

    private int status;
    private Optional<Double> solution;
}
