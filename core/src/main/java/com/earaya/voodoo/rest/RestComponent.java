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

package com.earaya.voodoo.rest;

import com.earaya.voodoo.Component;
import com.earaya.voodoo.filters.LoggingFilter;
import com.earaya.voodoo.rest.exceptions.DefaultExceptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ScanningResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.GzipFilter;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.List;

public class RestComponent implements Component {

    private final PackagesResourceConfig resourceConfig;
    private final JacksonMessageBodyProvider jacksonProvider = new JacksonMessageBodyProvider(new ObjectMapper());
    private final Injector injector;
    private String rootPath = "/";
    private String version = "1";
    private Class documentationListing = DocumentationListing.class;
    private List<FilterHolder> filters = new ArrayList<>();

    public RestComponent(String packageName, Injector injector) {
        this.injector = injector;
        resourceConfig = new PackagesResourceConfig(packageName);
        setupResourceConfig();
    }

    public RestComponent(String packageName, Module... modules) {
        this(packageName, Guice.createInjector(modules));
    }

    public RestComponent(Package pkg, Module... modules) {
        this(pkg.getName(), modules);
    }

    public RestComponent provider(Class provider) {
        return provider(injector.getInstance(provider));
    }

    public RestComponent provider(Object provider) {
        resourceConfig.getSingletons().add(provider);
        return this;
    }

    public RestComponent version(String version) {
        this.version = version;
        return this;
    }

    public RestComponent root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public RestComponent documentation(Class documentationListing) {
        this.documentationListing = documentationListing;
        return this;
    }

    public RestComponent mapper(ObjectMapper jacksonMapper) {
        this.jacksonProvider.setMapper(jacksonMapper);
        return this;
    }

    public RestComponent filter(FilterHolder filterHolder) {
        this.filters.add(filterHolder);
        return this;
    }

    @Override
    public ContextHandler getHandler() {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath(rootPath);

        resourceConfig.getClasses().add(documentationListing);

        ServletHolder voodooServletHolder = new ServletHolder(new VoodooServletContainer(resourceConfig, injector));
        voodooServletHolder.setInitParameter("swagger.api.basepath", rootPath);
        voodooServletHolder.setInitParameter("api.version", version);

        final EnumSet<DispatcherType> dispatcherTypes = EnumSet.allOf(DispatcherType.class);
        context.addServlet(voodooServletHolder, "/*");
        for (FilterHolder holder : filters) {
            context.addFilter(holder, "/*", dispatcherTypes);
        }
        context.addFilter(GuiceFilter.class, "/*", dispatcherTypes);
        context.addFilter(LoggingFilter.class, "/*", dispatcherTypes);
        context.addFilter(GzipFilter.class, "/*", dispatcherTypes);

        return context;
    }

    @SuppressWarnings("unchecked")
    private void setupResourceConfig() {
        // Features
        resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);

        // Voodoo Providers
        resourceConfig.getSingletons().add(jacksonProvider);
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
