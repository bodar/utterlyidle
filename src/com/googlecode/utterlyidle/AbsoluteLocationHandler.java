package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.io.Url.url;
import static javax.ws.rs.core.HttpHeaders.LOCATION;

public class AbsoluteLocationHandler implements HttpHandler{
    private final HttpHandler httpHandler;
    private final BasePath basePath;

    public AbsoluteLocationHandler(final HttpHandler httpHandler, final BasePath basePath) {
        this.httpHandler = httpHandler;
        this.basePath = basePath;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        Sequence<String> absoluteLocations = sequence(response.headers(LOCATION)).realise().map(addBasePathIfNeeded(request, basePath));
        response.headers().remove(LOCATION);
        for (String absoluteLocation : absoluteLocations) {
            response.header(LOCATION, absoluteLocation);
        }
        return response;
    }

    private Callable1<? super String, String> addBasePathIfNeeded(final Request request, final BasePath basePath) {
        return new Callable1<String, String>() {
            public String call(String location) throws Exception {
                if(url(location).isAbsolute()){
                    return location;
                }
                return basePath.file(location).toString();
            }
        };
    }
}
