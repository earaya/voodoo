package com.earaya.voodoo.filters;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;


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

	private LoggingFilter loggingFilter;
	private ContainerRequest mockRequest ;
	private ContainerResponse mockResponse;

	@Before
	public void setup() {
		loggingFilter = new LoggingFilter();
		
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
		
		assertEquals(1,responseHeaders.size());
		
		assertHeaderValue(responseHeaders, LoggingFilter.X_VOODOO_RESPONSE_ID, MDC.get(LoggingFilter.REQUEST_UID));
	}

	@Test
	public void xVoodooResponseIDReturnedWhenHeadersAlreadyExist() {
		loggingFilter.filter(mockRequest);
		
		MultivaluedMap<String, Object> responseHeaders = mockResponse.getHttpHeaders();
		responseHeaders.add("customheader1", "foo");
		
		loggingFilter.filter(mockRequest, mockResponse);		
		
		assertEquals(2,responseHeaders.size());
		
		assertHeaderValue(responseHeaders, LoggingFilter.X_VOODOO_RESPONSE_ID, MDC.get(LoggingFilter.REQUEST_UID));
		assertHeaderValue(responseHeaders, "customheader1", "foo");
	}

	private void assertHeaderValue(MultivaluedMap<String, Object> responseHeaders, String headerName, Object expectedHeaderValue) {
		assertTrue(responseHeaders.containsKey(headerName));		
		List<Object> headerValues = (List<Object>) responseHeaders.get(headerName);
		assertEquals(1,headerValues.size());
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
