package com.earaya.voodoosample;

import com.earaya.voodoo.auth.rest.Auth;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;

@Path("/sample")
@Api(value = "/sample", description = "Test operations")
@Consumes("application/json")
@Produces("application/json")
public class Resource {

    @GET
    @Path("/test/{name}")
    @ApiOperation(value = "say hi", responseClass = "com.earaya.voodoosample.User")
    @Timed
    public User sayHi(@Auth User user, @PathParam("name") String name) {
        User u = new User();
        u.name = name;
        return u;
    }

    @POST
    @ApiOperation(value = "echo", responseClass = "com.earaya.voodoosample.User")
    @Path("/echo")
    public User respondWithUser(@Valid User user) {
        return user;
    }
}
