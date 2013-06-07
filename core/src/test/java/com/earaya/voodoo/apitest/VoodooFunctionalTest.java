package com.earaya.voodoo.apitest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

import static org.junit.Assert.assertEquals;

public class VoodooFunctionalTest {

    protected static void assertMsgAndStatus(HttpResponse response, int statusCode,
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

    protected static int findFreePort() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        int localPort = serverSocket.getLocalPort();
        serverSocket.close();
        return localPort;
    }

    protected static String getUrl(int port, String path) {
        return String.format("http://localhost:%d/%s", port, path);
    }
}
