package com.frost.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

public class Serializer {
    private static final Logger log = LogManager.getLogger (Serializer.class);

    private static final String TYPE_KEY = "type";
    private static final String KEY_KEY = "key";
    private static final String VALUE_KEY = "value";

    private static SerializationType serializationType (Class type) {
        if (BagHelper.isPrimitive (type)) return SerializationType.PRIMITIVE;
        if (type.isArray ()) return SerializationType.ARRAY;
        if (Collection.class.isAssignableFrom (type)) return SerializationType.COLLECTION;
        if (Map.class.isAssignableFrom (type)) return SerializationType.MAP;
        if (BagObject.class.isAssignableFrom (type)) return SerializationType.BAG_OBJECT;
        if (BagArray.class.isAssignableFrom (type)) return SerializationType.BAG_ARRAY;

        // if it's none of the above...
        return SerializationType.JAVA_OBJECT;
    }

    private static SerializationType serializationType (String typeString) throws ClassNotFoundException {
        if (typeString.charAt (0) == '[') {
            return SerializationType.ARRAY;
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
        Class type = classLoader.loadClass (typeString);
        return serializationType (type);
    }

    private static BagObject serializePrimitiveType (BagObject bagObject, Object object) {
        return bagObject.put (VALUE_KEY, object);
    }

    private static BagObject serializeJavaObjectType (BagObject bagObject, Object object, Class type) throws IllegalAccessException {
        BagObject value = new BagObject ();
        Field fields[] = type.getFields ();
        for (Field field : fields) {
            String name = field.getName ();
            log.info ("Add " + name + " as " + field.getType ().getName ());
            value.put (name, toBagObject (field.get (object)));
        }
        return bagObject.put (VALUE_KEY, value);
    }

    private static BagObject serializeArrayType (BagObject bagObject, Object object) {
        int length = Array.getLength (object);
        BagArray value = new BagArray (length);
        for (int i = 0; i < length; ++i) {
            // at runtime, we don't know what the array type is, and frankly we don't care
            value.add (toBagObject (Array.get (object, i)));
        }
        return bagObject.put (VALUE_KEY, value);
    }

    private static BagObject serializeMapType (BagObject bagObject, Map object) {
        Object[] keys = object.keySet ().toArray ();
        int count = keys.length;
        BagArray value = new BagArray (count);
        for (int i = 0; i < count; ++i) {
            Object key = keys[i];
            Object item = object.get (key);
            BagObject pair = new BagObject (2)
                    .put (KEY_KEY, toBagObject (key))
                    .put (VALUE_KEY, toBagObject (item));
            value.add (pair);
        }
        return bagObject.put (VALUE_KEY, value);
    }

    public static BagObject toBagObject (Object object) {
        Class type = object.getClass ();
        BagObject bagObject = new BagObject (2)
                .put (TYPE_KEY, type.getName ());

        try {
            switch (serializationType (type)) {
                case PRIMITIVE: return serializePrimitiveType (bagObject, object);
                case BAG_OBJECT: return serializePrimitiveType (bagObject, object);
                case BAG_ARRAY: return serializePrimitiveType (bagObject, object);
                case JAVA_OBJECT: return serializeJavaObjectType (bagObject, object, type);
                case COLLECTION: return serializeArrayType (bagObject, ((Collection) object).toArray ());
                case MAP: return serializeMapType (bagObject, (Map) object);
                case ARRAY: return serializeArrayType (bagObject, object);
            }
        }
        catch (Exception exception) {
            log.error (exception);
        }
        return null;
    }

    private static Object deserializeType (BagObject bagObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        return type.getConstructor (String.class).newInstance (bagObject.getString (VALUE_KEY));
    }

    private static Object deserializeJavaObjectType (BagObject bagObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        Object target = type.newInstance ();

        // traverse the fields via reflection to set the values
        BagObject value = bagObject.getBagObject (VALUE_KEY);
        for (Field field : type.getFields ()) {
            //log.info ("Add " + name + " as " + field.getType ().getName ());
            field.set (target, fromBagObject (value.getBagObject (field.getName ())));
        }
        return target;
    }

    private static Object deserializeCollectionType (BagObject bagObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        Collection target = (Collection) type.newInstance ();
        BagArray value = bagObject.getBagArray (VALUE_KEY);
        for (int i = 0, end = value.getCount (); i < end; ++i) {
            target.add (fromBagObject (value.getBagObject (i)));
        }
        return target;
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
        }

        throw new ClassNotFoundException(typeName);
    }

    private static int[] getArraySizes (BagObject bagObject) {
        // figure the array dimension
        String typeString = bagObject.getString (TYPE_KEY);
        int dimension = 0;
        while (typeString.charAt (dimension) == '[') { ++dimension; }

        // create and populate the sizes array
        int sizes[] = new int[dimension];
        for (int i = 0; i < dimension; ++i) {
            BagArray value = bagObject.getBagArray (VALUE_KEY);
            sizes[i] = value.getCount ();
            bagObject = value.getBagObject (0);
        }

        // return the result
        return sizes;
    }

    private static void populateArray(Object target, BagObject bagObject) {
        String classString = bagObject.getString (TYPE_KEY);
        BagArray values = bagObject.getBagArray (VALUE_KEY);
        for (int i = 0, end = values.getCount (); i < end; ++i) {
            if (classString.charAt (1) == '[') {
                // we should recur for each value
                Object newTarget = Array.get (target, i);
                BagObject newBagObject = values.getBagObject (i);
                populateArray (newTarget, newBagObject);
            } else {
                Array.set (target, i, fromBagObject (values.getBagObject (i)));
            }
        }
    }

    private static Object deserializeArrayType (BagObject bagObject) throws ClassNotFoundException {
        int[] arraySizes = getArraySizes (bagObject);
        Class type = getArrayType (bagObject.getString (TYPE_KEY));
        Object target = Array.newInstance (type, arraySizes);
        populateArray (target, bagObject);
        return target;
    }

    public static Object fromBagObject (BagObject bagObject) {
        try {
            switch (serializationType (bagObject.getString (TYPE_KEY))) {
                case PRIMITIVE: return deserializeType (bagObject);
                case BAG_OBJECT: return bagObject.getBagObject (VALUE_KEY);
                case BAG_ARRAY: return bagObject.getBagArray (VALUE_KEY);
                case JAVA_OBJECT: return deserializeJavaObjectType (bagObject);
                case COLLECTION: return deserializeCollectionType (bagObject);
                case MAP: break;
                case ARRAY: return deserializeArrayType (bagObject);
            }
        }
        catch (Exception exception){
            log.error (exception);
        }
        return null;
    }
}
