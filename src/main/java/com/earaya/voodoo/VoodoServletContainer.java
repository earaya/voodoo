package com.earaya.voodoo;

import com.google.inject.Injector;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import java.util.Map;

@Singleton
public class VoodoServletContainer extends GuiceContainer {

    private final ScanningResourceConfig resourceConfig;
    private final Injector injector;

    @Inject
    public VoodoServletContainer(PackagesResourceConfig resourceConfig, Injector injector) {
        super(injector);
        this.resourceConfig = resourceConfig;
        this.injector = injector;
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props,
                                                      WebConfig webConfig) throws ServletException {
        return this.resourceConfig;
    }

    @Override
    protected void initiate(ResourceConfig config, WebApplication webapp) {
        webapp.initiate(config, new GuiceContainer.ServletGuiceComponentProviderFactory(config, injector));
    }
}
