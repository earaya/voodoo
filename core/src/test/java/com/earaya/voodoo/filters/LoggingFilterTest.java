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

import com.earaya.voodoo.filters.answers.ContainsHeaderAnswer;
import com.earaya.voodoo.filters.answers.GetHeaderAnswer;
import com.earaya.voodoo.filters.answers.SetHeaderAnswer;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoggingFilterTest {

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private FilterChain mockFilterChain;

    private String fakeRUID;
    private LoggingFilter loggingFilter;

    MultivaluedMap<String, String> responseHeaders;

    @Before
    public void setup() {
        responseHeaders = new MultivaluedMapImpl();
        fakeRUID = UUID.randomUUID().toString();

        final RuidSupplier ruidSupplier = mock(RuidSupplier.class);
        when(ruidSupplier.get()).thenReturn(fakeRUID);

        loggingFilter = new LoggingFilter(ruidSupplier);

        String randomPath = UUID.randomUUID().toString();

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn(randomPath);


        mockResponse = mock(HttpServletResponse.class);
        when(mockResponse.containsHeader(anyString())).thenAnswer(ContainsHeaderAnswer.create(responseHeaders));
        when(mockResponse.getHeader(anyString())).thenAnswer(GetHeaderAnswer.create(responseHeaders));
        doAnswer(SetHeaderAnswer.create(responseHeaders)).when(mockResponse).setHeader(anyString(), anyString());
        doAnswer(SetHeaderAnswer.create(responseHeaders)).when(mockResponse).addHeader(anyString(), anyString());
    }

    @Test
    public void afterHandleRecordsResponseTimeOnNodeMetrics() throws IOException, ServletException {
        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
    }

    @Test
    public void xVoodooResponseIDReturnedWhenNoResponseHeadersExist() throws IOException, ServletException {
        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertEquals(1, responseHeaders.size());

        assertHeaderValue(mockResponse, LoggingFilter.X_VOODOO_RESPONSE_ID, fakeRUID);
    }

    @Test
    public void xVoodooResponseIDReturnedWhenHeadersAlreadyExist() throws IOException, ServletException {
        assertTrue(responseHeaders.isEmpty());
        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(responseHeaders.isEmpty());

        mockResponse.setHeader("customheader1", "foo");

        loggingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertEquals(2, responseHeaders.size());

        assertHeaderValue(mockResponse, LoggingFilter.X_VOODOO_RESPONSE_ID, fakeRUID);
        assertHeaderValue(mockResponse, "customheader1", "foo");
    }

    private void assertHeaderValue(final HttpServletResponse response, String headerName, Object expectedHeaderValue) {
        assertTrue(response.containsHeader(headerName));

        final String header = response.getHeader(headerName);
        assertNotNull(header);
        assertEquals(expectedHeaderValue, header);
    }

}
