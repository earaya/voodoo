package com.earaya.voodoo.guice;

import com.earaya.voodoo.filters.ServerInfo;
import com.google.inject.AbstractModule;

public class GenericServerInfoModule extends AbstractModule {

    public static final String SERVER_IDENTIFIER_DEFAULT = "Jetty/Jersey Web Server";
    public static final String SERVER_IDENTIFIER_PROPERTY = "com.earaya.voodoo.guice.serverid";

    @Override
    protected void configure() {
        bind(ServerInfo.class).toInstance(new ServerInfo() {
            @Override
            public String getServerIdentifier() {
                return System.getProperty(SERVER_IDENTIFIER_PROPERTY, SERVER_IDENTIFIER_DEFAULT);
            }
        });
    }
}
