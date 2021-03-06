package com.earaya.voodoo.assets;

import com.earaya.voodoo.Component;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Component} that serves static assets. There are two flavors of this class: {@link FilePathAssetsComponent}
 * and {@link ClassPathAssetsComponent}.
 */
public abstract class AssetsComponent implements Component {

    protected String rootPath;
    protected final Resource resourceBase;
    private int cacheAge = 30 * 24 * 60 * 60; // One month in seconds.
    private Logger logger = LoggerFactory.getLogger(AssetsComponent.class);

    /**
     * The Resource at which the static assets are found.
     *
     * @param resourceBase
     */
    protected AssetsComponent(Resource resourceBase) {
        this.resourceBase = resourceBase;
    }

    /**
     * Sets the URL root path at which to serve the static assets.
     *
     * @param rootPath
     * @return
     */
    public AssetsComponent root(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    /**
     * Sets the max max-age directive.
     *
     * @param cacheAge (in seconds)
     * @return
     */
    public AssetsComponent cacheAge(int cacheAge) {
        this.cacheAge = cacheAge;
        return this;
    }

    @Override
    public ContextHandler getHandler() {
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("woff", "application/x-font-woff");
        mimeTypes.addMimeMapping("ttf", "font/ttf");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setMimeTypes(mimeTypes);
        resourceHandler.setCacheControl(String.format("max-age=%s,public", cacheAge));
        resourceHandler.setBaseResource(resourceBase);

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(resourceHandler);
        gzipHandler.setMimeTypes("text/html,text/plain,text/xml,application/xhtml+xml,text/css,application/javascript,application/x-javascript,application/json,image/svg+xml");

        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[]{gzipHandler, resourceHandler, new DefaultHandler()});

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath(rootPath);

        contextHandler.setHandler(collection);
        return contextHandler;
    }
}
