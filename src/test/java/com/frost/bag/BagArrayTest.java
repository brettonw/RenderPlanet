package com.frost.bag;

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
    }
}
