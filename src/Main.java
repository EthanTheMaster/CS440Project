import test.CLRSSimplexTest;
import test.LinearProgramTest;
import test.StressTester;

public class Main {
    public static void main(String[] args) {
//        LinearProgramTest.runAllTests();
        int rounds = 100;
        long totalTime = 0;
        for (int i = 0; i < rounds; i++) {
            long time = 0;
            try {
                time = StressTester.maxFlow(10, 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(time);
            totalTime += time;
        }
        System.out.println((double) totalTime / (double) rounds + " ms");
    }

}
