package lib.math;

public class Angle {
    public static double radians(double degrees) {
        return (degrees / 180.0) * Math.PI;
    }

    public static double degrees(double radians) {
        return (radians / Math.PI) * 180.0;
    }

}
