package com.earaya.voodoo.config;

public class FlatConfigWithConstructors {

    public String aString;
    public int anInt = 777;

    public FlatConfigWithConstructors() {
    }

    public FlatConfigWithConstructors(int anInt) {
        this.anInt = anInt;
    }
}
