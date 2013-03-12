package com.earaya.voodoo.sample;


import com.earaya.voodoo.VoodooServer;
import com.earaya.voodoo.auth.basic.BasicAuthProvider;
import com.earaya.voodoo.config.HttpServerConfig;
import com.earaya.voodoo.ApiModule;
import com.earaya.voodoo.modules.MetricsModule;
import com.google.inject.Module;

public class SampleServer {
    public static void main(String[] args) throws Exception {
        ApiModule module = new ApiModule("com.earaya.voodoo.sample").module(new MetricsModule());
        module.addProvider(new BasicAuthProvider<SampleUser>(new SampleAuthenticator(), "realm"));

        VoodooServer vuduServer = new VoodooServer(new HttpServerConfig(8080), module);
        vuduServer.start();
    }
}
