/*
 *    Copyright 2011 Talis Systems Ltd
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
