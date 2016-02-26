package com.frost.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Serializer {
    private static final Logger log = LogManager.getLogger (Serializer.class);

    private static final String CLASS_KEY = "class";
    private static final String VALUES_KEY = "values";

    private static Class arrayType (Class type) throws ClassNotFoundException {
        String typeName = type.getName ();
        switch (typeName.substring (0, 1)) {
            case "B":
                return Byte.class;
            case "C":
                return Character.class;
            case "D":
                return Double.class;
            case "F":
                return Float.class;
            case "I":
                return Integer.class;
            case "J":
                return Long.class;
            case "L": {
                ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
                return classLoader.loadClass (typeName.substring (2));
            }
            case "S":
                return Short.class;
            case "Z":
                return Boolean.class;
        }
        throw new ClassNotFoundException (typeName);
    }

    private static Object objectify (Object value, String typeName) {
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

    public static BagObject toBagObject (Object target) {
        BagObject serializedBagObject = new BagObject (2);
        Class targetClass = target.getClass ();
        serializedBagObject.put (CLASS_KEY, targetClass.getName ());

        // check to see if this is an array or an object
        if (targetClass.isArray ()) {
            // an array is serialized as an ... array
            int length = Array.getLength (target);
            BagArray values = new BagArray (length);
            serializedBagObject.put (VALUES_KEY, values);

            // add the values to the array
            for (int i = 0; i < length; ++i) {
                Object item = Array.get (target, i);
                Class itemType = item.getClass ();
                String typeName = itemType.getName ();
                log.info ("Add item (" + i + ") as " + typeName);
                if (BagHelper.isPrimitive (itemType) || typeName.equals ("java.lang.String")) {
                    values.addObject (item);
                } else {
                    values.add (toBagObject (item));
                }
            }
        } else {
            // an object is serialized as a bag of values
            BagObject values = new BagObject ();
            serializedBagObject.put (VALUES_KEY, values);

            // traverse the fields to get the values
            Field fields[] = targetClass.getFields ();
            for (Field field : fields) {
                try {
                    String name = field.getName ();
                    Class fieldType = field.getType ();
                    String typeName = fieldType.getName ();
                    log.info ("Add " + name + " as " + typeName);
                    if (BagHelper.isPrimitive (fieldType) || typeName.equals ("java.lang.String")) {
                        values.putObject (name, field.get (target));
                    } else {
                        values.put (name, toBagObject (field.get (target)));
                    }
                } catch (IllegalAccessException illegalAccessException) {
                    log.error ("Serializing non-POJO");
                }
            }
        }

        // return the result
        return serializedBagObject;
    }

    public static Object fromBagObject (BagObject bagObject) {
        Object result = null;
        try {
            String targetClassName = bagObject.getString (CLASS_KEY);
            ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
            Class targetClass = classLoader.loadClass (targetClassName);

            // check to see if this is an array or an object
            if (targetClass.isArray ()) {
                Class arrayType = arrayType (targetClass);
                String typeName = arrayType.getName ();
                BagArray values = bagObject.getBagArray (VALUES_KEY);
                int length = values.getCount ();
                Object target = Array.newInstance (arrayType, length);
                for (int i = 0; i < length; ++i) {
                    log.info ("Add item (" + i + ") as " + typeName);
                    if (BagHelper.isPrimitive (arrayType) || typeName.equals ("java.lang.String")) {
                        Array.set (target, i, objectify (values.getString (i), typeName));
                    } else {
                        Array.set (target, i, fromBagObject (values.getBagObject (i)));
                    }
                }

                // copy the created object if everything succeeded to here
                result = target;
            } else {
                Constructor constructor = targetClass.getConstructor ();
                Object target = constructor.newInstance ();

                // traverse the fields to set the values
                Field fields[] = targetClass.getFields ();
                BagObject values = bagObject.getBagObject (VALUES_KEY);
                for (Field field : fields) {
                    String name = field.getName ();
                    Class fieldType = field.getType ();
                    String typeName = fieldType.getName ();
                    log.info ("Add " + name + " as " + typeName);
                    if (BagHelper.isPrimitive (fieldType) || typeName.equals ("java.lang.String")) {
                        field.set (target, objectify (values.getObject (name), typeName));
                    } else {
                        field.set (target, fromBagObject (values.getBagObject (name)));
                    }
                }

                // copy the created object if everything succeeded to here
                result = target;
            }
        }catch (Exception exception){
            log.error (exception);
        }
        return result;
    }
}
