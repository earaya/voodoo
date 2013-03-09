package com.earaya.voodoo.sample;

import com.earaya.voodoo.auth.Auth;
import com.earaya.voodoo.auth.basic.BasicAuthProvider;
import com.sun.jersey.guice.JerseyServletModule;
import com.yammer.metrics.annotation.Timed;
import sun.awt.TimedWindowEvent;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 * Created with IntelliJ IDEA.
 * User: earaya
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
    public User sayHi(@Auth User user, @PathParam("name") String myName) {
        User u = new User();
        u.name = myName;
        return u;
    }
}
