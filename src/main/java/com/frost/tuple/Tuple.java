package com.frost.tuple;

public class Tuple {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    //region Utility Methods

    // XXX I'm not sure this method will ever be needed
    public static double[] make (int dimension) {
        return new double[dimension];
    }

    public static double[] make(int dimension, double value) {
        double tuple[] = new double[dimension];
        for (int i = 0; i < dimension; ++i) {
            tuple[i] = value;
        }
        return tuple;
    }

    // shorthand for new double[] {value, value, value};
    public static double[] make(double... values) {
        // be aware that passing a double[] into this will compile, but it will
        // simply pass through, and not be a copy...
        return values;
    }

    public static double[] copy(double[] tuple) {
        int dimension = tuple.length;
        double copy[] = new double[dimension];
        System.arraycopy(tuple, 0, copy, 0, dimension);
        return copy;
    }

    public static double[] trim(double[] tuple, int dimension) {
        double copy[] = new double[dimension];
        // this will throw an IndexOutOfBoundsException if the user tries to
        // "trim" the size *up*; that should be a call to "pad"
        System.arraycopy(tuple, 0, copy, 0, dimension);
        return copy;
    }

    public static double[] trim(double[] tuple) {
        return trim (tuple, tuple.length - 1);
    }

    public static double[] pad(double[] tuple, double pad, int dimension) {
        double copy[] = new double[dimension];
        int i = Math.min(dimension, tuple.length);
        System.arraycopy(tuple, 0, copy, 0, i);
        while (i < dimension) {
            copy[i++] = pad;
        }
        return copy;
    }

    public static double[] pad(double[] tuple, double pad) {
        return pad(tuple, pad, tuple.length + 1);
    }
    //endregion

    //region Homogenous Types

    // this method will both take an array and make a homogenous copy, AND
    // act as shorthand for new double[] { value, value, value, pad}
    public static double[] homogenous(double pad, double... values) {
        int dimension = values.length;
        double tuple[] = new double[dimension + 1];
        System.arraycopy(values, 0, tuple, 0, dimension);
        tuple[dimension] = 1;
        return tuple;
    }

    public static double[] vector(double... values) {
        return homogenous(0, values);
    }

    public static double[] point(double... values) {
        return homogenous(1, values);
    }
    //endregion


    private static int verifyDimension (double[]... tuples) throws TupleDimensionException {
        int dimension = tuples[0].length;
        for (int i = 1, end = tuples.length; i < end; ++i) {
            if (dimension != tuples[i].length) {
                throw new TupleDimensionException("All operators must be the same dimension");
            }
        }
        return dimension;
    }

    //region Add
    public static double[] add(double[] left, double[] right, double[] tuple, int i) {
        tuple[i] = left[i] + right[i];
        if (i > 0) {
            add(left, right, tuple, --i);
        }
        return tuple;
    }

    public static double[] add(double[] left, double[] right, double[] tuple) throws TupleDimensionException {
        int i = verifyDimension(left, right, tuple);
        return add(left, right, tuple, --i);
    }

    public static double[] add(double[] left, double[] right) throws TupleDimensionException {
        // reuse the addition in place operator with a new tuple. size the tuple
        // from the right input to catch the error early if it's wrong
        return add(left, right, new double[right.length]);
    }
    //endregion

    //region Subtract
    public static double[] subtract(double[] left, double[] right, double[] tuple, int i) {
        tuple[i] = left[i] - right[i];
        if (i > 0) {
            subtract(left, right, tuple, --i);
        }
        return tuple;
    }

    public static double[] subtract(double[] left, double[] right, double[] tuple) throws TupleDimensionException {
        int i = verifyDimension(left, right, tuple);
        return subtract(left, right, tuple, --i);
    }

    public static double[] subtract(double[] left, double[] right) throws TupleDimensionException {
        // reuse the subtraction in place operator with a new tuple. size the
        // tuple from the right input to catch the error early if it's wrong
        return subtract(left, right, new double[right.length]);
    }
    //endregion

    //region Multiply
    public static double[] multiply(double[] left, double right, double[] tuple, int i) {
        tuple[i] = left[i] * right;
        if (i > 0) {
            multiply(left, right, tuple, --i);
        }
        return tuple;
    }

    public static double[] multiply(double[] left, double right, double[] tuple) throws TupleDimensionException {
        int i = verifyDimension(left, tuple);
        return multiply(left, right, tuple, --i);
    }

    public static double[] multiply(double[] left, double right) throws TupleDimensionException {
        // reuse the multiply in place operator with a new tuple.
        return multiply(left, right, new double[left.length]);
    }
    //endregion

    //region Divide
    public static double[] divide(double[] left, double right, double[] tuple) throws TupleDimensionException {
        int i = verifyDimension(left, tuple);
        // because multiplication is much faster than division
        return multiply(left, 1.0 / right, tuple, --i);
    }

    public static double[] divide(double[] left, double right) throws TupleDimensionException {
        // reuse the divide in place operator with a new tuple.
        return divide(left, right, new double[left.length]);
    }
    //endregion

    //region Dot
    public static double dot(double[] left, double[] right, int i) {
        return (i > 0) ? ((left[i] * right[i]) + dot(left, right, --i)) : (left[i] * right[i]);
    }

    public static double dot(double[] left, double[] right) throws TupleDimensionException {
        int i = verifyDimension(left, right);
        return dot(left, right, --i);
    }
    //endregion


    //region Cross
    public static double[] crossProduct3(double[] left, double[] right) throws TupleDimensionException {
        if (verifyDimension(left, right) == 3) {
            return make(
                    (left[Y] * right[Z]) - (left[Z] * right[Y]),
                    (left[Z] * right[X]) - (left[X] * right[Z]),
                    (left[X] * right[Y]) - (left[Y] * right[X]));
        }
        throw new TupleDimensionException("crossProduct3 is only defined for 3 dimensional vectors");
    }

    public static double crossProduct2(double[] left, double[] right) throws TupleDimensionException {
        if (verifyDimension(left, right) == 2) {
            // if we padded left and right to call crossProduct3, this is the
            // z-component of the return
            return (left[X] * right[Y]) - (left[Y] * right[X]);
        }
        throw new TupleDimensionException("crossProduct2 is only defined for 2 dimensional vectors");
    }
    //endregion

}
