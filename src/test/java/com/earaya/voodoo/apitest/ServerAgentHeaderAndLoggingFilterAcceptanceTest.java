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
package com.earaya.voodoo.apitest;


import com.earaya.voodoo.VuduResourceConfig;
import com.earaya.voodoo.VuduServer;
import com.earaya.voodoo.config.HttpServerConfig;
import com.earaya.voodoo.filters.LoggingFilter;
import com.earaya.voodoo.filters.ServerAgentHeaderFilter;
import com.earaya.voodoo.modules.GenericServerInfoModule;
import com.earaya.voodoo.modules.ResourceServletModule;
import com.google.inject.Module;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServerAgentHeaderAndLoggingFilterAcceptanceTest {

    private final String expectedServerAgent = "myServer";

    int httpPort;
    VuduServer embeddedServer;
    HttpClient httpClient = new DefaultHttpClient();

    @Before
    public void setUp() throws Exception {
        System.setProperty(GenericServerInfoModule.SERVER_IDENTIFIER_PROPERTY, expectedServerAgent);
        httpPort = findFreePort();
        Module[] modules = {
                new ResourceServletModule(new VuduResourceConfig("com.earaya.voodoo.apitest")),
                new GenericServerInfoModule()};
        embeddedServer = new VuduServer(new HttpServerConfig(httpPort));
        embeddedServer.initialize(modules);
        embeddedServer.start();
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
        assertVoodooResponseIdPresent(response);
        assertServerAgentHeaderPresent(response);
    }

    @Test
    public void serverInfoAndLoggingFilterAreApplied() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertVoodooResponseIdPresent(response);
        assertServerAgentHeaderPresent(response);
    }

    @Test
    public void serverInfoAndLoggingFilterAreAppliedEvenWhenExceptionsThrown() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/internalErr"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(500, response.getStatusLine().getStatusCode());
        assertVoodooResponseIdPresent(response);
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
        assertEquals(expectedServerAgent, serverHeader.getValue());
    }

    private void assertVoodooResponseIdPresent(HttpResponse response) {
        Header voodooResponseId = response.getFirstHeader(LoggingFilter.X_VOODOO_RESPONSE_ID);
        assertNotNull(voodooResponseId);
    }
}


