package com.earaya.voodoo.sample;

import com.earaya.voodoo.VuduResourceConfig;
import com.earaya.voodoo.VuduServer;
import com.earaya.voodoo.auth.basic.BasicAuthProvider;
import com.earaya.voodoo.config.HttpServerConfig;
import com.earaya.voodoo.modules.GenericServerInfoModule;
import com.earaya.voodoo.modules.ResourceServletModule;
import com.earaya.voodoo.modules.VuduModule;
import com.google.inject.Module;

public class SampleServer {
    public static void main(String[] args) throws Exception {

        VuduResourceConfig config = new VuduResourceConfig();
        config.addSingleton(new BasicAuthProvider<User>(new SimpleAuthenticator(), "realm"));

        VuduServer vuduServer = new VuduServer(new HttpServerConfig(8080));
        vuduServer.initialize(new Module[] {
                new VuduModule(config), // Sets up a lot fo the Vudu magic.
                new ResourceServletModule("com.earaya.voodoo.sample"), // Sets up resources.
                new GenericServerInfoModule() // Some server info.
        });
        vuduServer.start();
    }
}
