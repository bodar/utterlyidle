package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.HttpHeaders.LOCATION;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

public class AbsoluteLocationHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final BasePath basePath;

    public AbsoluteLocationHandler(final HttpHandler httpHandler, final BasePath basePath) {
        this.httpHandler = httpHandler;
        this.basePath = basePath;
    }

    public Response handle(Request request) throws Exception {
        Response response = httpHandler.handle(removeBasePathFromUri(request, basePath));
        Sequence<Uri> absoluteLocations = sequence(response.headers(LOCATION)).realise().map(asAbsoluteUri(request, basePath));
        response.headers().remove(LOCATION);
        for (Uri absoluteLocation : absoluteLocations) {
            response.header(LOCATION, absoluteLocation.toString());
        }
        return response;
    }

    public static Uri toAbsoluteUri(String absolutePath, Request request, BasePath basePath) {
        return toAbsoluteUri(Uri.uri(absolutePath), baseUri(removeBasePathFromUri(request, basePath), basePath));
    }

    public static Callable1<String, Uri> asAbsoluteUri(final Request request, final BasePath basePath) {
        return new Callable1<String, Uri>() {
            @Override
            public Uri call(String value) throws Exception {
                return toAbsoluteUri(value, request, basePath);
            }
        };
    }

    public static Uri toAbsoluteUri(Uri uri, Uri baseUri) {
        if (uri.isFullyQualified()) {
            return uri;
        }
        return baseUri.path(uri.path()).query(uri.query()).fragment(uri.fragment());
    }

    public static Callable1<? super Uri, Uri> asAbsoluteUri(final Uri baseUri) {
        return new Callable1<Uri, Uri>() {
            public Uri call(Uri uri) throws Exception {
                return toAbsoluteUri(uri, baseUri);
            }
        };
    }

    private static Request removeBasePathFromUri(Request request, BasePath basePath) {
        return request.uri(replacePath(request.uri(), removeBasePath(request.uri().path(), basePath)));
    }

    private static Uri replacePath(Uri uri, String value) {
        return uri.path(value);
    }

    private static String removeBasePath(String path, BasePath basePath) {
        String regex = "(" + quote(basePath.toString()) + ")";
        return path.replaceFirst(regex, "/");
    }

    public static Callable1<? super String, Uri> asUri() {
        return new Callable1<String, Uri>() {
            @Override
            public Uri call(String value) throws Exception {
                return Uri.uri(value);
            }
        };
    }

    private static Uri baseUri(Request request, BasePath basePath) {
        String host = request.headers().getValue(HttpHeaders.HOST);
        if (host == null) {
            return uri(basePath.toString());
        }
        return uri(format("http://%s%s", host, basePath));
    }
}
