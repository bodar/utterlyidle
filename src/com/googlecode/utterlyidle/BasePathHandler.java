package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.BaseUri.baseUri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
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
        Sequence<Uri> absoluteLocations = sequence(response.headers(LOCATION)).realise().map(asAbsoluteUri(baseUri(request, basePath)));
        response.headers().remove(LOCATION);
        for (Uri absoluteLocation : absoluteLocations) {
            response.header(LOCATION, absoluteLocation.toString());
        }
        return response;
    }

    public Uri toAbsoluteUri(String absolutePath, BaseUri baseUri) {
        Uri uri = uri(absolutePath);
        if (uri.isFullyQualified()) {
            return uri;
        }
        return baseUri.value().path(stripLeadingSlash(uri.path())).query(uri.query()).fragment(uri.fragment());
    }

    public Callable1<String, Uri> asAbsoluteUri(final BaseUri baseUri) {
        return new Callable1<String, Uri>() {
            @Override
            public Uri call(String value) throws Exception {
                return toAbsoluteUri(value, baseUri);
            }
        };
    }

    private static String stripLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private Request removeBasePathFromUri(Request request) {
        return request.uri(replacePath(request.uri(), removeBasePath(request.uri().path())));
    }

    private static Uri replacePath(Uri uri, String value) {
        return uri.path(value);
    }

    private String removeBasePath(String path) {
        String regex = "(" + quote(basePath.toString()) + ")";
        return path.replaceFirst(regex, "/");
    }
}
