package com.earaya.voodoo.apitest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

import com.earaya.voodoo.config.HttpServerConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Module;
import com.earaya.voodoo.HttpServer;
import com.earaya.voodoo.guice.GenericServerInfoModule;
import com.earaya.voodoo.guice.JerseyServletModule;

public class DefaultExceptionMapperAcceptanceTest {
	
	int httpPort;
	HttpServer embeddedServer;
	HttpClient httpClient = new DefaultHttpClient();

	@Before
	public void setUp() throws Exception {
		httpPort = findFreePort();
		Module[] modules = {new JerseyServletModule("com.earaya.voodoo.apitest"),
							new GenericServerInfoModule()};
		embeddedServer = new HttpServer(new HttpServerConfig(httpPort));
		embeddedServer.start(modules);
	}
		
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testUnknownResourceIs404() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "foo"));
		HttpResponse response = httpClient.execute(httpGet);
		assertEquals(404, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testStubResourceWorksFor200() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub"));
		HttpResponse response = httpClient.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testStubResourceThrows400() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/badReq"));
		HttpResponse response = httpClient.execute(httpGet);
		assertMsgAndStatus(response, 400, StubResource.SORRY_BAD_REQUEST);
	}
	
	@Test
	public void testStubResourceThrows404() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/notFound"));
		HttpResponse response = httpClient.execute(httpGet);
		assertMsgAndStatus(response, 404, StubResource.SORRY_NOT_FOUND);
	}
	
	@Test
	public void testStubResourceThrows500() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/internalErr"));
		HttpResponse response = httpClient.execute(httpGet);
		assertMsgAndStatus(response, 500, StubResource.SORRY_INTERNAL_ERROR);
	}
	
	@Test
	public void testStubResourceThrows503WithDefaultRetry() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/unavailableErr"));
		HttpResponse response = httpClient.execute(httpGet);
		assertMsgAndStatus(response, 503, StubResource.SORRY_UNAVAILABLE_ERROR);
		Header retryAfter = response.getFirstHeader("Retry-After");
		assertNotNull(retryAfter);
	}
	
	@Test
	public void testStubResourceThrows503WithSpecificRetry() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/unavailableErr/3min"));
		HttpResponse response = httpClient.execute(httpGet);
		assertMsgAndStatus(response, 503, StubResource.SORRY_UNAVAILABLE_ERROR);
		Header retryAfter = response.getFirstHeader("Retry-After");
		assertEquals("180", retryAfter.getValue());
	}

	private void assertMsgAndStatus(HttpResponse response, int statusCode,
			String msg) throws IOException {
		assertEquals(statusCode, response.getStatusLine().getStatusCode());
		HttpEntity entity = response.getEntity();
		try {
			InputStream content = entity.getContent();
			assertEquals(msg, IOUtils.toString(content));
		} finally {
			EntityUtils.consume(entity);
		}
	}

	private int findFreePort() throws IOException {
		ServerSocket serverSocket = new ServerSocket(0);
		int localPort = serverSocket.getLocalPort();
		serverSocket.close();
		return localPort;
	}
	
	private String getUrl(int port, String path) {
		return String.format("http://localhost:%d/%s", port, path);
	}

}

