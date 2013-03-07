package com.earaya.voodoo.apitest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.earaya.voodoo.exceptions.BadRequestException;
import com.earaya.voodoo.exceptions.NotFoundException;
import com.earaya.voodoo.exceptions.ServerErrorException;
import com.earaya.voodoo.exceptions.ServiceUnavailableException;

@Path("/stub")
public class StubResource {

	public static final String SORRY_BAD_REQUEST = "Sorry, bad request";
	public static final String SORRY_NOT_FOUND = "Sorry, not found";
	public static final String SORRY_INTERNAL_ERROR = "Sorry, internal error";
	public static final String SORRY_UNAVAILABLE_ERROR = "Sorry, service unavailable";

	@GET
	public String get() {
		return "hi";
	}
	
	@GET
	@Path("badReq")
	public String get400() {
		throw new BadRequestException(SORRY_BAD_REQUEST);
	}
	
	@GET
	@Path("notFound")
	public String get404() {
		throw new NotFoundException(SORRY_NOT_FOUND);
	}
	
	@GET
	@Path("internalErr")
	public String get500() {
		throw new ServerErrorException(SORRY_INTERNAL_ERROR);
	}
	
	@GET
	@Path("unavailableErr")
	public String get503() {
		throw new ServiceUnavailableException(SORRY_UNAVAILABLE_ERROR);
	}
	
	@GET
	@Path("unavailableErr/3min")
	public String get503WithRetry() {
		throw new ServiceUnavailableException(SORRY_UNAVAILABLE_ERROR, 180);
	}
}
