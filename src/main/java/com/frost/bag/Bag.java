package com.frost.bag;

// A Bag is a collection of values in text-based key/value pairs. Typing is performed lazily
// on extraction, and presumes the user knows what they are expecting to get. This class is
// primarily intended for messaging, events, and other applications that require complex values to
// be shared in a text- based data interchange format. It is loosely based on a combination of XML
// in use, and the JSONObject from the website (http://www.json.org).

import java.util.Arrays;

public class Bag {
    protected static final int START_SIZE = 4;
    protected static final int DOUBLING_CAP = 4 * 2 * 2 * 2 *2 * 2;
    protected BagPair[] pairs;
    protected int size;

    public Bag () {
        size = START_SIZE;
        pairs = new BagPair[size];
    }

    public Bag (int size) {
        this.size = size;
        pairs = new BagPair[size];
    }

    private BagPair[] growPairs (int to) {
        // if the array is smaller than the cap then double its size, otherwise just add the block
        int newSize = (size > DOUBLING_CAP) ? (size + DOUBLING_CAP) : (size * 2);
        BagPair newPairs[] = new BagPair[newSize];
        Arrays.
    }

    public Bag insert(int index, Object value) {
        // check to see if we need to grow the array
        int newLength = pairs.length + 1;
        if (newLength > size) {
            // make a copy of the pairs array according to the growth strategy

        } else {

        }
        return this;
    }
    public Bag replace(int index, Object value) {
        return this;
    }
}
