import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MatrixPrinterTest {

    @Test()
    void shouldHandleSquareMatrix() {
        int[][] demoMatrix = new int[][]{
                {11111, 2, 3, 4, 10},
                {10, 11, 12, 5, 20},
                {10, 77, 999999, 99, 30},
                {9, 8, 7, 6, 40},
                {9, 8, 7, 6, 50}
        };
        String result = MatrixPrinter.arrayClockWise(demoMatrix);
        assertEquals(result,"11111,2,3,4,10,20,30,40,50,6,7,8,9,9,10,10,11,12,5,99,6,7,8,77,999999");
    }

    @Test()
    void shouldHandleRectangleMatrix() {
        int[][] fourByFiveMatrix = new int[][]{
                {11111, 2, 3, 4},
                {10, 11, 12, 5},
                {10, 9999, 89, 99},
                {9, 8, 7, 6},
                {9, 8, 7, 6}
        };
        String result = MatrixPrinter.arrayClockWise(fourByFiveMatrix);
        assertEquals(result,"11111,2,3,4,5,99,6,6,7,8,9,9,10,10,11,12,89,7,8,9999");
    }

    @Test()
    void shouldOneLine() {
        int[][] oneLineMatrix = new int[][]{
                {11111, 2, 3, 99999}
        };
        String result = MatrixPrinter.arrayClockWise(oneLineMatrix);
        assertEquals(result,"11111,2,3,99999");
    }

    @Test()
    void shouldHandleOneColumn() {
        int[][] oneColumnMatrix = new int[][]{
                {11111},
                {1},
                {2},
                {3},
                {9999}
        };
        String result = MatrixPrinter.arrayClockWise(oneColumnMatrix);
        assertEquals(result,"11111,1,2,3,9999");
    }

    @Test()
    void shouldHandleEmptyMatrix() {
        int[][] oneColumnMatrix = new int[][]{};
        String result = MatrixPrinter.arrayClockWise(oneColumnMatrix);
        assertEquals(result,"");
    }
}