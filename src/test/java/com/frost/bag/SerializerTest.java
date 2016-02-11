package com.frost.bag;

import com.frost.AppTest;
import com.frost.bag.test.TestClassA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


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
        log.info ("got here");
    }
}
