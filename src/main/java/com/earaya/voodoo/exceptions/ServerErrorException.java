package com.earaya.voodoo.exceptions;

import javax.ws.rs.core.Response.Status;

public class ServerErrorException extends HttpException {

	public ServerErrorException(String msg) {
	     super(Status.INTERNAL_SERVER_ERROR, flattenMessage(msg));
	}

	private static String flattenMessage(String msg){
		return msg.replaceAll("\n", " ");
	}
}
