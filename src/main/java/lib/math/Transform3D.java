package lib.math;

public class Transform3D extends Transform {
    protected static final int SIZE_DIMENSION_3D = 4;
    protected static final int H_IDX = SIZE_DIMENSION_3D - 1;

    public Transform3D(double... values) {
        super(SIZE_DIMENSION_3D, values);
    }

    public static Transform identity() {
        return identity (SIZE_DIMENSION_3D);
    }

    public static Transform translate(double x, double y, double z) {
        return identity().set(H_IDX, 0, x).set(H_IDX, 1, y).set(H_IDX, 2, z);
    }

    public static Transform translate(double... tuple) {
        return translate(tuple[Tuple.X], tuple[Tuple.Y]);
    }

    public static Transform scale(double x, double y, double z) {
        return identity().set(0, 0, x).set(1, 1, y).set(2, 2, z);
    }

    public static Transform scale(double... tuple) {
        return scale(tuple[Tuple.X], tuple[Tuple.Y]);
    }

    public static Transform rotateX(double angle) {
        double cosa = Math.cos(angle), sina = Math.sin(angle);
        return identity()
                .set(1, 1,  cosa).set(1, 2,  sina)
                .set(2, 1, -sina).set(2, 2,  cosa);
    }

    public static Transform rotateY(double angle) {
        double cosa = Math.cos(angle), sina = Math.sin(angle);
        return identity()
                .set(0, 0,  cosa).set(0, 2, -sina)
                .set(2, 0,  sina).set(2, 2,  cosa);
    }

    public static Transform rotateZ(double angle) {
        double cosa = Math.cos(angle), sina = Math.sin(angle);
        return identity()
                .set(0, 0,  cosa).set(0, 1,  sina)
                .set(1, 0, -sina).set(1, 1,  cosa);
    }

}
