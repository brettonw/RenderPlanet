package lib.bag;

import AppTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BagArrayTest {
    @Test
    public void test() {
        BagArray bagArray = new BagArray ()
            .add ("abdefg")
            .add (123456)
            .add (123.456)
            .add (true);
        bagArray.insert (1, 234567);
        bagArray.replace (2, 345678);

        assertEquals ("Check get double", 123.456, bagArray.getDouble (3), 1.0e-9);
        assertEquals ("Check size", 5, bagArray.getCount ());

        BagArray testArray = new BagArray();
        testArray.add("Bretton");
        testArray.add("Wade");
        testArray.add(220.5);
        testArray.add(true);
        testArray.add(42);

        AppTest.report(testArray.getString(0), "Bretton", "BagArray simple string extraction");
        AppTest.report(testArray.getDouble(2), 220.5, "BagArray simple double extraction");
        AppTest.report(testArray.getBoolean(3), true, "BagArray simple bool extraction");
        AppTest.report(testArray.getInteger (4), 42, "BagArray simple int extraction");

        String testString = testArray.toString();
        AppTest.report(testString, testString, "BagArray simple toString exercise (" + testString + ")");

        BagArray reconArray = BagArray.fromString(testString);
        String reconString = reconArray.toString();
        AppTest.report(reconString, testString, "BagArray simple reconstitution");

        BagObject dateObject = new BagObject ();
        dateObject.put ("Year", 2015);
        dateObject.put ("Month", 11);
        dateObject.put ("Day", 18);

        reconArray.insert(1, dateObject);
        testString = reconArray.toString();
        AppTest.report(testString, testString, "BagArray complex toString exercise (" + testString + ")");

        reconArray = BagArray.fromString(testString);
        reconString = reconArray.toString();
        AppTest.report(reconString, testString, "BagArray complex reconstitution");

        AppTest.report(reconArray.getString(2), "Wade", "BagArray simple string extraction after insert");
        AppTest.report(reconArray.getBagObject(1).getInteger ("Year"), 2015, "BagArray complex bag/int extraction");
        //AppTest.report (false, "Test Failure");
    }
}
