package com.earaya.voodoo.sample;


import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.auth.basic.BasicAuthProvider;
import com.earaya.voodoo.components.RestComponent;
import com.earaya.voodoo.components.StaticComponent;
import com.earaya.voodoo.config.HttpServerConfig;
import com.earaya.voodoo.modules.MetricsModule;
import com.google.inject.Guice;

public class SampleServer {
    public static void main(String[] args) throws Exception {
        RestComponent module = new RestComponent("com.earaya.voodoo.sample");
        module.provider(new BasicAuthProvider<SampleUser>(new SampleAuthenticator(), "realm"));
        VoodooApplication vuduServer = new VoodooApplication(new HttpServerConfig(8080), new StaticComponent("."), module);
        vuduServer.start();
    }
}
