package com.earaya.voodoo.sample;

import com.earaya.voodoo.auth.Auth;
import com.sun.jersey.core.header.MediaTypes;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;

/**
 * Created with IntelliJ IDEA.
 * SampleUser: earaya
 * Date: 3/8/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */

@Path("/sample")
public class SampleResource {

    @GET
    @Path("/test/{name}")
    @Consumes(value = "application/json")
    @Produces(value = "application/json")
    @Timed
    public SampleUser sayHi(@Auth SampleUser user, @PathParam("name") String myName) {
        SampleUser u = new SampleUser();
        u.name = myName;
        return u;
    }

    @POST
    @Consumes("application/json")
    @Produces(value = "application/json")
    @Path("/echo")
    public SampleUser respondWithUser(@Valid SampleUser user) {
        return user;
    }
}
