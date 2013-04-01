package com.earaya.voodoosample;

import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.auth.rest.basic.BasicAuthProvider;
import com.earaya.voodoo.assets.ApiDocUIComponent;
import com.earaya.voodoo.rest.RestComponent;
import com.earaya.voodoo.assets.StaticComponent;
import com.earaya.voodoo.config.HttpServerConfig;

public class Server {
    public static void main(String[] args) throws Exception {
        VoodooApplication app = new VoodooApplication(
                new HttpServerConfig(8080),
                new ApiDocUIComponent().root("/api-ui"),
                new StaticComponent(".") // Serve static files on this folder,
                    .root("/static"), // under /static path.
                new RestComponent("com.earaya.voodoosample") // Scan this package for resources,
                        .provider(new BasicAuthProvider<User>(new SecretAuthenticator(), "realm")) // protect using BasicAuth,
                        .root("/api") // and serve under /api
        );
        app.start();
    }
}
