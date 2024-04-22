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
        String recursiveResult = MatrixPrinter.recursiveArrayClockWise(demoMatrix);
        String iterativeResult = MatrixPrinter.iterativeArrayClockWise(demoMatrix);
        String expected = "11111, 2, 3, 4, 10, 20, 30, 40, 50, 6, 7, 8, 9, 9, 10, 10, 11, 12, 5, 99, 6, 7, 8, 77, 999999";
        assertEquals(recursiveResult, expected);
        assertEquals(iterativeResult, expected);
    }

    @Test()
    void shouldHandleRectangleMatrix() {
        int[][] fourByFiveMatrix = new int[][]{
                {11111,  2,  3, 4},
                {10, 11, 12, 5},
                {10, 9999, 89, 99},
                {9, 8, 7, 6},
                {9, 8, 7, 6}
        };
        String recursiveResult = MatrixPrinter.recursiveArrayClockWise(fourByFiveMatrix);
        String iterativeResult = MatrixPrinter.iterativeArrayClockWise(fourByFiveMatrix);
        String expected = "11111, 2, 3, 4, 5, 99, 6, 6, 7, 8, 9, 9, 10, 10, 11, 12, 89, 7, 8, 9999";
        assertEquals(recursiveResult, expected);
        assertEquals(iterativeResult, expected);
    }

    @Test()
    void shouldOneLine() {
        int[][] oneLineMatrix = new int[][]{
                {11111, 2, 3, 99999}
        };
        String recursiveResult = MatrixPrinter.recursiveArrayClockWise(oneLineMatrix);
        String iterativeResult = MatrixPrinter.iterativeArrayClockWise(oneLineMatrix);
        String expected = "11111, 2, 3, 99999";
        assertEquals(recursiveResult, expected);
        assertEquals(iterativeResult, expected);
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
        String recursiveResult = MatrixPrinter.recursiveArrayClockWise(oneColumnMatrix);
        String iterativeResult = MatrixPrinter.iterativeArrayClockWise(oneColumnMatrix);
        String expected = "11111, 1, 2, 3, 9999";
        assertEquals(recursiveResult, expected);
        assertEquals(iterativeResult, expected);
    }

    @Test()
    void shouldHandleEmptyMatrix() {
        int[][] emptyMatrix = new int[][]{};
        String recursiveResult = MatrixPrinter.recursiveArrayClockWise(emptyMatrix);
        String iterativeResult = MatrixPrinter.iterativeArrayClockWise(emptyMatrix);
        String expected = "";
        assertEquals(recursiveResult, expected);
        assertEquals(iterativeResult, expected);
    }
}