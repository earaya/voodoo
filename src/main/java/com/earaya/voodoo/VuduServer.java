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
 *    limitations under the License. *
 */

package com.earaya.voodoo;

import com.earaya.voodoo.config.HttpServerConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.*;
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
        this.httpServerConfig = httpServerConfig;
        server = new Server();
        server.addConnector(getConnector());
    }

    public void initialize(final Module[] modules, final ContextHandler... contextHandlers) {
        HandlerCollection handlerCollection = new ContextHandlerCollection();
        for(ContextHandler contextHandler : contextHandlers) {
            handlerCollection.addHandler(contextHandler);
        }
        handlerCollection.addHandler(getVoodooContextHandler(modules));
        server.setHandler(handlerCollection);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void start() throws Exception {
        LOG.info("Starting http server on port {}", httpServerConfig.getPort());

        try {
            server.start();
        } catch (Exception e) {
            LOG.error("Error starting HTTP Server", e);
            throw e;
        }
    }

    private ContextHandler getVoodooContextHandler(final Module[] modules) {
        // Question: Should we inject Jetty's GzipHandler here by default? Will this conflict with the JersyGzip stuff
        // in ResourceServletModule?
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(DefaultServlet.class, "/");
        context.addFilter(GuiceFilter.class, "/*", null);
        context.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(modules);
            }
        });
        return context;
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
