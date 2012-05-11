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
package com.talis.jersey.apitest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.ServerSocket;

import com.talis.jersey.config.HttpServerConfig;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.talis.jersey.HttpServer;
import com.talis.jersey.filters.LoggingFilter;
import com.talis.jersey.filters.ServerAgentHeaderFilter;
import com.talis.jersey.guice.GenericServerInfoModule;
import com.talis.jersey.guice.JerseyServletModule;
import com.talis.jersey.guice.NoopAuthenticationModule;

public class ServerAgentHeaderAndLoggingFilterAcceptanceTest {
	
	private final String expectedServerAgent = "myServer";
	
	int httpPort;
	HttpServer embeddedServer;
	HttpClient httpClient = new DefaultHttpClient();
	
	@Before
	public void setUp() throws Exception {
		System.setProperty(GenericServerInfoModule.SERVER_IDENTIFIER_PROPERTY, expectedServerAgent);
		httpPort = findFreePort();
		Module[] modules = {new JerseyServletModule("com.talis.jersey.apitest"), 
							new NoopAuthenticationModule(),
							new GenericServerInfoModule()};
		embeddedServer = new HttpServer(new HttpServerConfig(httpPort));
		embeddedServer.start(modules);
	}
		
	@After
	public void tearDown() throws Exception {
		System.clearProperty(GenericServerInfoModule.SERVER_IDENTIFIER_PROPERTY);
	}
	
	@Test
	public void unknownResourceIs404ButStillHasServerInfoAndLoggingApplied() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "foo"));
		HttpResponse response = httpClient.execute(httpGet);
		assertEquals(404, response.getStatusLine().getStatusCode());
		assertTalisResponseIdPresent(response);
		assertServerAgentHeaderPresent(response);
	}

	@Test
	public void serverInfoAndLoggingFilterAreApplied() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub"));
		HttpResponse response = httpClient.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTalisResponseIdPresent(response);
		assertServerAgentHeaderPresent(response);
	}
	
	@Test
	public void serverInfoAndLoggingFilterAreAppliedEvenWhenExceptionsThrown() throws Exception {
		HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/internalErr"));
		HttpResponse response = httpClient.execute(httpGet);
		assertEquals(500, response.getStatusLine().getStatusCode());
		assertTalisResponseIdPresent(response);
		assertServerAgentHeaderPresent(response);
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
	
	private void assertServerAgentHeaderPresent(HttpResponse response) {
		Header serverHeader = response.getFirstHeader(ServerAgentHeaderFilter.SERVER_AGENT_HEADER);
		assertEquals(expectedServerAgent , serverHeader.getValue());
	}

	private void assertTalisResponseIdPresent(HttpResponse response) {
		Header talisResponseId = response.getFirstHeader(LoggingFilter.X_TALIS_RESPONSE_ID);
		assertNotNull(talisResponseId);
	}
}


