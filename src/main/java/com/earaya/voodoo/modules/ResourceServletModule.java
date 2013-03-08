/*
 *    Copyright 2010 Talis Systems Ltd
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

import com.earaya.voodoo.filters.LoggingFilter;
import com.earaya.voodoo.filters.ServerAgentHeaderFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.container.filter.RolesAllowedResourceFilterFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogManager;

public class ResourceServletModule extends ServletModule {

    private final List<String> resourcePackages = new ArrayList<>();
    private String rootPath = "";
    public static final String DISABLE_DEFAULT_FILTERS_PROPERTY = "com.earaya.voodoo.modules.disable-default-filters";


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

    public ResourceServletModule(String resourcePackage) {
        this.resourcePackages.add(resourcePackage);
    }

    public ResourceServletModule packageName(String resourcePackage) {
        this.resourcePackages.add(resourcePackage);
        return this;
    }

    public ResourceServletModule root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    @Override
    protected void configureServlets() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(PackagesResourceConfig.PROPERTY_PACKAGES, joinPackageNames(resourcePackages));
        params.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());

        String requestFilters = joinClassNames(LoggingFilter.class, GZIPContentEncodingFilter.class);
        String responseFilters = joinClassNames(LoggingFilter.class, ServerAgentHeaderFilter.class, GZIPContentEncodingFilter.class);

        params.put(PackagesResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, requestFilters);
        params.put(PackagesResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, responseFilters);

        params.put(PackagesResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, RolesAllowedResourceFilterFactory.class.getName());

        serve(rootPath + "/*").with(GuiceContainer.class, params);
    }

    private String joinPackageNames(List<String> packages) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        if (!Boolean.getBoolean(DISABLE_DEFAULT_FILTERS_PROPERTY)) {
            builder.append("com.earaya.voodoo");
            first = false;
        }

        for (String name : packages) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }
            builder.append(name);
        }
        return builder.toString();
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
