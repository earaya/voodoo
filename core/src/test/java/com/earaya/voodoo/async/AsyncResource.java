package com.earaya.voodoo.async;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@Path("async")
public class AsyncResource {

    @Context
    HttpServletRequest request;

    @GET
    @Path("/echo/{value}")
    public String async(@PathParam("value") final String value) {
        return AsyncExecutor.execute(request, new UncheckedCallable<String>() {
            @Override
            public String call() {
                return value;
            }
        });
    }

    @GET
    @Path("exception/{code}")
    public Response exception(@PathParam("code") final int statusCode) {
        return AsyncExecutor.execute(request, new UncheckedCallable<Response>() {
            @Override
            public Response call() {
                throw new WebApplicationException(statusCode);
            }
        });
    }

    @GET
    @Path("multiple/{count}")
    public String multiple(@PathParam("count") final int count) {
        List<UncheckedCallable<String>> callables = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            callables.add(new StringCallable("success"));
        }
        List<String> allStrings = AsyncExecutor.execute(request, callables);
        if (allStrings != null)
            return allStrings.toString();

        return null;
    }

    private static class StringCallable implements UncheckedCallable<String> {
        private String result;

        public StringCallable(String result) {
            this.result = result;
        }

        @Override
        public String call() {
            return result;
        }
    }
}
