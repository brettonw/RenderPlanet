package com.frost.math;

public class Transform2D extends Transform {
    protected static final int SIZE_DIMENSION_2D = 3;
    protected static final int H_IDX = SIZE_DIMENSION_2D - 1;

    public Transform2D(double... values) {
        super(SIZE_DIMENSION_2D, values);
    }

    public static Transform make (double... values) {
        return new Transform (SIZE_DIMENSION_2D, values);
    }

    public static Transform identity() {
        return Transform.identity(SIZE_DIMENSION_2D);
    }

    public static Transform translate(double x, double y) {
        return identity().set(H_IDX, 0, x).set(H_IDX, 1, y);
    }

    public static Transform translate(double... tuple) {
        return translate(tuple[Tuple.X], tuple[Tuple.Y]);
    }

    public static Transform scale(double x, double y) {
        return identity().set(0, 0, x).set(1, 1, y);
    }

    public static Transform scale(double... tuple) {
        return scale(tuple[Tuple.X], tuple[Tuple.Y]);
    }

    public static Transform rotate(double angle) {
        double cosa = Math.cos(angle);
        double sina = Math.sin(angle);
        return identity()
                .set(0, 0,  cosa).set(0, 1,  sina)
                .set(1, 0, -sina).set(1, 1,  cosa);
    }
}
