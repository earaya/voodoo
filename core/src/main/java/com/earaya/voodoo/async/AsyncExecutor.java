package com.earaya.voodoo.async;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class AsyncExecutor {

    private static final String ASYNC_RESULT_ERROR = "ASYNC_RESULT_ERROR";
    private static final String ASYNC_RESULT_SUCCESS = "ASYNC_RESULT_SUCCESS";

    public static ExecutorService threadPool = new ThreadPoolExecutor(1, 100,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    public static <T> T execute(HttpServletRequest request, final UncheckedCallable<T> callable) {
        List<T> asList = execute(request, new UncheckedCallable[] {callable});
        if (asList != null)
            return asList.get(0);
        return null;
    }

    public static <T> List<T> execute(HttpServletRequest request, List<UncheckedCallable<T>> callables) {
        return execute(request, callables.toArray(new UncheckedCallable[callables.size()]));
    }

    public static <T> List<T> execute(HttpServletRequest request, final UncheckedCallable<T>... callables) {
        if (!request.isAsyncSupported()) {
            return invokeAll(callables);
        }
        if (request.isAsyncStarted()) {
            return (List<T>) getAsyncResult(request);
        } else {
            final AsyncContext asyncContext = request.startAsync();
            asyncContext.setTimeout(60000);
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<T> allResults = invokeAll(callables);
                        asyncContext.getRequest().setAttribute(ASYNC_RESULT_SUCCESS, allResults);
                    } catch (Exception e) {
                        asyncContext.getRequest().setAttribute(ASYNC_RESULT_ERROR, e);
                    } finally {
                        asyncContext.dispatch();
                    }
                }
            });
            return null;
        }
    }

    public static Object getAsyncResult(HttpServletRequest request) {
        // First check if error was thrown
        final RuntimeException exception = (RuntimeException) request.getAttribute(ASYNC_RESULT_ERROR);
        if (exception != null) {
            throw exception;
        }
        // If no error thrown return the success result
        return request.getAttribute(ASYNC_RESULT_SUCCESS);
    }

    public static <T> List<T> invokeAll(UncheckedCallable<T>... callables) {
        try {
            List<Future<T>> allFutures = threadPool.invokeAll(Arrays.asList(callables));
            List<T> allResults = new ArrayList<>(allFutures.size());
            for (Future<T> future : allFutures) {
                allResults.add(future.get());
            }
            return allResults;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e.getCause() != null && e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new WebApplicationException(e);
        }
    }

}
