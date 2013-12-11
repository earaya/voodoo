package com.earaya.voodoo.config;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class SslConnectorConfig extends ConnectorConfig {

    private String keyStorePath;
    private String keyStorePassword;
    private String keyManagerPassword;

    public SslConnectorConfig(int port, String keyStorePath, String keyStorePassword, String keyManagerPassword) {
        super(port);
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyManagerPassword = keyManagerPassword;
    }

    public Connector getConnector(Server server) {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStorePath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);

        ServerConnector connector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));

        connector.setPort(getPort());
        return connector;
    }
}
