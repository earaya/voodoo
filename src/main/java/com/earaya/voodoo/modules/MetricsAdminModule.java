package com.earaya.voodoo.modules;

import com.google.inject.servlet.ServletModule;
import com.yammer.metrics.reporting.AdminServlet;

import javax.inject.Singleton;

public class MetricsAdminModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(AdminServlet.class).in(Singleton.class);
        serve("/ops-menu/*").with(AdminServlet.class);
    }
}
