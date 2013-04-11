package com.earaya.voodoo;

import org.eclipse.jetty.server.Handler;

/**
 * Adds functionality to a {@link VoodooApplication}.
 */
public interface Component {

    public Handler getHandler();
}
