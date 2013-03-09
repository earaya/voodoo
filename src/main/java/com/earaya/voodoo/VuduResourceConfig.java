package com.earaya.voodoo;

import com.sun.jersey.api.core.PackagesResourceConfig;

public class VuduResourceConfig extends PackagesResourceConfig {

    public VuduResourceConfig() {
        super("com.earaya.voodoo");
    }

    public void addSingleton(Object provdier) {
        this.getSingletons().add(provdier);
    }
}
