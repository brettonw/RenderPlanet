package com.frost.bag;

// a BagPair is the joining of a name and an object, a key-value pairing if you will
public class BagPair {
    protected String key;
    protected Object value;

    public BagPair (String key, Object value) {
        this.key  key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue () {
        return value;
    }

    public BagPair setValue (Object value) {
        this.value = value;
        return this;
    }
}
