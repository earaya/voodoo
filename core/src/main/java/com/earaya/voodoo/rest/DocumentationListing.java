package com.earaya.voodoo.rest;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListing;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api("/api-docs")
@Path("/api-docs")
@Produces({"application/json"})
public class DocumentationListing extends ApiListing {
}
