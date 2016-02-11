package com.frost.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BagHelper {
    private static final Logger log = LogManager.getLogger (BagHelper.class);

    public static String enclose (String input, String bracket) {
        char bracket0 = bracket.charAt (0);
        char bracket1 = bracket.length () > 1 ? bracket.charAt (1) : bracket0;
        return new StringBuilder ().append (bracket0).append (input).append (bracket1).toString ();
    }

    public static String quote (String input) {
        // XXX should this escape quote marks in the string?
        return enclose (input, "\"");
    }

    public static String stringify (Object value) {
        if (value != null) {
            switch (value.getClass ().getName ()) {
                case "java.lang.String":
                    return quote ((String) value);

                case "com.frost.bag.BagObject":
                case "com.frost.bag.BagArray":
                    return value.toString ();

                default:
                    // no other type should be stored in the bag classes
                    log.debug ("Unhandled type: " + value.getClass ().getName ());
                    break;
            }
        }
        return null;
    }

    public static Object objectify (Object value) {
        if (value != null) {
            String className = value.getClass ().getName ();
            switch (className) {
                case "java.lang.String":
                    return value;

                case "java.lang.Long": case "java.lang.Integer": case "java.lang.Short": case "java.lang.Byte":
                case "java.lang.Char":
                case "java.lang.Boolean":
                case "java.lang.Double": case "java.lang.Float":
                    return value.toString ();

                case "com.frost.bag.BagObject":
                case "com.frost.bag.BagArray":
                    return value;

                default:
                    // no other type should be stored in the bag classes
                    log.debug ("Unhandled type: " + className);
                    break;
            }
        }
        return null;
    }

    public static Object objectify (Object value, String typeName) {
        if (value != null) {
            if (value instanceof String) {
                String valueString = (String) value;
                switch (typeName) {
                    case "java.lang.String":
                        return value;
                    case "java.lang.Character":
                    case "char":
                        return new Character (valueString.charAt (0));
                    case "java.lang.Byte":
                    case "byte":
                        return new Byte (valueString);
                    case "java.lang.Short":
                    case "short":
                        return new Short (valueString);
                    case "java.lang.Integer":
                    case "int":
                        return new Integer (valueString);
                    case "java.lang.Long":
                    case "long":
                        return new Long (valueString);
                    case "java.lang.Boolean":
                    case "boolean":
                        return new Boolean (valueString);
                    case "java.lang.Double":
                    case "double":
                        return new Double (valueString);
                    case "java.lang.Float":
                    case "float":
                        return new Float (valueString);
                }
            }
        }
        return null;
    }

    public static boolean isPrimitive (Class cls) {
        // obviously, if Java thinks it's a primitive, it is
        if (cls.isPrimitive ()) {
            return true;
        }

        // but we want this for boxed primitives as well
        switch (cls.getName ()) {
            case "java.lang.Long": case "java.lang.Integer": case "java.lang.Short": case "java.lang.Byte":
            case "java.lang.Char":
            case "java.lang.Boolean":
            case "java.lang.Double": case "java.lang.Float":
                return true;
        }

        // it wasn't any of those, return false;
        return false;
    }
}
