package com.googlecode.utterlyidle.authentication;

import com.googlecode.utterlyidle.handlers.HttpClient;
import org.junit.Test;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.utterlyidle.authentication.Credential.credential;
import static com.googlecode.utterlyidle.authentication.ClientCredentials.clientCredentials;

public class BasicHttpClientTest {
    @Test
    public void foo() throws Exception {
        HttpClient server = null;
        ClientCredentials credentials = clientCredentials(map("example.com", credential("dan", "blah")));
        HttpClient client = new BasicHttpClient(server, credentials);
        // GET /foo
        // <- 401 with realm
        // GET /foo + Basic



    }

}