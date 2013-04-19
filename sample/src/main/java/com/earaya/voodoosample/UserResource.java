package com.earaya.voodoosample;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;

@Path("/user")
@Api(value = "/user", description = "User operations")
@Consumes("application/json")
@Produces("application/json")
public class UserResource {

    @GET
    @Timed
    @Path("/{name}")
    @ApiOperation(value = "Get user by name", responseClass = "com.earaya.voodoosample.User")
    public User getUser(@ApiParam(value = "Name of the user to fetch") @PathParam("name")
                            String name) {
        User u = new User();
        u.name = name;
        return u;
    }
}
