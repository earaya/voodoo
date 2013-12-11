package com.earaya.voodoo.config;

public class ServerConfig {

    private ConnectorConfig[] connectorConfigs;

    public ServerConfig(ConnectorConfig... connectorConfigs) {
        this.connectorConfigs = connectorConfigs;
    }

    public ConnectorConfig[] getConnectorConfigs() {
        return connectorConfigs;
    }
}
