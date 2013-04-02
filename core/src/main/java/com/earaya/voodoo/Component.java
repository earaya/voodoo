package com.earaya.voodoo;

/**
 * Adds functionality to a {@link VoodooApplication}.
 */
public interface Component {

    /**
     * Starts the component. In here you'll want to add your {@link org.eclipse.jetty.server.Handler} instance to the using {@link VoodooApplication#addHandler}.
     * Components are started when the VoodooApplication is started.
     *
     * @param application the @see VoodooApplication.
     */
    public void start(VoodooApplication application);
}
