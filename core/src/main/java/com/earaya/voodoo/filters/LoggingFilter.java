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

package com.earaya.voodoo.filters;

import com.google.common.base.Supplier;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    public static final String X_VOODOO_RESPONSE_ID = "X-VOODOO-RESPONSE-ID";
    public static final String REQUEST_UID = "R_UID";
    static final String REQUEST_START_TIME = "R_START_TIME";

    private final ThreadLocal<Long> requestStartTime = new ThreadLocal<>();
    private final Supplier<String> ruidSupplier;

    public LoggingFilter() {
        this(new RuidSupplier());
    }

    public LoggingFilter(Supplier<String> ruidSupplier) {
        this.ruidSupplier = ruidSupplier;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        MDC.put(REQUEST_UID, ruidSupplier.get());
        requestStartTime.set(System.currentTimeMillis());
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting request {}", request.getPath());
        }

        return request;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        String requestUid = (String) MDC.get(REQUEST_UID);
        response.getHttpHeaders().add(X_VOODOO_RESPONSE_ID, requestUid);

        Long startTime = requestStartTime.get();
        if (startTime != null) {
            if (LOG.isInfoEnabled()) {
                long duration = System.currentTimeMillis() - startTime;
                LOG.info("Finished request in {} milliseconds.", duration);
            }
        } else {
            LOG.warn("Finished request, but did not have a start time to compare with. No metrics have been recorded.");
        }
        // Finally cleanup ThreadLocal
        requestStartTime.remove();
        return response;
    }
}
