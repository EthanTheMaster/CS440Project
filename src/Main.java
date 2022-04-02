import test.LinearProgramTest;
import test.StressTester;

public class Main {
    public static void main(String[] args) {
//        LinearProgramTest.runAllTests();
        long time = StressTester.maxFlow(10, 10);
        System.out.println(time + " ms");;
    }

}
