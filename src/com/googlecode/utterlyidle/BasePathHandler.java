package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.BaseUri.baseUri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static com.googlecode.utterlyidle.Response.methods.headers;
import static java.util.regex.Pattern.quote;

public class BasePathHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final BasePath basePath;

    public BasePathHandler(final HttpHandler httpHandler, final BasePath basePath) {
        this.httpHandler = httpHandler;
        this.basePath = basePath;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(removeBasePathFromUri(request));
        Sequence<Uri> absoluteLocations = headers(response, LOCATION).
                map(uri()).
                map(asFullyQualified(baseUri(request, basePath)));
        ResponseBuilder responseBuilder = ResponseBuilder.modify(response).removeHeaders(LOCATION);
        for (Uri absoluteLocation : absoluteLocations) {
            responseBuilder.header(LOCATION, absoluteLocation.toString());
        }
        return responseBuilder.build();
    }

    private Callable1<? super String, Uri> uri() {
        return new Callable1<String, Uri>() {
            @Override
            public Uri call(String value) throws Exception {
                return Uri.uri(value);
            }
        };
    }

    public static Uri toFullyQualified(Uri uri, BaseUri baseUri) {
        if (uri.isFullyQualified()) {
            return uri;
        }
        return baseUri.value().mergePath(uri.path()).query(uri.query()).fragment(uri.fragment());
    }

    public static Callable1<Uri, Uri> asFullyQualified(final BaseUri baseUri) {
        return new Callable1<Uri, Uri>() {
            @Override
            public Uri call(Uri value) throws Exception {
                return toFullyQualified(value, baseUri);
            }
        };
    }

    private Request removeBasePathFromUri(Request request) {
        return modify(request).
                uri(request.uri().path(removeBasePath(request.uri().path()))).
                build();
    }

    private String removeBasePath(String path) {
        String regex = "(" + quote(basePath.toString()) + ")";
        return path.replaceFirst(regex, "/");
    }
}
