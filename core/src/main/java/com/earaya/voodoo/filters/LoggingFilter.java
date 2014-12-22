package com.earaya.voodoo.filters;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A {@link Filter} that logs a unique ID on every request, and times every request.
 */
public class LoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    public static final String X_VOODOO_RESPONSE_ID = "X-VOODOO-RESPONSE-ID";
    public static final String REQUEST_UID = "R_UID";

    private final Supplier<String> ruidSupplier;

    public LoggingFilter() {
        this(new RuidSupplier());
    }

    @VisibleForTesting
    LoggingFilter(final Supplier<String> ruidSupplier) {
        this.ruidSupplier = ruidSupplier;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No need to do anything here
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Not entirely sure when this will ever be false, but test for it anyway
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String ruid = ruidSupplier.get();

        MDC.put(REQUEST_UID, ruid);
        if (!response.containsHeader(X_VOODOO_RESPONSE_ID)) {
            response.addHeader(X_VOODOO_RESPONSE_ID, ruid);
        }

        final Stopwatch timer = Stopwatch.createStarted();
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting request {}", request.getRequestURI());
        }
        try {
            chain.doFilter(request, response);
        } finally {
            timer.stop();
            if (LOG.isInfoEnabled()) {
                LOG.info("Finished request in {}.", timer.toString());
            }

            // Cleanup MDC Variables
            MDC.remove(REQUEST_UID);
        }
    }

    @Override
    public void destroy() {
        // No need to do anything here
    }
}
