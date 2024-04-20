public class Main {

    public static void main(String[] args) {
        int[][] demoMatrix = new int[][]{
                {1, 2, 3, 4, 10},
                {10, 11, 12, 5, 20},
                {10, 77, 88, 99, 20},
                {9, 8, 7, 6, 30}
        };
        String result = MatrixPrinter.arrayClockWise(demoMatrix);
        System.out.println(result);
    }
}