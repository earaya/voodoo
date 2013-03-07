package com.earaya.voodoo.exceptions;

import javax.ws.rs.core.Response.Status;

public class HttpException extends RuntimeException {
	
	private final Status status;
	private final String message;
	private final int retryAfter;
	
	public HttpException(Status status, String message, int retryAfter){
		this.status = status;
		this.message = message;
		this.retryAfter = retryAfter;
	}
	
	public HttpException(Status status, String message){
		this(status, message, -1);
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public int getRetryAfter() {
		return retryAfter;
	}

}
