package com.earaya.voodoo.config;

import com.earaya.voodoo.ObjectMapperProvider;
import com.earaya.voodoo.exceptions.DefaultExceptionMapper;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter;

public class ApiConfig extends PackagesResourceConfig {

    public ApiConfig(String... packageName) {
        super(packageName);
        this.getClasses().add(InstrumentedResourceMethodDispatchAdapter.class);
        this.getSingletons().add(new ObjectMapperProvider());
        this.getSingletons().add(new DefaultExceptionMapper());
    }

    public void addProvider(Object provider) {
        this.getSingletons().add(provider);
    }
}
