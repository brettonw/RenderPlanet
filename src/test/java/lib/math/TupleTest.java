package lib.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class TupleTest {
    @Test
    public void test() {
        assertArrayEquals("Check Tuple 'make' - A", new double[]{0, 0, 0}, Tuple.make(3), 0);
        assertArrayEquals("Check Tuple 'make' - B", new double[]{1, 2, 3}, Tuple.make(1, 2, 3), 0);

        assertArrayEquals("Check Tuple 'fill'", new double[]{4, 4, 4}, Tuple.fill(3, 4), 0);

        assertArrayEquals("Check Tuple 'copy'", Tuple.make(1, 2, 3), Tuple.copy(Tuple.make(1, 2, 3)), 0);

        assertArrayEquals("Check Tuple 'trim' - A", Tuple.make(1, 2), Tuple.trim(Tuple.make(1, 2, 3)), 0);
        assertArrayEquals("Check Tuple 'trim' - B", Tuple.make(1, 2), Tuple.trim(Tuple.make(1, 2, 3, 4), 2), 0);

        assertArrayEquals("Check Tuple 'pad' - A", Tuple.make(1, 2, 3), Tuple.pad(Tuple.make(1, 2), 3), 0);
        assertArrayEquals("Check Tuple 'pad' - B", Tuple.make(1, 2, 3, 3), Tuple.pad(Tuple.make(1, 2), 3, 4), 0);

        assertArrayEquals("Check Tuple 'vector' - A", Tuple.make(1, 2, 3, 0), Tuple.vector(1, 2, 3), 0);
        assertArrayEquals("Check Tuple 'vector' - B", Tuple.make(1, 2, 0), Tuple.vector(1, 2), 0);

        assertArrayEquals("Check Tuple 'point' - A", Tuple.make(1, 2, 3, 1), Tuple.point(1, 2, 3), 0);
        assertArrayEquals("Check Tuple 'point' - B", Tuple.make(1, 2, 1), Tuple.point(1, 2), 0);

        try {
            assertArrayEquals("Check Tuple 'add' - A", Tuple.make(2, 4, 1), Tuple.add(Tuple.point(1, 2), Tuple.vector(1, 2)), 0);
            assertArrayEquals("Check Tuple 'add' - B", Tuple.make(3, 5, 7, 9), Tuple.add(Tuple.make(1, 2, 3, 4), Tuple.make(2, 3, 4, 5)), 0);
            assertArrayEquals("Check Tuple 'add' - C", Tuple.make(3, 5, 7), Tuple.add(Tuple.make(1, 2, 3), Tuple.make(2, 3, 4)), 0);
            assertArrayEquals("Check Tuple 'add' - D", Tuple.make(3, 5), Tuple.add(Tuple.make(1, 2), Tuple.make(2, 3)), 0);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            assertArrayEquals("Check Tuple 'subtract' - A", Tuple.make(2, 4, 1), Tuple.subtract(Tuple.point(3, 6), Tuple.vector(1, 2)), 0);
            assertArrayEquals("Check Tuple 'subtract' - B", Tuple.make(3, 5, 7, 9), Tuple.subtract(Tuple.make(5, 8, 11, 14), Tuple.make(2, 3, 4, 5)), 0);
            assertArrayEquals("Check Tuple 'subtract' - C", Tuple.make(3, 5, 7), Tuple.subtract(Tuple.make(5, 8, 11), Tuple.make(2, 3, 4)), 0);
            assertArrayEquals("Check Tuple 'subtract' - D", Tuple.make(3, 5), Tuple.subtract(Tuple.make(5, 8), Tuple.make(2, 3)), 0);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            assertArrayEquals("Check Tuple 'multiply' - A", Tuple.make(6, 12, 0), Tuple.multiply(Tuple.vector(3, 6), 2), 0);
            assertArrayEquals("Check Tuple 'multiply' - B", Tuple.make(10, 16, 22, 28), Tuple.multiply(Tuple.make(5, 8, 11, 14), 2), 0);
            assertArrayEquals("Check Tuple 'multiply' - C", Tuple.make(10, 16, 22), Tuple.multiply(Tuple.make(5, 8, 11), 2), 0);
            assertArrayEquals("Check Tuple 'multiply' - D", Tuple.make(10, 16), Tuple.multiply(Tuple.make(5, 8), 2), 0);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            double epsilon = 1.0e-6;
            assertArrayEquals("Check Tuple 'divide' - A", Tuple.make(1.5, 3, 0), Tuple.divide(Tuple.vector(3, 6), 2), epsilon);
            assertArrayEquals("Check Tuple 'divide' - B", Tuple.make(2.5, 4, 5.5, 7), Tuple.divide(Tuple.make(5, 8, 11, 14), 2), epsilon);
            assertArrayEquals("Check Tuple 'divide' - C", Tuple.make(2.5, 4, 5.5), Tuple.divide(Tuple.make(5, 8, 11), 2), epsilon);
            assertArrayEquals("Check Tuple 'divide' - D", Tuple.make(2.5, 4), Tuple.divide(Tuple.make(5, 8), 2), epsilon);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            double epsilon = 1.0e-6;
            assertEquals("Check Tuple 'dot' - A", 63, Tuple.dot(Tuple.vector(3, 6), Tuple.vector(5, 8)), epsilon);
            assertEquals("Check Tuple 'dot' - B", 406, Tuple.dot(Tuple.make(5, 8, 11, 14), Tuple.make(5, 8, 11, 14)), epsilon);
            assertEquals("Check Tuple 'dot' - C", 210, Tuple.dot(Tuple.make(5, 8, 11), Tuple.make(5, 8, 11)), epsilon);
            assertEquals("Check Tuple 'dot' - D", 89, Tuple.dot(Tuple.make(5, 8), Tuple.make(5, 8)), epsilon);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            double epsilon = 1.0e-6;
            assertEquals("Check Tuple 'lengthSq' - A", 406, Tuple.lengthSq(Tuple.vector(5, 8, 11, 14)), epsilon);
            assertEquals("Check Tuple 'lengthSq' - B", 210, Tuple.lengthSq(Tuple.vector(5, 8, 11)), epsilon);
            assertEquals("Check Tuple 'lengthSq' - C", 89, Tuple.lengthSq(Tuple.vector(5, 8)), epsilon);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            double epsilon = 1.0e-6;
            assertEquals("Check Tuple 'length' - A", Math.sqrt(406), Tuple.length(Tuple.vector(5, 8, 11, 14)), epsilon);
            assertEquals("Check Tuple 'length' - B", Math.sqrt(210), Tuple.length(Tuple.vector(5, 8, 11)), epsilon);
            assertEquals("Check Tuple 'length' - C", Math.sqrt(89), Tuple.length(Tuple.vector(5, 8)), epsilon);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

        try {
            double epsilon = 1.0e-2;

            // area of a triangle using cross product 3
            double[] A = Tuple.make(1, 1, 3);
            double[] B = Tuple.make(4, -1, 1);
            double[] C = Tuple.make(0, 1, 8);
            double[] a = Tuple.subtract(B, A);
            double[] b = Tuple.subtract(C, A);
            assertArrayEquals("Triangle area - interim A", Tuple.make(3, -2, -2), a, epsilon);
            assertArrayEquals("Triangle area - interim B", Tuple.make(-1, 0, 5), b, epsilon);
            double[] c = Tuple.crossProduct3(a, b);
            double length = Tuple.length(c);
            assertEquals("Triangle area - result", 8.26, length / 2.0, epsilon);
        } catch (TupleSizeException tse) {
            fail(tse.getMessage());
        }

    }

}
