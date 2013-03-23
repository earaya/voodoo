package com.earaya.voodoo.components;

import com.earaya.voodoo.VoodooApplication;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.gzip.GzipHandler;

public class StaticComponent implements Component {
    private final String filePath;
    private String rootPath = "/";
    int cacheAge = 30 * 24 * 60 * 60; // One month in seconds.

    public StaticComponent(String filePath) {
        this.filePath = filePath;
    }

    public StaticComponent root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public StaticComponent cacheAge(int cacheAge) {
        this.cacheAge = cacheAge;
        return this;
    }

    @Override
    public void start(VoodooApplication application) {
        HandlerCollection handlerCollection = (HandlerCollection) application.server.getHandler();
        handlerCollection.addHandler(getHandler());
    }

    private ContextHandler getHandler() {
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("woff", "font/woff");
        mimeTypes.addMimeMapping("ttf", "font/ttf");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setMimeTypes(mimeTypes);
        resourceHandler.setCacheControl(String.format("max-age=%s,public", cacheAge));
        resourceHandler.setResourceBase(filePath);

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(resourceHandler);
        gzipHandler.setMimeTypes("text/html,text/plain,text/xml,application/xhtml+xml,text/css,application/javascript,application/x-javascript,application/json,image/svg+xml");

        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[]{gzipHandler, resourceHandler});

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath(rootPath);

        contextHandler.setHandler(collection);
        return contextHandler;
    }
}
