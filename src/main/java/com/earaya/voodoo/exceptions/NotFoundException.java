package com.earaya.voodoo.exceptions;

import javax.ws.rs.core.Response.Status;

public class NotFoundException extends HttpException {

    public NotFoundException(String msg) {
        super(Status.NOT_FOUND, flattenMessage(msg));
    }

    private static String flattenMessage(String msg) {
        return msg.replaceAll("\n", " ");
    }
}
