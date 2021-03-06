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

import com.earaya.voodoo.config.ConnectorConfig;
import com.earaya.voodoo.config.ServerConfig;
import com.wordnik.swagger.jaxrs.JaxrsApiReader;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;


/**
 * Sets up and starts a HTTP server that surfaces the functionality of the {@link Component}s contained here.
 */
public class VoodooApplication {

    static {
        JaxrsApiReader.setFormatString("");
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }
    }

    private static final transient Logger LOG = LoggerFactory.getLogger(VoodooApplication.class);
    private final Server server;
    private final ServerConfig serverConfig;
    private final Component[] components;
    private final ContextHandlerCollection handlerCollection = new ContextHandlerCollection();

    public VoodooApplication(ConnectorConfig connectorConfig, Component... components) {
        this(new ServerConfig(connectorConfig), components);
    }

    public VoodooApplication(ServerConfig serverConfig, Component... components) {
        this.serverConfig = serverConfig;
        this.components = components;

        server = new Server();
        for (ConnectorConfig connectorConfig : serverConfig.getConnectorConfigs()) {
            server.addConnector(connectorConfig.getConnector(server));
        }

        server.setHandler(handlerCollection);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void start() throws Exception {
        //LOG.info("Starting http server on port {}", httpServerConfig.getPort());
        initComponents();

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

    private void initComponents() {
        for (Component component : components) {
            handlerCollection.addHandler(component.getHandler());
        }
    }
}
