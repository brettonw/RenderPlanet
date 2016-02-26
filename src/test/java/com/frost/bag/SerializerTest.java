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

        int testArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        bagObject = Serializer.toBagObject (testArray);
        log.info (bagObject.toString ());
        int reconArray[] = (int[]) Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray, reconArray);

        log.info ("got here");
    }
}
