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

package com.talis.jersey.exceptions;

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
