package com.earaya.voodoosample;

import com.earaya.voodoo.auth.Auth;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;

@Path("/sample")
@Consumes("application/json")
@Produces("application/json")
public class Resource {

    @GET
    @Path("/test/{name}")
    @Timed
    public User sayHi(@Auth User user, @PathParam("name") String name) {
        User u = new User();
        u.name = name;
        return u;
    }

    @POST
    @Path("/echo")
    public User respondWithUser(@Valid User user) {
        return user;
    }
}
