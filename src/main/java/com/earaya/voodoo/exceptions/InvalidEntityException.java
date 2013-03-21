package com.earaya.voodoo.exceptions;

import com.google.common.collect.ImmutableList;

import javax.ws.rs.core.Response;

public class InvalidEntityException extends HttpException {
    private static final long serialVersionUID = -8762073181655035705L;

    private final ImmutableList<String> errors;

    public InvalidEntityException(String message, Iterable<String> errors) {
        super(Response.Status.BAD_REQUEST, message);
        this.errors = ImmutableList.copyOf(errors);
    }

    public ImmutableList<String> getErrors() {
        return errors;
    }
}
