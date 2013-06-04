package com.earaya.voodoosample;

import com.earaya.voodoo.rest.validation.Editable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import java.util.Map;

@Path("/users")
@Api(value = "/users", description = "User operations")
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

    @PUT
    public User updateUser(@Editable(type = User.class, fields = {"name"}) Map updates) {
        // Editable annotation makes sure only the editable fields are being updated and validates them according to
        // the type specified. In this case, it validates against the user class.
        return new User();
    }
}
