public class Main {

    public static void main(String[] args) {
        int[][] demoMatrix = new int[][]{
                {2, 3, 4, 8},
                {5, 7, 9, 12},
                {1, 0, 6, 10}
        };
        String result = MatrixPrinter.arrayClockWise(demoMatrix);
        System.out.println("Clockwize matrix result: "+result);
    }
}