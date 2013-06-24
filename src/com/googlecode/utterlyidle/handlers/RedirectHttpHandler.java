package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;

import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Response.methods.header;

public class RedirectHttpHandler implements HttpClient {
    private final HttpHandler httpHandler;

    public RedirectHttpHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if (response.status().isRedirect()) {
            return handle(get(header(response, LOCATION)).build());
        }
        return response;
    }
}
