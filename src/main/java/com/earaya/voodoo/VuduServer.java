/*
 *    Copyright 2013 Esteban Araya
 *
 */

package com.earaya.voodoo;

import com.earaya.voodoo.config.HttpServerConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.yammer.metrics.reporting.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.spdy.server.http.HTTPSPDYServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VuduServer {

    private static final transient Logger LOG = LoggerFactory.getLogger(VuduServer.class);
    private final Server server;
    private final HttpServerConfig httpServerConfig;

    public VuduServer(HttpServerConfig httpServerConfig) {
        this.server = new Server();
        this.httpServerConfig = httpServerConfig;
        this.server.addConnector(getConnector());

        // Setup Metrics.
        ServletContextHandler metrics = new ServletContextHandler(server, "/metrics");
        metrics.addServlet(MetricsServlet.class, "/*");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void start(final Module... modules) throws Exception {
        LOG.info("Starting http server on port {}", httpServerConfig.getPort());

        // Setup Guice
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(DefaultServlet.class, "/");
        context.addFilter(GuiceFilter.class, "/*", null);
        context.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(modules);
            }
        });

        try {
            server.start();
        } catch (Exception e) {
            LOG.error("Error starting HTTP Server", e);
            throw e;
        }
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void stop() throws Exception {
        server.stop();
    }

    public void waitForShutdown() throws InterruptedException {
        server.join();
    }

    private ServerConnector getConnector() {
        ServerConnector connector;

        if (null == httpServerConfig.getSslConfig()) {
            connector = new HTTPSPDYServerConnector(this.server);
        } else {
            SslContextFactory sslContextFactory = new SslContextFactory(httpServerConfig.getSslConfig().getKeyStorePath());
            sslContextFactory.setKeyStorePassword(httpServerConfig.getSslConfig().getKeyStorePassword());
            sslContextFactory.setProtocol("TLSv1");
            connector = new HTTPSPDYServerConnector(this.server, sslContextFactory);
        }
        connector.setPort(httpServerConfig.getPort());
        return connector;
    }
}
