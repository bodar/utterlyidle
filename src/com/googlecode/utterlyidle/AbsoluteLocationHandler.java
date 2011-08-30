package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
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
        Sequence<Uri> absoluteLocations = sequence(response.headers(LOCATION)).realise().map(asUri()).map(changeToAbsoluteUrl(baseUri(request)));
        response.headers().remove(LOCATION);
        for (Uri absoluteLocation : absoluteLocations) {
            response.header(LOCATION, absoluteLocation.toString());
        }
        return response;
    }

    public static Callable1<? super String, Uri> asUri() {
        return new Callable1<String, Uri>() {
            @Override
            public Uri call(String value) throws Exception {
                return Uri.uri(value);
            }
        };
    }

    private Uri baseUri(Request request) {
        String host = request.headers().getValue(HttpHeaders.HOST);
        if(host == null){
            return uri(basePath.toString());
        }
        return uri(format("http://%s%s", host, basePath));
    }

    public static Callable1<? super Uri, Uri> changeToAbsoluteUrl(final Uri baseUri) {
        return new Callable1<Uri, Uri>() {
            public Uri call(Uri uri) throws Exception {
                if(uri.authority() != null && uri.authority() != ""){
                    return uri;
                }
                return baseUri.path(uri.path()).query(uri.query()).fragment(uri.fragment());
            }
        };
    }


}
