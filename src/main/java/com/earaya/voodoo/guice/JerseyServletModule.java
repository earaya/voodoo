package com.earaya.voodoo.guice;

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

public class JerseyServletModule extends ServletModule {

    private final String[] propertyPackages;
    public static final String DISABLE_DEFAULT_FILTERS_PROPERTY = "com.earaya.voodoo.guice.disable-default-filters";


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

    public JerseyServletModule(String... propertyPackages) {
        this.propertyPackages = propertyPackages;
    }

    @Override
    protected void configureServlets() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put(PackagesResourceConfig.PROPERTY_PACKAGES, joinPackageNames(propertyPackages));
        params.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE.toString());

        String requestFilters = joinClassNames(LoggingFilter.class, GZIPContentEncodingFilter.class);
        String responseFilters = joinClassNames(LoggingFilter.class, ServerAgentHeaderFilter.class, GZIPContentEncodingFilter.class);

        params.put(PackagesResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, requestFilters);
        params.put(PackagesResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, responseFilters);

        params.put(PackagesResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, RolesAllowedResourceFilterFactory.class.getName());

        serve("/*").with(GuiceContainer.class, params);
    }

    private String joinPackageNames(String... packageName) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        if (!Boolean.getBoolean(DISABLE_DEFAULT_FILTERS_PROPERTY)) {
            builder.append("com.earaya.voodoo");
            first = false;
        }

        for (String name : packageName) {
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
    private String joinClassNames(Class... clazz) {
        StringBuilder builder = new StringBuilder("");
        boolean first = true;
        for (Class theClass : clazz) {
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
