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


import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.components.RestComponent;
import com.earaya.voodoo.config.HttpServerConfig;
import com.earaya.voodoo.filters.LoggingFilter;
import com.earaya.voodoo.filters.ServletLoggingFilter;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServerAgentHeaderAndLoggingFilterAcceptanceTest {

    private final String expectedServerAgent = "myServer";

    int httpPort;
    VoodooApplication embeddedServer;
    HttpClient httpClient = new DefaultHttpClient();

    @Before
    public void setUp() throws Exception {
        httpPort = findFreePort();
        embeddedServer = new VoodooApplication(new HttpServerConfig(httpPort), new RestComponent("com.earaya.voodoo.apitest"));
        embeddedServer.start();
    }

    @Test
    public void unknownResourceIs404ButStillHasServerInfoAndLoggingApplied() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "foo"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(404, response.getStatusLine().getStatusCode());
        assertVoodooResponseIdPresent(response);
    }

    @Test
    public void serverInfoAndLoggingFilterAreApplied() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertVoodooResponseIdPresent(response);
    }

    @Test
    public void serverInfoAndLoggingFilterAreAppliedEvenWhenExceptionsThrown() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "stub/internalErr"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(500, response.getStatusLine().getStatusCode());
        assertVoodooResponseIdPresent(response);
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

    private void assertVoodooResponseIdPresent(HttpResponse response) {
        Header voodooResponseId = response.getFirstHeader(ServletLoggingFilter.X_VOODOO_RESPONSE_ID);
        assertNotNull(voodooResponseId);
    }
}


