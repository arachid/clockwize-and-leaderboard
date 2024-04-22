import java.util.function.Function;

public class MatrixPrinter {

    /***
     *
     * @param matrix to print
     * @return a string with all number in the matrix displayed in a clockwize manner
     */
    public static String recursiveArrayClockWise(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return "";
        }
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        return recursiveArrayClockWise(matrix, 0, 0, visited);
    }

    private static String recursiveArrayClockWise(int[][] matrix, int i, int j, boolean[][] visited) {
        String current = matrix[i][j]+"";
        visited[i][j] = true;
        if (canGo(matrix, i, j+1, visited) && !canGo(matrix, i-1, j, visited)) { // check right and go right if possible
            return current+", "+ recursiveArrayClockWise(matrix, i, j+1, visited);
        } else if(canGo(matrix, i+1, j, visited)) { // check bottom and go bottom if possible
            return current+", "+ recursiveArrayClockWise(matrix, i+1, j, visited);
        } else if(canGo(matrix, i, j-1, visited)) { // check left and go left if possible
            return current+", "+ recursiveArrayClockWise(matrix, i, j-1, visited);
        } else if(canGo(matrix, i-1, j, visited)) { // check left and go left if possible
            return current+", "+ recursiveArrayClockWise(matrix, i-1, j, visited);
        } else { // if no way to go, return current value only
            return current;
        }
    }
    private static boolean canGo(int[][] matrix, int i, int j, boolean[][] visited) {
        return i >= 0 && i < matrix.length && j >= 0 && j < matrix[0].length && !visited[i][j];
    }



    /***
     *
     * @param matrix to print
     * @return a string with all number in the matrix displayed in a clockwize manner
     */
    public static String iterativeArrayClockWise(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return "";
        }

        int topBoundary = -1;
        int rightBoundary = matrix[0].length;
        int bottomBoundary = matrix.length;
        int leftBoundary = -1;

        boolean goingRight = true;
        boolean goingBottom = false;
        boolean goingLeft = false;
        boolean goingTop = false;

        int row = 0;
        int col = 0;

        // Using a string builder since it's more performant when having to concatenate multiple times.
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;


        int numberOfElements = matrix[0].length * matrix.length;
        while (i < numberOfElements) {
            if (i != 0) { // ignore comma on first iteration
                stringBuilder.append(", ");
            }
            stringBuilder.append(matrix[row][col]);
            if (goingRight) {
                if (col+1 < rightBoundary) {
                    col++;
                } else {
                    topBoundary++;
                    goingRight = false;
                    goingBottom = true;
                    row++;
                }
            } else if (goingBottom) {
                if (row+1 < bottomBoundary) {
                    row++;
                } else {
                    rightBoundary--;
                    goingBottom = false;
                    goingLeft = true;
                    col--;
                }
            } else if (goingLeft) {
                if (col-1 > leftBoundary) {
                    col--;
                } else {
                    bottomBoundary--;
                    goingLeft = false;
                    goingTop = true;
                    row--;
                }
            } else if (goingTop) {
                if (row-1 > topBoundary) {
                    row--;
                } else {
                    leftBoundary++;
                    goingTop = false;
                    goingRight = true;
                    col++;
                }
            }
            i++;
        }
        return stringBuilder.toString();
    }

}
