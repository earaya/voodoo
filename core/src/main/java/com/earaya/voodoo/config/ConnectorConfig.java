package com.earaya.voodoo.config;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class ConnectorConfig {

    private int port = 8080;

    /**
     * Default constructor for ConnectorConfig.
     * Sets port to 8080.
     * Mostly used for serialization by {@link ConfigFactory} when reading values from a file.
     */
    public ConnectorConfig() {
        // Used by ConfigFactory when reading values from file. Defaults port to 8080.
    }

    public ConnectorConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Connector getConnector(Server server) {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        return connector;
    }
}
