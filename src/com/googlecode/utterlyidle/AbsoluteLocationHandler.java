package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.ServerUrl.*;
import static com.googlecode.utterlyidle.io.Url.url;
import static javax.ws.rs.core.HttpHeaders.LOCATION;

public class AbsoluteLocationHandler implements HttpHandler{
    private final HttpHandler httpHandler;
    private final ServerUrl serverUrl;

    public AbsoluteLocationHandler(final HttpHandler httpHandler, final ServerUrl serverUrl) {
        this.httpHandler = httpHandler;
        this.serverUrl = serverUrl;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        Sequence<String> absoluteLocations = sequence(response.headers(LOCATION)).realise().map(changeToAbsoluteUrl(serverUrl));
        response.headers().remove(LOCATION);
        for (String absoluteLocation : absoluteLocations) {
            response.header(LOCATION, absoluteLocation);
        }
        return response;
    }

}
