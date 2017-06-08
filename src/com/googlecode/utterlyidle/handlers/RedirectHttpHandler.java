package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.functions.Functions;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.Status.SEE_OTHER;

public class RedirectHttpHandler implements HttpClient {
    private final HttpHandler httpHandler;

    public RedirectHttpHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        Option<String> location = response.header(LOCATION);
        if (response.status().isRedirect() && location.isDefined()) {
            return SEE_OTHER.equals(response.status()) ?
                    handle(Request.get(location.get())) :
                    handle(Functions.modify(request, Request.Builder.uri(uri(location.get()))));
        }
        return response;
    }
}
