package com.frost.bag;

import com.frost.AppTest;
import com.frost.bag.test.TestClassA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


public class SerializerTest {
    private static final Logger log = LogManager.getLogger (SerializerTest.class);

    @Test
    public void test() {
        TestClassA testClass = new TestClassA (5, true, 123.0, "pdq");
        BagObject bagObject = Serializer.toBagObject (testClass);
        log.info (bagObject.toString ());

        TestClassA reconClass = (TestClassA) Serializer.fromBagObject (bagObject);
        BagObject reconBagObject = Serializer.toBagObject (reconClass);
        AppTest.report (reconBagObject.toString (),bagObject.toString (), "Serializer test round trip");

        Integer testArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        bagObject = Serializer.toBagObject (testArray);
        log.info (bagObject.toString ());
        Integer reconArray[] = (Integer[]) Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray, reconArray);

        int testArray2[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        bagObject = Serializer.toBagObject (testArray2);
        log.info (bagObject.toString ());
        int reconArray2[] = (int[]) Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray2, reconArray2);

        int testArray3[][] = { {0,0}, {1,1}, {2,2} };
        bagObject = Serializer.toBagObject (testArray3);
        log.info (bagObject.toString ());
        int reconArray3[][] = (int[][]) Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray3, reconArray3);

        log.info ("got here");
    }
}
