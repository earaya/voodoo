package com.earaya.voodoo.exceptions;

import javax.ws.rs.core.Response.Status;

public class BadRequestException extends HttpException {

	public BadRequestException(String msg) {
	     super(Status.BAD_REQUEST, flattenMessage(msg));
	}

	private static String flattenMessage(String msg){
		return msg.replaceAll("\n", " ");
	}
}
