package com.earaya.voodoo.assets;

import com.earaya.voodoo.Component;
import com.earaya.voodoo.VoodooApplication;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

abstract class AssetsComponent implements Component {

    protected String rootPath;
    protected final String assetsPath;
    private int cacheAge = 30 * 24 * 60 * 60; // One month in seconds.
    private Logger logger = LoggerFactory.getLogger(AssetsComponent.class);

    protected AssetsComponent(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    public AssetsComponent root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public AssetsComponent cacheAge(int cacheAge) {
        this.cacheAge = cacheAge;
        return this;
    }

    @Override
    public void start(VoodooApplication application) {
        try {
            application.addHandler(getHandler(getBaseResource()));
        } catch (IOException e) {
            logger.warn("Unable to load base resource: " + assetsPath, e);
        }
    }

    private ContextHandler getHandler(Resource base) {
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("woff", "font/woff");
        mimeTypes.addMimeMapping("ttf", "font/ttf");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setMimeTypes(mimeTypes);
        resourceHandler.setCacheControl(String.format("max-age=%s,public", cacheAge));
        resourceHandler.setBaseResource(base);

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

    protected abstract Resource getBaseResource() throws IOException;
}
