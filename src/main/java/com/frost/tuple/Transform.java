package com.frost.tuple;

// Transforms are generally presumed to be square, homogenous coordinate system
// transformation matrices
public class Transform {

    protected int size;
    protected double[] matrix;

    protected Transform(int size, double... values) {
        this.size = size;
        matrix = values;
    }

    protected static Transform identity(int size) {
        double[] matrix = new double[size * size];
        int offset = 0;
        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column) {
                matrix[offset + column] = (column == row) ? 1 : 0;
            }
            offset += size;
        }
        return new Transform(size, matrix);
    }

    public double get(int row, int column) {
        return matrix[(row * size) + column];
    }

    public Transform set(int row, int column, double value) {
        matrix[(row * size) + column] = value;
        return this;
    }

    public static Transform multiply(Transform left, Transform right) throws MatrixSizeException {
        int size = left.size;
        if (size != right.size) {
            throw new MatrixSizeException("Matrices are different sizes");
        }

        // r & c are scratch vectors so that Java doesn't allocate tons of memory
        double[] r = Tuple.make(size);
        double[] c = Tuple.make(size);

        // this is the place to assemble the result
        double[] matrix = new double[size * size];

        // loop over the matrices to compute the product matrix
        try {
            int offset = 0;
            for (int row = 0; row < size; ++row) {
                for (int column = 0; column < size; ++column) {
                    for (int k = 0; k < size; ++k) {
                        r[k] = left.get(row, k);
                        c[k] = right.get(k, column);
                    }
                    matrix[offset + column] = Tuple.dot(r, c);
                }
                offset += size;
            }
        } catch (TupleSizeException tse) {
            // this should never happen because we created the tuples to
            // be the right size in the first place
            assert false;
        }

        // create the final result transformation and return it
        return new Transform(size, matrix);
    }

    // pre-multiply by a row vector
    public static double[] multiply(double[] left, Transform right) throws MatrixSizeException {
        int size = left.length;
        if (size != right.size) {
            throw new MatrixSizeException("left and right are different sizes");
        }

        double[] tuple = null;
        return tuple;
    }


    public static double[] multiply(Transform left, double[] right) throws MatrixSizeException {
        double[] tuple = null;
        return tuple;
    }

    private static int verifySize(double[]... tuples) throws TupleSizeException {
        int size = tuples[0].length;
        for (int i = 1, end = tuples.length; i < end; ++i) {
            if (size != tuples[i].length) {
                throw new TupleSizeException("All operators must be the same dimension");
            }
        }
        return size;
    }
}
