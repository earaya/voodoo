package com.earaya.voodoo.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

    public static final String X_VOODOO_RESPONSE_ID = "X-VOODOO-RESPONSE-ID";
    public static final String REQUEST_UID = "R_UID";
    static final String REQUEST_START_TIME = "R_START_TIME";
    private final Random r = new Random();

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        MDC.put(REQUEST_UID, getRUID(8));
        MDC.put(REQUEST_START_TIME, new Long(System.currentTimeMillis()));
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Starting request %s", request.getPath()));
        }

        return request;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        Object object = MDC.get(REQUEST_START_TIME);
        if (object instanceof Long) {
            Long startTime = (Long) object;
            String requestUid = (String) MDC.get(REQUEST_UID);
            long duration = System.currentTimeMillis() - startTime;
            response.getHttpHeaders().add(X_VOODOO_RESPONSE_ID, requestUid);
            LOG.info("Finished request in {} milliseconds.", duration);
        } else {
            LOG.warn("Finished request, but did not have a start time to compare with. No metrics have been recorded.");
        }
        return response;
    }

    private String getRUID(int len) {
        StringBuffer uid = new StringBuffer();
        for (int i = 0; i < len; i++) {
            int rand = r.nextInt(10000);
            int mod36 = rand % 36;
            encodeAndAdd(uid, mod36);
        }
        return uid.toString();
    }

    private void encodeAndAdd(StringBuffer ret, long mod36Val) {
        if (mod36Val < 10) {
            ret.append((char) (((int) '0') + (int) mod36Val));
        } else {
            ret.append((char) (((int) 'a') + (int) (mod36Val - 10)));
        }
    }
}
