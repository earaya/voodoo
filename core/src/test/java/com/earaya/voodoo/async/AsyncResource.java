package com.earaya.voodoo.async;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.xml.ws.Response;

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

}
