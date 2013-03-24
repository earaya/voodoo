/*
 *    Copyright 2010 Talis Systems Ltd
 *    Copyright 2013 Esteban Araya
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.earaya.voodoo.components;

import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.exceptions.DefaultExceptionMapper;
import com.earaya.voodoo.filters.LoggingFilter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogManager;

public class RestComponent implements Component {

    private final PackagesResourceConfig resourceConfig;
    private final Injector injector;
    private String rootPath = "/";

    static {
        // Jersey uses java.util.logging, so here we bridge to slf4
        // This is a static initialiser because we don't want to do this multiple times.
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();

	    //noinspection ForLoopReplaceableByForEach
	    for (int i = 0; i < handlers.length; i++) {
            rootLogger.removeHandler(handlers[i]);
        }
        SLF4JBridgeHandler.install();
    }

    public RestComponent(String packageName, Module... modules) {
        resourceConfig = new PackagesResourceConfig(packageName);
        injector = Guice.createInjector(modules);
        setupResourceConfig();
    }

	public RestComponent(Package pkg, Module... modules) {
		this(pkg.getName(), modules);
	}

    public RestComponent provider(Class<?> provider) {
        return provider(injector.getInstance(provider));
    }

    public RestComponent provider(Object provider) {
        resourceConfig.getSingletons().add(provider);
        return this;
    }

    public RestComponent root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public void start(VoodooApplication application) {
        HandlerCollection handlerCollection = (HandlerCollection) application.server.getHandler();
        handlerCollection.addHandler(getHandler());
    }

    private ContextHandler getHandler() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath(rootPath);

        context.addServlet(new ServletHolder(new VoodooServletContainer(resourceConfig, injector)), "/*");
        context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        return context;
    }

    // Question: Should we use the injector to construct these?
    @SuppressWarnings("unchecked")
    private void setupResourceConfig() {
        // Features
        resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);

	    final LoggingFilter loggingFilter = new LoggingFilter();
	    final GZIPContentEncodingFilter encodingFilter = new GZIPContentEncodingFilter();

	    // Request Filters
        resourceConfig.getContainerRequestFilters().add(loggingFilter);
        resourceConfig.getContainerRequestFilters().add(encodingFilter);

        // Response Filters
        resourceConfig.getContainerResponseFilters().add(loggingFilter);
        resourceConfig.getContainerResponseFilters().add(encodingFilter);

        // Voodoo Providers
        resourceConfig.getSingletons().add(new JacksonMessageBodyProvider());
        resourceConfig.getSingletons().add(new DefaultExceptionMapper());
        resourceConfig.getClasses().add(InstrumentedResourceMethodDispatchAdapter.class);
    }

    // NOTE: Tighten scope on some of these methods.
    private static class VoodooServletContainer extends GuiceContainer {

        private final ScanningResourceConfig resourceConfig;
        private final Injector injector;

        @Inject
        public VoodooServletContainer(PackagesResourceConfig resourceConfig, Injector injector) {
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
            webapp.initiate(config, new ServletGuiceComponentProviderFactory(config, injector));
        }
    }
}
