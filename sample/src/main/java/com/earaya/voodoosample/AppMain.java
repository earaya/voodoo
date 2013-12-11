package com.earaya.voodoosample;

import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.assets.ClassPathAssetsComponent;
import com.earaya.voodoo.assets.FilePathAssetsComponent;
import com.earaya.voodoo.config.ConnectorConfig;
import com.earaya.voodoo.rest.RestComponent;
import com.earaya.voodoo.rest.auth.basic.BasicAuthProvider;

public class AppMain {
    public static void main(String[] args) throws Exception {
        VoodooApplication app = new VoodooApplication(
                new ConnectorConfig(8080),
                new ClassPathAssetsComponent("swagger-ui").root("/api-ui"),
                new FilePathAssetsComponent(".") // Serve static files on this folder,
                        .root("/static"), // under /static path.
                new RestComponent("com.earaya.voodoosample") // Scan this package for resources,
                        .provider(new BasicAuthProvider<User>(new SecretAuthenticator(), "realm")) // protect using BasicAuth,
                        .root("/api") // and serve under /api
        );
        app.start();
    }
}
