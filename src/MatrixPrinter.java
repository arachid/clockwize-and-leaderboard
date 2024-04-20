public class MatrixPrinter {

    /***
     *
     * @param matrix to print
     * @return a string with all number in the matrix displayed in a clockwize manner
     */
    public static String arrayClockWise(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return "";
        }
        boolean[][] visited = new boolean[matrix.length][matrix[0].length];
        return arrayClockWise(matrix, 0, 0, visited);
    }

    private static String arrayClockWise(int[][] matrix, int i, int j, boolean[][] visited) {
        String current = matrix[i][j]+"";
        visited[i][j] = true;
        if (canGo(matrix, i, j+1, visited) && !canGo(matrix, i-1, j, visited)) { // check right and go right if possible
            return current+","+arrayClockWise(matrix, i, j+1, visited);
        } else if(canGo(matrix, i+1, j, visited)) { // check bottom and go bottom if possible
            return current+","+arrayClockWise(matrix, i+1, j, visited);
        } else if(canGo(matrix, i, j-1, visited)) { // check left and go left if possible
            return current+","+arrayClockWise(matrix, i, j-1, visited);
        } else if(canGo(matrix, i-1, j, visited)) { // check left and go left if possible
            return current+","+arrayClockWise(matrix, i-1, j, visited);
        } else { // if no way to go, return current value only
            return current;
        }
    }
    private static boolean canGo(int[][] matrix, int i, int j, boolean[][] visited) {
        return i >= 0 && i < matrix.length && j >= 0 && j < matrix[0].length && !visited[i][j];
    }

}
