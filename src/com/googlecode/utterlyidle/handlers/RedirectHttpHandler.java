package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Response.methods.header;
import static com.googlecode.utterlyidle.Status.FOUND;
import static com.googlecode.utterlyidle.Status.MOVED_PERMANENTLY;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;
import static com.googlecode.utterlyidle.Status.TEMPORARY_REDIRECT;

public class RedirectHttpHandler implements HttpClient {
    private final HttpHandler httpHandler;

    public RedirectHttpHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        if(isRedirect(response)){
           return handle(RequestBuilder.get(header(response, LOCATION)).build());
        }
        return response;
    }

    private boolean isRedirect(Response response) {
        return sequence(MOVED_PERMANENTLY, TEMPORARY_REDIRECT, FOUND, SEE_OTHER).contains(response.status());
    }
}
