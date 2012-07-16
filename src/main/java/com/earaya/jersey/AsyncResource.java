package com.talis.jersey;

import com.google.inject.Inject;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AsyncResource {
    @Inject
    private HttpServletRequest httpRequest;

    @Inject
    private HttpServletResponse httpResponse;


    protected <T> T await(final Future<T> future) throws Throwable {
        final Continuation continuation = ContinuationSupport.getContinuation(httpRequest);
        try {
            continuation.suspend(httpResponse);
            return future.get();
        } catch (ExecutionException ee) {
            throw ee.getCause();
        } finally {
            if (continuation.isSuspended()) {
                continuation.complete();
            }
        }
    }
}
