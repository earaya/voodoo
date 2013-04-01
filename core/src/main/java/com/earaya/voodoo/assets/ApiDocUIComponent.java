package com.earaya.voodoo.assets;

import com.earaya.voodoo.Component;
import com.earaya.voodoo.VoodooApplication;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

public class ApiDocUIComponent implements Component {

    private String rootPath = "/";
    private String webDir = "swagger-ui";

    public ApiDocUIComponent root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    @Override
    public void start(VoodooApplication application) {
        application.addHandler(getHandler());
    }

    protected ContextHandler getHandler() {
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("woff", "font/woff");
        mimeTypes.addMimeMapping("ttf", "font/ttf");

        ResourceHandler resourceHandler = new ResourceHandler();

        //String resourceBase = this.getClass().getClassLoader().getResource(webDir).toExternalForm();
        //resourceHandler.setResourceBase(resourceBase);
        resourceHandler.setBaseResource(Resource.newClassPathResource(webDir));
        resourceHandler.setMimeTypes(mimeTypes);
        //TODO: should we set cache control?

        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[]{resourceHandler});

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath(rootPath);

        contextHandler.setHandler(collection);
        return contextHandler;
    }
}
