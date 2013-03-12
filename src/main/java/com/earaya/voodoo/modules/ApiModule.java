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

import com.earaya.voodoo.config.ApiConfig;
import com.earaya.voodoo.filters.LoggingFilter;
import com.earaya.voodoo.filters.ServerAgentHeaderFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.container.filter.RolesAllowedResourceFilterFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogManager;

public class ApiModule extends ServletModule {

    private final ApiConfig config;
    private final String rootPath;

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

    public ApiModule(ApiConfig config) {
        this(config, "");
    }

    public ApiModule(ApiConfig config, String rootPath) {
        this.config = config;
        this.rootPath = rootPath;
    }

    @Override
    protected void configureServlets() {
        // TODO: These settings should be moved to ApiConfig. Only the bind and serv calls should be here.
        final Map<String, String> params = new HashMap<>();
        params.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());

        String requestFilters = joinClassNames(LoggingFilter.class, GZIPContentEncodingFilter.class);
        String responseFilters = joinClassNames(LoggingFilter.class, ServerAgentHeaderFilter.class, GZIPContentEncodingFilter.class);

        params.put(PackagesResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, requestFilters);
        params.put(PackagesResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, responseFilters);

        params.put(PackagesResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, RolesAllowedResourceFilterFactory.class.getName());
        params.put(GuiceContainer.RESOURCE_CONFIG_CLASS, "com.earaya.voodoo.config.ApiConfig");

        bind(ApiConfig.class).toInstance(config);
        serve(rootPath + "/*").with(GuiceContainer.class, params);
    }

    @SuppressWarnings("rawtypes")
    private String joinClassNames(Class... classes) {
        StringBuilder builder = new StringBuilder("");
        boolean first = true;
        for (Class theClass : classes) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(theClass.getName());
        }
        return builder.toString();
    }
}
