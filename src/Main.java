public class Main {

    public static void main(String[] args) {
        int[][] demoMatrix = new int[][]{
                {11111, 2, 3, 4, 10},
                {10, 11, 12, 5, 20},
                {10, 77, 999999, 99, 30},
                {9, 15, 16, 17, 40},
                {9, 8, 7, 6, 50}
        };
        String iterativeArrayClockWise = MatrixPrinter.iterativeArrayClockWise(demoMatrix);
        String recursiveArrayClockWize = MatrixPrinter.recursiveArrayClockWise(demoMatrix);
        System.out.println("Iterative result: "+iterativeArrayClockWise);
        System.out.println("Recursive result: "+recursiveArrayClockWize);
    }
}