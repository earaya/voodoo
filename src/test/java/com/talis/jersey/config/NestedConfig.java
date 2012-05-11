package com.talis.jersey.config;

public class NestedConfig {
    public class ChildConfig {
        public int anInt;
        public String aString;
    }

    public ChildConfig childConfig;
    public int anInt;
    public String aString;
}
