package com.earaya.voodoo.exceptions;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class DefaultExceptionMapper implements ExceptionMapper<HttpException> {

    @Override
    public Response toResponse(HttpException exception) {
        ResponseBuilder response =
                Response.status(exception.getStatus())
                        .entity(exception.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        if (exception.getRetryAfter() >= 0) {
            response.header("Retry-After", exception.getRetryAfter());
        }
        return response.build();
    }

}