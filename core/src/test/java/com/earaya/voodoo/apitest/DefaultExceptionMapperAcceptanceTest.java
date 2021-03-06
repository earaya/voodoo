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
import com.earaya.voodoo.config.ConnectorConfig;
import com.earaya.voodoo.config.ServerConfig;
import com.earaya.voodoo.rest.RestComponent;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultExceptionMapperAcceptanceTest extends VoodooFunctionalTest {

    int httpPort;
    VoodooApplication embeddedServer;
    HttpClient httpClient = new DefaultHttpClient();

    @Before
    public void setUp() throws Exception {
        httpPort = findFreePort();
        embeddedServer = new VoodooApplication(new ServerConfig(new ConnectorConfig(httpPort)),
                new RestComponent("com.earaya.voodoo.apitest"));
        embeddedServer.start();
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

}

