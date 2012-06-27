/*
 *    Copyright 2011 Talis Systems Ltd
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

package com.talis.jersey;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.talis.jersey.config.HttpServerConfig;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.spdy.http.HTTPSPDYServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.yammer.metrics.reporting.MetricsServlet;

public class HttpServer {

	private static final transient Logger LOG = LoggerFactory.getLogger(HttpServer.class);
	private final Server server;

    public HttpServer(HttpServerConfig httpServerConfig) {
        this.server = new Server();
        this.server.addConnector(getConnector(httpServerConfig));

        // Setup Metrics.
        ServletContextHandler metrics = new ServletContextHandler(server, "/metrics");
        metrics.addServlet(MetricsServlet.class, "/*");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void start(final Module... modules) throws Exception {
        LOG.info("Starting http server on port {}", server.getConnectors()[0].getPort());

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
            LOG.error("Error starting HTTP Server" , e);
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

    private Connector getConnector(HttpServerConfig httpServerConfig) {
        Connector connector;

        if(null == httpServerConfig.getSslConfig()) {
            connector = new HTTPSPDYServerConnector();
        } else {
            SslContextFactory sslContextFactory = new SslContextFactory(httpServerConfig.getSslConfig().getKeyStorePath());
            sslContextFactory.setKeyStorePassword(httpServerConfig.getSslConfig().getKeyStorePassword());
            sslContextFactory.setProtocol("TLSv1");

            connector = new HTTPSPDYServerConnector(sslContextFactory);
        }

        connector.setPort(httpServerConfig.getPort());
        return connector;
    }
}
