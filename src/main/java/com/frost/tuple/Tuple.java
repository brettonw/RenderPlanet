package com.frost.tuple;

public class Tuple {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    //region Utility Methods

    // XXX I'm not sure this method will ever be needed
    public static double[] make (int size) {
        return new double[size];
    }

    public static double[] make(int size, double value) {
        double tuple[] = new double[size];
        for (int i = 0; i < size; ++i) {
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
        int size = tuple.length;
        double copy[] = new double[size];
        System.arraycopy(tuple, 0, copy, 0, size);
        return copy;
    }

    public static double[] trim(double[] tuple, int size) {
        double copy[] = new double[size];
        // this will throw an IndexOutOfBoundsException if the user tries to
        // "trim" the size *up*; that should be a call to "pad"
        System.arraycopy(tuple, 0, copy, 0, size);
        return copy;
    }

    public static double[] trim(double[] tuple) {
        return trim (tuple, tuple.length - 1);
    }

    public static double[] pad(double[] tuple, double pad, int size) {
        double copy[] = new double[size];
        int i = Math.min(size, tuple.length);
        System.arraycopy(tuple, 0, copy, 0, i);
        while (i < size) {
            copy[i++] = pad;
        }
        return copy;
    }

    public static double[] pad(double[] tuple, double pad) {
        return pad(tuple, pad, tuple.length + 1);
    }
    //endregion

    //region Homogenous Types
    public static double[] vector(double... values) {
        return pad(values, 0);
    }

    public static double[] point(double... values) {
        return pad(values, 1);
    }
    //endregion

    //region Add
    public static double[] add(double[] left, double[] right, double[] tuple, int i) {
        tuple[i] = left[i] + right[i];
        if (i > 0) {
            add(left, right, tuple, --i);
        }
        return tuple;
    }

    public static double[] add(double[] left, double[] right, double[] tuple) throws TupleSizeException {
        int i = verifySize(left, right, tuple);
        return add(left, right, tuple, --i);
    }

    public static double[] add(double[] left, double[] right) throws TupleSizeException {
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

    public static double[] subtract(double[] left, double[] right, double[] tuple) throws TupleSizeException {
        int i = verifySize(left, right, tuple);
        return subtract(left, right, tuple, --i);
    }

    public static double[] subtract(double[] left, double[] right) throws TupleSizeException {
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

    public static double[] multiply(double[] left, double right, double[] tuple) throws TupleSizeException {
        int i = verifySize(left, tuple);
        return multiply(left, right, tuple, --i);
    }

    public static double[] multiply(double[] left, double right) throws TupleSizeException {
        // reuse the multiply in place operator with a new tuple.
        return multiply(left, right, new double[left.length]);
    }
    //endregion

    //region Divide
    public static double[] divide(double[] left, double right, double[] tuple) throws TupleSizeException {
        int i = verifySize(left, tuple);
        // because multiplication is much faster than division
        return multiply(left, 1.0 / right, tuple, --i);
    }

    public static double[] divide(double[] left, double right) throws TupleSizeException {
        // reuse the divide in place operator with a new tuple.
        return divide(left, right, new double[left.length]);
    }
    //endregion

    //region Dot
    public static double dot(double[] left, double[] right, int i) {
        return (i > 0) ? ((left[i] * right[i]) + dot(left, right, --i)) : (left[i] * right[i]);
    }

    public static double dot(double[] left, double[] right) throws TupleSizeException {
        int i = verifySize(left, right);
        return dot(left, right, --i);
    }
    //endregion


    //region Cross
    public static double[] crossProduct3(double[] left, double[] right) throws TupleSizeException {
        if (verifySize(left, right) == 3) {
            return make(
                    (left[Y] * right[Z]) - (left[Z] * right[Y]),
                    (left[Z] * right[X]) - (left[X] * right[Z]),
                    (left[X] * right[Y]) - (left[Y] * right[X]));
        }
        throw new TupleSizeException("crossProduct3 is only defined for 3 dimensional vectors");
    }

    public static double crossProduct2(double[] left, double[] right) throws TupleSizeException {
        if (verifySize(left, right) == 2) {
            // if we padded left and right to call crossProduct3, this is the
            // z-component of the return
            return (left[X] * right[Y]) - (left[Y] * right[X]);
        }
        throw new TupleSizeException("crossProduct2 is only defined for 2 dimensional vectors");
    }
    //endregion

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
