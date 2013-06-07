package com.earaya.voodoo.async;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncExecutor {

    private static final String ASYNC_RESULT_ERROR = "ASYNC_RESULT_ERROR";
    private static final String ASYNC_RESULT_SUCCESS = "ASYNC_RESULT_SUCCESS";

    private static ExecutorService threadPool = new ThreadPoolExecutor(1, 100,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    public static <T> T execute(HttpServletRequest request, final UncheckedCallable<T> callable) {
        if (!request.isAsyncSupported()) {
            return callable.call();
        }
        if (request.isAsyncStarted()) {
            // First check if error was thrown
            final RuntimeException exception = (RuntimeException) request.getAttribute(ASYNC_RESULT_ERROR);
            if (exception != null) {
                throw exception;
            }
            // If no error thrown return the success result
            return (T) request.getAttribute(ASYNC_RESULT_SUCCESS);
        } else {
            final AsyncContext asyncContext = request.startAsync();
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        T successResult = callable.call();
                        asyncContext.getRequest().setAttribute(ASYNC_RESULT_SUCCESS, successResult);
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

}
