package com.earaya.voodoo.modules;

import com.earaya.voodoo.VuduResourceConfig;
import com.earaya.voodoo.auth.basic.BasicAuthProvider;
import com.earaya.voodoo.sample.SimpleAuthenticator;
import com.earaya.voodoo.sample.User;
import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.guice.InstrumentationModule;
import com.yammer.metrics.guice.JmxReporterProvider;
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter;
import com.yammer.metrics.reporting.AdminServlet;

import javax.inject.Singleton;

public class VuduModule extends AbstractModule {
    private final VuduResourceConfig resourceConfig;

    public VuduModule(VuduResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
        this.resourceConfig.getClasses().add(InstrumentedResourceMethodDispatchAdapter.class);
        // TODO: add CacheControlDispatcher from DropWizard.
    }

    @Override
    protected void configure() {
        install(new InstrumentationModule());
        install(new MetricsAdminModule());
        bind(VuduResourceConfig.class).toInstance(resourceConfig);
    }

    public class MetricsAdminModule extends ServletModule {

        @Override
        protected void configureServlets() {
            bind(AdminServlet.class).in(Singleton.class);
            serve("/ops-menu/*").with(AdminServlet.class);
        }
    }
}
