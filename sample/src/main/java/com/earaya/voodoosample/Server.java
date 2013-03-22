package com.earaya.voodoosample;

import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.auth.basic.BasicAuthProvider;
import com.earaya.voodoo.components.RestComponent;
import com.earaya.voodoo.components.StaticComponent;
import com.earaya.voodoo.config.HttpServerConfig;
import com.earaya.voodoo.modules.MetricsModule;
import com.google.inject.Guice;

public class Server {
    public static void main(String[] args) throws Exception {
        VoodooApplication app = new VoodooApplication(
                new HttpServerConfig(8080),
                new StaticComponent(".") // Serve static files on this folder,
                    .root("/static"), // under /static path.
                new RestComponent("com.earaya.voodoosample") // Scan this package for resources,
                        .provider(new BasicAuthProvider<User>(new SecretAuthenticator(), "realm")) // protect using BasicAuth,
                        .root("/api") // and serve under /api
        );
        app.start();
    }
}
