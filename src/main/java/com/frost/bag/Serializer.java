package com.frost.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Serializer {
    private static final Logger log = LogManager.getLogger (Serializer.class);

    private static final String CLASS_KEY = "class";
    private static final String VALUES_KEY = "values";

    public static BagObject toBagObject (Object target) {
        BagObject serializedBagObject = new BagObject (2);
        Class targetClass = target.getClass ();
        serializedBagObject.put (CLASS_KEY, targetClass.getName ());
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
        return serializedBagObject;
    }

    public static Object fromBagObject (BagObject bagObject) {
        Object result = null;
        try {
            String targetClassName = bagObject.getString (CLASS_KEY);
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            Class targetClass = classLoader.loadClass (targetClassName);
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
                        field.set (target, BagHelper.objectify (values.getObject (name), typeName));
                    } else {
                        field.set (target, fromBagObject (values.getBagObject (name)));
                    }
            }

            // copy the created object if everything succeeded to here
            result = target;
        } catch (Exception exception) {
            log.error (exception);
        }
        return result;
    }
}
