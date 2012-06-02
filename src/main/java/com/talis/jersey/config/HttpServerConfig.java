package com.talis.jersey.config;

public class HttpServerConfig {
    public class SslConfig {
        private String keyStorePath;
        private String keyStorePassword;

        public String getKeyStorePath() {
            return keyStorePath;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }
    }

    private SslConfig sslConfig;
    private int port = 8080;

    /**
     * Default constructor for HttpServerConfig.
     * Sets port to 8080.
     * Mostly used for serialization by {@link ConfigFactory} when reading values from a file.
     */
    public HttpServerConfig() {
        // Used by ConfigFactory when reading values from file. Defaults port to 8080.
    }

    public HttpServerConfig(int port) {
        this.port = port;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
