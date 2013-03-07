package com.earaya.voodoo.exceptions;

import javax.ws.rs.core.Response.Status;

public class ServiceUnavailableException extends HttpException {
	
	public static int DEFAULT_RETRY = 300; // 5 min

	public ServiceUnavailableException(String msg) {
		this(msg, DEFAULT_RETRY);
	}

	public ServiceUnavailableException(String msg, int retryAfter) {
	     super(Status.SERVICE_UNAVAILABLE, flattenMessage(msg), retryAfter);
	}

	private static String flattenMessage(String msg){
		return msg.replaceAll("\n", " ");
	}
}
