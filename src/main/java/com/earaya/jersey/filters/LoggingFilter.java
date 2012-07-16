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

package com.talis.jersey.filters;

import java.util.Random;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);
    
	public static final String X_TALIS_RESPONSE_ID = "X-TALIS-RESPONSE-ID";
	public static final String REQUEST_UID = "R_UID";
	static final String REQUEST_START_TIME = "R_START_TIME";
	private final Random r = new Random();
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
    	MDC.put(REQUEST_UID, getRUID(8));
    	MDC.put(REQUEST_START_TIME, new Long(System.currentTimeMillis()));
    	if (LOG.isInfoEnabled()) {
    		LOG.info(String.format("Starting request %s",request.getPath()));
    	}
        
		return request;
	}
	
	@Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		Object object = MDC.get(REQUEST_START_TIME);
		if (object instanceof Long) {
			Long startTime = (Long)object;
			String requestUid = (String)MDC.get(REQUEST_UID);
			long duration = System.currentTimeMillis() - startTime;
			response.getHttpHeaders().add(X_TALIS_RESPONSE_ID, requestUid);  
			LOG.info("Finished request in {} milliseconds.",duration);
		} else {
			LOG.warn("Finished request, but did not have a start time to compare with. No metrics have been recorded.");
		}
		return response;
    }
	
	private String getRUID(int len){
	    StringBuffer uid = new StringBuffer();
	    for (int i=0;i<len;i++) {
			int rand = r.nextInt(10000);
			int mod36 = rand % 36;
	    	encodeAndAdd(uid, mod36);
	    }
	    return uid.toString();
    }

	private void encodeAndAdd(StringBuffer ret, long mod36Val) {
		if (mod36Val < 10) { 
			ret.append((char)(((int)'0') + (int)mod36Val));
		} else { 
			ret.append((char)(((int)'a') + (int)(mod36Val - 10)));
		}
	}
}
