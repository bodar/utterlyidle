package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.io.Url;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.io.Url.url;
import static java.lang.String.format;

public class AbsoluteLocationHandler implements HttpHandler{
    private final HttpHandler httpHandler;
    private final BasePath basePath;

    public AbsoluteLocationHandler(final HttpHandler httpHandler, final BasePath basePath) {
        this.httpHandler = httpHandler;
        this.basePath = basePath;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(request);
        Sequence<String> absoluteLocations = sequence(response.headers(LOCATION)).realise().map(changeToAbsoluteUrl(createUrl(request)));
        response.headers().remove(LOCATION);
        for (String absoluteLocation : absoluteLocations) {
            response.header(LOCATION, absoluteLocation);
        }
        return response;
    }

    private Url createUrl(Request request) {
        String host = request.headers().getValue(HttpHeaders.HOST);
        if(host == null){
            return url(basePath.toString());
        }
        return url(format("http://%s%s", host, basePath));
    }

    public static Callable1<? super String, String> changeToAbsoluteUrl(final Url Url) {
        return new Callable1<String, String>() {
            public String call(String path) throws Exception {
                if (url(path).isAbsolute()) {
                    return path;
                }
                return Url.replacePath(Url.path().file(path)).toString();
            }
        };
    }


}
