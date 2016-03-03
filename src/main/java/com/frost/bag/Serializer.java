package com.frost.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Serializer {
    private static final Logger log = LogManager.getLogger (Serializer.class);

    private static final String TYPE_KEY = "type";
    private static final String VALUE_KEY = "value";

    private static final String SERIALIZATION_TYPE_PRIMITIVE = "primitive";
    private static final String SERIALIZATION_TYPE_ARRAY = "array";
    private static final String SERIALIZATION_TYPE_COLLECTION = "collection";
    private static final String SERIALIZATION_TYPE_MAP = "map";
    private static final String SERIALIZATION_TYPE_BAG_OBJECT = "bagobject";
    private static final String SERIALIZATION_TYPE_BAG_ARRAY = "bagarray";
    private static final String SERIALIZATION_TYPE_JAVA_OBJECT = "java";

    private static String serializationType (Class type) {
        if (BagHelper.isPrimitive (type)) return SERIALIZATION_TYPE_PRIMITIVE;
        if (type.isArray ()) return SERIALIZATION_TYPE_ARRAY;
        if (Collection.class.isAssignableFrom (type)) return SERIALIZATION_TYPE_COLLECTION;
        if (Map.class.isAssignableFrom (type)) return SERIALIZATION_TYPE_MAP;
        if (BagObject.class.isAssignableFrom (type)) return SERIALIZATION_TYPE_BAG_OBJECT;
        if (BagArray.class.isAssignableFrom (type)) return SERIALIZATION_TYPE_BAG_ARRAY;

        // if it's none of the above...
        return SERIALIZATION_TYPE_JAVA_OBJECT;
    }

    private static BagObject serializeType (BagObject bagObject, Object object, Class type) {
        return bagObject.put (VALUE_KEY, object);
    }

    private static BagObject serializeArrayType (BagObject bagObject, Object object, Class type) {
        int length = Array.getLength (object);
        BagArray values = new BagArray (length);
        for (int i = 0; i < length; ++i) {
            Object item = Array.get (object, i);
            Class itemType = item.getClass ();
            log.info ("Add item (" + i + ") as " + itemType.getName ());
            if (BagHelper.isPrimitive (itemType)) {
                values.addObject (item);
            } else {
                values.add (toBagObject (item));
            }
        }
        return bagObject.put (VALUE_KEY, values);
    }

    private static BagObject serializeCollectionType (BagObject bagObject, Collection object, Class type) {
        return null;
    }

    private static BagObject serializeMapType (BagObject bagObject, Map object, Class type) {
        return null;

    }

    private static BagObject serializeJavaObjectType (BagObject bagObject, Object object, Class type) {
        BagObject value = new BagObject ();
        Field fields[] = type.getFields ();
        for (Field field : fields) {
            try {
                String name = field.getName ();
                Class fieldType = field.getType ();
                log.info ("Add " + name + " as " + fieldType.getName ());
                if (BagHelper.isPrimitive (fieldType)) {
                    value.put (name, field.get (object));
                } else {
                    value.put (name, toBagObject (field.get (object)));
                }
            } catch (IllegalAccessException illegalAccessException) {
                log.error ("Serializing non-POJO", illegalAccessException);
                // XXX what do I want to do about this?
            }
        }
        return bagObject.put (VALUE_KEY, value);
    }

    public static BagObject toBagObject (Object object) {
        Class type = object.getClass ();
        BagObject bagObject = new BagObject (2).put (TYPE_KEY, type.getName ());

        switch (serializationType (type)) {
            case SERIALIZATION_TYPE_PRIMITIVE: return serializeType (bagObject, object, type);
            case SERIALIZATION_TYPE_ARRAY: return serializeArrayType (bagObject, object, type);
            case SERIALIZATION_TYPE_COLLECTION: break;
            case SERIALIZATION_TYPE_MAP: break;
            case SERIALIZATION_TYPE_BAG_OBJECT: return serializeType (bagObject, object, type);
            case SERIALIZATION_TYPE_BAG_ARRAY: return serializeType (bagObject, object, type);
            case SERIALIZATION_TYPE_JAVA_OBJECT: return serializeJavaObjectType (bagObject, object, type);
        }

        // return the result
        return bagObject;
    }

    private static Object objectify (String value, Class type) {
        if (value != null) {
            switch (type.getName ()) {
                case "java.lang.String":
                    return value;
                case "java.lang.Character":
                case "char":
                    return new Character (value.charAt (0));
                case "java.lang.Byte":
                case "byte":
                    return new Byte (value);
                case "java.lang.Short":
                case "short":
                    return new Short (value);
                case "java.lang.Integer":
                case "int":
                    return new Integer (value);
                case "java.lang.Long":
                case "long":
                    return new Long (value);
                case "java.lang.Boolean":
                case "boolean":
                    return new Boolean (value);
                case "java.lang.Double":
                case "double":
                    return new Double (value);
                case "java.lang.Float":
                case "float":
                    return new Float (value);
            }
        }
        return null;
    }

    private static Class getArrayType (String typeName) throws ClassNotFoundException {
        int arrayDepth = 0;
        while (typeName.charAt (arrayDepth) == '[') { ++arrayDepth; }
        switch (typeName.substring (arrayDepth)) {
            case "B": return byte.class;
            case "C": return char.class;
            case "D": return double.class;
            case "F": return float.class;
            case "I": return int.class;
            case "J": return long.class;
            case "S": return short.class;
            case "Z": return boolean.class;

            case "Ljava.lang.Byte;": return Byte.class;
            case "Ljava.lang.Character;": return Character.class;
            case "Ljava.lang.Double;": return Double.class;
            case "Ljava.lang.Float;": return Float.class;
            case "Ljava.lang.Integer;": return Integer.class;
            case "Ljava.lang.Long;": return Long.class;
            case "Ljava.lang.Short;": return Short.class;
            case "Ljava.lang.Boolean;": return Boolean.class;
        }

        // if we get here, the type is either a class name, or ???
        if (typeName.charAt (arrayDepth) == 'L') {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
            return classLoader.loadClass (typeName.substring (arrayDepth + 1));
        } else {
            throw new ClassNotFoundException(typeName);
        }
    }

    private static int[] getArraySizes (BagObject bagObject) {
        ArrayList<Integer> sizeList = new ArrayList<> (1);
        String classString = bagObject.getString (TYPE_KEY);
        boolean finished = false;
        while (! finished) {
            BagArray values = bagObject.getBagArray (VALUE_KEY);
            sizeList.add (values.getCount ());
            if (classString.charAt (1) == '[') {
                bagObject = values.getBagObject (0);
                classString = bagObject.getString (TYPE_KEY);
            } else {
                finished = true;
            }
        }

        // convert the sizeList to a return result - not using toArray because it requires boxed
        // types, not all aspects of Java have been fully thought through
        int count = sizeList.size ();
        int[] result = new int[count];
        for (int i = 0; i < count; ++i) {
            result[i] = sizeList.get (i);
        }
        return result;
    }

    private static void populateArray(Object target, BagObject bagObject, Class type) {
        String classString = bagObject.getString (TYPE_KEY);
        BagArray values = bagObject.getBagArray (VALUE_KEY);
        for (int i = 0, end = values.getCount (); i < end; ++i) {
            if (classString.charAt (1) == '[') {
                // we should recur for each value
                Object newTarget = Array.get (target, i);
                BagObject newBagObject = values.getBagObject (i);
                populateArray (newTarget, newBagObject, type);
            } else {
                // we should set the value
                if (BagHelper.isPrimitive (type)) {
                    Array.set (target, i, objectify (values.getString (i), type));
                } else {
                    Array.set (target, i, fromBagObject (values.getBagObject (i)));
                }
            }
        }
    }

    public static Object fromBagObject (BagObject bagObject) {
        Object target = null;
        try {
            String classString = bagObject.getString (TYPE_KEY);

            // check to see if this is an array or an object
            if (classString.charAt (0) == '[') {
                // get the type of the array element to reconstruct
                Class arrayType = getArrayType (classString);
                int[] arraySizes = getArraySizes (bagObject);
                target = Array.newInstance (arrayType, arraySizes);
                populateArray (target, bagObject, arrayType);
            } else {
                // get the class loader and fetch the class default constructor via reflection
                ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
                Class targetClass = classLoader.loadClass (classString);
                Constructor constructor = targetClass.getConstructor ();
                target = constructor.newInstance ();

                // traverse the fields via reflection to set the values
                Field fields[] = targetClass.getFields ();
                BagObject values = bagObject.getBagObject (VALUE_KEY);
                for (Field field : fields) {
                    String name = field.getName ();
                    Class fieldType = field.getType ();
                    log.info ("Add " + name + " as " + fieldType.getName ());
                    if (BagHelper.isPrimitive (fieldType)) {
                        field.set (target, objectify (values.getString (name), fieldType));
                    } else {
                        field.set (target, fromBagObject (values.getBagObject (name)));
                    }
                }
            }
        }
        catch (Exception exception){
            log.error (exception);
            target = null;
        }
        return target;
    }
}
