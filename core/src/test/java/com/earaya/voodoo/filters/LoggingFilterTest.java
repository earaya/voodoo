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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


@RunWith(Theories.class)
public class LoggingFilterTest {

    @DataPoints
    public static final int[] HTTP_SUCCESS_RESPONSE_CODES =
            new int[]{200, 201, 202, 203, 204, 205, 206};

    @DataPoints
    public static final int[] HTTP_REDIRECT_RESPONSE_CODES =
            new int[]{300, 301, 302, 303, 304, 305, 307};

    @DataPoints
    public static final int[] HTTP_CLIENT_ERROR_CODES =
            new int[]{400,
                    401,
                    402,
                    403,
                    404,
                    405,
                    406,
                    407,
                    408,
                    409,
                    410,
                    411,
                    412,
                    413,
                    414,
                    415,
                    416,
                    417};

    @DataPoints
    public static final int[] HTTP_SERVER_ERROR_CODES =
            new int[]{500, 501, 502, 503, 504, 505};

    private String mockRUID;
    private LoggingFilter loggingFilter;
    private ContainerRequest mockRequest;
    private ContainerResponse mockResponse;

    @Before
    public void setup() {

        mockRUID = UUID.randomUUID().toString();

        final RuidSupplier ruidSupplier = createNiceMock(RuidSupplier.class);
        expect(ruidSupplier.get()).andReturn(mockRUID).anyTimes();
        replay(ruidSupplier);

        loggingFilter = new LoggingFilter(ruidSupplier);

        String randomPath = UUID.randomUUID().toString();

        mockRequest = createNiceMock(ContainerRequest.class);
        expect(mockRequest.getPath()).andReturn(randomPath).anyTimes();
        replay(mockRequest);

        mockResponse = createNiceMock(ContainerResponse.class);
        MultivaluedMap<String, String> responseHeaders = new MultivaluedMapImpl();
        mockResponse.getHttpHeaders();
        expectLastCall().andReturn(responseHeaders).anyTimes();
        replay(mockResponse);

    }

    @Test
    public void afterHandleRecordsResponseTimeOnNodeMetrics() {
        loggingFilter.filter(mockRequest);
        loggingFilter.filter(mockRequest, mockResponse);
    }

    @Test
    public void xVoodooResponseIDReturnedWhenNoResponseHeadersExist() {
        loggingFilter.filter(mockRequest);
        loggingFilter.filter(mockRequest, mockResponse);
        MultivaluedMap<String, Object> responseHeaders = mockResponse.getHttpHeaders();

        assertEquals(1, responseHeaders.size());

        assertHeaderValue(responseHeaders, ServletLoggingFilter.X_VOODOO_RESPONSE_ID, mockRUID);
    }

    @Test
    public void xVoodooResponseIDReturnedWhenHeadersAlreadyExist() {
        loggingFilter.filter(mockRequest);

        MultivaluedMap<String, Object> responseHeaders = mockResponse.getHttpHeaders();
        responseHeaders.add("customheader1", "foo");

        loggingFilter.filter(mockRequest, mockResponse);

        assertEquals(2, responseHeaders.size());

        assertHeaderValue(responseHeaders, ServletLoggingFilter.X_VOODOO_RESPONSE_ID, mockRUID);
        assertHeaderValue(responseHeaders, "customheader1", "foo");
    }

    private void assertHeaderValue(MultivaluedMap<String, Object> responseHeaders, String headerName, Object expectedHeaderValue) {
        assertTrue(responseHeaders.containsKey(headerName));
        List<Object> headerValues = (List<Object>) responseHeaders.get(headerName);
        assertEquals(1, headerValues.size());
        Object requestId = headerValues.get(0);
        assertNotNull(requestId);
        assertEquals(expectedHeaderValue, requestId);
    }

    @Test
    public void calculationOfDurationDoesntFailIfNoStartTimeRecorded() {
        loggingFilter.filter(mockRequest);
        MDC.remove(LoggingFilter.REQUEST_START_TIME);
        loggingFilter.filter(mockRequest, mockResponse);
    }

    @Test
    public void calculationOfDurationDoesntFailIfMDCIsAccidentallyCleared() {
        loggingFilter.filter(mockRequest);
        MDC.clear();
        loggingFilter.filter(mockRequest, mockResponse);
    }

}
