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

package com.earaya.voodoo.modules;

import com.earaya.voodoo.ObjectMapperProvider;
import com.earaya.voodoo.VoodoServletContainer;
import com.earaya.voodoo.exceptions.DefaultExceptionMapper;
import com.earaya.voodoo.filters.LoggingFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Handler;
import java.util.logging.LogManager;

public class ApiModule extends ServletModule {

    private final PackagesResourceConfig resourceConfig;
    private String rootPath = "";

    static {
        // Jersey uses java.util.logging, so here we bridge to slf4
        // This is a static initialiser because we don't want to do this multiple times.
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            rootLogger.removeHandler(handlers[i]);
        }
        SLF4JBridgeHandler.install();
    }

    public ApiModule(String... packageName) {
        resourceConfig = new PackagesResourceConfig(packageName);
        setupResourceConfig();
    }

    public ApiModule addProvider(Object provider) {
        resourceConfig.getSingletons().add(provider);
        return this;
    }

    public ApiModule root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    @Override
    protected void configureServlets() {
        bind(PackagesResourceConfig.class).toInstance(resourceConfig);
        serve(rootPath + "/*").with(VoodoServletContainer.class);
    }

    private void setupResourceConfig() {
        // Features
        resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);

        // Request Filters
        resourceConfig.getContainerRequestFilters().add(new LoggingFilter());
        resourceConfig.getContainerRequestFilters().add(new GZIPContentEncodingFilter());

        // Response Filters
        resourceConfig.getContainerResponseFilters().add(new LoggingFilter());
        resourceConfig.getContainerResponseFilters().add(new GZIPContentEncodingFilter());

        // Voodoo "Providers"
        resourceConfig.getSingletons().add(new ObjectMapperProvider());
        resourceConfig.getSingletons().add(new DefaultExceptionMapper());
        resourceConfig.getClasses().add(InstrumentedResourceMethodDispatchAdapter.class);
        // TODO: Add validation provider so you can do @Valid.
    }
}
