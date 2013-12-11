package com.earaya.voodoo.async;

import com.earaya.voodoo.VoodooApplication;
import com.earaya.voodoo.apitest.VoodooFunctionalTest;
import com.earaya.voodoo.config.ConnectorConfig;
import com.earaya.voodoo.config.ServerConfig;
import com.earaya.voodoo.rest.RestComponent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncExecutorTest extends VoodooFunctionalTest {

    static int httpPort;

    @BeforeClass
    public static void setupClass() throws Exception {
        httpPort = findFreePort();
        VoodooApplication server = new VoodooApplication(new ServerConfig(new ConnectorConfig(httpPort)),
                new RestComponent(AsyncResource.class.getPackage()));
        server.start();
    }

    private HttpClient httpClient = new DefaultHttpClient();

    @Test
    public void simpleEchoTest() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "async/echo/simple"));
        HttpResponse response = httpClient.execute(httpGet);
        assertMsgAndStatus(response, 200, "simple");
    }

    @Test
    public void anotherEchoTest() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "async/echo/ASYNC_RESULT_SUCCESS"));
        HttpResponse response = httpClient.execute(httpGet);
        assertMsgAndStatus(response, 200, "ASYNC_RESULT_SUCCESS");
    }

    @Test
    public void throw503Test() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "async/exception/503"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(503, response.getStatusLine().getStatusCode());
    }

    @Test
    public void throw401Test() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "async/exception/401"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void differentThreadTest() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "async/checkThreadIds"));
        HttpResponse response = httpClient.execute(httpGet);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void multipleJobsTest() throws Exception {
        HttpGet httpGet = new HttpGet(getUrl(httpPort, "async/multiple/5"));
        HttpResponse response = httpClient.execute(httpGet);
        assertMsgAndStatus(response, 200, "[success, success, success, success, success]");
    }
}
